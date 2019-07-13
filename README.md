# StringFormatter

A class that provides `interpolate` and `colorize` methods, intended for use with Spigot.

## Methods

### `String colorize(String message)`

Replace `&`-escaped color codes with their `§` equivalents, which will be properly formatted in-game.

```java
formatter.colorize("&6Welcome to the server, #{DISPLAYNAME}.");
// => "§6Welcome to the server, #{DISPLAYNAME}."
```

### `String interpolate(String message, Map<String, String> values)`

Replace each Ruby-style placeholder with its corresponding String value in the Map.

```java
Map<String, String> values = Map.of(
  "DISPLAYNAME", "lain1998"
);
formatter.interpolate("&6Welcome to the server, #{DISPLAYNAME}.", values);
// => "&6Welcome to the server, lain1998."
```

### `String format(String message, Map<String, String> values)`

`colorize` the result of `interpolate(message, values)`.

```java
Map<String, String> values = Map.of(
  "FMTNAME", "&llain1998"
);
formatter.format("&6Welcome to the server, #{FMTNAME}&r&6.", values);
// => "§6Welcome to the server, §llain1998§r§6."
```

## Building

Ant.

## License

MIT
