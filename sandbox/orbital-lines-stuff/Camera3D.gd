extends Camera3D

@export var sensitivity: float = 0.3

var rotating: bool = false
var tracked_rotation: Vector3

@onready var xValLabel = get_node("../DebugPanel/MarginContainer/GridContainer/xValLabel")
@onready var yValLabel = get_node("../DebugPanel/MarginContainer/GridContainer/yValLabel")
@onready var zValLabel = get_node("../DebugPanel/MarginContainer/GridContainer/zValLabel")

# Reference to the star (center of the solar system)
var star: Node3D

func _process(delta):
	updateLabels()
	
func updateLabels():
	xValLabel.text = "%d" % position.x
	yValLabel.text = "%d" % position.y
	zValLabel.text = "%d" % position.z

func _ready():


	star = get_node("../SolarSystem/Star") as Node3D
	look_at(star.global_transform.origin, Vector3.UP)
	
	# setup initial rotation based on current camera state
	var to_star = (global_transform.origin - star.global_transform.origin).normalized()
	tracked_rotation.y = atan2(to_star.x, to_star.z)
	tracked_rotation.x = asin(to_star.y)



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
		update_camera()

func update_camera():
	
	var direction = Vector3(
		sin(tracked_rotation.y) * cos(tracked_rotation.x), 
		sin(tracked_rotation.x), 
		cos(tracked_rotation.y) * cos(tracked_rotation.x)
	)
	
	var distance = (global_transform.origin - star.global_transform.origin).length()
	var nextOrigin = star.global_transform.origin + direction * distance

	global_transform.origin = nextOrigin
	look_at(star.global_transform.origin, Vector3.UP)
