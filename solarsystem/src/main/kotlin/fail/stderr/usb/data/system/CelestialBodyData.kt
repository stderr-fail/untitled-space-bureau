package fail.stderr.usb.data.system

@JvmRecord
data class CelestialBodyData(
  val name: String,
  val mu: Double,
  val initialOrbitalParameters: KeplerianOrbitalParametersData,
  val children: Array<CelestialBodyData>? = null,
)
