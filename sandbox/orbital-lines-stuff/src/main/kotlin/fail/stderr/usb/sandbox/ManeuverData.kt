package fail.stderr.usb.sandbox

import org.hipparchus.geometry.euclidean.threed.Vector3D

data class ManeuverData(
  val vec: Vector3D,
  val isp: Double,
)