var React = require('react');
var ReactDOM = require('react-dom');
import { Tab, Tabs, TabList, TabPanel } from 'react-tabs';

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
            <div id ="setuptab">
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
            <div id ="scenarios" className = "box">Scenarios</div>
        )
    }
}

class AddBot extends React.Component {
    //TODO
    render(){
        return (
            <div id ="addbot" className = "box">AddBot</div>
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
            <div id ="view" className = "box">GridView</div>
        )
    }
}
//Control Tab
class ControlTab extends React.Component {
    render(){
        return (
            <div id ="controltab">
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

class GridView2 extends React.Component {
    //TODO
    componentDidMount(){
        //main();
    }
    render() {
        return(
            <div id ="view2" className = "box">GridView</div>
        )
    }
}

class ControlPanel extends React.Component {
    //TODO
    render(){
        return (
            <div id ="controlpanel" className = "box">Control Panel</div>
        )
    }
}

class Python extends React.Component {
    //TODO DOWNLOAD, STYLING
    render(){
        return (
            <div id ="python" className ="box">
                Python
                <form id="dwn" onsubmit="download(this['name'].value, this['data'].value)">
                    <input type="text" name="name" value="myBlocklyCode.py"/>
                        <textarea name="data" size="100" cols="100" rows="10" id="data"></textarea><br/>
                        <input type="submit" value="Download"/>
                </form>
                <button id="send">Run</button>
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
    //TODO LATER
    render(){
        return (
            <div id ="blocklyDiv" className = "box">Blockly</div>
        )
    }
}

ReactDOM.render(
    <Platform/>, document.getElementById('root')
);