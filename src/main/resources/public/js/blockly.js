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
  document.getElementById('textarea').value = code;
}
workspace.addChangeListener(myUpdateFunction);

/* downloading code */
$("#download").click(function(event) {
  event.preventDefault();
  window.open("data:application/txt," + encodeURIComponent($("#textarea").value), Blockly.JavaScript.workspaceToCode(workspace));
  //TODO: make download work
});

function downloadScript(){
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

function destroyClickedElement(event) { document.body.removeChild(event.target); }

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