extends Label


func _on_zoom_slider_value_changed(value):
	self.text = "%d" % value
