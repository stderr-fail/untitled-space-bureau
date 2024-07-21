extends Control

@export
var simNode: Node3D

var line = Line2D.new()

# Called when the node enters the scene tree for the first time.
func _ready():
	
	line.width = 3.0
	line.default_color = Color(Color.YELLOW, 0.3)
	line.closed = false
	
	add_child(line)



# Called every frame. 'delta' is the elapsed time since the previous frame.
func _process(delta):
	var sim = simNode as GenericSystemNode3D
	
	line.clear_points()
	
	if sim.maneuver_points.size() > 0:
		for p in sim.maneuver_points:
			line.add_point(p)
			

			
		
	
	
	
