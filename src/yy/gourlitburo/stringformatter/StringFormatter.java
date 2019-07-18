package yy.gourlitburo.stringformatter;

import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

class MDTag {
  private final String tagSource;
  private final Pattern pattern;
  private final Character tagOpen;

  MDTag(String tagSource, Pattern pattern, Character tagOpen) {
    this.tagSource = tagSource; this.pattern = pattern; this.tagOpen = tagOpen;
  }

  public String tagSource() { return this.tagSource; }
  public Pattern pattern() { return this.pattern; }
  public Character tagOpen() { return this.tagOpen; }
}

public class StringFormatter {

  private static final String COLOR_PREFIX = "\u00A7";
  private static final char COLOR_PREFIX_CHAR = COLOR_PREFIX.charAt(0);

  private static final Pattern patternInterp = Pattern.compile("#\\{[A-Z]+\\}");
  private static final Pattern patternColor = Pattern.compile("&([0-9a-flmnor])");

  private static final List<MDTag> mdMap = List.of(
    new MDTag("__", Pattern.compile("(__)(.*?)\\1"), 'n'),
    new MDTag("**", Pattern.compile("(\\*\\*)(.*?)\\1"), 'l'),
    new MDTag("*", Pattern.compile("(\\*)(.*?)\\1"), 'o'),
    new MDTag("_", Pattern.compile("(_)(.*?)\\1"), 'o'),
    new MDTag("~~", Pattern.compile("(~~)(.*?)\\1"), 'm')
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
    for (MDTag tag : mdMap) {
      int keyLen = tag.tagSource().length();
      Matcher matcher = tag.pattern().matcher(message);
      StringBuilder builder = new StringBuilder();
      int prevEnd = 0;
      int offsetIncr = 0;
      while (matcher.find()) {
        String matched = matcher.group(0);
        String inner = matched.substring(keyLen, matched.length() - keyLen);
        builder.append(message.substring(prevEnd, matcher.start()));
        builder.append(COLOR_PREFIX + tag.tagOpen() + inner + COLOR_PREFIX + 'r');
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
