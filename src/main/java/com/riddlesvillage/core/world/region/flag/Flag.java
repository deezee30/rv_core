package com.riddlesvillage.core.world.region.flag;

import com.riddlesvillage.core.Core;
import com.riddlesvillage.core.Messaging;
import com.riddlesvillage.core.collect.EnhancedList;
import com.riddlesvillage.core.player.event.CorePlayerDamageEntityEvent;
import com.riddlesvillage.core.player.event.CorePlayerDamagePlayerEvent;
import com.riddlesvillage.core.world.Vector3D;
import com.riddlesvillage.core.world.region.Region;
import com.riddlesvillage.core.world.region.RegionCriteria;
import com.riddlesvillage.core.world.region.Regions;
import org.apache.commons.lang3.Validate;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Animals;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Vehicle;
import org.bukkit.event.*;
import org.bukkit.event.block.*;
import org.bukkit.event.entity.*;
import org.bukkit.event.entity.EntityRegainHealthEvent.RegainReason;
import org.bukkit.event.player.*;
import org.bukkit.event.vehicle.VehicleDestroyEvent;
import org.bukkit.plugin.EventExecutor;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;

public class Flag<E extends Event & Cancellable> implements EventExecutor {

    private static final EventPriority EVENT_PRIORITY = EventPriority.MONITOR;
    private static final EnhancedList<Flag> ALL_FLAGS = new EnhancedList<>();

    public static final Flag
            BUILD				= new Flag<>(BlockPlaceEvent.class, e -> e.getBlock().getLocation()),
            BREAK				= new Flag<>(BlockBreakEvent.class, e -> e.getBlock().getLocation()),
            CHAT				= new Flag<>(AsyncPlayerChatEvent.class, e -> e.getPlayer().getLocation()),
            COMMAND				= new Flag<>(PlayerCommandPreprocessEvent.class, e -> e.getPlayer().getLocation(), e -> !e.getMessage().startsWith("/")),
            PVP					= new Flag<>(CorePlayerDamagePlayerEvent.class, e -> e.getPlayer().getLocation()),
            PVE					= new Flag<>(CorePlayerDamageEntityEvent.class, e -> e.getPlayer().getLocation()),
            ALL_DAMAGE			= new Flag<>(EntityDamageEvent.class, e -> e.getEntity().getLocation()),
            BLOCK_INTERACT		= new Flag<>(PlayerInteractEvent.class, e -> e.getClickedBlock().getLocation(), e -> e.getAction().equals(Action.LEFT_CLICK_BLOCK)),
            ITEM_INTERACT		= new Flag<>(PlayerInteractEvent.class, e -> e.getPlayer().getLocation(), PlayerInteractEvent::hasItem),
            ENTITY_INTERACT		= new Flag<>(PlayerInteractAtEntityEvent.class, e -> e.getPlayer().getLocation()),
            ENDERMAN_INTERACT	= new Flag<>(EntityChangeBlockEvent.class, e -> e.getBlock().getLocation(), e -> e.getEntityType().equals(EntityType.ENDERMAN)),
            ITEM_SPAWN			= new Flag<>(EntitySpawnEvent.class, EntitySpawnEvent::getLocation, e -> e.getEntityType().equals(EntityType.DROPPED_ITEM)),
            EXP_SPAWN			= new Flag<>(EntitySpawnEvent.class, EntitySpawnEvent::getLocation, e -> e.getEntityType().equals(EntityType.EXPERIENCE_ORB)),
            ANIMAL_SPAWN		= new Flag<>(EntitySpawnEvent.class, EntitySpawnEvent::getLocation, e -> e.getEntity() instanceof Animals),
            MONSTER_SPAWN		= new Flag<>(EntitySpawnEvent.class, EntitySpawnEvent::getLocation, e -> e.getEntity() instanceof Monster),
            EXPLOSION			= new Flag<>(ExplosionPrimeEvent.class, e -> e.getEntity().getLocation()),
            HEALTH_REGEN		= new Flag<>(EntityRegainHealthEvent.class, e -> e.getEntity().getLocation(), e -> e.getRegainReason().equals(RegainReason.REGEN) || e.getRegainReason().equals(RegainReason.SATIATED)),
            HUNGER_LOSS			= new Flag<>(FoodLevelChangeEvent.class, e -> e.getEntity().getLocation()),
            POTION_SPLASH		= new Flag<>(PotionSplashEvent.class, e -> e.getEntity().getLocation()),
            BLOCK_BURN			= new Flag<>(BlockBurnEvent.class, e -> e.getBlock().getLocation()),
            VEHICLE_PLACE		= new Flag<>(EntitySpawnEvent.class, e -> e.getEntity().getLocation(), e -> e.getEntity() instanceof Vehicle),
            VEHICLE_DESTROY		= new Flag<>(VehicleDestroyEvent.class, e -> e.getVehicle().getLocation()),
            SLEEP				= new Flag<>(PlayerBedEnterEvent.class, e -> e.getBed().getLocation()),
            BLOCK_FORM			= new Flag<>(BlockFormEvent.class, e -> e.getBlock().getLocation()),
            BLOCK_FADE			= new Flag<>(BlockFadeEvent.class, e -> e.getBlock().getLocation()),
            BLOCK_MOVE			= new Flag<>(BlockFromToEvent.class, e -> e.getBlock().getLocation()),
            BLOCK_SPREAD		= new Flag<>(BlockSpreadEvent.class, e -> e.getBlock().getLocation());

    protected String flag;
    protected Class<E> event;
    protected Function<E, Location> locationOfAction;
    protected Optional<Predicate<E>> predicate;

    private Flag(final Class<E> event,
                 final Function<E, Location> locationOfAction) {
        this(event, locationOfAction, null);
    }

    private Flag(final Class<E> event,
                 final Function<E, Location> locationOfAction,
                 final Predicate<E> predicate) {
        this.event = event;
        this.locationOfAction = locationOfAction;
        this.predicate = Optional.ofNullable(predicate);
        ALL_FLAGS.add(this);
        Bukkit.getPluginManager().registerEvent(
                event, new Listener() {}, EVENT_PRIORITY, this, Core.get());
    }

    public String getFlag() {
        return flag;
    }

    @Override
    public String toString() {
        return flag;
    }

    @Override
    public void execute(final Listener listener,
                        final Event providedEvent) throws EventException {
        if (!(providedEvent instanceof Cancellable))
            throw new EventException("Event " + providedEvent.getEventName() + " is not cancellable");

        if (!this.event.isInstance(providedEvent) || locationOfAction == null)
            return; // wrong event

        E event = (E) providedEvent;

        if (predicate.isPresent() && !predicate.get().test(event))
            return; // does not match predicate

        Optional<Location> locOfAction = Optional.empty();

        try {
            locOfAction = Optional.ofNullable(locationOfAction.apply(event));
        } catch (Exception e) {
            Messaging.debug("Could not apply event to location function: " + e);
        }

        if (!locOfAction.isPresent()) return;

        Location locAction = locOfAction.get();
        Vector3D location = Vector3D.fromLocation(locAction);

        // obtain registered regions with current flag registered
        List<Region> regions = Regions.getRegions(new RegionCriteria()
                .inWorlds(locAction.getWorld())
                .at(location)
                .withFlags(new FlagMap(this, true)));

        // there should be maximum 1 region with this criteria
        if (regions.isEmpty()) return;

        // TODO: Call an unspecified action when cancel is successful
        // TODO: eg: A message to the associated player
        event.setCancelled(true);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        Flag<?> flag1 = (Flag<?>) o;

        return new EqualsBuilder()
                .append(flag, flag1.flag)
                .append(event, flag1.event)
                .append(locationOfAction, flag1.locationOfAction)
                .append(predicate, flag1.predicate)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(flag)
                .append(event)
                .append(locationOfAction)
                .append(predicate)
                .toHashCode();
    }

    public static Optional<Flag> from(final String flag) {
        for (Flag f : ALL_FLAGS) {
            if (f.flag.toUpperCase().equals(flag.toUpperCase())) {
                return Optional.of(f);
            }
        }

        return Optional.empty();
    }

    public static Optional<Flag> from(final int id) {
        return Optional.ofNullable(ALL_FLAGS.get(id));
    }

    public static <E extends Event & Cancellable> EnhancedList<Flag> from(final Class<E> event) {
        EnhancedList<Flag> flags = new EnhancedList<>();
        ALL_FLAGS.forEach(f -> flags.addIf(f.event.equals(event), f));
        return flags;
    }

    public static <E extends Event & Cancellable> Flag create(final String flag,
                                                              final Class<E> event,
                                                              final Function<E, Location> locationOfAction) {
        return create(flag, event, locationOfAction, Optional.empty());
    }

    public static <E extends Event & Cancellable> Flag create(final String flag,
                                                              final Class<E> event,
                                                              final Function<E, Location> locationOfAction,
                                                              final Optional<Predicate<E>> predicate) {
        Validate.notNull(flag);
        Validate.notNull(event);
        Validate.notNull(locationOfAction);

        Flag f = new Flag<>(event, locationOfAction, predicate.isPresent() ? predicate.get() : null);
        f.flag = flag.toUpperCase();
        return f;
    }

    public static void init() {
        try {
            for (Field field : Flag.class.getDeclaredFields()) {
                if (Modifier.isStatic(field.getModifiers()) && Modifier.isPublic(field.getModifiers())) {
                    Flag flag = (Flag) field.get(null);
                    flag.flag = field.getName();
                }
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }
}