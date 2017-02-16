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

function sendMotors(fl, fr, bl, br) {
	$.ajax({
		method: "POST",
		url: "/commandBot",
		data: JSON.stringify({
			botID: getBotID(),
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
    			botID: getBotID(),
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

/* Eventlistener for mouseclick on controls (adding, removing, etc.) */
$(".controls").click(function(event) {
	event.preventDefault();

	if($(event.target).is("#removeBot")){
		manageBots("/removeBot", $("#id").val(), $("#port").val());
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
			ip: getIP(),
			port: (getPort() || 10000),
			name: $("#id").val()
		}),
		contentType: 'application/json',
		success: function properlyRemoved(data) {
			updateDropdown(false, data, data);
		}
	});
});

// when adding a bot
$('#addBot').click(function() {
    $.ajax({
        method: "POST",
        url: '/addBot',
        dataType: 'json',
        data: JSON.stringify({
                ip: getIP(),
                port: (getPort() || 10000),
                name: $("#id").val(),
                type: $('#bot-type').val()
            }),
        contentType: 'application/json',
        success: function addSuccess(data) {
            updateDropdown(true, data, data);
        }
    });
});

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

/* Helper function called from the eventlistener
*/
function manageBots(option, ip, port){
	$.ajax({
		method: "POST",
		url: getIP() + option,
		data: JSON.stringify({
			ip: ip,
			port: port
		}),
		processData: false,
		contentType: 'application/json'
	});
	//update(false);

	//TODO: do something that adds or removes the robot.
}

function listBots(){
	// lists all the bots
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