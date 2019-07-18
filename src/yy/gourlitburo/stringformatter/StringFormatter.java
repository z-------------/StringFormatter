package yy.gourlitburo.stringformatter;

import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringFormatter {

  private static final String COLOR_PREFIX = "\u00A7";
  private static final char COLOR_PREFIX_CHAR = COLOR_PREFIX.charAt(0);

  private static final Pattern patternInterp = Pattern.compile("#\\{[A-Z]+\\}");
  private static final Pattern patternColor = Pattern.compile("&([0-9a-flmnor])");

  // TODO: explore ways to combine these defs
  private static final List<String> mdOrder = List.of(
    "__", "**", "*", "_", "~~"
  );
  private static final Map<String, Pattern> mdMapP = Map.of(
    "__", Pattern.compile("(__)(.*?)\\1"),
    "**", Pattern.compile("(\\*\\*)(.*?)\\1"),
    "*", Pattern.compile("(\\*)(.*?)\\1"),
    "_", Pattern.compile("(_)(.*?)\\1"),
    "~~", Pattern.compile("(~~)(.*?)\\1")
  );
  private static final Map<String, Character> mdMapT = Map.of(
    "__", 'n',
    "**", 'l',
    "*", 'o',
    "_", 'o',
    "~~", 'm'
  );

  public String processMarkup(String message) {
    /* compute active colors at each index */
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

    /* process markdown + restore colors */
    int offset = 0;
    for (String key : mdOrder) {
      int keyLen = key.length();
      Matcher matcher = mdMapP.get(key).matcher(message);
      StringBuilder builder = new StringBuilder();
      int prevEnd = 0;
      int offsetIncr = 0;
      while (matcher.find()) {
        String matched = matcher.group(0);
        String inner = matched.substring(keyLen, matched.length() - keyLen);
        builder.append(message.substring(prevEnd, matcher.start()));
        builder.append(COLOR_PREFIX + mdMapT.get(key) + inner + COLOR_PREFIX + 'r');
        char activeColor = activeColors[matcher.end() - keyLen - offset - 1];
        offsetIncr += 2 * (2 - keyLen);
        if (activeColor != ' ') {
          builder.append(COLOR_PREFIX + activeColor);
          offsetIncr += 2;
        }
        prevEnd = matcher.end();
      }
      if (prevEnd != message.length()) {
        builder.append(message.substring(prevEnd));
      }
      message = builder.toString();
      offset += offsetIncr;
    }

    return message;
  }

  public String colorize(String message) {
    return patternColor.matcher(message).replaceAll("\u00A7$1");
  }

  public String interpolate(String message, Map<String, String> values) {
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

  public String format(String message, Map<String, String> values) {
    return colorize(interpolate(message, values));
  }

}
