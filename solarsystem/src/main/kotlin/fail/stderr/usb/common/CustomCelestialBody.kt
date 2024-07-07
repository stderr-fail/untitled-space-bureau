package fail.stderr.usb.common

import org.hipparchus.CalculusFieldElement
import org.orekit.bodies.CelestialBody
import org.orekit.frames.Frame
import org.orekit.orbits.Orbit
import org.orekit.time.FieldAbsoluteDate
import org.orekit.utils.PVCoordinates
import org.orekit.utils.TimeStampedFieldPVCoordinates
import org.orekit.utils.TimeStampedPVCoordinates

class CustomCelestialBody(
  val bodyName: String,
  val mu: Double,
  val frame: Frame,
  val orbit: Orbit?,
) : CelestialBody {

  override fun <T : CalculusFieldElement<T>?> getPVCoordinates(
    date: FieldAbsoluteDate<T>, frame: Frame
  ): TimeStampedFieldPVCoordinates<T> {
    orbit?.let {
      val pv = orbit.getPVCoordinates(date.toAbsoluteDate(), frame)
      return TimeStampedFieldPVCoordinates(date.field, pv)
    }
    return TimeStampedFieldPVCoordinates(
      date.field,
      TimeStampedPVCoordinates(date.toAbsoluteDate(), PVCoordinates.ZERO)
    )
  }

  override fun getInertiallyOrientedFrame(): Frame {
    return frame
  }

  override fun getBodyOrientedFrame(): Frame {
    return frame
  }

  override fun getName(): String {
    return bodyName
  }

  override fun getGM(): Double {
    return mu
  }
}
