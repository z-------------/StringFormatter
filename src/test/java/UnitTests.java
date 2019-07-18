import org.testng.Assert;
import org.testng.annotations.Test;

import yy.gourlitburo.stringformatter.StringFormatter;

public class UnitTests {
  private static final StringFormatter formatter = new StringFormatter();

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
}
