package fail.stderr.usb.data.system

import fail.stderr.usb.system.model.DefaultCelestialBodyHolder

@JvmRecord
data class RootCelestialBodyData(
  val name: String,
  val mu: Double,
  val children: Array<CelestialBodyData>,
)
