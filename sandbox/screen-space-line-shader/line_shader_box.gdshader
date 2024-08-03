shader_type spatial;

//render_mode unshaded;

render_mode unshaded, depth_draw_opaque, cull_disabled;

//render_mode unshaded, depth_test_disabled;

uniform float line_thickness : hint_range(1, 10) = 5.0;
uniform vec4 line_color : source_color = vec4(1.0, 1.0, 1.0, 1.0);

varying vec2 line_uv;
/*
starting our shader over from scratch,

For a Godot 4.3 shader which operates on..

the vertex() function is being run on the 4 vertices of a QuadMesh that is part of a MeshInstance3D that contains an array of Transform3D's to arrange the QuadMesh instances to represent a elliptical orbital path.

assume the following:

- the shader has a float uniform "line_thickness" which represents the exact pixel height of our final screen space lines
- the input QuadMesh instances always have a unit height on the y axis of 1

give me vertex() function code which will:

1. create a variable "screen_space" which holds the screen space coordinates of the vertex, give me code that derives this value correctly

2. create a variable "pixel_height" which uses the VIEWPORT_SIZE built-in (which is the viewport size in pixels)

3. create a series of variables with the following logic which will figure out the height in pixels of a unit of 1 in local space

    // Project a point 1 unit above in world space to screen space
    vec4 top_point = PROJECTION_MATRIX * MODELVIEW_MATRIX * vec4(0.0, 1.0, 0.0, 1.0);
    vec4 bottom_point = PROJECTION_MATRIX * MODELVIEW_MATRIX * vec4(0.0, 0.0, 0.0, 1.0);

    vec2 top_screen = (top_point.xy / top_point.w + 1.0) * 0.5 * VIEWPORT_SIZE;
    vec2 bottom_screen = (bottom_point.xy / bottom_point.w + 1.0) * 0.5 * VIEWPORT_SIZE;

    float height_pixels_per_unit = distance(top_screen, bottom_screen);

4.  given the previous variables, this should be enough information to determine how tall the mesh should be in model space to represent the exact line_thickness in pixels in screen space,
    I'm thinking we can use the vertex UV coordinates of 0,0 (bottom left), 0,1 (top left), 1,1 (top right) and 1,0 (bottom right) to determine which
    corner of the QuadMesh the current vertex is.  Knowing all the previous information, we can shift each corner up or down on the y axis in model space
    and then convert that to clip space and set the POSITION out variable to achieve our final screen space lines of exact pixel height defined by line_thickness uniform

*/

/*
the vertex() function is being run on the 4 vertices of a QuadMesh that is part of a
MeshInstance3D that contains an array of Transform3D's to arrange the QuadMesh instances
to represent a elliptical orbital path.
*/
void vertex() {


		// calculate the VERTEX clip space
	vec4 clip_space = PROJECTION_MATRIX * MODELVIEW_MATRIX * vec4(VERTEX, 1.0);

	// define two points, top and bottom, one unit apart in the y-component in clip space
	vec4 top_point = PROJECTION_MATRIX * MODELVIEW_MATRIX * vec4(0.0, 1.0, 0.0, 1.0);
	vec4 bottom_point = PROJECTION_MATRIX * MODELVIEW_MATRIX * vec4(0.0, 0.0, 0.0, 1.0);

	//  modify the top/bottom points w-component (perspective) to match the current VERTEX
	top_point.w = clip_space.w;
	bottom_point.w = clip_space.w;

	vec2 top_screen = (top_point.xy / top_point.w + 1.0) * 0.5 * VIEWPORT_SIZE;
	vec2 bottom_screen = (bottom_point.xy / bottom_point.w + 1.0) * 0.5 * VIEWPORT_SIZE;

	//vec2 top_screen = (top_point.xy / clip_space.w + 1.0) * 0.5 * VIEWPORT_SIZE;
	//vec2 bottom_screen = (bottom_point.xy / clip_space.w + 1.0) * 0.5 * VIEWPORT_SIZE;

	float height_pixels_per_unit = distance(top_screen, bottom_screen);

    // 4. Adjust vertex position based on desired line thickness
    float desired_model_height = line_thickness / height_pixels_per_unit;



    float half_height = desired_model_height * 0.5;

    // Adjust Y position based on UV
    //float y_offset = (UV.y - 0.5) * desired_model_height;
	float y_offset = (UV.y - 0.5) * desired_model_height;

    //vec3 adjusted_vertex = VERTEX;
    //adjusted_vertex.y = y_offset;
//
    //vec4 final_position = PROJECTION_MATRIX * MODELVIEW_MATRIX * vec4(adjusted_vertex, 1.0);
    //POSITION = final_position;


	vec4 pos_next = PROJECTION_MATRIX * MODELVIEW_MATRIX * vec4(VERTEX, 1.0);

	pos_next.x += y_offset;
	POSITION = pos_next;


	if (NORMAL.y == 1.0 && UV.y == 1.0) { // * toplleft
	//if (NORMAL.y == 1.0 && UV.y == 0.0) {
	//if (NORMAL.y == 1.0 && UV.y == -1.0) {
	//if (NORMAL.y == 1.0 && UV.x == 1.0) {
	//if (NORMAL.y == 1.0 && UV.x == 0.0) {
	//if (NORMAL.y == 1.0 && UV.x == -1.0) {
	//
	//if (NORMAL.y == -1.0 && UV.y == 1.0) { // *
	//if (NORMAL.y == -1.0 && UV.y == 0.0) {
	//if (NORMAL.y == -1.0 && UV.y == -1.0) {
	//if (NORMAL.y == -1.0 && UV.x == 1.0) { // *
	//if (NORMAL.y == -1.0 && UV.x == 0.0) {
	//if (NORMAL.y == -1.0 && UV.x == -1.0) {
	//
	//if (NORMAL.y == 0.0 && UV.y == 1.0) { // *
	//if (NORMAL.y == 0.0 && UV.y == 0.0) { // *
	//if (NORMAL.y == 0.0 && UV.y == -1.0) {
	//if (NORMAL.y == 0.0 && UV.x == 1.0) { // *
	//if (NORMAL.y == 0.0 && UV.x == 0.0) { // *
	//if (NORMAL.y == 0.0 && UV.x == -1.0) {

		//
	//if (NORMAL.x == 1.0 && UV.y == 1.0) {
	//if (NORMAL.x == 1.0 && UV.y == 0.0) { // *
	//if (NORMAL.x == 1.0 && UV.y == -1.0) {
	//if (NORMAL.x == 1.0 && UV.x == 1.0) {
	//if (NORMAL.x == 1.0 && UV.x == 0.0) {
	//if (NORMAL.x == 1.0 && UV.x == -1.0) {
	//
	//if (NORMAL.x == -1.0 && UV.y == 1.0) { // *
	//if (NORMAL.x == -1.0 && UV.y == 0.0) {
	//if (NORMAL.x == -1.0 && UV.y == -1.0) {
	//if (NORMAL.x == -1.0 && UV.x == 1.0) {
	//if (NORMAL.x == -1.0 && UV.x == 0.0) { // *
	//if (NORMAL.x == -1.0 && UV.x == -1.0) {
	//
	//if (NORMAL.x == 0.0 && UV.y == 1.0) { // *
	//if (NORMAL.x == 0.0 && UV.y == 0.0) { // *
	//if (NORMAL.x == 0.0 && UV.y == -1.0) {
	//if (NORMAL.x == 0.0 && UV.x == 1.0) { // *
	//if (NORMAL.x == 0.0 && UV.x == 0.0) {
	//if (NORMAL.x == 0.0 && UV.x == -1.0) {


		//
	//if (NORMAL.z == 1.0 && UV.y == 1.0) {
	//if (NORMAL.z == 1.0 && UV.y == 0.0) { // *
	//if (NORMAL.z == 1.0 && UV.y == -1.0) {
	//if (NORMAL.z == 1.0 && UV.x == 1.0) {
	//if (NORMAL.z == 1.0 && UV.x == 0.0) { // *
	//if (NORMAL.z == 1.0 && UV.x == -1.0) {

		//POSITION.y += 0.25;

		//vec4 current_pos = PROJECTION_MATRIX * MODELVIEW_MATRIX * vec4(VERTEX, 1.0);
//
		//POSITION = current_pos;


		//vec3 adjusted_vertex = VERTEX;
		//adjusted_vertex.y = y_offset;
////
		//vec4 final_position = PROJECTION_MATRIX * MODELVIEW_MATRIX * vec4(adjusted_vertex, 1.0);
		//
		//POSITION = final_position;

	}


    // For left face vertices
    if (NORMAL.x < -0.5) {
        // NORMAL will be approximately (-1, 0, 0), but x component might not be exactly -1
        // due to scaling

    }
    // For right face vertices
    else if (NORMAL.x > 0.5) {
        // NORMAL will be approximately (1, 0, 0), but x component might not be exactly 1
        // due to scaling
    }
    // For bottom face vertices
    else if (NORMAL.y < -0.5) {
        // NORMAL should be exactly (0, -1, 0)

		//vec3 adjusted_vertex = VERTEX;
		//adjusted_vertex.y = y_offset;
//
		//vec4 final_position = PROJECTION_MATRIX * MODELVIEW_MATRIX * vec4(adjusted_vertex, 1.0);
		//POSITION = final_position;
    }
    // For top face vertices
    else if (NORMAL.y > 0.5) {
        // NORMAL should be exactly (0, 1, 0)


		//vec3 adjusted_vertex = VERTEX;
		//adjusted_vertex.y = -y_offset;
//
		//vec4 final_position = PROJECTION_MATRIX * MODELVIEW_MATRIX * vec4(adjusted_vertex, 1.0);
		//POSITION = final_position;


    }
    // For back face vertices
    else if (NORMAL.z < -0.5) {
        // NORMAL should be exactly (0, 0, -1)
    }
    // For front face vertices
    else if (NORMAL.z > 0.5) {
        // NORMAL should be exactly (0, 0, 1)
    }



}

void fragment() {

	// Set final color and alpha
	ALBEDO = line_color.rgb;
	ALPHA = line_color.a;

}
