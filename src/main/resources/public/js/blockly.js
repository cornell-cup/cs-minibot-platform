//blockly config
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

/* Realtime code generation */
function myUpdateFunction(event) {
  //var code = Blockly.JavaScript.workspaceToCode(workspace);
  var code = getBlocklyScript();
  document.getElementById('data').value = code;
}
workspace.addChangeListener(myUpdateFunction);

/* DOWNLOAD ATTEMPT V1
$("#download").click(function(event) {
  event.preventDefault();
  window.open("data:application/txt," + encodeURIComponent($("#data").value), Blockly.JavaScript.workspaceToCode(workspace));
  //TODO: make download work
});

*/

/* DOWNLOAD ATTEMPT V2

function downloadScript1(){
  var script = getBlocklyScript();
  var scriptBlob = new Blob([script], {type:"text/plain"});
  var url = window.URL.createObjectURL(scriptBlob);

  var downloadLink = document.createElement("a");
  downloadLink.download = "my_blockly_script";
  downloadLink.innerHTML = "Download File";
  downloadLink.href = url;
  downloadLink.onclick = destroyClickedElement;
  downloadLink.style.display = "none";
  document.body.appendChild(downloadLink);

  downloadLink.click();
}

function downloadScript(){
  $("#data").dialog({
    autoOpen: false,
    modal: true,
    width:400,
    height:300,
    buttons{
      Save: function() {},
      Cancel: function() {$(this).dialog("close"); }
    }
  });
}

function destroyClickedElement(event) { document.body.removeChild(event.target); }

*/

/* DOWNLOAD ATTEMPT V3 */

function download(filename, text) {
  var element = document.createElement('a');
  element.setAttribute('href', 'data:text/plain;charset=utf-8,' + encodeURIComponent(text));
  element.setAttribute('download', filename);

  element.style.display = 'none';
  document.body.appendChild(element);

  element.click();

  document.body.removeChild(element);
}


/*
  Clicking "run" will send this to the base station.
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

/* Returns a string of the entire blockly script. */
function getBlocklyScript() {
  return Blockly.Python.workspaceToCode(workspace);
}

