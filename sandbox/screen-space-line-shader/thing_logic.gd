extends Node3D

var line_material: ShaderMaterial
var multi_mesh: MultiMesh
var mesh_instance: MultiMeshInstance3D

func _ready():
	setup_material()
	setup_multi_mesh()
	
	create_orbital_path(Vector3.ZERO, 10.0, 8.0, 50)
	#create_orbital_path(Vector3.ZERO, 10.0, 8.0, 100)
	
	# Add more orbits as needed
	#create_orbital_path(Vector3(20, 0, 0), 5.0, 4.0, 50)
	#create_orbital_path(Vector3(-20, 0, 0), 7.0, 6.0, 75)
	
#func _process(delta):
	#var debug_val = line_material.get_shader_parameter("debug_value")
	#var lineThick = line_material.get_shader_parameter("line_thickness")
	#print("Debug value: %s" % debug_val)
	#print("lineThick value: %s" % lineThick)



func setup_material():
	line_material = ShaderMaterial.new()
	line_material.shader = preload("res://line_shader.gdshader")
	
	# Set default uniform values
	line_material.set_shader_parameter("line_color", Color(Color.AQUA, 0.5))
	line_material.set_shader_parameter("line_thickness", 5.0)

func setup_multi_mesh():
	multi_mesh = MultiMesh.new()
	multi_mesh.transform_format = MultiMesh.TRANSFORM_3D
	multi_mesh.mesh = QuadMesh.new()
	multi_mesh.mesh.size = Vector2(1, 1)
	
	mesh_instance = MultiMeshInstance3D.new()
	mesh_instance.multimesh = multi_mesh
	mesh_instance.material_override = line_material
	add_child(mesh_instance)

func set_line_segments(segments: Array):
	multi_mesh.instance_count = segments.size()
	
	for i in range(segments.size()):
		var start = segments[i][0]
		var end = segments[i][1]
		var direction = (end - start).normalized()
		var length = start.distance_to(end)
		
		var tf = Transform3D()
		tf = tf.looking_at(direction, Vector3.UP)
		tf.origin = (start + end) / 2
		tf = tf.scaled(Vector3(1, 1, length))
		
		print("added transform %s" % tf)
		multi_mesh.set_instance_transform(i, tf)

# Example usage
func create_orbital_path(center: Vector3, semi_major_axis: float, semi_minor_axis: float, segments: int):
	var points = []
	for i in range(segments + 1):
		var angle = 2 * PI * i / segments
		var x = center.x + semi_major_axis * cos(angle)
		var z = center.z + semi_minor_axis * sin(angle)
		points.append(Vector3(x, center.y, z))
	
	var line_segments = []
	for i in range(segments):
		line_segments.append([points[i], points[i+1]])
	
	set_line_segments(line_segments)
