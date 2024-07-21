package fail.stderr.usb.sandbox

import fail.stderr.usb.system.KeplerianSystem
import fail.stderr.usb.system.model.DefaultCelestialBodyHolder
import org.hipparchus.geometry.euclidean.threed.Vector3D
import org.hipparchus.ode.events.Action
import org.orekit.attitudes.LofOffset
import org.orekit.bodies.BodyShape
import org.orekit.bodies.OneAxisEllipsoid
import org.orekit.forces.maneuvers.ImpulseManeuver
import org.orekit.frames.LOFType
import org.orekit.propagation.SpacecraftState
import org.orekit.propagation.analytical.KeplerianPropagator
import org.orekit.propagation.events.AltitudeDetector
import org.orekit.propagation.events.DateDetector
import org.orekit.time.AbsoluteDate
import org.orekit.utils.Constants
import java.time.Duration
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicReference

class ManeuverPropagator(val system: KeplerianSystem) {

  fun propagate(maneuverData: ManeuverData, startDate: AbsoluteDate, satelliteName: String): List<Vector3D> {

    val satellite = system.allCelestialBodies[satelliteName]!! as DefaultCelestialBodyHolder
    val planet = satellite.parent as DefaultCelestialBodyHolder

    val satelliteStartPV = satellite.orbit.getPVCoordinates(startDate, planet.frame)
    val deltaV = satelliteStartPV.velocity.normalize().scalarMultiply(maneuverData.vec.z)

    val trigger = DateDetector(startDate.shiftedBy(.001))

    // create the maneuver, using ascending node detector as a trigger
    val impulseManeuver = ImpulseManeuver(trigger, deltaV, maneuverData.isp)

    val exitSOIStateRef = AtomicReference<SpacecraftState>()

    val soiExitDetector = altitudeDetector(planet, exitSOIStateRef, planet.soiRadius - planet.data.equatorialRadius)

    val attitudeProvider = LofOffset(system.refFrame, LOFType.LVLH)


    val startOrbit = satellite.orbit.shiftedBy(startDate.durationFrom(satellite.orbit))

    val propagator = KeplerianPropagator(startOrbit, attitudeProvider)

    propagator.addEventDetector(soiExitDetector)
    propagator.addEventDetector(impulseManeuver)

    val positions = mutableListOf<Vector3D>()

    propagator.multiplexer.add(Duration.ofMinutes(30L).toSeconds().toDouble()) { state: SpacecraftState ->
//    propagator.multiplexer.add(120.0) { state: SpacecraftState ->
      positions.add(state.pvCoordinates.position)
    }

    propagator.propagate(startDate.shiftedBy(96L, TimeUnit.HOURS))

    return positions

  }

  protected fun altitudeDetector(
    planet: DefaultCelestialBodyHolder,
    soiExitStateRef: AtomicReference<SpacecraftState>,
    altitude: Double,
  ): AltitudeDetector {

    val planetShape: BodyShape = OneAxisEllipsoid(
      planet.data.equatorialRadius,
      Constants.WGS84_EARTH_FLATTENING, // todo, use custom
      planet.frame
    )

    val altitudeDetector = AltitudeDetector(
      AltitudeDetector.DEFAULT_MAXCHECK,
      AltitudeDetector.DEFAULT_THRESHOLD,
      altitude,
      planetShape,
    )
      .withMaxIter(AltitudeDetector.DEFAULT_MAX_ITER)
      .withHandler { state, detector, b ->

//        val norm = state.pvCoordinates.position.norm
//        val altitude = norm - Constants.WGS84_EARTH_EQUATORIAL_RADIUS

//        System.out.printf(
//          "${state.date} :: !!!ALTITUDE!!! norm=%5.2f alt=%5.1f e=%5.3f %n",
//          norm,
//          altitude,
//          state.orbit.e
//        )

        soiExitStateRef.set(state)
        return@withHandler Action.STOP
      }
    return altitudeDetector
  }


}