package fail.stderr.usb.data.system

@JvmRecord
data class CelestialBodyData(
  val name: String,
  val mu: Double,
  val equatorialRadius: Double,
  val type: CelestialBodyType,
  val initialOrbitalParameters: KeplerianOrbitalParametersData,
  val children: Array<CelestialBodyData>? = null,
)
