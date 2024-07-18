package fail.stderr.usb.extensions

import godot.core.Vector3
import org.hipparchus.geometry.euclidean.threed.Vector3D

fun Vector3D.toGodotVectorString(): String {
  return "Vector3(${x}, ${y}, ${z})"
}

fun Vector3D.toGodotVectorStringFlipYZ(): String {
  return "Vector3(${x}, ${z}, ${y})"
}

fun Vector3D.toGodot(): Vector3 = Vector3(x, z, y) // flip z/y for godot

fun Vector3.toJVM(): Vector3D =
  Vector3D(x, z, y) // flip z/y back tp JVM vector space

operator fun Vector3D.plus(vec: Vector3D) = Vector3D(x + vec.x, y + vec.y, z + vec.z)
