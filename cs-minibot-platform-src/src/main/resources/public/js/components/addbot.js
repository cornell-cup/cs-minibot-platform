var React = require('react');

/**
 * Component for displaying each discovered bot
 *
 */
class DiscoveredBot extends React.Component {
    render() {
        var styles = {
            ipAddress: {
                float: "left"
            }
        }
        return (
            <div className="discoveredbot">
                <p style={styles.ipAddress}>{this.props.ip_address}</p>
                <button id={'discoverBot' + this.props.idx} value={this.props.ip_address} className="addBot">
                    add bot
                </button>
            </div>
        )
    }
}

/**
 * Component for the add bot interface
 *
 */
export default class AddBot extends React.Component {
    constructor(props) {
        super(props);
        this.state = {
            ip: "",
            port: "10000",
            name: "Bot0",
            type: "minibot",
            discoveredBots: []
        };

        this.handleInputChange = this.handleInputChange.bind(this);
        this.addbot = this.addbot.bind(this);
        this.updateDiscoveredBots = this.updateDiscoveredBots.bind(this);
    }

    /* handles input change for input fields */
    handleInputChange(event) {
        const target = event.target;
        const value = target.value;
        const name = target.name;

        this.setState({
            [name]: value
        });
    }

    /* Attempts to add a bot with the specified params
    * */
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

    /*
        Get set of discoverable minibots
    */
    updateDiscoveredBots(){
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
        var styles = {
            ActiveBotHeader: {
                height: '25%'
            },
            ActiveBotTitle: {
                float: "left"
            }
        }
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
                        <div className="activeBotHeader" style={styles.ActiveBotHeader}>
                            <p style={styles.ActiveBotTitle}>Select an Active Bot</p>
                            <button className="refresh-discovery" onClick={this.updateDiscoveredBots}>
                                Refresh
                            </button>
                        </div>
                        <div className="discovered-bot" id="discovered">
                            {
                                this.state.discoveredBots.map(function(ip, idx){
                                    return <DiscoveredBot key={idx} idx={idx} ip_address={ip} />;
                                })
                            }
                        </div>
                    </div>
                </div>
            </div>
        )
    }
}