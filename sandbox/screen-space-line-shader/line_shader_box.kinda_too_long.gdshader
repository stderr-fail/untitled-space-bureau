shader_type spatial;

//render_mode unshaded;

render_mode unshaded, depth_draw_opaque, cull_disabled;

//render_mode unshaded, depth_test_disabled;

uniform float line_thickness : hint_range(1, 10) = 5.0;
uniform vec4 line_color : source_color = vec4(1.0, 1.0, 1.0, 1.0);


void vertex() {
    // Transform the vertex to view space
    vec4 view_vertex = MODELVIEW_MATRIX * vec4(VERTEX, 1.0);

    // Calculate the direction from the camera to the vertex in view space
    vec3 view_dir = normalize(view_vertex.xyz);

    // Calculate right vector (perpendicular to view direction and up vector)
    vec3 right = normalize(cross(view_dir, vec3(0.0, 1.0, 0.0)));

    // Recalculate up vector to ensure orthogonality
    vec3 up = normalize(cross(right, view_dir));

    // Calculate the scale factor based on the distance from the camera
    float scale_factor = length(view_vertex.xyz) * line_thickness / VIEWPORT_SIZE.y;

    // Adjust the vertex position based on its original position in the unit cube
    vec3 offset = (VERTEX.x * right + VERTEX.y * up) * scale_factor;

    // Apply the offset to the original vertex position
    vec3 adjusted_vertex = VERTEX + offset;

    // Transform the adjusted vertex back to world space
    vec4 world_pos = MODELVIEW_MATRIX * vec4(adjusted_vertex, 1.0);

    // Output the final position
    POSITION = PROJECTION_MATRIX * world_pos;
}

void fragment() {

	// Set final color and alpha
	ALBEDO = line_color.rgb;
	ALPHA = line_color.a;

}
