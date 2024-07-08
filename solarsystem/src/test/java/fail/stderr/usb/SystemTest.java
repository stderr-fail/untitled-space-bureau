package fail.stderr.usb;

import fail.stderr.usb.data.system.SystemData;
import fail.stderr.usb.system.SystemDataUtils;
import org.junit.jupiter.api.Test;

public class SystemTest {

  @Test
  public void test2() throws Exception {
    final SystemData systemData = SystemDataUtils.fromYAML(() -> Thread.currentThread().getContextClassLoader().getResourceAsStream("systems/dwarla/system.yaml"));



    System.out.println("here!");
  }

}
