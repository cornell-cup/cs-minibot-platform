var React = require('react');
var ReactDOM = require('react-dom');
import { Tab, Tabs, TabList, TabPanel } from 'react-tabs';
//	<link rel="stylesheet" href="../css/vendor/prettify/prettify.css">
class Navbar extends React.Component {
    render () {
        return (
            <div className="navbar">
                <img className="logo" src = "../img/logo.png"/><h1>MiniBot GUI</h1>
            </div>
        )
    }
}

class Platform extends React.Component {
    render() {
        return (
            <div id='platform'>
                <Navbar/>
                <Tabs>
                    <TabList>
                        <Tab>Setup</Tab>
                        <Tab>Coding/Control</Tab>
                    </TabList>

                    <TabPanel>
                        <SetupTab />
                    </TabPanel>
                    <TabPanel>
                        <ControlTab />
                    </TabPanel>
                </Tabs>
            </div>
        )
    }
}
//Setup Tab
class SetupTab extends React.Component {
    render() {
        return (
            <div id ="tab_setup">
                <div className="row">
                    <div className="col-md-6">
                        <AddBot/>
                        <GridView/>
                    </div>
                    <div className="col-md-6">
                        <Scenarios/>
                    </div>
                </div>
            </div>
        )
    }
}

class Scenarios extends React.Component {
    //TODO
    render() {
        return(
            <div id ="component_scenarios" className = "box">Scenarios</div>
        )
    }
}

class AddBot extends React.Component {
    //TODO
    constructor(props) {
        super(props);
        this.state = {
            ip: "",
            port: "10000",
            name: "Bot0",
            type: "minibot"
        };

        this.handleInputChange = this.handleInputChange.bind(this);
        this.addbot = this.addbot.bind(this);
    }

    handleInputChange(event) {
        const target = event.target;
        const value = target.value;
        const name = target.name;

        this.setState({
            [name]: value
        });
    }
    addbot(e){
        //TODO
        console.log('addbot button clicked');
        // $.ajax({
        //     method: "POST",
        //     url: '/addBot',
        //     dataType: 'json',
        //     data: JSON.stringify({
        //         ip: getIP(),
        //         port: (getPort() || 10000),
        //         name: $("#name").val(),
        //         type: $('#bot-type').val()
        //     }),
        //     contentType: 'application/json',
        //     success: function addSuccess(data) {
        //         updateDropdown(true, data, data);
        //     }
        // });
    }
    render(){
        return (
            <div id ="component_addbot" className = "box">
                <div className = "row">
                    <div className = "col-md-6">
                        Add a Bot
                        <table>
                            <tbody>
                            <tr>
                                <th>IP: </th>
                                <th><input type="text" name="ip" id="ip" value={this.state.ip} onChange={this.handleInputChange}/></th>
                            </tr>
                            <tr>
                                <th>Port: </th>
                                <th><input type="text" name="port" id="port" value={this.state.port} onChange={this.handleInputChange}/></th>
                            </tr>
                            <tr>
                                <th>Name: </th>
                                <th><input type="text" name="name" id="name" value={this.state.name} onChange={this.handleInputChange}/></th>
                            </tr>
                            <tr>
                                <th>Type: </th>
                                <th>
                                    <select id="bot-type" name="type" value={this.state.type} onChange={this.handleInputChange}>
                                        <option value ="minibot">Minibot</option>
                                        <option value = "simulator.simbot">Simulated Minibot</option>
                                    </select>
                                </th>
                            </tr>
                            </tbody>
                        </table>
                        <button id="addBot" onClick={this.addbot}>Add Bot</button>
                    </div>
                    <div className = "col-md-6">
                        Select Active Bot
                    </div>
                </div>
            </div>
        )
    }
}

class GridView extends React.Component {
    //TODO
    componentDidMount(){
        //main();
    }
    render() {
        return(
            <div id ="component_view" className = "box">GridView</div>
        )
    }
}
//Control Tab
class ControlTab extends React.Component {
    render(){
        return (
            <div id ="tab_control">
                <div className="row">
                    <div className="col-md-6">
                        <Blockly/>
                        <GridView/>
                    </div>
                    <div className="col-md-6">
                        <Python/>
                        <ControlPanel/>
                    </div>
                </div>
            </div>
        )
    }
}


class ControlPanel extends React.Component {
    //TODO
    render(){
        return (
            <div id ="component_controlpanel" className = "box">Control Panel</div>
        )
    }
}

class Python extends React.Component {
    //TODO DOWNLOAD, STYLING
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
    }

    handleInputChange(event) {
        const target = event.target;
        const value = target.value;
        const name = target.name;

        this.setState({
            [name]: value
        });
        if (name=="data") {document.getElementById("data").value = this.state.data;}
    }

    download(event){
        /* DOWNLOAD FUNCTION
          Allows users to download raw code as a file. Users must
          manually input file name and file ext. */
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

    upload(event){
        console.log("upload change listener");
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

    send(){
        console.log("send listener");
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

    render(){
        return (
            <div id ="python" className ="box">
                Python
                <form id="dwn" onSubmit={this.download}>
                    File Name: <input type="text" name="filename" value={this.state.filename} onChange={this.handleInputChange}/>
                    <textarea name="data" cols="75" rows="20" id="data" value={this.state.data} onChange={this.handleInputChange}></textarea><br/>
                    <input type="submit" value="Download"/>
                </form>
                <button id="send" onClick={this.send}>Run Code</button>
                <form>
                    <input
                        type="file"
                        id="upload"
                        multipleSize="1"
                        accept=".py"
                    />
                </form>
            </div>
        )
    }
}

class Blockly extends React.Component {
    //TODO THIS DOES NOT WORK ???
    componentDidMount(){
        /* Blockly Configurations */
        setUpBlockly();
    }
    render(){
        return (
            <div id ="blocklyDiv" className = "box">Blockly</div>
        )
    }
}

ReactDOM.render(
    <Platform/>, document.getElementById('root')
);