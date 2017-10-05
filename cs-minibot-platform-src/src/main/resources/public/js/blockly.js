/*
*   BLOCKLY.JS
*   
*   Lauren Hsu, Celine Choo, and MiniBot team
*
*   Provides scripts for visual coding using Google Blockly API. Users can
*   drag and drop to write code that can be downloaded as a file or sent 
*   directly to the Base Station to be run on a MiniBot. Default language is
*   Python.
*/

/* ======================= BASIC SETUP ======================== */
var workspace;
var pythonConverter;
function setUpBlockly(){
    console.log("setup blockly called");
    workspace = Blockly.inject('blocklyDiv',
        {
            toolbox: document.getElementById('toolbox'),
            grid: {
                spacing:20,
                length:3,
                colour: '#ccc',
                snap: true
            },
            trashcan: true,
            scroll: true
        });

    /* Realtime code generation
      (Every drag/drop or change in visual code will be
      reflected in actual code view) */
    workspace.addChangeListener(function(event){
        setCode(getBlocklyScript());
    });
    pythonConverter = new Blockly.Generator("Python");
}


/* UPLOAD FUNCTION 

    Allows users to upload previously written code as a file
    so that they may run Python scripts that have been written
    externally without Blockly.

    TODO: possibly make it so that uploaded scripts can be also
    represented as blocks in the blockly view???

    */


/*
  RUN/SEND FUNCTION

  Clicking "run" will send Blockly scripts to the base station for
  the actual MiniBot.
*/

/* ======================= HELPER FUNCTIONS ======================== */
/* Returns a string of the entire blockly script. */
function getBlocklyScript() { return Blockly.Python.workspaceToCode(workspace); }
// function setCode(code) { $("#data").val(code); }
// function appendCode(code) {
//   var content = $("#data").val();
//   $("data").val(content + code);
// }

