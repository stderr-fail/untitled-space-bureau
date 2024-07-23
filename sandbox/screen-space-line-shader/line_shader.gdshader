shader_type spatial;
render_mode unshaded, depth_draw_opaque, cull_disabled;

uniform vec4 line_color : source_color = vec4(1.0);
uniform float line_thickness : hint_range(0, 100) = 5.0;

varying vec3 vertex_pos;

varying vec3 debug_value;


void vertex() {

    //debug_value=vec3(1.0, 2.0, 3.0);
    //vertex_pos = VERTEX;
//
    //// Get the model space position
    //vec3 model_pos = (MODEL_MATRIX * vec4(VERTEX, 1.0)).xyz;
//
    //// Billboard the quad to face the camera
    //MODELVIEW_MATRIX = INV_VIEW_MATRIX * MODEL_MATRIX;
    //MODELVIEW_MATRIX[0].xyz = normalize(MODEL_MATRIX[0].xyz);
    //MODELVIEW_MATRIX[1].xyz = normalize(MODEL_MATRIX[1].xyz);
    //MODELVIEW_MATRIX[2].xyz = normalize(MODEL_MATRIX[2].xyz);
//
    //// Scale the quad based on distance to maintain consistent screen-space size
    //float distance = length((MODELVIEW_MATRIX * vec4(0.0, 0.0, 0.0, 1.0)).xyz);
    //MODELVIEW_MATRIX[0] *= distance;
    //MODELVIEW_MATRIX[1] *= distance;
//
    //// Apply projection
    //POSITION = PROJECTION_MATRIX * MODELVIEW_MATRIX * vec4(VERTEX, 1.0);

}

void fragment() {
    //ALBEDO = vec3(UV, 0.0);
	ALBEDO = vec3(0.0, 1.0, 0.0);
	//return;

	//debug_value=vec3(UV, 0.0);
	//debug_value=vec3(1.0, 2.0, 3.0);


    //// Calculate distance from fragment to line center
    //vec2 coord = (vertex_pos.xy / vertex_pos.z) * 0.5 + 0.5;
    //float dist = abs(coord.y - 0.5) * 2.0;
//
    //// Discard fragments outside the line width
    //float half_thickness = line_thickness / (2.0 * VIEWPORT_SIZE.y);
    //if (dist > half_thickness) {
        //discard;
    //}
//
    //// Apply anti-aliasing
    //float alpha = smoothstep(half_thickness, half_thickness - 0.001, dist);
    //ALBEDO = line_color.rgb;
    //ALPHA = alpha * line_color.a;

}

