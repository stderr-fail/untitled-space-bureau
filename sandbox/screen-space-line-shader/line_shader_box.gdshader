shader_type spatial;

//render_mode unshaded;

render_mode unshaded, depth_draw_opaque, cull_disabled;

//render_mode unshaded, depth_test_disabled;

uniform float line_thickness : hint_range(1, 10) = 5.0;
uniform vec4 line_color : source_color = vec4(1.0, 1.0, 1.0, 1.0);
uniform float smoothness : hint_range(0.0, 1.0) = 0.05;

//varying vec3 vertex_position;

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
	//POSITION = PROJECTION_MATRIX * MODELVIEW_MATRIX * vec4(adjusted_vertex, 1.0);


}

void fragment() {

	vec4 face_data = COLOR;
	vec3 face_color = face_data.rgb;

	ALBEDO = face_color;
	ALPHA = face_data.a;


	// Set final color and alpha
	//ALBEDO = line_color.rgb;
	//ALPHA = line_color.a;

	//// Calculate the distance from the center of the cube
	//vec2 coord = (UV - 0.5) * 2.0;
	//float dist = length(coord);
//
	//float thickness = 10.0;
	//float v_smoothness = 0.05;
//
	//// Calculate the alpha based on the distance and line thickness
    //float alpha = 1.0 - smoothstep(line_thickness - smoothness, line_thickness, dist);
//
	//// Apply the color and alpha
	//ALBEDO = line_color.rgb;
	//ALPHA = alpha * line_color.a;
//
	    //// Discard fragments that are fully transparent
    //if (alpha < 0.001) {
        //discard;
    //}




}
