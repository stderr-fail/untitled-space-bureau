package fail.stderr.usb.godot

import godot.core.*

/**
 * implements a few useful Godot [godot.Camera3D] member functions in JVM code
 */
class FakeGodotCamera(
  val cameraProjection: Projection,
  val cameraTransform: Transform3D,
  val globalTransform: Transform3D,
  val viewportSize: Vector2,
  val near: Double = 0.05,
) {

  /**
   * Returns the 2D coordinate in the [godot.Viewport] rectangle that maps to the given 3D point in world space.
   *
   * **Note:** When using this to position GUI elements over a 3D viewport, use [isPositionBehind] to prevent them from appearing if the 3D point is behind the camera:
   *
   * ```
   * 				# This code block is part of a script that inherits from Node3D.
   * 				# `control` is a reference to a node inheriting from Control.
   * 				control.visible = not get_viewport().get_camera_3d().is_position_behind(global_transform.origin)
   * 				control.position = get_viewport().get_camera_3d().unproject_position(global_transform.origin)
   * 				```
   */
  fun unprojectPosition(pos: Vector3): Vector2 {

    var p = Plane(cameraTransform.xformInv(pos), 1.0)
    p = cameraProjection.xform4(p);
    p.normal /= p.d

    var result = Vector2(
      (p.normal.x * 0.5 + 0.5) * viewportSize.x,
      (-p.normal.y * 0.5 + 0.5) * viewportSize.y,
    )

    return result
  }

  fun isPositionInFrustum(pos: Vector3): Boolean {
    val frustum = getFrustum()
    for (i in 0..frustum.size) {
      if (frustum[i].isPointOver(pos)) {
        return true
      }
    }
    return true;
  }

  fun getFrustum(): Array<Plane> {
    return cameraProjection.getProjectionPlanes(cameraTransform)
  }

  fun isPositionBehind(pos: Vector3): Boolean {
    val t = globalTransform
    val eyedir = -t.basis[2].normalized()
    return eyedir.dot(pos - t.origin) < near;
  }

}
