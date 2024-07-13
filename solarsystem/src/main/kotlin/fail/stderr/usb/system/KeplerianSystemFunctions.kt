package fail.stderr.usb.system

import fail.stderr.usb.common.CustomCelestialBody
import fail.stderr.usb.common.FrameFunctions
import fail.stderr.usb.common.OrbitFunctions
import fail.stderr.usb.data.system.CelestialBodyData
import fail.stderr.usb.data.system.SystemData
import fail.stderr.usb.system.model.CelestialBodyHolder
import fail.stderr.usb.system.model.DefaultCelestialBodyHolder
import fail.stderr.usb.system.model.RootCelestialBodyHolder
import fail.stderr.usb.time.CustomTimeScale
import org.orekit.time.AbsoluteDate

fun createGenericSystem(name: String, data: SystemData): KeplerianSystem {

  val timeScale = CustomTimeScale("${name}Time", 0.0)

  val refDate = AbsoluteDate(1983, 7, 14, timeScale)

  val rootBodyFrame = FrameFunctions.createRootFrame(data.rootBody.name)

  val rootBodyHolder = RootCelestialBodyHolder(
    data = data.rootBody,
    frame = rootBodyFrame,
    children = emptyList(),
  )

  rootBodyHolder.children = createBodies(refDate, rootBodyHolder, data.rootBody.children)

  val nonRootCelestialBodies = enumerateCelestialBodiesFromTree(rootBodyHolder)

  val allCelestialBodies = nonRootCelestialBodies
    .associateBy(CelestialBodyHolder::name)
    .toMutableMap<String, CelestialBodyHolder>()

  allCelestialBodies.put(rootBodyHolder.name, rootBodyHolder)

  return KeplerianSystem(
    name = name,
    rootBody = rootBodyHolder,
    refFrame = rootBodyFrame,
    refDate = refDate,
    timeScale = timeScale,
    nonRootCelestialBodies = nonRootCelestialBodies,
    allCelestialBodies = allCelestialBodies,
  )
}

private fun enumerateCelestialBodiesFromTree(
  body: CelestialBodyHolder,
  collector: MutableList<DefaultCelestialBodyHolder> = mutableListOf(),
): List<DefaultCelestialBodyHolder> {

  collector.addAll(body.children)

  body.children.forEach { enumerateCelestialBodiesFromTree(it, collector) }

  return collector
}

private fun createBodies(
  refDate: AbsoluteDate,
  parent: CelestialBodyHolder,
  bodies: Array<CelestialBodyData>
): List<DefaultCelestialBodyHolder> {
  return bodies.map { createBody(refDate, parent, it) }
}

private fun createBody(
  refDate: AbsoluteDate,
  parent: CelestialBodyHolder,
  bodyData: CelestialBodyData
): DefaultCelestialBodyHolder {

  val bodyOrbit = OrbitFunctions.createKeplerianOrbit(
    params = bodyData.initialOrbitalParameters,
    parentFrame = parent.frame,
    refDate = refDate,
    parentMU = parent.mu,
  )

  val bodyFrame = FrameFunctions.createBodyFrame(bodyData.name, bodyOrbit, parent.frame)

  val body = CustomCelestialBody(bodyData.name, bodyData.mu, bodyFrame, bodyOrbit)

  val genericBody = DefaultCelestialBodyHolder(
    body = body,
    data = bodyData,
    frame = bodyFrame,
    orbit = bodyOrbit,
    parent = parent,
    children = emptyList(),
  )

  bodyData.children?.let {
    genericBody.children = createBodies(refDate, genericBody, bodyData.children)
  }

  return genericBody
}
