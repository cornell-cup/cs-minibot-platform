/*
	Code generators for custom blocks.
*

Blockly.Python.math_arithmetic = function(a){
  var b={
      ADD:[" + ",Blockly.Python.ORDER_ADDITIVE],
      MINUS:[" - ",Blockly.Python.ORDER_ADDITIVE],
      MULTIPLY:[" * ",Blockly.Python.ORDER_MULTIPLICATIVE],
      DIVIDE:[" / ",Blockly.Python.ORDER_MULTIPLICATIVE],
      POWER:[" ** ",Blockly.Python.ORDER_EXPONENTIATION]
    }[a.getFieldValue("OP")],
    c=b[0],
    b=b[1],
    d=Blockly.Python.valueToCode(a,"A",b)||"0";
  a=Blockly.Python.valueToCode(a,"B",b)||"0";
  return[d+c+a,b]
};*/

var a = {
    "type": "move",
    "message0": "move %1 with %2 %% power",
    "args0": [
    {
      "type": "field_dropdown",
      "name": "direction",
      "options": [
          [
          "forward",
          "fwd"
          ],
          [
          "backwards",
          "bkw"
          ]
      ]
    },
    {
      "type": "field_number",
      "name": "speed",
      "value": 50,
      "min": 0,
      "max": 100
    }
    ],
    "output": "Boolean",
    "colour": 230,
    "tooltip": "",
    "helpUrl": ""
}

Blockly.Python['move'] = {
  init: function() {
    this.jsonInit(a);
    var thisBlock = this;
    this.setTooltip(function() {
      return 'Add a number to variable';
    });
  }
}

// Blockly.Python['move'] = function(block) {
//   console.log("move has been touched");
// 	// from blockly
//   var dropdown_direction = block.getFieldValue('direction');
// 	var number_speed = block.getFieldValue('speed');
	
//   //string representation of function
//   var fcn = {
//     fwd: "move_forward(",
//     bkw: "move_backward("
//   }[dropdown_direction];
// 	return [fcn+number_speed+")", Blockly.Python.ORDER_NONE];
// };

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