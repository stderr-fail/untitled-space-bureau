class_name FollowCamera

extends Camera3D

@export var sensitivity: float = 0.3

var rotating: bool = false
var tracked_rotation: Vector3
var tracked_distance: float = 0.0
var tracked_direction: Vector3

@onready var xValLabel = get_node("../DebugPanelz/MarginContainer/GridContainer/xValLabel")
@onready var yValLabel = get_node("../DebugPanelz/MarginContainer/GridContainer/yValLabel")
@onready var zValLabel = get_node("../DebugPanelz/MarginContainer/GridContainer/zValLabel")

# Reference to the focal point node
var focus_node: Node3D

func _ready():
	followNode("Star", true)

func _process(_delta):
	update_labels()
	update_camera()

func _input(event):
	if event is InputEventKey:
		if event.keycode == Key.KEY_ALT:
			rotating = event.pressed
	
	elif event is InputEventMouseButton:
		if event.button_index == MouseButton.MOUSE_BUTTON_RIGHT:
			rotating = event.pressed
			Input.set_mouse_mode(Input.MOUSE_MODE_CAPTURED if rotating else Input.MOUSE_MODE_VISIBLE)
	
	elif event is InputEventMouseMotion and rotating:
		tracked_rotation.y -= event.relative.x * sensitivity * 0.01
		tracked_rotation.x -= event.relative.y * sensitivity * 0.01
		tracked_rotation.x = clamp(tracked_rotation.x, -PI / 2, PI / 2)
		
		tracked_direction = Vector3(
			sin(tracked_rotation.y) * cos(tracked_rotation.x), 
			sin(tracked_rotation.x), 
			cos(tracked_rotation.y) * cos(tracked_rotation.x)
		)
		
		update_camera()

func followNode(bodyName: String, calc_tracked_distance: bool):
	var systemNode = get_node("../SolarSystem")
	focus_node = systemNode.find_child(bodyName, true) as Node3D
	look_at(focus_node.global_transform.origin, Vector3.UP)
	
	if calc_tracked_distance:
		tracked_distance = (global_transform.origin - focus_node.global_transform.origin).length()
	
	# setup initial rotation based on current camera state
	var to_focus_node = (global_transform.origin - focus_node.global_transform.origin).normalized()
	
	tracked_rotation.y = atan2(to_focus_node.x, to_focus_node.z)
	tracked_rotation.x = asin(to_focus_node.y)
	
	tracked_direction = Vector3(
		sin(tracked_rotation.y) * cos(tracked_rotation.x), 
		sin(tracked_rotation.x), 
		cos(tracked_rotation.y) * cos(tracked_rotation.x)
	)
	
	update_camera()
	
func update_labels():
	xValLabel.text = "%d" % position.x
	yValLabel.text = "%d" % position.y
	zValLabel.text = "%d" % position.z

func update_camera():		
	var nextOrigin = focus_node.global_transform.origin + tracked_direction * tracked_distance
	global_transform.origin = nextOrigin
	
	# must be last, after the global_transform.origin update
	if rotating:
		reset_look_at()
		
func reset_look_at():
		look_at(focus_node.global_transform.origin, Vector3.UP)

func set_camera_distance(desired_distance: float):
	tracked_distance = desired_distance
	update_camera()
	
	#print("set_camera_distance(%s)" % desired_distance)
	#
	#var reference_position: Vector3 = focus_node.global_transform.origin
#
	## Get the current position of the camera
	#var camera_position: Vector3 = global_transform.origin
	#
	## Calculate the direction vector from the camera to the reference point
	#var direction_vector: Vector3 = reference_position - camera_position
	#
	## Normalize the direction vector
	#direction_vector = direction_vector.normalized()
	#
	## Scale the direction vector by the desired distance
	#direction_vector *= desired_distance
	#
	## Set the new position of the camera
	#global_transform.origin = reference_position - direction_vector
	#
	## Make the camera look at the reference point
	##camera.look_at(reference_point, Vector3.UP)
	#
	#reset_look_at()

