extends MarginContainer

signal maneuverChanged

var maneuverData = ManeuverData.new(Vector3.ZERO, 100.0)

func _init():
	maneuverData.vec = Vector3.ZERO
	maneuverData.isp = 100.0
	
func fire_maneuver_changed():
	maneuverChanged.emit(maneuverData.vec, maneuverData.isp)

func is_valid_float(value: String) -> bool:
	var number = value.to_float()
	return not is_nan(number)

func _on_x_text_field_text_changed(new_text: String):
	if is_valid_float(new_text):
		maneuverData.vec.x = new_text.to_float()
		fire_maneuver_changed()

func _on_y_text_field_text_changed(new_text: String):
	if is_valid_float(new_text):
		maneuverData.vec.y = new_text.to_float()
		fire_maneuver_changed()

func _on_z_text_field_text_changed(new_text: String):
	if is_valid_float(new_text):
		maneuverData.vec.z = new_text.to_float()
		fire_maneuver_changed()

func _on_isp_text_field_text_changed(new_text: String):
	if is_valid_float(new_text):
		maneuverData.isp = new_text.to_float()
		fire_maneuver_changed()
