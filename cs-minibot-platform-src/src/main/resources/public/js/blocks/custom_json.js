/* 
	JSON configurations for custom blocks.

	Current custom blocks:
	- move
	- turn
	- set wheel power
  - wait
*/

var miniblocks = {
// MOVE
move: {
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
  },
 
// TURN 
turn: {
  "type": "turn",
  "message0": "turn %1 with %2 %% power",
  "args0": [
    {
      "type": "field_dropdown",
      "name": "direction",
      "options": [
        [
          "counterclockwise",
          "turn_counter_clockwise"
        ],
        [
          "clockwise",
          "turn_clockwise"
        ]
      ]
    },
    {
      "type": "field_number",
      "name": "power",
      "value": 50,
      "min": 0,
      "max": 100
    }
  ],
  "output": null,
  "colour": 230,
  "tooltip": "",
  "helpUrl": ""
},

// SET WHEELPOWER
setwheelpower: {
  "type": "setwheelpower",
  "message0": "set wheelpower %1 front left (%%) %2 front right (%%) %3 back left (%%) %4 back right (%%) %5",
  "args0": [
    {
      "type": "input_dummy",
      "align": "CENTRE"
    },
    {
      "type": "input_value",
      "name": "FL",
      "check": "Number",
      "align": "RIGHT"
    },
    {
      "type": "input_value",
      "name": "FR",
      "check": "Number",
      "align": "RIGHT"
    },
    {
      "type": "input_value",
      "name": "BL",
      "check": "Number",
      "align": "RIGHT"
    },
    {
      "type": "input_value",
      "name": "BR",
      "check": "Number",
      "align": "RIGHT"
    }
  ],
  "previousStatement": null,
  "nextStatement": null,
  "colour": 230,
  "tooltip": "",
  "helpUrl": ""
},

wait: {
  "type": "wait",
  "message0": "wait for %1 seconds",
  "args0": [
    {
      "type": "input_value",
      "name": "time",
      "check": "Number"
    }
  ],
  "previousStatement": null,
  "nextStatement": null,
  "colour": 230,
  "tooltip": "",
  "helpUrl": ""
}
};