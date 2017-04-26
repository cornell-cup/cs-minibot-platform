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
/* Blockly Configurations */
var workspace = Blockly.inject('blocklyDiv',
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


/* ======================= USER FUNCTIONALITY ======================== */

/* DOWNLOAD FUNCTION 

  Allows users to download raw code as a file. Users must
  manually input file name and file type. */

// Prevents page from refreshing when download button is clicked.
$("#dwn").submit(function(event){ event.preventDefault(); });

// Download file as file-name manually inputted into textbox.
function download(filename, text) {
  var element = document.createElement('a');
  element.setAttribute('href', 'data:text/plain;charset=utf-8,' + encodeURIComponent(text));
  element.setAttribute('download', filename);

  element.style.display = 'none';
  document.body.appendChild(element);

  element.click();

  document.body.removeChild(element);
}

/* UPLOAD FUNCTION 

    Allows users to upload previously written code as a file
    so that they may run Python scripts that have been written
    externally without Blockly.

    TODO: possibly make it so that uploaded scripts can be also
    represented as blocks in the blockly view???

    */
$("#upload").change(function(event) {
  //console.log("upload change listener");
  var files = event.target.files;
  //console.log("files:" + files[0]);
  var reader = new FileReader();
  var f = files[0];
  reader.onload = (function(file) {
    return function(e) {
      setCode(e.target.result);
    }
  })(f);
  reader.readAsText(f);
});

/*
  RUN/SEND FUNCTION

  Clicking "run" will send Blockly scripts to the base station for
  the actual MiniBot.
*/
var pythonConverter = new Blockly.Generator("Python");

$("#send").click(sendBlockly);
function sendBlockly(event){
  $.ajax({
    method: "POST",
    url: '/uploadScript',
    dataType: 'json',
    data: JSON.stringify({
      name: $("#id").val(),
      script: getBlocklyScript()
    }),
    contentType: 'application/json'
  });
}

/* ======================= HELPER FUNCTIONS ======================== */
/* Returns a string of the entire blockly script. */
function getBlocklyScript() { return Blockly.Python.workspaceToCode(workspace); }
function setCode(code) { $("#data").val(code); }
function appendCode(code) {
  var content = $("#data").val();
  $("data").val(content + code);
}

