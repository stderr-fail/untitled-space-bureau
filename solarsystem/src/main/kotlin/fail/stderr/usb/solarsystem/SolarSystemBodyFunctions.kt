package fail.stderr.usb.solarsystem

import org.hipparchus.ode.nonstiff.DormandPrince853Integrator
import org.orekit.bodies.CelestialBodyFactory
import org.orekit.forces.gravity.NewtonianAttraction
import org.orekit.forces.gravity.ThirdBodyAttraction
import org.orekit.frames.FramesFactory
import org.orekit.orbits.KeplerianOrbit
import org.orekit.orbits.Orbit
import org.orekit.orbits.OrbitType
import org.orekit.orbits.PositionAngleType
import org.orekit.propagation.SpacecraftState
import org.orekit.propagation.numerical.NumericalPropagator
import org.orekit.time.AbsoluteDate

fun buildEarthInitialOrbit(startDate: AbsoluteDate, mu: Double): Orbit {

  val inertialFrame = FramesFactory.getICRF()
  val semiMajorAxis = 1.496e11 // Approximate semi-major axis of Earth's orbit around the Sun in meters
  val eccentricity = 0.0167 // Earth's orbital eccentricity
  val inclination = Math.toRadians(0.0) // Inclination (for simplicity, assuming 0 for Earth's orbit)
  val perigeeArgument = Math.toRadians(102.9372) // Argument of perihelion in degrees
  val raan = Math.toRadians(0.0) // Right ascension of ascending node (assuming 0 for simplicity)
  val meanAnomaly = Math.toRadians(0.0) // Mean anomaly at epoch

  val orbit: Orbit = KeplerianOrbit(
    semiMajorAxis,
    eccentricity,
    inclination,
    perigeeArgument,
    raan,
    meanAnomaly,
    PositionAngleType.MEAN,
    inertialFrame,
    startDate,
    mu
  )

  return orbit
}

fun buildMoonInitialOrbit(startDate: AbsoluteDate, mu: Double): Orbit {

  val inertialFrame = FramesFactory.getGCRF()
  val semiMajorAxisMoon = 384.4e6 // Semi-major axis in meters
  val eccentricityMoon = 0.0549 // Orbital eccentricity
  val inclinationMoon = Math.toRadians(5.145) // Inclination in radians
  val perigeeArgumentMoon = Math.toRadians(318.15) // Argument of perigee in radians
  val raanMoon = Math.toRadians(125.08) // Right ascension of ascending node in radians
  val meanAnomalyMoon = Math.toRadians(115.3654) // Mean anomaly at epoch in radians

  val orbit: Orbit = KeplerianOrbit(
    semiMajorAxisMoon,
    eccentricityMoon,
    inclinationMoon,
    perigeeArgumentMoon,
    raanMoon,
    meanAnomalyMoon,
    PositionAngleType.MEAN,
    inertialFrame,
    startDate,
    mu
  )

  return orbit
}

fun buildMercuryInitialOrbit(startDate: AbsoluteDate, mu: Double): Orbit {

  val inertialFrame = FramesFactory.getGCRF()
  val semiMajorAxisMercury = 57.91e9 // Semi-major axis in meters
  val eccentricityMercury = 0.2056 // Orbital eccentricity
  val inclinationMercury = Math.toRadians(7.0) // Inclination in radians
  val perigeeArgumentMercury = Math.toRadians(29.124) // Argument of perihelion in radians
  val raanMercury = Math.toRadians(48.331) // Right ascension of ascending node in radians
  val meanAnomalyMercury = Math.toRadians(174.796) // Mean anomaly at epoch in radians

  val orbit: Orbit = KeplerianOrbit(
    semiMajorAxisMercury,
    eccentricityMercury,
    inclinationMercury,
    perigeeArgumentMercury,
    raanMercury,
    meanAnomalyMercury,
    PositionAngleType.MEAN,
    inertialFrame,
    startDate,
    mu
  )

  return orbit
}

data class PropagationSettings(
  val positionTolerance: Double,
  val minStep: Double,
  val maxStep: Double,
  val propagationType: OrbitType,
)

fun buildEarthPropagator(startDate: AbsoluteDate, settings: PropagationSettings): NumericalPropagator {

  val sun = CelestialBodyFactory.getSun()
  val earth = CelestialBodyFactory.getEarth()

  val initialOrbit = buildEarthInitialOrbit(startDate, sun.gm)

  val propagator = buildBaseNumericalPropagator(settings, initialOrbit)

  propagator.addForceModel(NewtonianAttraction(sun.gm))
  propagator.addForceModel(ThirdBodyAttraction(earth))

  return propagator
}

fun buildMoonPropagator(startDate: AbsoluteDate, settings: PropagationSettings): NumericalPropagator {

  val earth = CelestialBodyFactory.getEarth()
  val moon = CelestialBodyFactory.getMoon()

  val initialOrbit = buildMoonInitialOrbit(startDate, earth.gm)

  val propagator = buildBaseNumericalPropagator(settings, initialOrbit)

  propagator.addForceModel(NewtonianAttraction(earth.gm))
  propagator.addForceModel(ThirdBodyAttraction(moon))

  return propagator
}
fun buildMercuryPropagator(startDate: AbsoluteDate, settings: PropagationSettings): NumericalPropagator {

  val sun = CelestialBodyFactory.getSun()
  val mercury = CelestialBodyFactory.getMercury()

  val initialOrbit = buildMercuryInitialOrbit(startDate, sun.gm)

  val propagator = buildBaseNumericalPropagator(settings, initialOrbit)

  propagator.addForceModel(NewtonianAttraction(sun.gm))
  propagator.addForceModel(ThirdBodyAttraction(mercury))

  return propagator
}

fun buildBaseNumericalPropagator(settings: PropagationSettings, initialOrbit: Orbit): NumericalPropagator {

  val tolerances = NumericalPropagator.tolerances(settings.positionTolerance, initialOrbit, settings.propagationType)
  val integrator = DormandPrince853Integrator(settings.minStep, settings.maxStep, tolerances[0], tolerances[1])

  val propagator = NumericalPropagator(integrator)

  val initialState = SpacecraftState(initialOrbit)
  propagator.initialState = initialState

  // Disable central attraction to use specific force models
  propagator.setIgnoreCentralAttraction(true);

  return propagator
}