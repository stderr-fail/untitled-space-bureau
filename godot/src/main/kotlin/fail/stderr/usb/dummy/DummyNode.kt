package fail.stderr.usb.dummy

import godot.Node
import godot.annotation.RegisterClass

/**
 * Need at least a single class in the "godot" module so that the gradle build
 * properly shadows everything
 */
@RegisterClass
class DummyNode : Node() {

}
