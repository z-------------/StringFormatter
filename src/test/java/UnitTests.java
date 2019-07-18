import java.util.Map;

import org.testng.Assert;
import org.testng.annotations.Test;

import yy.gourlitburo.stringformatter.StringFormatter;

public class UnitTests {
  private static final StringFormatter formatter = new StringFormatter();

  @Test
  public void colorize() {
    final String actual = formatter.colorize("&6Welcome to the server, #{DISPLAYNAME}.");
    final String expected = "§6Welcome to the server, #{DISPLAYNAME}.";
    Assert.assertEquals(actual, expected);
  }

  @Test
  public void interpolate() {
    Map<String, String> values = Map.of(
      "DISPLAYNAME", "lain1998"
    );
    final String actual = formatter.interpolate("&6Welcome to the server, #{DISPLAYNAME}.", values);
    final String expected = "&6Welcome to the server, lain1998.";
    Assert.assertEquals(actual, expected);
  } 

  @Test
  public void processMarkupNoMix() {
    final String actual = formatter.processMarkup("§6**Welcome to the server**, __#{DISPLAYNAME}__.");
    final String expected = "§6§lWelcome to the server§r§6, §n#{DISPLAYNAME}§r§6.";
    Assert.assertEquals(actual, expected);
  }

  @Test
  public void processMarkupMix() {
    final String actual = formatter.processMarkup("§6**Welcome to the server**, __#{DISPLAYNAME}§4__. I hope you have a §3**great§2** time!");
    final String expected = "§6§lWelcome to the server§r§6, §n#{DISPLAYNAME}§4§r§4. I hope you have a §3§lgreat§2§r§2 time!";
    Assert.assertEquals(actual, expected);
  }

  @Test
  public void format() {
    Map<String, String> values = Map.of(
      "FMTNAME", "&llain1998"
    );
    final String actual = formatter.format("&6Welcome to the server, #{FMTNAME}&r&6.", values);
    final String expected = "§6Welcome to the server, §llain1998§r§6.";
    Assert.assertEquals(actual, expected);
  }
}
