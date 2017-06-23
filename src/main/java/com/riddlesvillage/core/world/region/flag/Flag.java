package com.riddlesvillage.core.world.region.flag;

import com.riddlesvillage.core.collect.EnhancedList;
import com.riddlesvillage.core.player.event.CorePlayerDamageEntityEvent;
import com.riddlesvillage.core.player.event.CorePlayerDamagePlayerEvent;
import org.bukkit.entity.Animals;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Vehicle;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.block.*;
import org.bukkit.event.entity.*;
import org.bukkit.event.player.*;
import org.bukkit.event.vehicle.VehicleDestroyEvent;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Optional;
import java.util.function.Predicate;

public class Flag<E extends Event & Cancellable> {

	private static final EnhancedList<Flag> ALL_FLAGS = new EnhancedList<>();

	public static final Flag
			BUILD				= new Flag<>(BlockPlaceEvent.class),
			BREAK				= new Flag<>(BlockBreakEvent.class),
			TALK				= new Flag<>(AsyncPlayerChatEvent.class),
			COMMAND				= new Flag<>(PlayerCommandPreprocessEvent.class),
			PVP					= new Flag<>(CorePlayerDamagePlayerEvent.class),
			PVE					= new Flag<>(CorePlayerDamageEntityEvent.class),
			ALL_DAMAGE			= new Flag<>(EntityDamageEvent.class),
			BLOCK_INTERACTION	= new Flag<>(PlayerInteractEvent.class, e -> e.getAction().equals(Action.LEFT_CLICK_BLOCK)),
			ITEM_INTERACTION	= new Flag<>(PlayerInteractEvent.class, PlayerInteractEvent::hasItem),
			ENTITY_INTERACTION	= new Flag<>(PlayerInteractAtEntityEvent.class),
			ITEM_SPAWN			= new Flag<>(EntitySpawnEvent.class, e -> e.getEntityType().equals(EntityType.DROPPED_ITEM)),
			EXP_SPAWN			= new Flag<>(EntitySpawnEvent.class, e -> e.getEntityType().equals(EntityType.EXPERIENCE_ORB)),
			PASSIVE_MOB_SPAWN	= new Flag<>(EntitySpawnEvent.class, e -> e.getEntity() instanceof Animals),
			AGGRESSIVE_MOB_SPAWN= new Flag<>(EntitySpawnEvent.class, e -> e.getEntity() instanceof Monster),
			EXPLOSION			= new Flag<>(ExplosionPrimeEvent.class),
			HEALTH_REGENERATION = new Flag<>(EntityRegainHealthEvent.class),
			HUNGER_LOSS			= new Flag<>(FoodLevelChangeEvent.class),
			POTION_SPLASH		= new Flag<>(PotionSplashEvent.class),
			BLOCK_BURN			= new Flag<>(BlockBurnEvent.class),
			VEHICLE_PLACE		= new Flag<>(EntitySpawnEvent.class, e -> e.getEntity() instanceof Vehicle),
			VEHICLE_DESTROY		= new Flag<>(VehicleDestroyEvent.class),
			SLEEP				= new Flag<>(PlayerBedEnterEvent.class),
			BLOCK_FORM			= new Flag<>(BlockFormEvent.class),
			BLOCK_FADE			= new Flag<>(BlockFadeEvent.class),
			BLOCK_MOVE			= new Flag<>(BlockFromToEvent.class),
			BLOCK_SPREAD		= new Flag<>(BlockSpreadEvent.class),
			ENDERMAN_INTERACT	= new Flag<>(EntityChangeBlockEvent.class, e -> e.getEntityType().equals(EntityType.ENDERMAN));

	protected String flag;
	protected Class<E> event;
	protected Optional<Predicate<E>> predicate;

	private Flag(Class<E> event) {
		this(event, null);
	}

	private Flag(Class<E> event, Predicate<E> predicate) {
		this.event = event;
		this.predicate = Optional.ofNullable(predicate);
		ALL_FLAGS.add(this);
	}

	public String getFlag() {
		return flag;
	}

	@Override
	public String toString() {
		return flag;
	}

	public static Flag from(String flag) {
		for (Flag f : ALL_FLAGS) {
			if (f.getFlag().toUpperCase().equals(flag.toUpperCase())) {
				return f;
			}
		}

		return null;
	}

	public static Flag from(int id) {
		return ALL_FLAGS.get(id);
	}

	public static <E extends Event & Cancellable> Flag create(String flag, Class<E> event) {
		return create(flag, event, null);
	}

	public static <E extends Event & Cancellable> Flag create(
			String flag,
			Class<E> event,
			Predicate<E> predicate) {
		Flag f = new Flag<>(event, predicate);
		f.flag = flag;
		return f;
	}

	static {
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