extends Node3D

@export
var camera: Camera3D

var line_material: ShaderMaterial
var multi_mesh: MultiMesh
var mesh_instance: MultiMeshInstance3D


func _ready():
	setup_material()
	setup_multi_mesh()
	create_orbital_path(Vector3.ZERO, 20.0, 20.0, 64)
  

func setup_material():
	line_material = ShaderMaterial.new()
	line_material.shader = preload("res://line_shader_quads.gdshader")

	# Set default uniform values
	line_material.set_shader_parameter("line_color", Color(Color.AQUA, 0.33))
	line_material.set_shader_parameter("line_thickness", 25.0)


func setup_multi_mesh():
	multi_mesh = MultiMesh.new()
	multi_mesh.transform_format = MultiMesh.TRANSFORM_3D
	multi_mesh.mesh = QuadMesh.new()
	multi_mesh.mesh.size = Vector2(1, 1)

	mesh_instance = MultiMeshInstance3D.new()
	mesh_instance.multimesh = multi_mesh
	mesh_instance.material_override = line_material
	add_child(mesh_instance)

# creats an Array[Tuple(Vector3, Vector3)] where each tuple is a start/end vec3 of a line segment
func create_orbital_path(center: Vector3, semi_major_axis: float, semi_minor_axis: float, segments: int):
	var points: Array[Vector3] = []

	for i in range(segments + 1):
		var angle = 2 * PI * i / segments
		var x = center.x + semi_major_axis * cos(angle)
		var z = center.z + semi_minor_axis * sin(angle)
		points.append(Vector3(x, center.y, z))

	var line_segments: Array = []
  
  # this wraps around to reconnect the last point to the first
	for i in range(segments):
		line_segments.append([points[i], points[i+1]])
  
	set_line_segments(line_segments)

# takes in an Array[Tuple[Vector3, Vector3]] where each tuple is a start/end vec3 of a line segment
func set_line_segments(segments: Array):
	multi_mesh.instance_count = segments.size()
  
	for i in range(segments.size()):
		var start = segments[i][0]
		var end = segments[i][1]
		var direction = (end - start).normalized()
		var length = start.distance_to(end)
	
		var tf = Transform3D()
		# tf = tf.looking_at(direction, Vector3.UP)
		tf.origin = (start + end) / 2
		tf = tf.looking_at(Vector3.ZERO)
		
		tf = tf.scaled_local(Vector3(length, 1, 1))
		
		#print("added transform %s" % tf)
		multi_mesh.set_instance_transform(i, tf)
