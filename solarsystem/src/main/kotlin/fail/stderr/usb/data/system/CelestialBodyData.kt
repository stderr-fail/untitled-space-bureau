package fail.stderr.usb.data.system

import fail.stderr.usb.system.model.DefaultCelestialBodyHolder

@JvmRecord
data class CelestialBodyData(
  val name: String,
  val mu: Double,
  val initialOrbitalParameters: KeplerianOrbitalParametersData,
  val children: Array<CelestialBodyData>? = null,
)
