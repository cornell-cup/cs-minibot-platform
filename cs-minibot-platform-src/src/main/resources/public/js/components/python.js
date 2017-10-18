var React = require('react');
var ReactDOM = require('react-dom');

/**
 * Component for the Python text box
 * Contains:
 * upload, download, send script
 */
export default class Python extends React.Component {
    //TODO UPLOAD, CAPTURE TAB KEY AND PREVENT DEFAULT ACTION, SEND SCRIPT
    constructor(props) {
        super(props);
        this.state = {
            filename:"myBlocklyCode.py",
            data:""
        };

        this.handleInputChange = this.handleInputChange.bind(this);
        this.download = this.download.bind(this);
        this.upload = this.upload.bind(this);
        this.send = this.send.bind(this);
        this.handleKeyInput = this.handleKeyInput.bind(this);
    }

    /* handles input change for file name and coding textboxes */
    handleInputChange(event) {
        const target = event.target;
        const value = target.value;
        const name = target.name;

        this.setState({
            [name]: value
        });
        if (name=="data") {document.getElementById("data").value = this.state.data;}
    }

    /* DOWNLOAD FUNCTION
       Allows users to download raw code as a file. Users must
       manually input file name and file ext.
    */
    download(event){

        console.log("download listener");
        event.preventDefault();
        var element = document.createElement('a');
        element.setAttribute('href', 'data:text/plain;charset=utf-8,' + encodeURIComponent(this.state.data));
        element.setAttribute('download', this.state.filename);
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
    upload(event){
        console.log("upload listener");
        var files = event.target.files;
        var reader = new FileReader();
        var f = files[0];
        // reader.onload = (function(this.state.file) {
        //     return function(e) {
        //         this.state.code = e.target.result;
        //     }
        // })(f);
        // reader.readAsText(f);
    }

    /* Handler for key input; allows for tabs (4 spaces!!) in text box */
    handleKeyInput(event){
        if(event.keyCode===9){
            event.preventDefault();
            var data = document.getElementById("data");
            var v=data.value;
            var s=data.selectionStart;
            var e=data.selectionEnd;
            data.value=v.substring(0, s)+'    '+v.substring(e);
            data.selectionStart=this.selectionEnd=s+1;
        }
    }

    /*
      RUN/SEND FUNCTION
      Clicking "run" will send Blockly scripts to the base station for
      the actual MiniBot.
    */
    send(){
        console.log("send listener");
        // $.ajax({
        //     method: "POST",
        //     url: '/uploadScript',
        //     dataType: 'json',
        //     data: JSON.stringify({
        //         name: $("#id").val(),
        //         script: getBlocklyScript()
        //     }),
        //     contentType: 'application/json'
        // });
    }

    render(){
        return (
            <div id ="python" className ="box">
                Python
                File Name: <input type="text" name="filename" value={this.state.filename} onChange={this.handleInputChange}/><br/>
                <textarea name="data" id="data" value={this.state.data} onChange={this.handleInputChange} onKeyDown={this.handleKeyInput} ></textarea><br/>
                    <button id="submit" onClick={this.download}>Download</button>
                <button id="send" onClick={this.send}>Run Code</button>
                <form>
                    <input
                        type="file"
                        id="upload"
                        multiplesize="1"
                        accept=".py"
                    />
                </form>
            </div>
        )
    }
}

// /* ======================= USER FUNCTIONALITY ======================== */
//
// /* DOWNLOAD FUNCTION
//
//   Allows users to download raw code as a file. Users must
//   manually input file name and file type. */
//
// // Prevents page from refreshing when download button is clicked.
// $("#dwn").submit(function(event){ event.preventDefault(); });
//
// // Download file as file-name manually inputted into textbox.
// function download(filename, text) {
//     var element = document.createElement('a');
//     element.setAttribute('href', 'data:text/plain;charset=utf-8,' + encodeURIComponent(text));
//     element.setAttribute('download', filename);
//
//     element.style.display = 'none';
//     document.body.appendChild(element);
//
//     element.click();
//
//     document.body.removeChild(element);
// }
//
// /* UPLOAD FUNCTION
//
//     Allows users to upload previously written code as a file
//     so that they may run Python scripts that have been written
//     externally without Blockly.
//
//     TODO: possibly make it so that uploaded scripts can be also
//     represented as blocks in the blockly view???
//
//     */
// $("#upload").change(function(event) {
//     //console.log("upload change listener");
//     var files = event.target.files;
//     //console.log("files:" + files[0]);
//     var reader = new FileReader();
//     var f = files[0];
//     reader.onload = (function(file) {
//         return function(e) {
//             setCode(e.target.result);
//         }
//     })(f);
//     reader.readAsText(f);
// });
//
// /*
//   RUN/SEND FUNCTION
//
//   Clicking "run" will send Blockly scripts to the base station for
//   the actual MiniBot.
// */
// var pythonConverter = new Blockly.Generator("Python");
//
// $("#send").click(sendBlockly);
// function sendBlockly(event){
//     $.ajax({
//         method: "POST",
//         url: '/uploadScript',
//         dataType: 'json',
//         data: JSON.stringify({
//             name: $("#id").val(),
//             script: getBlocklyScript()
//         }),
//         contentType: 'application/json'
//     });
// }
//
// /* ======================= HELPER FUNCTIONS ======================== */
// /* Returns a string of the entire blockly script. */

// function appendCode(code) {
//     var content = $("#data").val();
//     $("data").val(content + code);
// }
