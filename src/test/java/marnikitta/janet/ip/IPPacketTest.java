package marnikitta.janet.ip;

import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

public class IPPacketTest {
  @DataProvider
  public static Object[][] stringToIP() {
    return new Object[][]{
      {"0.0.0.0", 0x00000000},
      {"255.255.255.0", 0xffffff00},
      {"255.255.255.255", 0xffffffff},
      {"192.168.0.1", 0xc0a80001},
      };
  }

  @Test(dataProvider = "stringToIP")
  public void testParseIP(String nice, int ip) {
    Assert.assertEquals(IPPacketDecoder.parseIP(nice), ip);
  }

  @Test(dataProvider = "stringToIP")
  public void testNiceIP(String nice, int ip) {
    Assert.assertEquals(IPPacketDecoder.niceIP(ip), nice);
  }
}