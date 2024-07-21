extends Control

signal speed_changed
signal zoom_changed

var speed: int = 1
var zoom: int = 200

@export
var camera: FollowCamera

var sim: GenericSystemNode3D

# Called when the node enters the scene tree for the first time.
func _ready():
	sim = get_node("../SolarSystem")
	
	# watch for the slider change and convert it into a speed_changed signal
	var speedSlider = get_node("ColorRect/MarginContainer/VBoxContainer/HBoxContainer/SpeedSlider")
	speedSlider.value_changed.connect(_on_speed_value_changed)
	
	var zoomSlider = get_node("ColorRect/MarginContainer/VBoxContainer/ZoomContainer/ZoomSlider")
	zoomSlider.value_changed.connect(_on_zoom_value_changed)
	
	# hook up the "speed_changed" signal to the Kotlin sim class
	speed_changed.connect(Callable(sim, "speed_changed"))
	
	# hook up the "speed_changed" signal to the Kotlin sim class
	#zoom_changed.connect(Callable(sim, "zoom_changed"))

func _on_speed_value_changed(val: float):
	speed = val as int
	speed_changed.emit(speed)
	
func _on_zoom_value_changed(val: float):
	zoom = val as int
	zoom_changed.emit(zoom)
	
	camera.set_camera_distance(zoom)

