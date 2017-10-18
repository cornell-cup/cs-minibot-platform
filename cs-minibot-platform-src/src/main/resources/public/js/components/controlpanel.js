var React = require('react');

export default class ControlPanel extends React.Component {
    //TODO: add listeners for Keyboard/Xbox controls, removing bot
    constructor(props) {
        super(props);
        this.state = {
            power: 50,
            discoveredBots: [],
        };

        this.handleInputChange = this.handleInputChange.bind(this);
        this.updateDiscoveredBots = this.updateDiscoveredBots.bind(this);
        this.sendKV = this.sendKV.bind(this);
        this.startLogging = this.startLogging.bind(this);
        this.sendMotors = this.sendMotors.bind(this);
        this.removeBot = this.removeBot.bind(this);
        this.xboxToggle = this.xboxToggle.bind(this);
    }

    handleInputChange(event) {
        const target = event.target;
        const value = target.value;
        const name = target.name;

        this.setState({
            [name]: value
        });
    }

    sendKV(event){
        //TODO
        const pow = this.state.power;
        const target = event.target;
        console.log('sendKV listener');
        if(target.id=="fwd") {
            this.sendMotors(pow, pow, pow, pow);
        }
        else if(target.id=="bck") {
            this.sendMotors(-pow, -pow, -pow, -pow);
        }
        else if(target.id=="lft") {
            this.sendMotors(-pow, pow, -pow, pow);
        }
        else if(target.id=="rt") {
            this.sendMotors(pow, -pow, pow, -pow);
        }
        else if(target.id=="cw"){
            this.sendMotors(pow, -pow, pow, -pow);
        }
        else if(target.id=="ccw"){
            this.sendMotors(-pow, pow, -pow, pow);
        }
        else if(target.id=="stop"){
            this.sendMotors(0,0,0,0);
        }
        else if(target.id=="log") {
            this.startLogging();
        }
        else {
            console.log("Clicked on a direction button but nothing has been executed.");
        }
    }

    sendMotors(a,b,c,d){
        //TODO
        console.log(a.toString()+b.toString()+c.toString()+d.toString());
    }

    startLogging(){
        //TODO
        console.log("logging data listener");
    }

    removeBot(){
        //     // ajax post to backend to remove a bot from list.
        //     $.ajax({
        //         method: "POST",
        //         url: '/removeBot',
        //         dataType: 'json',
        //         data: JSON.stringify({
        //             name: getBotID()
        //         }),
        //         contentType: 'application/json',
        //         success: function properlyRemoved(data) {
        //             console.log("removed bot");
        //         }
        //     });
    }

    xboxToggle(){
        // $('#xbox-on').click(function() {
        //     // ajax post to backend to remove a bot from list.
        //     $.ajax({
        //         method: "POST",
        //         url: '/runXbox',
        //         dataType: 'json',
        //         data: JSON.stringify({
        //             name: getBotID()
        //         }),
        //         contentType: 'application/json',
        //         success: function properlyRemoved(data) {
        //             console.log("TODO");
        //         }
        //     });
        // });
        //
        // $('#xbox-off').click(function() {
        //     // ajax post to backend to remove a bot from list.
        //     $.ajax({
        //         method: "POST",
        //         url: '/stopXbox',
        //         dataType: 'json',
        //         contentType: 'application/json',
        //         success: function properlyRemoved(data) {
        //             console.log("TODO");
        //         }
        //     });
        // });
    }


    updateDiscoveredBots(){
        //TODO
        //        $.ajax({
        //            method: "POST",
        //            url: '/discoverBots',
        //            dataType: 'json',
        //            data: '',
        //            contentType: 'application/json',
        //            success: function (data) {
        //                 //Check if discovered_bots and data are the same (check length and then contents)
        //                if(data.length != discovered_bots.length){
        //                    //If not then clear list and re-make displayed elements
        //                    redoDiscoverList(data);
        //                }
        //                else{
        //                    //Check value to ensure both structures contain the same data
        //                    for(let x=0;x<data.length;x++){
        //                        if(data[x]!=discovered_bots[x]){
        //                            redoDiscoverList(data);
        //                            //Prevent the list from being remade constantly
        //                            break;
        //                        }
        //                    }
        //                }
        //                setTimeout(updateDiscoveredBots,3000); // Try again in 3 sec
        //            }
        //        });
    }

    redoDiscoverList(data){
        let new_bots = [];

        for (let i = 0; i < data.length; i++) {
            //Trim the forward-slash
            var ip_address = data[i].substring(1);
            new_bots.push(ip_address)
        }
        this.setState({discoveredBots: new_bots})
    }

    render(){
        return (
            <div id ="component_controlpanel" className = "box">
                Control Panel<br/>
                <h4>Movement controls:</h4>
                {/*Choose bot:<br/>*/}
                {/*<select id="botlist" name="bots">*/}
                {/*<option value="">-- Choose a bot --</option>*/}
                {/*<option value="0">(DEBUG) Sim Bot</option>*/}
                {/*</select>*/}
                {/*<button className="controls" id="removeBot">Remove Bot</button><br/>*/}
                Power ({this.state.power}): <input id="power" type="range" name="power" min="0" max="100" value={this.state.power} defaultValue="50" onChange={this.handleInputChange}/><br/>
                <b>Directions:</b><br/>
                <table>
                    <tbody>
                    <tr>
                        <td className="controlgrid"><button className="btn" id="ccw" onClick={this.sendKV}>turn CCW</button></td>
                        <td className="controlgrid"><button className="btn" id="fwd" onClick={this.sendKV}>forward</button></td>
                        <td className="controlgrid"><button className="btn" id="cw" onClick={this.sendKV}>turn CW</button></td>
                    </tr>
                    <tr>
                        <td className="controlgrid"><button className="btn" id="lft" onClick={this.sendKV}>left</button></td>
                        <td className="controlgrid"><button className="btn btn-danger" id="stop" onClick={this.sendKV}>STOP</button></td>
                        <td className="controlgrid"><button className="btn" id="rt" onClick={this.sendKV}>right</button></td>
                    </tr>
                    <tr>
                        <td className="controlgrid"><button className="btn btn-success" id="log" onClick={this.sendKV}>log data</button></td>
                        <td className="controlgrid"><button className="btn" id="bck" onClick={this.sendKV}>backward</button></td>
                        <td className="controlgrid"></td>
                    </tr>
                    <tr>
                        <td>
                            Keyboard Controls <br/>
                            <label className="switch">
                                <input type="checkbox" id="keyboard-controls"/>
                                <span className="slider"></span>
                            </label>
                        </td>
                        <td></td>
                        <td>
                            Xbox Controls <br/>
                            <label className="switch">
                                <input type="checkbox" id="xbox-controls"/>
                                <span className="slider"></span>
                            </label>
                        </td>
                    </tr>
                    {/*<tr>*/}
                        {/*<td><input type="text" id="kv_key" placeholder="Key (e.g. WHEELS)"/></td>*/}
                        {/*<td><input type="text" id="kv_value" placeholder="Value (e.g. 10,10)"/></td>*/}
                        {/*<td><button id="sendkv" onClick={this.sendKV} className="btn btn-success">Send KV</button></td>*/}
                    {/*</tr>*/}
                    </tbody>
                </table>
            </div>
        )
    }
}

// $(document).ready(function() {
//     /*
//      * Event listener for key inputs. Sends to selected bot.
//      */
//     var lastKeyPressed;
//     window.onkeydown = function (e) {
//         let keyboardEnable = document.getElementById('keyboard-controls').checked;
//         if (!keyboardEnable) return;
//
//         let pow = getPower();
//         let code = e.keyCode ? e.keyCode : e.which;
//
//         if (code === lastKeyPressed) return;
//
//         if (code === 87) {
//             // w=forward
//             sendMotors(pow, pow, pow, pow);
//
//         } else if (code === 83) {
//             // s=backward
//             sendMotors(-pow, -pow, -pow, -pow);
//
//         } else if (code == 65) {
//             // a=ccw
//             sendMotors(-pow, pow, -pow, pow);
//
//         } else if (code == 68) {
//             // d=cw
//             sendMotors(pow, -pow, pow, -pow);
//
//         } else if (code == 81) {
//             // q=left
//             sendMotors(-pow, pow, pow, -pow);
//
//         } else if (code == 69) {
//             // e=right
//             sendMotors(pow, -pow, -pow, pow);
//         } else {
//             return;
//         }
//         lastKeyPressed = code;
//     };
//
//     window.onkeyup = function (e) {
//         let keyboardEnable = document.getElementById('keyboard-controls').checked;
//         if (!keyboardEnable) return;
//
//         let code = e.keyCode ? e.keyCode : e.which;
//
//         if (code === lastKeyPressed) {
//             // Stop
//             sendMotors(0,0,0,0);
//             lastKeyPressed = -1;
//         }
//     };
// });
//
// /*
// *   Send KV -- allows users to manually send key and value to bot (for debugging/testing
//     purposes)
// */
// function sendKV(){
//     $.ajax({
//         method:'POST',
//         url:'/sendKV',
//         dataType: 'json',
//         data: JSON.stringify({
//             key:$("#kv_key").val(),
//             value:$("#kv_value").val(),
//             name:getBotID()
//         }),
//         contentType: 'application/json'
//     });
// }
