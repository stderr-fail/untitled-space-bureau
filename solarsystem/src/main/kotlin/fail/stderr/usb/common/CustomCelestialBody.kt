package fail.stderr.usb.common

import org.hipparchus.CalculusFieldElement
import org.orekit.bodies.CelestialBody
import org.orekit.frames.Frame
import org.orekit.orbits.Orbit
import org.orekit.time.FieldAbsoluteDate
import org.orekit.utils.PVCoordinates
import org.orekit.utils.TimeStampedFieldPVCoordinates
import org.orekit.utils.TimeStampedPVCoordinates

@JvmRecord
data class CustomCelestialBody(
  val name: String,
  val mu: Double,
  val frame: Frame,
  val orbit: Orbit?,
) : CelestialBody {
  override fun getInertiallyOrientedFrame(): Frame {
    return frame
  }

  override fun getBodyOrientedFrame(): Frame {
    return frame
  }

  override fun getName(): String {
    return name
  }

  override fun getGM(): Double {
    return mu
  }

  override fun <T : CalculusFieldElement<T>> getPVCoordinates(
    date: FieldAbsoluteDate<T>,
    frame: Frame
  ): TimeStampedFieldPVCoordinates<T> =
    when (orbit) {
      null -> TimeStampedFieldPVCoordinates(
        date.field,
        TimeStampedPVCoordinates(date.toAbsoluteDate(), PVCoordinates.ZERO)
      )

      else -> {
        val pv = orbit.getPVCoordinates(date.toAbsoluteDate(), frame)
        TimeStampedFieldPVCoordinates(date.field, pv)
      }
    }

}
