package fail.stderr.usb.system

import org.junit.jupiter.api.Test

class SystemTest {

  @Test
  fun test() {

    val result = SystemDataUtils.fromYAML {
      Thread.currentThread().contextClassLoader.getResourceAsStream("systems/dwarla/system.yaml")
    }

    val system = createGenericSystem("Dwarlis", result)
    println("here!")


  }
}