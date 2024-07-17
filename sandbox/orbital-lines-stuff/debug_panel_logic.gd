extends Control

@export
var sim: Node3D

@export
var camera: FollowCamera

@onready
var bodySelectors = $Margin/BodySelection/BodySelectors

var checkboxTemplate = preload("res://template_checkbox.tscn")

func bodySelected(button: BaseButton):
	#print("bodySelected %s" % button.text)
	camera.followNode(button.text, false)

# Called when the node enters the scene tree for the first time.
func _ready():

	var sim2: GenericSystemNode3D = sim as GenericSystemNode3D
	
	#print("sim2.bodies_dict %s" % sim2.bodies_dict)
	#print("sim2.system_root.name %s" % sim2.system_root.name)
	#print("camera.sensitivity %s" % camera.sensitivity)
	#print("checkboxTemplate %s" % checkboxTemplate)
	
	var buttonGroup = ButtonGroup.new()
	buttonGroup.pressed.connect(bodySelected)
	
	for key in sim2.bodies_dict.keys():
		var checkbox: CheckBox = checkboxTemplate.instantiate()
		checkbox.toggle_mode = true
		
		if key == sim2.system_root.name:
			checkbox.button_pressed = true
		
		checkbox.text = key
		checkbox.button_group = buttonGroup
		bodySelectors.add_child(checkbox)
	
	


# Called every frame. 'delta' is the elapsed time since the previous frame.
func _process(delta):
	pass
