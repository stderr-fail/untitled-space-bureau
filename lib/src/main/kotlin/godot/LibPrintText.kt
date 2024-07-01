package godot

import godot.annotation.Export
import godot.annotation.RegisterClass
import godot.annotation.RegisterFunction
import godot.annotation.RegisterProperty

@RegisterClass
class LibPrintText : Label() {

	@Export
	@RegisterProperty
	var test: Int = 0

	var count: Long = 0L

	@RegisterFunction
	override fun _ready() {
		text = "Hi! From Kotlin Lib!!!"
	}

	@RegisterFunction
	override fun _process(delta: Double) {
		text = "count lib: ${count++}"

	}
}
