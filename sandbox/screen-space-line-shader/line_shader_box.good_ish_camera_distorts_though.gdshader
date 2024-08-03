shader_type spatial;

//render_mode unshaded;

render_mode unshaded, depth_draw_opaque, cull_disabled;

//render_mode unshaded, depth_test_disabled;

uniform float line_thickness : hint_range(1, 10) = 5.0;
uniform vec4 line_color : source_color = vec4(1.0, 1.0, 1.0, 1.0);


void vertex() {
	// Calculate the vertex in clip space
	vec4 clip_space = PROJECTION_MATRIX * MODELVIEW_MATRIX * vec4(VERTEX, 1.0);

	// Define two reference points in model space
	vec4 ref_point1 = PROJECTION_MATRIX * MODELVIEW_MATRIX * vec4(0.0, 0.0, 0.0, 1.0);
	vec4 ref_point2 = PROJECTION_MATRIX * MODELVIEW_MATRIX * vec4(0.0, 1.0, 0.0, 1.0);

	// Adjust w-component of reference points to match the current vertex
	ref_point1.w = clip_space.w;
	ref_point2.w = clip_space.w;

	// Convert reference points to screen space
	vec2 screen_point1 = (ref_point1.xy / ref_point1.w + 1.0) * 0.5 * VIEWPORT_SIZE;
	vec2 screen_point2 = (ref_point2.xy / ref_point2.w + 1.0) * 0.5 * VIEWPORT_SIZE;

	// Calculate pixels per unit in screen space
	float pixels_per_unit = distance(screen_point1, screen_point2);

	// Calculate the desired model size to achieve the line thickness
	float desired_model_size = line_thickness / pixels_per_unit;

	// Adjust vertex position based on its original position in the unit cube
	vec3 adjusted_vertex = VERTEX;
	// X component (length) remains unchanged
	adjusted_vertex.y = sign(VERTEX.y) * desired_model_size * 0.5;
	adjusted_vertex.z = sign(VERTEX.z) * desired_model_size * 0.5;

	// Transform the adjusted vertex back to clip space
	POSITION = PROJECTION_MATRIX * MODELVIEW_MATRIX * vec4(adjusted_vertex, 1.0);

}

void fragment() {

	// Set final color and alpha
	ALBEDO = line_color.rgb;
	ALPHA = line_color.a;

}
