extends Control

signal speed_changed

var speed: int = 1

var sim: GenericSystemNode3D

# Called when the node enters the scene tree for the first time.
func _ready():
	sim = get_node("../SolarSystem")
	
	# watch for the slider change and convert it into a speed_changed signal
	var slider = get_node("ColorRect/MarginContainer/VBoxContainer/HBoxContainer/SpeedSlider")
	slider.value_changed.connect(_on_value_changed)
	
	# hook up the "speed_changed" signal to the Kotlin sim class
	speed_changed.connect(Callable(sim, "speed_changed"))

func _on_value_changed(val: float):
	speed = val as int
	speed_changed.emit(speed)
