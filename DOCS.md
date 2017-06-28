# RiddlesCore
Will be moved to Wiki

## API

### Player

#### Management and setup

If the developer wants to adapt to the player engine correctly,
the online and offline player objects should be sub-classes of
`AbstractCoreProfile`.

`AbstractCoreProfile`'s job is to download relevant statistics from
the database including internal and custom `Document`s from any
provided `MongoCollection<Document>` automatically before the player
is interactable. This is done upon the instantiation of the class.

There are three constructors to call from the sub class:
- `AbstractCoreProfile(UUID uuid)`
- `AbstractCoreProfile(String name)`
- `AbstractCoreProfile(UUID uuid, String name)`

The first 2 should be called when **only either** the name **or**
the `UUID` of the player are known, but not both. This will trigger
a lookup for either of those in the `MongoDB` database. If the
lookup fails, ie: a player with such credentials wasn't found -
then a *fake player* is generated. The unknown `UUID` or name will
remain the same as provided.

However, when a player's credentials are known, `AbstractCoreProfile(UUID, String)`
should be called. The player's statistics will be downloaded from
which ever `MongoCollection<Document>` is provided from the sub
class's `#getCollection()` via the primary key, AKA the player's 
`UUID`. Every player-related collection must contain a `UUID` field
to be used as a primary key. If the sub class does not have an
associated collection in the database, `null` can be returned and
a further statistic lookup won't be performed.

Once the lookup is made, `#onLoad(Optional<Document> stats)` will be
called, where the argument contains all fields found in the player's
collection. This is called directly after all database lookups have
been performed. Those can be extracted and set as local variables.
Below is a quick example of how this procedure would go.

*In the event listener:*
```java
@EventHandler(priority = EventPriority.LOW)
public void onCorePlayerPostLoad(CorePlayerPostLoadEvent event) {
    CorePlayer player = event.getPlayer();
    CustomPlayer customPlayer = new CustomPlayer(player);
    // Also add customPlayer to cache in the player manager
}
```

`CustomPlayer.class`:
```java
public final class CustomPlayer extends AbstractCoreProfile {

    // reference to core player is handy
    private final CorePlayer player;

    // statistics from database
    private int kills = 0, deaths = 0;

    CustomPlayer(CorePlayer player) {
        super(player.getUuid(), player.getName());
        this.player = player;

        // ... load the player instance as you do ...
    }

    @Override
    public void onLoad(Optional<Document> doc) {
        if (doc.isPresent()) {
            // update cached statistics as soon as they are downloaded async
            Document stats = doc.get();
            kills = stats.getInteger("kills");
            deaths = stats.getInteger("deaths");

            // load the rest of the player based on given data
        } else {
            // handle error
            RiddlesCore.log("Failed player lookup!");
        }
    }

    @Override
    public MongoCollection<Document> getCollection() {
        // return a custom collection or null if none
        return database.getCollection("pvp");
    }

    @Override
    public CorePlayer toCorePlayer() {
        return player;
    }
}
```

A similar approach should be used for offline players too, if needed.

> Note that there must only be at most **ONE** online **and** offline player instances per player.

> There must only be either an online or an offline player instance cached at once but not both.

> When an instance isn't needed anymore (ie the player leaves), the instance needs to be destroyed.

#### Economy

By default, the core utilizes two forms of economy, **coins** and
**tokens**. Coins are the general everyday currency players can
obtain from participating and winning gamemodes, etc. Tokens are
a rarer currency that are more difficult to come by. A rough
estimated currency exchange would be `1 token = 1000 coins`.

##### Managing currency

The most simplistic way to change a player's coin count is via
`profile#setCoins(Value)`, where `profile` is the online or offline
player instance - for example `CorePlayer`. `Value` is a simple
class that stores an `int` and whether the value is supposed to
**increase**, **decrease** or **set** the player's new coin count
by the specified `int`. Tokens work the exact same way.

Suppose you want to award a player with `1` token for winning a raffle:
```java
CorePlayer player = CorePlayerManager.getInstance().get("winner");
Value<Integer> value = new Value(1, ValueType.GIVE);
player.setTokens(value); // this will append 1 token to the current count
player.sendMessage("1 token has been added to your account");
player.sendMessage("You now have a total of %s tokens", player.getTokens());
```

##### Coin multiplier

The coin multiplier is a personal factor `double` that accelerates earning
coins. This can be enabled for a single player or the full server temporarily.
This factor is only applied when the coin value increases, and an additional
`boolean` parameter is also passed through `#setCoins()`.

Calling `setCoins(value)` by default will not apply the coin multiplier. This
is for use such as manual adding of coins via prizes, commands, etc. In
situations where deserved, `setCoins(value, true)` should be called.

#### Events

##### `CoinValueModificationEvent`:
> Called before a profile's coin value is bound to be changed.

Example:
```java
@EventHandle(priority = EventPriority.HIGH)
public void onCoinAdd(CoinValueModificationEvent event) {
    // disable having more than 10,000 coins for players
    if (event.getNewCoins() > 10000) {
        event.setCancelled(true);
    }
}
```

##### `TokenValueModificationEvent`:
> Called before a profile's token value is bound to be changed.

Works the same way as `CoinValueModificationEvent`

##### `PremiumStatusModificationEvent`:
> Called before a profile's premium status is bound to be changed.

```java
@EventHandle(priority = EventPriority.HIGH)
public void onPremiumStateChange(PremiumStatusModificationEvent event) {
    boolean newState = event.isPremium();
}
```

##### `CorePlayerDeathByPlayerEvent`:
> Called when one live player kills another.

**Settings:**

| Method                       | Description |
| ---------------------------- | ----------- |
| `autoRespawn(boolean)`       | Whether or not the player should automatically respawn without a death screen |
| `sendVictimMessage(boolean)` | Whether or not to send the victim a killing message |
| `sendKillerMessage(boolean)` | Whether or not to send the victim a killing message |
| `playSound(boolean)`         | Whether or not to play a sound for the killer |
| `clearDrops(boolean)`        | Whether or not to clear the victim's item drops |
| `clearExp(boolean)`          | Whether or not to clear the dropped experience |

##### `CorePlayerJumpEvent`:
> Called when a player jumps

A single jump, no matter how high or if incomplete, will trigger this event exactly once.

##### `CorePlayerPostLoadEvent`:
> Called as soon as `CorePlayer` finishes loading

This event is called after the `CorePlayer` instance has been created and set up
and the database statistics have been downloaded and initialized.

This event should be used for creating new unique player instances if
the class is a sub class of `AbstractCoreProfile`:

```java
@EventHandle(priority = EventPriority.HIGHEST)
public void onPostLoad(CorePlayerPostLoadEvent event) {
    CustomPlayer player = new CustomPlayer(event.getProfile());
    if (event.isNew()) {
        RiddlesCore.broadcast("&aWelcome, &6%s &ato the server!", player.getName());
    }
}
```

#### Ranks

| Rank     | ID      | Color      | Permissions |
| -------- |:-------:|:----------:| ----------- |
| Member   | `0`     | Gray       | Default access |
| Helper   | `4`     | Aqua       | Bypass chat limitations |
| Mod      | `5`     | Green      | Access to player management and restrictions, such as muting, banning, kicking, etc... |
| Dev      | `10`    | Dark Green | Access to non-public areas of plugins |
| Lead Dev | `10`    | Gold       | Access to non-public areas of plugins |
| Admin    | `99999` | Blue       | Access to backend server commands and utilities |

Obtainable via `AbstractCoreProfile#getRank()`

`CorePlayer` provides helpful methods to distinguish between ranks.
For example, to check if a player is a moderator, one can simply
call `player.isMod()`.

#### Premium

...

#### Violations

...

#### Chat Blocking Filters

...

### Database

...

### Regions

...

#### Vector3D

...

#### Flags

...

> **Note:** Flags only apply to registered regions

##### Default flags

| Flag              | Default | Associated event                     | Info                                                         |
| ----------------- |:-------:| ------------------------------------ | ------------------------------------------------------------ |
| BUILD             | `true`  | `BlockPlaceEvent.class`              | Natural placing of blocks                                               |
| BREAK             | `true`  | `BlockBreakEvent.class`              | Natural breaking of blocks                                              |
| CHAT              | `true`  | `AsyncPlayerChatEvent.class`         | Chatting                                                     |
| COMMAND           | `true`  | `PlayerCommandPreprocessEvent.class` | Dispatching commands                                         |
| PVP               | `true`  | `CorePlayerDamagePlayerEvent.class`  | Player vs Player damage                                      |
| PVE               | `true`  | `CorePlayerDamageEntityEvent.class`  | Player vs Environment (mobs) damage                          |
| ALL_DAMAGE        | `true`  | `EntityDamageEvent.class`            | Any sort of damage                                           |
| BLOCK_INTERACT    | `true`  | `PlayerInteractEvent.class`          | Right clicling on blocks                                     |
| ITEM_INTERACT     | `true`  | `PlayerInteractEvent.class`          | Right clicking on items in hand                              |
| ENTITY_INTERACT   | `true`  | `PlayerInteractAtEntityEvent.class`  | Right clicking on physical entities                          |
| ENDERMAN_INTERACT | `true`  | `EntityChangeBlockEvent.class`       | Endermen placing/breaking blocks                             |
| ITEM_SPAWN        | `true`  | `EntitySpawnEvent.class`             | Spawning of physical item entities                           |
| EXP_SPAWN         | `true`  | `EntitySpawnEvent.class`             | Spawning of experience orb entities                          |
| ANIMAL_SPAWN      | `true`  | `EntitySpawnEvent.class`             | Spawning of animals (passive)                                |
| MONSTER_SPAWN     | `true`  | `EntitySpawnEvent.class`             | Spawning of monsters (aggressive)                            |
| EXPLOSION         | `true`  | `ExplosionPrimeEvent.class`          | Explosions                                                   |
| HEALTH_REGEN      | `true`  | `EntityRegainHealthEvent.class`      | Natural regeneration of health (peaceful + satiated)         |
| HUNGER_LOSS       | `true`  | `FoodLevelChangeEvent.class`         | Changing food level                                          |
| POTION_SPLASH     | `true`  | `PotionSplashEvent.class`            | Any type of potion splashing                                 |
| BLOCK_BURN        | `true`  | `BlockBurnEvent.class`               | Block grief as a result of fire (Not the spreading of fire)  |
| VEHICLE_PLACE     | `true`  | `EntitySpawnEvent.class`             | Manual placing of vehicles                                   |
| VEHICLE_DESTROY   | `true`  | `VehicleDestroyEvent.class`          | Manual destroying of vehicles                                |
| SLEEP             | `true`  | `PlayerBedEnterEvent.class`          | Player entering bed                                          |
| BLOCK_FORM        | `true`  | `BlockFormEvent.class`               | Block forming based on world conditions (ie: snow, ice)      |
| BLOCK_FADE        | `true`  | `BlockFadeEvent.class`               | Block fading based on world conditions (ie: snow, ice, fire) |
| BLOCK_MOVE        | `true`  | `BlockFromToEvent.class`             | Liquids or dragon egg moving                                 |
| BLOCK_SPREAD      | `true`  | `BlockSpreadEvent.class`             | Spreading of blocks (ie: fire, mycel, grass, mushrooms)      |

##### Custom flags

...

#### RegionCriteria

...

#### Serialization

##### JSON

...

##### YAML

...

#### Types of default regions

| Type        | Class                     | Info |
| ----------- | ------------------------- | ---- |
| Cuboid      | `CuboidRegion.class`      | Standard quadrilateral region with min and max vectors |
| Spherical   | `SphericalRegion.class`   | Spherical region with a defined center a radius |
| Cylindrical | `CylindricalRegion.class` | Cylindrical region with a center point at the base and a defined radius and height extending from the base |
| Pyramidal   | `PyramidalRegion.class`   | A complex pyramind that may or may not be inverted with odd dimensions |
| Polygonal   | `null`                    | Any custom type of complex polygon |
| Custom      | `null`                    | Implemented for special cases |

#### Joining regions together

...

### Command API

...

### Configuration files

...

### Inventory GUI

...

### Boss bars

...

### Scoreboard

...

### Titles

The title API is based on a builder design with each `TitleMessage`
object resembling either an actual **title __or__ a subtitle**.
Each `TitleMessage` has its own animation timings attached to them
so the titles can appear in their own pace independently.

Here is a basic example of how to build a title and send it when a
player joins the server:

```java
public class TitleExample implements Listener {

    @EventHandler
    public void onPlayerJoin(CorePlayerPostLoadEvent event) {
        CorePlayer player = event.getProfile();
        Title title = new TitleBuilder()
                .withTitle(new TitleMessage(TitleMessage.Type.TITLE)
                        .withMessage("&8Welcome, " + player.getDisplayName())
                        .after(40)
                        .fadeInFor(40)
                        .stayFor(80)
                        .fadeOutFor(40)
                ).withTitle(new TitleMessage(TitleMessage.Type.SUBTITLE)
                        .withMessage("&7Enjoy your time on &6RiddlesVillage!")
                        .after(60)
                        .fadeInFor(20)
                        .stayFor(40)
                        .fadeOutFor(20)
                ).build();

        title.send(player);
    }
}
```

> Notice how the subtitle comes in `20` ticks after the title.

### Server Ping

...

### Holograms

...

### Guns

...

### Fanciful messages

...

### jnbt

...

### Packets

...

### Utilities

#### HTTP connections

...

#### Pasters

...

#### URL shorteners

...