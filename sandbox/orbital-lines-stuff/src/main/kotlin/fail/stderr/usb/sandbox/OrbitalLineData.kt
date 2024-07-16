package fail.stderr.usb.sandbox

import godot.annotation.RegisterClass
import godot.annotation.RegisterFunction
import godot.global.GD

@RegisterClass
class OrbitalLineData : godot.Object() {

  @RegisterFunction
  fun thing() {
    GD.print("thing!")
  }

}