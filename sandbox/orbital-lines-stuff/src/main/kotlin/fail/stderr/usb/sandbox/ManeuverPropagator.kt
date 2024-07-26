package fail.stderr.usb.sandbox

import fail.stderr.usb.extensions.times
import fail.stderr.usb.system.KeplerianSystem
import fail.stderr.usb.system.model.DefaultCelestialBodyHolder
import godot.global.GD
import org.hipparchus.geometry.euclidean.threed.Vector3D
import org.hipparchus.ode.events.Action
import org.orekit.attitudes.LofOffset
import org.orekit.bodies.BodyShape
import org.orekit.bodies.OneAxisEllipsoid
import org.orekit.forces.maneuvers.ImpulseManeuver
import org.orekit.frames.LOFType
import org.orekit.orbits.KeplerianOrbit
import org.orekit.propagation.SpacecraftState
import org.orekit.propagation.analytical.KeplerianPropagator
import org.orekit.propagation.events.AltitudeDetector
import org.orekit.propagation.events.DateDetector
import org.orekit.time.AbsoluteDate
import org.orekit.utils.Constants
import java.time.Duration
import java.util.concurrent.atomic.AtomicReference


class ManeuverPropagator(val system: KeplerianSystem) {

  val PROPAGATION_DURATION = Duration.ofDays(90L)

  fun propagate(maneuverData: ManeuverData, startDate: AbsoluteDate, satelliteName: String): List<Vector3D> {

    val duna = system.allCelestialBodies.get("Duna")!! as DefaultCelestialBodyHolder

    val satellite = system.allCelestialBodies[satelliteName]!! as DefaultCelestialBodyHolder
    val planet = satellite.parent as DefaultCelestialBodyHolder

    val satelliteStartPV = satellite.orbit.getPVCoordinates(startDate, planet.frame)
    val deltaV = satelliteStartPV.velocity.normalize() * maneuverData.vec.z

    val trigger = DateDetector(startDate.shiftedBy(.001))

    // create the maneuver, using ascending node detector as a trigger
    val impulseManeuver = ImpulseManeuver(trigger, deltaV, maneuverData.isp)

    val exitSOIStateRef = AtomicReference<SpacecraftState>()

    val soiExitDetector = altitudeDetector(planet, exitSOIStateRef, planet.soiRadius - planet.data.equatorialRadius)

    val attitudeProvider = LofOffset(system.refFrame, LOFType.LVLH)

    val startOrbit = satellite.orbit.shiftedBy(startDate.durationFrom(satellite.orbit))

    // initial propagator
    val firstPropagator = KeplerianPropagator(startOrbit, attitudeProvider)
    firstPropagator.addEventDetector(soiExitDetector)
    firstPropagator.addEventDetector(impulseManeuver)
//    currentPropagator.addEventDetector(buildSOIEnterDetector("Duna"))

    val positions = mutableListOf<Vector3D>()
    val distancesToDuna = mutableListOf<Double>()

    val stepSeconds = Duration.ofHours(6L).toSeconds().toDouble()
    firstPropagator.multiplexer.add(stepSeconds) { state: SpacecraftState ->

      val satellitePosition = state.getPVCoordinates(system.refFrame).position
      positions.add(satellitePosition)

      val dunaPosition = duna.orbit.getPVCoordinates(state.date, system.refFrame).position

      val distanceToDuna = dunaPosition.distance(satellitePosition)
      distancesToDuna.add(distanceToDuna)
    }

    // initial propagation
    firstPropagator.propagate(startDate.shiftedBy(PROPAGATION_DURATION.toSeconds().toDouble()))

    exitSOIStateRef.get()?.let { exitedSOIState ->

      // exit SOI point
      positions.add(exitedSOIState.pvCoordinates.position)

      val satelliteStarPV = exitedSOIState.orbit.getPVCoordinates(system.rootBody.frame)

      val starStartOrbit = KeplerianOrbit(satelliteStarPV, system.rootBody.frame, exitedSOIState.date, system.rootBody.mu)

      val starPropagator = KeplerianPropagator(starStartOrbit, attitudeProvider)
//      starPropagator.addEventDetector(soiExitDetector)
//      starPropagator.addEventDetector(impulseManeuver)
      starPropagator.addEventDetector(buildSOIEnterDetector("Duna"))

      starPropagator.multiplexer.add(stepSeconds) { state: SpacecraftState ->

        val satellitePosition = state.getPVCoordinates(system.refFrame).position
        positions.add(satellitePosition)

        val dunaPosition = duna.orbit.getPVCoordinates(state.date, system.refFrame).position

        val distanceToDuna = dunaPosition.distance(satellitePosition)
        distancesToDuna.add(distanceToDuna)
      }

      // remaining propagation time
      val currentPropagationSeconds = exitedSOIState.date.durationFrom(startDate)
      val remainingPropagationSeconds = PROPAGATION_DURATION.minusSeconds(currentPropagationSeconds.toLong()).toSeconds().toDouble()
      val propagateTo = exitedSOIState.date.shiftedBy(remainingPropagationSeconds)

      starPropagator.propagate(propagateTo)


    }


//    val propagator = KeplerianPropagator(startOrbit, attitudeProvider)
//
//    propagator.addEventDetector(soiExitDetector)
//    propagator.addEventDetector(impulseManeuver)
//    propagator.addEventDetector(buildSOIEnterDetector("Duna"))
//    propagator.addEventDetector(buildSOIEnterDetector("Planet C"))
//    propagator.addEventDetector(buildSOIEnterDetector("Planet D"))

//    val positions = mutableListOf<Vector3D>()
//
//    propagator.multiplexer.add(Duration.ofHours(6L).toSeconds().toDouble()) { state: SpacecraftState ->
////    propagator.multiplexer.add(120.0) { state: SpacecraftState ->
//      positions.add(state.pvCoordinates.position)
//    }
//
//    // 2Y
//    propagator.propagate(startDate.shiftedBy(90L, TimeUnit.DAYS))

    return positions

  }

  protected fun altitudeDetector(
    planet: DefaultCelestialBodyHolder,
    stopRef: AtomicReference<SpacecraftState>,
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
        stopRef.set(state)
        return@withHandler Action.STOP
      }
    return altitudeDetector
  }

  fun buildSOIEnterDetector(bodyName: String): AltitudeDetector {
    val planet = system.allCelestialBodies[bodyName]!! as DefaultCelestialBodyHolder

    val planetShape: BodyShape = OneAxisEllipsoid(
      planet.data.equatorialRadius,
      Constants.WGS84_EARTH_FLATTENING, // todo, use custom
      planet.frame
    )

    return object : AltitudeDetector(
      DEFAULT_MAXCHECK,
      DEFAULT_THRESHOLD,
      planet.soiRadius,
      planetShape,
    ) {
      override fun g(state: SpacecraftState): Double {

        val craftPosition = state.getPVCoordinates(system.refFrame).position
        val planetPosition = planet.orbit.getPVCoordinates(state.date, system.refFrame).position

        val distance = craftPosition.distance(planetPosition) - planet.soiRadius
        return distance

      }
    }
      .withMaxIter(AltitudeDetector.DEFAULT_MAX_ITER)
      .withHandler { state, detector, b ->

        val craftPosition = state.getPVCoordinates(system.refFrame).position
        val planetPosition = planet.orbit.getPVCoordinates(state.date, system.refFrame).position

        val distance = craftPosition.distance(planetPosition) - planet.soiRadius

        GD.print("!!! ENTERED SOI OF ${planet.name} AT ${state.date} with distance ${distance}")
        return@withHandler Action.CONTINUE
      }
  }


}