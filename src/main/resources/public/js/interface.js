/*
INTERFACE.

Pages and functions:
- getters
	- get ID
	- get IP
	- get power
- bot configs
	- addBot
	- removeBot
	- listBots
- bot dynamics
	- commandBot
	- ...
*/

$("#ip").value = document.URL;
active_bots = [];
discovered_bots = [];

/* Getters */
function getIP(){
	return $("#ip").val();
}

function getPort(){
	return $("#port").val();
}

function getPower(){
	return $("#power").val();
}

function getBotID() {
    return $("#botlist").val();
}

function getScript() {
    return $("#textarea").val();
}

//function getActiveBots(){
//    return active_bots;
//}
//
//function getDiscoveredBots(){
//    return discovered_bots;
//}

function sendMotors(fl, fr, bl, br) {
	$.ajax({
		method: "POST",
		url: "/commandBot",
		data: JSON.stringify({
			name: getBotID(),
			fl: fl,
			fr: fr,
			bl: bl,
			br: br
		}),
		processData: false,
		contentType: 'application/json'
	});
}

function sendScript() {
    $.ajax({
    		method: "POST",
    		url: "/sendScript",
    		data: JSON.stringify({
    			name: getBotID(),
    			script: getScript()
    		}),
    		processData: false,
    		contentType: 'application/json'
    	});
}

$("#send").click(function(event) {
    sendScript();
});

/* When .dir is clicked, send motors to act based on button clicked. */
$(".dir").click(function(event) {
	var pow = getPower();
	var target = $(event.target); //$target
	console.log(target);

	if(target.is("#fwd")) {
		sendMotors(pow, pow, pow, pow);
	}
	else if(target.is("#bck")) {
		sendMotors(-pow, -pow, -pow, -pow);
	}
	else if(target.is("#lft")) {
		sendMotors(-pow, pow, pow, -pow);
	}
	else if(target.is("#rt")) {
		sendMotors(pow, -pow, -pow, pow);
	}
	else if(target.is("#cw")){
		sendMotors(pow, -pow, pow, -pow);
	}
	else if(target.is("#ccw")){
		sendMotors(-pow, pow, -pow, pow);
	}
	else if(target.is("#stop")){
		sendMotors(0,0,0,0);
	}
	else {
		console.error("Clicked on a direction button but nothing has been executed.");
	}
});

// when removing a bot
$('#removeBot').click(function() {
	// ajax post to backend to remove a bot from list.
	$.ajax({
		method: "POST",
		url: '/removeBot',
		dataType: 'json',
		data: JSON.stringify({
			name: getBotID()
		}),
		contentType: 'application/json',
		success: function properlyRemoved(data) {
		    console.log("TODO");
		}
	});
});

// when adding a bot
$('#addBot').click(function() {
    console.log("addbot from interface.js")
    $.ajax({
        method: "POST",
        url: '/addBot',
        dataType: 'json',
        data: JSON.stringify({
                ip: getIP(),
                port: (getPort() || 10000),
                name: $("#name").val(),
                type: $('#bot-type').val()
            }),
        contentType: 'application/json',
        success: function addSuccess(data) {
            updateDropdown(true, data, data);
        }
    });
});

// When adding a discovered bot
//$('.discoverbot').click(function(event) {
//    console.log("Boop");
//    //Get minibot's IP address
//    var target = $(event.target); //$target
//    var bot_ip = target.getAttribute("value");
//
//    //Add to active_bots list
//    active_bots.push(bot_ip);
//
//    //Remove from discovered_bots list, display refactored later
//    for(let i=0; i<discovered_bots.length; i++){
//        //If the IP addresses match, remove that element in discovered_bots
//        if(discovered_bots[i]==bot_ip){
//            splice.discovered_bots(i,i+1);
//        }
//    }
//});
//
/*
	For any update to the list of active bots, the dropdown menu
	of active bots will update accordingly (depending on the addition
	or removal of a bot).
*/

function updateDropdown(toAdd, text, val) {
	// if adding to update
	if(toAdd) { 
		var opt = document.createElement('option');
	    opt.text = text;
	    opt.value = val;
	    opt.className = "blist";
	    var botlist = document.getElementById("botlist");
		botlist.appendChild(opt);
	}

	// if removing to update
    else { 
    	var allBots = $("#botlist").getElementsByTagName("*");
    	var removed = false;
    	for(var i=0; i<allBots.length && !removed; i++) {
    		if(allBots[i].text === text) {
    			$("#botlist").removeChild(allBots[i]);
    			removed = true;
    		}
    	}
    }
}

function redoDropdown(data) {
    $('#botlist').empty();
    for (let i = 0; i < data.length; i++) {
		var opt = document.createElement('option');
	    opt.text = data[i].name;
	    opt.value = data[i].name;
	    opt.className = "blist";
	    var botlist = document.getElementById("botlist");
		botlist.appendChild(opt);
    }
}

/* Helper function called from the eventlistener
*/
function manageBots(option, name){
	$.ajax({
		method: "POST",
		url: getIP() + option,
		data: JSON.stringify({
			name: name
		}),
		processData: false,
		contentType: 'application/json'
	});
}

/*
    Get set of discoverable minibots
*/
function updateDiscoveredBots(){
    $.ajax({
        method: "POST",
        url: '/discoverBots',
        dataType: 'json',
        data: '',
        contentType: 'application/json',
        success: function (data) {
            console.log(data);
             //Check if discovered_bots and data are the same (check length and then contents)
            if(data.length != discovered_bots.length){
                //If not then clear list and re-make displayed elements
                redoDiscoverList(data);
            }
            else{
                //Check value to ensure both structures contain the same data
                for(let x=0;x<data.length;x++){
                    if(data[x]!=discovered_bots[x]){
                        redoDiscoverList(data);
                        //Prevent the list from being remade constantly
                        break;
                    }
                }
            }
            setTimeout(updateDiscoveredBots,3000); // Try again in 3 sec
        }
    });
}

/*
    Recreates the display of discovered minibots
*/
function redoDiscoverList(data){
    var discover_list = document.getElementById("discovered");

    //Clear all child elements from the display list
    //discover_list.empty();
    $("#discovered").empty();
    discovered_bots = [];

    for (let i = 0; i < data.length; i++) {
        //Create elements for the site
        //if(!active_bots.includes(data[i]) && !discovered_bots.includes(data[i])){

        //Trim the forward-slash
        var ip_address = data[i].substring(1);

        if(!active_bots.includes(ip_address)){
            var bot_ip = document.createElement('p');
            var add_ip = document.createElement('button');
            var next = document.createElement('break');

            //Trim the forward-slash
            var ip_address = data[i].substring(1);

            //bot_ip.idName = data[i];
            //bot_ip.id = ip_address;
            //bot_ip.setAttribute("id", ip_address);
            //bot_ip.attr("id", ip_address);
            //console.log(bot_ip.id);
            //bot_ip.text = ip_address;
            var display_text = document.createTextNode(ip_address);
            //add_ip.idName = data[i];
            //add_ip.text = "Add bot";
            //add_ip.attr("id", ip_address);
            //add_ip.setAttribute("id", ip_address);
            add_ip.setAttribute("id", i); //Use i instead of IP addresses b/c not string friendly
            add_ip.value = ip_address;
            add_ip.className = "discoverbot";
            //add_ip.text = "Add bot"
            var button_text = document.createTextNode("add bot");

            //Append site elements
            discover_list.appendChild(bot_ip);
            //discover_list.appendChild(ip_address);
            bot_ip.appendChild(display_text);
            bot_ip.appendChild(add_ip);
            bot_ip.appendChild(next);
            add_ip.appendChild(button_text);

            //Add minibot address to discovered list
            discovered_bots.push(ip_address);
        }
    }

    /*Listener created repeatedly here, because listener only bound to elements
    * that CURRENTLY have the class, doesn't account for future elements with that class
    */
    $('.discoverbot').click(function(event) {
        //console.log("Boop");
        //Get minibot's IP address
        var target = $(event.target); //$target
        //var bot_ip = target.getAttribute("value");
        //var bot_ip = target.value;
        //var bot_idx = target.id;
        //var bot_ip = discovered_bots[bot_idx];
        var button_id = target[0]
        var bot_ip = button_id.value;
        var bot_idx = button_id.id;
        //console.log(target);
        //console.log(target[0]);
        //console.log(target[0].value);
        //console.log(target[1]);
        //console.log(bot_idx);
        //console.log(active_bots);

        console.log(bot_ip);

        //POST request to base station

        $.ajax({
            method: "POST",
            url: '/addBot',
            dataType: 'json',
            data: JSON.stringify({
                ip: bot_ip,
                port: 10000,
                name: bot_ip+"(discovered)",
                type: "minibot"
            }),
            contentType: 'application/json',
            success: function addSuccess(data) {
                console.log("Success!");
                updateDropdown(true, data, data);
            }
        });

        //

        //Add to active_bots list
        active_bots.push(bot_ip);
//        console.log(active_bots);
//        console.log("Myurp");

        //console.log(discovered_bots);
        //Remove from discovered_bots list, display refactored later
        //discovered_bots.splice(bot_idx,1);
        //console.log(discovered_bots);

        redoDiscoverList(discovered_bots);
        //listBots();

//        for(let i=0; i<discovered_bots.length; i++){
//            //If the IP addresses match, remove that element in discovered_bots
//            if(discovered_bots[i]==bot_ip){
//                //splice.discovered_bots(i,i+1);
//                console.log(discovered_bots);
//                discovered_bots.splice(i,1);
//                console.log(discovered_bots);
//
//            }
//        }
    });
}

function listBots(){
	// lists all the bots
//	console.log("Display things!")
//    var active_list = document.getElementById("active");
//
//    //Clear all child elements from the display list
//    //discover_list.empty();
//    $("#active").empty();
//
//    for (let i = 0; i < active_bots.length; i++) {
//        //Create elements for the site
//        var bot_ip = document.createElement('p');
//        //var next = document.createElement('break');
//
//        var ip_address = active_bots[i];
//        var display_text = document.createTextNode(ip_address);
//
//        //add_ip.text = "Add bot";
//        //add_ip.value = ip_address;
//        //add_ip.className = "discoverbot";
//
//        //Append site elements
//        active_list.appendChild(bot_ip);
//        bot_ip.appendChild(display_text);
//        //bot_ip.appendChild(next);
//
//    }
//
//    setTimeout(listBots,2000); // Try again in 5 sec
}

/*
 * Event listener for key inputs. Sends to selected bot.
 */
var lastKeyPressed;
window.onkeydown = function (e) {
    let keyboardEnable = document.getElementById('keyboard-controls').checked;
    if (!keyboardEnable) return;

    let pow = getPower();
    let code = e.keyCode ? e.keyCode : e.which;

    if (code === lastKeyPressed) return;

    if (code === 87) {
       // w=forward
       sendMotors(pow, pow, pow, pow);

    } else if (code === 83) {
       // s=backward
       sendMotors(-pow, -pow, -pow, -pow);

    } else if (code == 65) {
    	// a=ccw
        sendMotors(-pow, pow, -pow, pow);

    } else if (code == 68) {
    	// d=cw
        sendMotors(pow, -pow, pow, -pow);

    } else if (code == 81) {
    	// q=left
        sendMotors(-pow, pow, pow, -pow);

    } else if (code == 69) {
    	// e=right
        sendMotors(pow, -pow, -pow, pow);
    } else {
        return;
    }
    lastKeyPressed = code;
};

window.onkeyup = function (e) {
    let keyboardEnable = document.getElementById('keyboard-controls').checked;
    if (!keyboardEnable) return;

    let code = e.keyCode ? e.keyCode : e.which;

    if (code === lastKeyPressed) {
        // Stop
       sendMotors(0,0,0,0);
       lastKeyPressed = -1;
    }
};

updateDiscoveredBots();
//listBots();