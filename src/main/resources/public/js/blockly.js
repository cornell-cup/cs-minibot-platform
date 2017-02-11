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

/* realtime code generation */
function myUpdateFunction(event) {
  var code = Blockly.Python.workspaceToCode(workspace);
  document.getElementById('textarea').value = code;
}
workspace.addChangeListener(myUpdateFunction);

/* downloading code */
$("#download").click(function(event) {
  event.preventDefault();
  window.open("data:application/txt," + encodeURIComponent($("#textarea").value), Blockly.JavaScript.workspaceToCode(workspace));
  //TODO: make download work
  console.log("bob");
});

/*
  Clicking "run" will send this to the base station.
*/
var pythonConverter = new Blockly.Generator("Python");

$("send").click(sendBlockly);
function sendBlockly(event){
  console.log("sendBlockly called");
  $.ajax({
    method: "POST",
    url: '/uploadScript',
    dataType: 'json',
    data: JSON.stringify({
      ip: getIP(),
      port: (getPort() || 10000),
      name: $("#id").val(),
      script: getBlocklyScript()
    }),
    contentType: 'application/json'
  });
}

/* Returns a string of the entire blockly script. */
function getBlocklyScript() {
  var script = $("#textarea").val();
  var pythonScript = pythonConverter.workspaceToCode(workspace);
  console.log(pythonScript);
  return pythonScript;
}