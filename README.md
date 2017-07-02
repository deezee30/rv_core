# RiddlesCore v0.1
Core engine for RiddlesVillage Spigot server

---

## Installation

Make sure to add the private repo:

```xml
<repository>
    <id>riddlesvillage</id>
    <url>https://nrm.riddlesvillage.com/content/groups/private/</url>
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

## Contributing

### Coding Guidelines

...

---

## Contributors
- Maulss
- matt11matthew

---

## License

...

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