package fail.stderr.usb.system.model

import fail.stderr.usb.data.system.CelestialBodyData
import fail.stderr.usb.data.system.RootCelestialBodyData
import org.orekit.bodies.CelestialBody
import org.orekit.frames.Frame
import org.orekit.orbits.KeplerianOrbit

sealed class CelestialBodyHolder(
  open val frame: Frame,
  open var children: List<CelestialBodyHolder>,
  open var name: String,
  val mu: Double,
)

data class DefaultCelestialHolder(
  val data: CelestialBodyData,
  val orbit: KeplerianOrbit,
  override val frame: Frame,
  val body: CelestialBody,
  val parent: CelestialBodyHolder,
  override var children: List<CelestialBodyHolder>,
) : CelestialBodyHolder(frame = frame, mu = data.mu, children = children, name = data.name) {
  override fun toString(): String {
    return "DefaultCelestialHolder(name=$name, data=$data, frame=$frame, orbit=$orbit, body=$body, parent=$parent)"
  }
}

data class RootCelestialBodyHolder(
  val data: RootCelestialBodyData,
  override val frame: Frame,
  override var children: List<CelestialBodyHolder>,
) : CelestialBodyHolder(frame = frame, mu = data.mu, children = children, name = data.name) {
  override fun toString(): String {
    return "RootCelestialBodyHolder(name=$name, data=$data, frame=$frame)"
  }
}
