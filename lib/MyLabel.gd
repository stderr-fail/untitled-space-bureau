extends Label

var count = 0

# Called when the node enters the scene tree for the first time.
func _ready():
	pass # Replace with function body.


var format_string = "gcount: {count}"


# Called every frame. 'delta' is the elapsed time since the previous frame.
func _process(delta):
	text = format_string.format({"count": count})
	count += 1
