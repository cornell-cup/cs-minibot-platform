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
  var code = dropdown_direction+"("+number_power+")";
  return [code, Blockly.Python.ORDER_NONE];
};

// ================ SET WHEELPOWER BLOCK ================ //
Blockly.Blocks['setwheelpower'] = {
  init: function() {
    this.jsonInit(miniblocks.setwheelpower);
  }
};
Blockly.Python['setwheelpower'] = function(block) {
  var power = [
    Blockly.Python.valueToCode(block, 'FL', Blockly.Python.ORDER_ATOMIC) || 0,
    Blockly.Python.valueToCode(block, 'FR', Blockly.Python.ORDER_ATOMIC) || 0,
    Blockly.Python.valueToCode(block, 'BL', Blockly.Python.ORDER_ATOMIC) || 0,
    Blockly.Python.valueToCode(block, 'BR', Blockly.Python.ORDER_ATOMIC) || 0
  ]

  // dealing with wrong inputs
  for(var i=0; i<4; i++){
    if(parseInt(power[i])<0) power[i] = 0;
    else if(power[i]>100) power[i] = 100;
  }
  var code = 'set_wheel_power(' 
    + power[0] + ',' 
    + power[1] + ',' 
    + power[2] + ',' 
    + power[3] + ')';
  return [code, Blockly.Python.ORDER_NONE];
};