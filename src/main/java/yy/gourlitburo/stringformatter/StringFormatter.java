package yy.gourlitburo.stringformatter;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

class MDTag {
  public final String source;
  public final Pattern pattern;
  public final Character open;

  MDTag(String source, String regex, Character open) {
    this.source = source;
    this.pattern = Pattern.compile(regex);
    this.open = open;
  }
}

public class StringFormatter {

  private static final String COLOR_PREFIX = "\u00A7";
  private static final char COLOR_PREFIX_CHAR = COLOR_PREFIX.charAt(0);

  private static final Pattern patternInterp = Pattern.compile("#\\{[A-Z]+\\}");
  private static final Pattern patternColor = Pattern.compile("&([0-9a-fk-or])");

  private static final List<MDTag> mdMap = Arrays.asList(
    new MDTag("__", "(__)(.*?)\\1",     'n'),
    new MDTag("**", "(\\*\\*)(.*?)\\1", 'l'),
    new MDTag("*",  "(\\*)(.*?)\\1",    'o'),
    new MDTag("_",  "(_)(.*?)\\1",      'o'),
    new MDTag("~~", "(~~)(.*?)\\1",     'm')
  );

  private static char[] getActiveColors(String message) {
    char[] chars = message.toCharArray();
    char[] activeColors = new char[message.length()];
    char prevChar = ' ';
    for (int i = 0; i < message.length(); ++i) {
      Character c = chars[i];
      int d = Character.getNumericValue(c);
      if (prevChar == COLOR_PREFIX_CHAR && c == 'r') {
        activeColors[i] = ' ';
      } else if (prevChar == COLOR_PREFIX_CHAR && d >= 0 && d <= 9) {
        activeColors[i] = c;
      } else {
        activeColors[i] = i == 0 ? ' ' : activeColors[i - 1];
      }
      prevChar = c;
    }
    return activeColors;
  }

  /**
   * Convert Markdown **, *, _, __, and ~~ to corresponding Minecraft format codes.
   * @param   message   Text containing Markdown.
   * @return            Text with Markdown converted to corresponding Minecraft format codes.
   */
  public static String processMarkup(String message) {
    for (MDTag tag : mdMap) {
      int keyLen = tag.source.length();
      Matcher matcher = tag.pattern.matcher(message);
      char[] activeColors = getActiveColors(message);
      StringBuilder builder = new StringBuilder();
      int prevEnd = 0;
      while (matcher.find()) {
        String matched = matcher.group(0);
        String inner = matched.substring(keyLen, matched.length() - keyLen);
        builder.append(message.substring(prevEnd, matcher.start()));
        builder.append(COLOR_PREFIX + tag.open + inner + COLOR_PREFIX + 'r');
        char activeColor = activeColors[matcher.end() - keyLen - 1];
        if (activeColor != ' ') {
          builder.append(COLOR_PREFIX + activeColor);
        }
        prevEnd = matcher.end();
      }
      if (prevEnd != message.length()) {
        builder.append(message.substring(prevEnd));
      }
      message = builder.toString();
    }

    return message;
  }

  /**
   * Replace &-escaped format codes with their §-escaped equivalents for in-game display.
   * @param   message   Text containing &-escaped format codes.
   * @return            Text containing §-escaped format codes.
   */
  public static String colorize(String message) {
    return patternColor.matcher(message).replaceAll("\u00A7$1");
  }

  /**
   * Replace each Ruby-style placeholder with its corresponding String value in values.
   * @param   message   Text containing Ruby-style placeholders.
   * @param   values    Mapping of placeholders to values.
   * @return            Text containing interpolated values.
   */
  public static String interpolate(String message, Map<String, String> values) {
    Matcher matcher = patternInterp.matcher(message);
    StringBuilder builder = new StringBuilder();
    int prevEnd = 0;
    while (matcher.find()) {
      String matched = matcher.group(0);
      String key = matched.substring(2, matched.length() - 1);
      if (values.containsKey(key)) {
        builder.append(message.substring(prevEnd, matcher.start()));
        builder.append(values.get(key));
        prevEnd = matcher.end();
      }
    }
    if (prevEnd != message.length()) {
      builder.append(message.substring(prevEnd));
    }
    return builder.toString();
  }

  /**
   * colorize() the result of interpolate(message, values).
   * @param   message   Text containing Ruby-style placeholders and &-escaped format codes.
   * @param   values    Mapping of placeholders to values.
   * @return            Text containing interpolated values and §-escaped format codes.
   */
  public static String format(String message, Map<String, String> values) {
    return colorize(interpolate(message, values));
  }

}
