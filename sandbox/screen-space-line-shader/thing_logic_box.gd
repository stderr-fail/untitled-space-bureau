extends Node3D

@export
var camera: Camera3D

var line_material: ShaderMaterial
var multi_mesh: MultiMesh
var mesh_instance: MultiMeshInstance3D


func _ready():
	setup_material()
	setup_multi_mesh()
	create_orbital_path(Vector3.ZERO, 20.0, 20.0, 128)
  

func setup_material():
	line_material = ShaderMaterial.new()
	line_material.shader = preload("res://line_shader_box.gdshader")

	# Set default uniform values
	line_material.set_shader_parameter("line_color", Color(Color.AQUA, 0.1))
	line_material.set_shader_parameter("line_thickness", 25.0)



func setup_multi_mesh():
	
	# Define vertices for six faces of the cube
	var vertices = PackedVector3Array([
		# Front face
		Vector3(-1, -1, 1), Vector3(1, -1, 1), Vector3(1, 1, 1), Vector3(-1, 1, 1),
		# Back face
		Vector3(1, -1, -1), Vector3(-1, -1, -1), Vector3(-1, 1, -1), Vector3(1, 1, -1),
		# Top face
		Vector3(-1, 1, 1), Vector3(1, 1, 1), Vector3(1, 1, -1), Vector3(-1, 1, -1),
		# Bottom face
		Vector3(-1, -1, -1), Vector3(1, -1, -1), Vector3(1, -1, 1), Vector3(-1, -1, 1)
	])

	# Define normals for each face
	var normals = PackedVector3Array([
		# Front face normals
		Vector3(0, 0, 1), Vector3(0, 0, 1), Vector3(0, 0, 1), Vector3(0, 0, 1),
		# Back face normals
		Vector3(0, 0, -1), Vector3(0, 0, -1), Vector3(0, 0, -1), Vector3(0, 0, -1),
		# Top face normals
		Vector3(0, 1, 0), Vector3(0, 1, 0), Vector3(0, 1, 0), Vector3(0, 1, 0),
		# Bottom face normals
		Vector3(0, -1, 0), Vector3(0, -1, 0), Vector3(0, -1, 0), Vector3(0, -1, 0)
	])

	# Define indices for each face
	var indices = PackedInt32Array([
		# Front face
		0, 1, 2, 0, 2, 3,
		# Back face
		4, 5, 6, 4, 6, 7,
		# Top face
		8, 9, 10, 8, 10, 11,
		# Bottom face
		12, 13, 14, 12, 14, 15,
	])
	
	# Define UVs for each vertex
	var uvs = PackedVector2Array([
		# Front face UVs
		Vector2(0, 0), Vector2(1, 0), Vector2(1, 1), Vector2(0, 1),
		# Back face UVs
		Vector2(0, 0), Vector2(1, 0), Vector2(1, 1), Vector2(0, 1),
		# Top face UVs
		Vector2(0, 0), Vector2(1, 0), Vector2(1, 1), Vector2(0, 1),
		# Bottom face UVs
		Vector2(0, 0), Vector2(1, 0), Vector2(1, 1), Vector2(0, 1)
	])

	
	var offAlpha = 0.01
	var onAlpha = 1.0
	
	var frontColor = Color(1, 0, 0, offAlpha)
	var backColor = Color(0, 1, 0, offAlpha)
	var topColor = Color(0, 0, 1, offAlpha)
	var bottomColor = Color(1, 1, 0, offAlpha)
	
	#var sharedColor = Color(1, 1, 0, 0.5)
	#var frontColor = sharedColor
	#var backColor = sharedColor
	#var topColor = sharedColor
	#var bottomColor = sharedColor
	
	# "front" = out facing
	# "back" = in facing
	# "left" = up facing
	# "right" = down facing

	# Vertex colors for distinguishing faces (custom data)
	var colors = PackedColorArray([
		frontColor, frontColor, frontColor, frontColor, # Red for front face
		backColor, backColor, backColor, backColor, # Green for back face
		topColor, topColor, topColor, topColor, # Blue for left face
		bottomColor, bottomColor, bottomColor, bottomColor, # Yellow for right face
	])
	
	
	var arrays = []
	arrays.resize(Mesh.ARRAY_MAX)
	arrays[Mesh.ARRAY_VERTEX] = vertices
	arrays[Mesh.ARRAY_NORMAL] = normals
	arrays[Mesh.ARRAY_INDEX] = indices
	arrays[Mesh.ARRAY_COLOR] = colors
	arrays[Mesh.ARRAY_TEX_UV] = uvs

	
	var m2 = ArrayMesh.new()
	m2.add_surface_from_arrays(Mesh.PRIMITIVE_TRIANGLES, arrays)


	
	
	
	
	multi_mesh = MultiMesh.new()
	multi_mesh.transform_format = MultiMesh.TRANSFORM_3D
	
	multi_mesh.mesh =  m2
	
	#multi_mesh.mesh = BoxMesh.new()
	#multi_mesh.mesh.size = Vector3(1, 1, 1)  # Set initial size to 1x1x1 cube
	
	
	

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
		#var direction = (end - start).normalized()
		var length = start.distance_to(end)
	
		var tf = Transform3D()
		tf.origin = (start + end) / 2
		tf = tf.looking_at(Vector3.UP)
		tf = tf.scaled_local(Vector3(length, 0.05, 0.05))
		
		#print("added transform %s" % tf)
		multi_mesh.set_instance_transform(i, tf)
