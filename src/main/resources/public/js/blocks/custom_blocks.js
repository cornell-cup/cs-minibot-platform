/*
	Code generators for custom blocks.
*/

Blockly.Python['move'] = function(block) {
	var dir = block.getFieldValue('direction');
	var number_speed = block.getFieldValue('speed');
	// TODO: Assemble Python into code variable.
	var code = "move_forward("+number_speed+")";
	return [code, Blockly.Python.ORDER_NONE];
};

Blockly.Python['turn'] = function(block) {
  var dropdown_direction = block.getFieldValue('direction');
  var number_power = block.getFieldValue('power');
  // TODO: Assemble Python into code variable.
  var code = '...';
  // TODO: Change ORDER_NONE to the correct strength.
  return [code, Blockly.Python.ORDER_NONE];
};

Blockly.Python['setpower'] = function(block) {
  var statements_fl = Blockly.Python.statementToCode(block, 'FL');
  // TODO: Assemble Python into code variable.
  var code = '...\n';
  return code;
};

Blockly.Python['wheelpower'] = function(block) {
  var dropdown_wheel = block.getFieldValue('wheel');
  var number_power = block.getFieldValue('power');
  // TODO: Assemble Python into code variable.
  var code = '...\n';
  return code;
};