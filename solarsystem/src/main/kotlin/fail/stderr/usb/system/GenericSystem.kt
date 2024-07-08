package fail.stderr.usb.system

import fail.stderr.usb.system.model.CelestialBodyHolder
import fail.stderr.usb.system.model.RootCelestialBodyHolder
import org.orekit.frames.Frame
import org.orekit.time.AbsoluteDate
import org.orekit.time.TimeScale

class GenericSystem(
  val name: String,
  val rootBody: RootCelestialBodyHolder,
  val refFrame: Frame,
  val refDate: AbsoluteDate,
  val timeScale: TimeScale,
  /** does not contain the root body */
  val nonRootCelestialBodies: List<CelestialBodyHolder>,
  val allCelestialBodies: Map<String, CelestialBodyHolder>,
) {

}

