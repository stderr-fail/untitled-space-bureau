package fail.stderr.usb.extensions

import org.hipparchus.geometry.euclidean.threed.Vector3D

fun Vector3D.toGodotVectorString(): String {
  return "Vector3(${x}, ${y}, ${z})"
}

fun Vector3D.toGodotVectorStringFlipYZ(): String {
  return "Vector3(${x}, ${z}, ${y})"
}
