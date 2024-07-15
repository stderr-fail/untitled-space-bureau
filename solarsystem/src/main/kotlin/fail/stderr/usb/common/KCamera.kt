package fail.stderr.usb.common

import org.joml.*

class KCamera(
  /**
   * Comes from Godot Camera3D.get_camera_projection()
   */
  val projectionMatrix: Matrix4f,
  /**
   * Comes from Godot Camera3D.get_camera_transform()
   */
  val cameraTransform: Matrix4x3f,
  val viewportW: Float,
  val viewportH: Float
) {

  fun unprojectPosition(worldPos: Vector3f): Vector2f {

    var p = Plane(
      normal = xform_inv(cameraTransform, worldPos),
      d = 1.0f,
    )

    p = xform4(projectionMatrix, p)
    p.normal /= p.d;

    val res = Vector2f(
      (p.normal.x * 0.5f + 0.5f) * viewportW,
      (-p.normal.y * 0.5f + 0.5f) * viewportH
    )

    return res
  }
}

class Plane(
  val normal: Vector3f,
  /** distance */
  val d: Float,
)

//Vector3 v = p_vector - origin;
//
//return Vector3(
//(basis.rows[0][0] * v.x) + (basis.rows[1][0] * v.y) + (basis.rows[2][0] * v.z),
//(basis.rows[0][1] * v.x) + (basis.rows[1][1] * v.y) + (basis.rows[2][1] * v.z),
//(basis.rows[0][2] * v.x) + (basis.rows[1][2] * v.y) + (basis.rows[2][2] * v.z));


fun xform_inv(matrix: Matrix4x3f, p_vector: Vector3f): Vector3f {
  val origin = Vector3f(matrix.m30(), matrix.m31(), matrix.m32())
  val v = p_vector - origin

  matrix.m00()

  return Vector3f(
    // depending on how Camera3D.get_camera_transform are arranged might need to lookup values vertically or horizontally

//    (matrix.m00() * v.x) + (matrix.m10() * v.y) + (matrix.m20() * v.z),
//    (matrix.m01() * v.x) + (matrix.m11() * v.y) + (matrix.m21() * v.z),
//    (matrix.m02() * v.x) + (matrix.m12() * v.y) + (matrix.m22() * v.z),

    (matrix.m00() * v.x) + (matrix.m01() * v.y) + (matrix.m02() * v.z),
    (matrix.m10() * v.x) + (matrix.m11() * v.y) + (matrix.m12() * v.z),
    (matrix.m20() * v.x) + (matrix.m21() * v.y) + (matrix.m22() * v.z),
  )

}

fun xform4(input: Matrix4f, inputPlane: Plane): Plane {

  val x = input.m00() * inputPlane.normal.x + input.m10() * inputPlane.normal.y + input.m20() * inputPlane.normal.z + input.m30() * inputPlane.d
  val y = input.m01() * inputPlane.normal.x + input.m11() * inputPlane.normal.y + input.m21() * inputPlane.normal.z + input.m31() * inputPlane.d
  val z = input.m02() * inputPlane.normal.x + input.m12() * inputPlane.normal.y + input.m22() * inputPlane.normal.z + input.m32() * inputPlane.d

  val d = input.m03() * inputPlane.normal.x + input.m13() * inputPlane.normal.y + input.m23() * inputPlane.normal.z + input.m33() * inputPlane.d

  return Plane(
    normal = Vector3f(x, y, z),
    d = d,
  )


}