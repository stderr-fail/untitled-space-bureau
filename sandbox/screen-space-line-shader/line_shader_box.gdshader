shader_type spatial;

//render_mode unshaded;

render_mode unshaded, depth_draw_opaque, cull_disabled;

//render_mode unshaded, depth_test_disabled;

uniform float line_thickness : hint_range(1, 10) = 5.0;
uniform vec4 line_color : source_color = vec4(1.0, 1.0, 1.0, 1.0);
uniform float smoothness : hint_range(0.0, 1.0) = 0.05;


uniform float fade_start_distance : hint_range(0.0, 0.5) = 0.05;
uniform float fade_end_distance : hint_range(0.0, 0.5) = 0.3;


//varying vec3 vertex_position;

//void vertex() {
	//// Calculate the vertex in clip space
	//vec4 clip_space = PROJECTION_MATRIX * MODELVIEW_MATRIX * vec4(VERTEX, 1.0);
//
	//// Define two reference points in model space
	//vec4 ref_point1 = PROJECTION_MATRIX * MODELVIEW_MATRIX * vec4(0.0, 0.0, 0.0, 1.0);
	//vec4 ref_point2 = PROJECTION_MATRIX * MODELVIEW_MATRIX * vec4(0.0, 1.0, 0.0, 1.0);
//
	//// Adjust w-component of reference points to match the current vertex
	//ref_point1.w = clip_space.w;
	//ref_point2.w = clip_space.w;
//
	//// Convert reference points to screen space
	//vec2 screen_point1 = (ref_point1.xy / ref_point1.w + 1.0) * 0.5 * VIEWPORT_SIZE;
	//vec2 screen_point2 = (ref_point2.xy / ref_point2.w + 1.0) * 0.5 * VIEWPORT_SIZE;
//
	//// Calculate pixels per unit in screen space
	//float pixels_per_unit = distance(screen_point1, screen_point2);
//
	//// Calculate the desired model size to achieve the line thickness
	//float desired_model_size = line_thickness / pixels_per_unit;
//
	//// Adjust vertex position based on its original position in the unit cube
	//vec3 adjusted_vertex = VERTEX;
	//// X component (length) remains unchanged
	//adjusted_vertex.y = sign(VERTEX.y) * desired_model_size * 0.5;
	//adjusted_vertex.z = sign(VERTEX.z) * desired_model_size * 0.5;
//
	//// Transform the adjusted vertex back to clip space
	////POSITION = PROJECTION_MATRIX * MODELVIEW_MATRIX * vec4(adjusted_vertex, 1.0);
//
//
//}

void fragment() {

	//vec4 face_data = COLOR;
	//vec3 face_color = face_data.rgb;

	//ALBEDO = face_color;
	ALBEDO = vec3(1.0, 1.0, 1.0);
	ALPHA = 0.1;

	//bool is_front_face = false;
	//bool is_back_face = false;
	//bool is_top_face = false;
	//bool is_bottom_face = false;
//
	//// Map face colors to faces
	//if (face_color == vec3(1.0, 0.0, 0.0)) {
		//is_front_face = true;
	//} else if (face_color == vec3(0.0, 1.0, 0.0)) {
		//is_back_face = true;
	//} else if (face_color == vec3(0.0, 0.0, 1.0)) {
		//is_top_face = true;
	//} else if (face_color == vec3(1.0, 1.0, 0.0)) {
		//is_bottom_face = true;
	//}
//
    ////// Calculate the y distance from the center (0.5)
    ////float distance_from_center = abs(UV.y - 0.5);
//
    //// Calculate the distance from the center based on the face orientation
    //float distance_from_center = 0.0;
//
	//if (is_front_face || is_back_face) {
		//// For front and back faces, center is along y-axis
		//distance_from_center = abs(UV.y - 0.5);
	//} else if (is_top_face || is_bottom_face) {
		//// For top and bottom faces, center is along x-axis
		//distance_from_center = abs(UV.y - 0.5);
		////discard;
	//}
//
//
    //// default to no alpha
    //float alpha = 0.0;
//
	//// apply some alpha depending on how close to center the fragment is
    ////if (distance_from_center > fade_start_distance) {
		////alpha = smoothstep(fade_end_distance, fade_start_distance, distance_from_center);
    ////}
//
	//// apply some alpha depending on how close to center the fragment is
	//if (distance_from_center > fade_end_distance) {
		//alpha = 0.0;
	//} else if (distance_from_center < fade_start_distance) {
		//// Fully opaque at the center
		//alpha = 1.0;
	//} else if (distance_from_center < fade_end_distance) {
		//// Smooth fade to 0 alpha
		//alpha = smoothstep(fade_end_distance, fade_start_distance, distance_from_center);
	//}


    //ALPHA = alpha;

}
