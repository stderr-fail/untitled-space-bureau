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


    // 1. Create screen_space variable
    vec4 clip_pos = PROJECTION_MATRIX * MODELVIEW_MATRIX * vec4(VERTEX, 1.0);
    vec3 ndc = clip_pos.xyz / clip_pos.w;
    vec2 screen_space = (ndc.xy + 1.0) * 0.5 * VIEWPORT_SIZE;

    // 2. Create pixel_height variable
    vec2 pixel_height = VIEWPORT_SIZE;

    // 3. Calculate height_pixels_per_unit
    vec4 top_point = PROJECTION_MATRIX * MODELVIEW_MATRIX * vec4(0.0, 1.0, 0.0, 1.0);
    vec4 bottom_point = PROJECTION_MATRIX * MODELVIEW_MATRIX * vec4(0.0, 0.0, 0.0, 1.0);

    vec2 top_screen = (top_point.xy / top_point.w + 1.0) * 0.5 * VIEWPORT_SIZE;
    vec2 bottom_screen = (bottom_point.xy / bottom_point.w + 1.0) * 0.5 * VIEWPORT_SIZE;

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
    POSITION = final_position;
}

void fragment() {

    // Set final color and alpha
    ALBEDO = line_color.rgb;
	  ALPHA = line_color.a;

}
