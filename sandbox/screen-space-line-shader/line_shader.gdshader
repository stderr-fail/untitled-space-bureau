shader_type spatial;
render_mode unshaded, depth_draw_opaque, cull_disabled;


uniform vec4 line_color : source_color = vec4(1.0);
uniform float line_thickness : hint_range(0, 100) = 5.0;
uniform sampler2D SCREEN_TEXTURE : hint_screen_texture, filter_linear_mipmap;


//void vertex() {
//
	//// Convert the vertex position to view space
	//vec4 view_pos = VIEW_MATRIX * MODEL_MATRIX * vec4(VERTEX, 1.0);
//
	//// Calculate the distance from the camera
	//float dist = -view_pos.z;
//
	//// Scale the y-component (thickness) based on distance
	//VERTEX.y *= dist;
//
	//// Apply the line thickness
	////VERTEX.y *= line_thickness * 0.01; // Divide by 100 to make the range more manageable
	//VERTEX.y *= 10.0 * 0.01; // Divide by 100 to make the range more manageable
//
//}

//void vertex() {
    //// Convert the vertex position to view space
    //vec4 view_pos = VIEW_MATRIX * MODEL_MATRIX * vec4(VERTEX, 1.0);
//
    //// Calculate the distance from the camera
    //float dist = -view_pos.z;
//
    //// Scale the y-component (thickness) to maintain constant screen-space size
    //VERTEX.y *= dist / PROJECTION_MATRIX[1][1];
	////VERTEX.y *= dist;
//
    //// Apply the line thickness
    //VERTEX.y *= line_thickness * 0.01; // Divide by 100 to make the range more manageable
//
//}

void vertex() {
	// Convert the vertex position to clip space
	vec4 clip_pos = PROJECTION_MATRIX * MODELVIEW_MATRIX * vec4(VERTEX, 1.0);

	// Get the viewport size
	vec2 screen_size = vec2(textureSize(SCREEN_TEXTURE, 0));

	// Calculate the clip space height of one pixel
	float clip_pixel_height = 2.0 / screen_size.y;

	// Adjust the y-component to achieve exact pixel height
	float y_sign = sign(VERTEX.y);
	clip_pos.y = clip_pos.y + y_sign * (line_thickness * 0.5 * clip_pixel_height);

	// Convert back to world space
	vec4 world_pos = INV_PROJECTION_MATRIX * clip_pos;


	//VERTEX = world_pos.xyz / world_pos.w;

	VERTEX.y = (world_pos.xyz / world_pos.w).y;

}


void fragment() {

    //ALBEDO = vec3(UV, 0.0);
	//ALBEDO = vec3(0.0, 1.0, 0.0);
	//ALPHA = 0.65;

	// Calculate distance from the center of the quad
	//vec2 center_dist = abs(UV - 0.5) * 2.0;
	//float dist = max(center_dist.x, center_dist.y);



	ALBEDO = line_color.rgb;
	ALPHA = line_color.a;



}
