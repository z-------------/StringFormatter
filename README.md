# StringFormatter

A Minecraft-server-oriented class for string interpolation, color code conversion, and basic Markdown formatting.

## Methods

### `static String colorize(String message)`

Replace `&`-escaped format codes with their `§` equivalents for in-game display.

```java
StringFormatter.colorize("&6Welcome to the server, #{DISPLAYNAME}.");
// => "§6Welcome to the server, #{DISPLAYNAME}."
```

### `static String interpolate(String message, Map<String, String> values)`

Replace each Ruby-style placeholder with its corresponding String value in `values`.

```java
Map<String, String> values = Map.of(
  "DISPLAYNAME", "lain1998"
);
StringFormatter.interpolate("&6Welcome to the server, #{DISPLAYNAME}.", values);
// => "&6Welcome to the server, lain1998."
```

### `static String processMarkup(String message)`

Convert Markdown `**`, `*`, `_`, `__`, and `~~` to corresponding Minecraft format codes.

```java
StringFormatter.processMarkup("§6**Welcome to the server**, __#{DISPLAYNAME}__.");
// => "§6§lWelcome to the server§r§6, §n#{DISPLAYNAME}§r§6."
```

### `static String format(String message, Map<String, String> values)`

`colorize` the result of `interpolate(message, values)`.

```java
Map<String, String> values = Map.of(
  "FMTNAME", "&llain1998"
);
StringFormatter.format("&6Welcome to the server, #{FMTNAME}&r&6.", values);
// => "§6Welcome to the server, §llain1998§r§6."
```

## Used by

* [YeyaChat](https://github.com/gourlitburo/YeyaChat)
* [YeyaDeathMessage](https://github.com/gourlitburo/YeyaDeathMessage)
* [YeyaJoinMessage](https://github.com/gourlitburo/YeyaJoinMessage)

## License

MIT
