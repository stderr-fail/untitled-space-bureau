package fail.stderr.usb.system

import fail.stderr.usb.QuickTest
import fail.stderr.usb.extensions.toGodotVectorStringFlipYZ
import fail.stderr.usb.godot.FakeGodotCamera
import fail.stderr.usb.system.model.DefaultCelestialBodyHolder
import godot.core.*
import org.junit.jupiter.api.Test
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.util.concurrent.TimeUnit


class OrbitCoordinatesTesselationTest {

  var log: Logger = LoggerFactory.getLogger(QuickTest::class.java)

  @Test
  fun fakeCamera() {
    val camera = FakeGodotCamera(
      cameraProjection = Projection(
        Vector4(1.30322527885437, 0, 0, 0),
        Vector4(0, 1.30322527885437, 0, 0),
        Vector4(0, 0, -1.00002503395081, -1),
        Vector4(0, 0, -0.10000125318766, 0),
      ),
      cameraTransform = Transform3D(
        Basis(
          Vector3(1, 0, -0),
          Vector3(-0, 0.991445, -0.130526),
          Vector3(0, 0.130526, 0.991445),
        ),
        Vector3(0, 3, 20),
      ),
      globalTransform = Transform3D(
        Basis(
          Vector3(1, 0, -0),
          Vector3(-0, 0.991445, -0.130526),
          Vector3(0, 0.130526, 0.991445),
        ),
        Vector3(0, 3, 20),
      ),
      viewportSize = Vector2i(1200, 1200),
    )

    val system = buildSystem()

    val body = system.allCelestialBodies.get("Dwar") as DefaultCelestialBodyHolder
    val frame = system.refFrame

    val segmentCount = 128
    val startDate = system.refDate

    val segmentTimeIncrement = Math.floor(body.orbit.keplerianPeriod / segmentCount).toLong() - 1L

    val vec2s = mutableListOf<String>()
    val vec3s = mutableListOf<String>()

    for (i in 0..segmentCount) {
      val date = startDate.shiftedBy(segmentTimeIncrement * i, TimeUnit.SECONDS)
      val pv = body.orbit.getPVCoordinates(date, frame)

//      10_000_000_000

      var v3 = Vector3(pv.position.x, pv.position.z, pv.position.y)

      var v3scaled = v3.div(10_000_000_000)

      val pos2d = camera.unprojectPosition(v3scaled)

      vec2s.add("Vector2(${pos2d.x}, ${pos2d.y})")
      vec3s.add(pv.position.toGodotVectorStringFlipYZ())
    }

    println(vec2s.joinToString(",\n"))
//    println(vec3s.joinToString(",\n"))
  }


  fun buildSystem(): KeplerianSystem {
    val result = SystemDataUtils.fromYAML {
      Thread.currentThread().contextClassLoader.getResourceAsStream("systems/dwarla/system.yaml")!!
    }

    val system = createGenericSystem("Dwarlis", result)
    return system
  }
}