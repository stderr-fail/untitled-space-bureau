package fail.stderr.usb;

import fail.stderr.usb.json.SystemDataUtils;
import fail.stderr.usb.model.data.DataDeserializationError;
import fail.stderr.usb.model.result.Result;
import fail.stderr.usb.data.system.SystemData;
import org.junit.jupiter.api.Test;

public class JsonTest {

  @Test
  public void test2() throws Exception {
    final Result<SystemData, DataDeserializationError> result = SystemDataUtils.fromYAML(() -> Thread.currentThread().getContextClassLoader().getResourceAsStream("systems/dwarla/system.yaml"));

    System.out.println("here!");
  }

}
