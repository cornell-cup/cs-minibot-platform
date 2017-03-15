/* 
	JSON configurations for custom blocks.

	Current custom blocks:
	- move
	- turn
	- set wheel power
	- wheel power percentage stub
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
          "CCW"
        ],
        [
          "clockwise",
          "CW"
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

// SETPOWER
setpower: {
  "type": "setpower",
  "message0": "set wheel power %1 %2",
  "args0": [
    {
      "type": "input_dummy"
    },
    {
      "type": "input_statement",
      "name": "FL",
      "check": "wheelpower"
    }
  ],
  "previousStatement": null,
  "nextStatement": null,
  "colour": 120,
  "tooltip": "",
  "helpUrl": ""
},

//WHEELPOWER
wheelpower: {
  "type": "wheelpower",
  "message0": "%1 %2 %%",
  "args0": [
    {
      "type": "field_dropdown",
      "name": "wheel",
      "options": [
        [
          "front left",
          "FL"
        ],
        [
          "front right",
          "FR"
        ],
        [
          "back left",
          "BL"
        ],
        [
          "back right",
          "BR"
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
  "previousStatement": null,
  "nextStatement": "wheelpower",
  "colour": 230,
  "tooltip": "",
  "helpUrl": ""
}
};