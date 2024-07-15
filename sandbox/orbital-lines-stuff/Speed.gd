extends Label

func _on_speed_slider_value_changed(value):
	self.text = "%d" % value
