shader_type spatial;

render_mode unshaded, depth_draw_opaque, cull_disabled;
//render_mode unshaded, depth_test_disabled;

uniform float line_thickness : hint_range(1, 10) = 5.0;
uniform vec4 line_color : source_color = vec4(1.0, 1.0, 1.0, 1.0);

varying vec2 line_uv;

void vertex() {

	// calculate the VERTEX clip space
	vec4 clip_space = PROJECTION_MATRIX * MODELVIEW_MATRIX * vec4(VERTEX, 1.0);

	// define two points, top and bottom, one unit apart in the y-component in clip space
	vec3 current = normalize(VERTEX);
	vec3 currentPlusOne = current + current;
	
	
	vec4 top_point = PROJECTION_MATRIX * MODELVIEW_MATRIX * vec4(current, 1.0);
	vec4 bottom_point = PROJECTION_MATRIX * MODELVIEW_MATRIX * vec4(currentPlusOne, 1.0);
	
	//vec4 top_point = PROJECTION_MATRIX * MODELVIEW_MATRIX * vec4(0.0, 1.0, 0.0, 1.0);
	//vec4 bottom_point = PROJECTION_MATRIX * MODELVIEW_MATRIX * vec4(0.0, 0.0, 0.0, 1.0);

	//  modify the top/bottom points w-component (perspective) to match the current VERTEX
	top_point.w = clip_space.w;
	bottom_point.w = clip_space.w;

	//vec2 top_screen = (top_point.xy / top_point.w + 1.0) * 0.5 * VIEWPORT_SIZE;
	//vec2 bottom_screen = (bottom_point.xy / bottom_point.w + 1.0) * 0.5 * VIEWPORT_SIZE;
	
	vec3 top_screen = (top_point.xyz / top_point.w + 1.0) * 0.5 * vec3(VIEWPORT_SIZE, 1.0);
	vec3 bottom_screen = (bottom_point.xyz / bottom_point.w + 1.0) * 0.5 * vec3(VIEWPORT_SIZE, 1.0);

	//vec2 top_screen = (top_point.xy / clip_space.w + 1.0) * 0.5 * VIEWPORT_SIZE;
	//vec2 bottom_screen = (bottom_point.xy / clip_space.w + 1.0) * 0.5 * VIEWPORT_SIZE;

	float height_pixels_per_unit = distance(top_screen, bottom_screen);

	// 4. Adjust vertex position based on desired line thickness
	float desired_model_height = line_thickness / height_pixels_per_unit;

	float half_height = desired_model_height * 0.5;

	// Adjust Y position based on UV
	float y_offset = (UV.y - 0.5) * desired_model_height;

	vec3 adjusted_vertex = VERTEX;
	adjusted_vertex.y = y_offset;

	// Transform the adjusted vertex to clip space
	vec4 final_position = PROJECTION_MATRIX * MODELVIEW_MATRIX * vec4(adjusted_vertex, 1.0);
	//POSITION = final_position;
}

void fragment() {

	// Set final color and alpha
	ALBEDO = line_color.rgb;
	ALPHA = line_color.a;

}
