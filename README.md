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
provided MongoCollection<Document>` automatically before the player
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
should be called. The player's statistics will downloaded from whatever
`MongoCollection<Document>` is provided from the sub class's `#getCollection()`
via the primary key, AKA the player's `UUID`. Every player-related collection must
contain a `UUID` field to be used as a primary key. If the sub class does not have
an associated collection in the database, `null` can be returned and a further
statistic lookup won't be performed.

Once the lookup is made, `#loadStats(Document stats)` will be called, where the
argument contains all fields found in the player's collection. Those can be
extracted and set as local variables.

> Note that there must only be at most online **and** offline player instances per player.

> There must only be either an online or an offline player instance cached at once but not both.

> When an instance isn't needed anymore (ie the player leaves), the instance needs to be destroyed.

#### Economy

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