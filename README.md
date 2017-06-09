# RiddlesCore v0.1
Core engine for RiddlesVillage

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

#### Ranks

#### Premium

### Database

### Command API

### Configuration files

### Inventory GUI

### Boss bars

### Scoreboard

### Titles

### Server Ping

### Holograms

### Utilities

#### HTTP connections

#### Pasters

#### URL shorteners

---

## Installation

Make sure to add the private repo:

```xml
<repository>
    <id>riddlesvillage</id>
    <url>https://repo.riddlesvillage.com/content/groups/private/</url>
</repository>
```

And then add RiddlesCore as a dependency in Maven:

```xml
<dependency>
    <groupId>com.riddlesvillage</groupId>
    <artifactId>core</artifactId>
    <version>latest</version>
    <scope>provided</scope>
</dependency>
```

... And in `plugin.yml`:

```
depend: [RiddlesCore]
```

---

## Links
[Website](https://riddlesvillage.com "RiddlesVillage")

[Git master](https://github.com/RiddlesVillage/core/tree/master "Master branch")

[Jenkins CI](https://riddlesvillage.com "Jenkins CI")

[Trello](https://trello.com/b/tEWzXRzj/riddles-village-zone)

[Admin panel](https://panel.riddlesvillage.com/auth/login)

---

## Contributors
- Maulss
- matt11matthew

---

## License
None

---

### TO DO
- OOP Command API for managing CommandSender and Permissible properly
- OOP Title API revolving around CorePlayer
- OOP Hologram API
- Item glow
- boss bar management
- tab list
- ping
- OOP Mongo API revolving around CorePlayer
- Testing