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
  var wheels = ['FL', 'FR', 'BL', 'BR']
  var power = [0,0,0,0];

  // dealing with wrong inputs
  for(var i=0; i<4; i++){
    power[i] = Blockly.Python.valueToCode(block, wheels[i], Blockly.Python.ORDER_ATOMIC) || 0;
    if(power[i] < 100) {
    }
    else if(power[i] > 100) {
      alert("Oops! Please insert a number between 0 and 100.");
      power[i] = 100;
    }
    else {
      alert("Oops! Please insert a number between 0 and 100.");
      power[i] = 0;
    }
  }
  var code = 'set_wheel_power(' 
    + power[0] + ',' 
    + power[1] + ',' 
    + power[2] + ',' 
    + power[3] + ')';
  return [code, Blockly.Python.ORDER_NONE];
};

// ================== WAIT BLOCK ================== //
Blockly.Blocks['wait'] = {
  init: function(){
    this.jsonInit(miniblocks.wait);
  }
};
Blockly.Python['wait'] = function(block) {
  var time = Blockly.Python.valueToCode(block, 'time', Blockly.Python.ORDER_ATOMIC) || 0;
  var code = 'wait(' + time + ')';
  return [code, Blockly.Python.ORDER_NONE];
};