/*
	Code generators for custom blocks.
*/

// ================ MOVE BLOCK ================ //
Blockly.Blocks['move'] = {
  init: function() {
    this.jsonInit(miniblocks.move);
  }
};

Blockly.Python['move'] = function(block) {
	// from blockly
  var dropdown_direction = block.getFieldValue('direction');
	var number_speed = block.getFieldValue('speed');
	
  //string representation of function
  var fcn = {
    fwd: "move_forward(",
    bkw: "move_backward("
  }[dropdown_direction];
	return [fcn+number_speed+")", Blockly.Python.ORDER_NONE];
};

// ================ TURN BLOCK ================ //
Blockly.Blocks['turn'] = {
  init: function() {
    this.jsonInit(miniblocks.turn);
  }
};

Blockly.Python['turn'] = function(block) {
  var dropdown_direction = block.getFieldValue('direction');
  var number_power = block.getFieldValue('power');
  // TODO: Assemble Python into code variable.
  var code = '...';
  // TODO: Change ORDER_NONE to the correct strength.
  return [code, Blockly.Python.ORDER_NONE];
};

// ================ SETPOWER BLOCK ================ //
Blockly.Blocks['setpower'] = {
  init: function() {
    console.log(miniblocks.setpower);
    this.jsonInit(miniblocks.setpower);
  }
};
Blockly.Python['setpower'] = function(block) {
  var statements_fl = Blockly.Python.statementToCode(block, 'FL');
  // TODO: Assemble Python into code variable.
  var code = '...\n';
  return code;
};

// ================ WHEELPOWER BLOCK ================ //
Blockly.Blocks['wheelpower'] = {
  init: function() {
    console.log(miniblocks.wheelpower);
    this.jsonInit(miniblocks.wheelpower);
  }
};

Blockly.Python['wheelpower'] = function(block) {
  var dropdown_wheel = block.getFieldValue('wheel');
  var number_power = block.getFieldValue('power');
  // TODO: Assemble Python into code variable.
  var code = '...\n';
  return code;
};