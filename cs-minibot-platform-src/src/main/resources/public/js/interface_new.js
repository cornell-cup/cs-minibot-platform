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
    return $("#data").val();
}

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

function startLogging() {
	$.ajax({
		method: "POST",
		url: "/logdata",
		data: JSON.stringify({
			name: getBotID()
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
	if(target.is("#fwd")) {
		sendMotors(pow, pow, pow, pow);
	}
	else if(target.is("#bck")) {
		sendMotors(-pow, -pow, -pow, -pow);
	}
	else if(target.is("#lft")) {
		sendMotors(-pow, pow, -pow, pow);
	}
	else if(target.is("#rt")) {
		sendMotors(pow, -pow, pow, -pow);
	}
	else if(target.is("#cw")){
		sendMotors(pow, -pow, pow, -pow);
	}
	else if(target.is("#ccw")){
		sendMotors(-pow, pow, -pow, pow);
	}
	else if(target.is("#stop")){
	    console.log("stop");
		sendMotors(0,0,0,0);
	}
	else if(target.is("#log")) {
	    startLogging();
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

$('#xbox-on').click(function() {
	// ajax post to backend to remove a bot from list.
	$.ajax({
		method: "POST",
		url: '/runXbox',
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

$('#xbox-off').click(function() {
	// ajax post to backend to remove a bot from list.
	$.ajax({
		method: "POST",
		url: '/stopXbox',
		dataType: 'json',
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

//adding a scenario from the value in the scenario viewer
$('#addScenario').click(function() {
    console.log("add scenario from interface.js")
    var scenario = $("#scenario").val();

    $.ajax({
        method: "POST",
        url: '/addScenario',
        dataType: 'text',
        data: JSON.stringify({
            scenario: scenario.toString()
        }),
        contentType: 'application/json; charset=utf-8',
        success: function (data){
            console.log("successfully added scenario: "+data);
        }
    });
 });

 //saving a scenario to a txt file with the specified filename
 $('#saveScenario').click(function() {
     console.log("saving a scenario")
     var scenario = $("#scenario").val();
     var filename = $("#scenarioname").val();

     $.ajax({
         method: "POST",
         url: '/saveScenario',
         dataType: 'text',
         data: JSON.stringify({scenario: scenario.toString(),
         name: filename.toString()}),
         contentType: 'application/json; charset=utf-8',
         success: function (data){
             console.log("successfully saved scenario: "+data);
         }
     });
  });

  /**loading a scenario - just type in a name, no need for directory or file
  extension*/
  $('#loadScenario').click(function() {
      active_bots = [];
      discovered_bots = [];

      var filename = $("#scenarioname").val();
      console.log("loading scenario: "+filename.toString());
      $.ajax({
          method: "POST",
          url: '/loadScenario',
          dataType: 'text',
          data: JSON.stringify({'name':filename.toString()}),
          contentType: 'application/json; charset=utf-8',
          success: function (data){
              $("#scenario").val(data);
              console.log("successfully loaded scenario: "+data);
          },
          error: function(data){
              console.log("error: please enter the name of an existing scenario")
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
    $("#discovered").empty();
    discovered_bots = [];

    for (let i = 0; i < data.length; i++) {
        //Trim the forward-slash
        var ip_address = data[i].substring(1);

        if(!active_bots.includes(ip_address)){
            var bot_ip = document.createElement('p');
            var add_ip = document.createElement('button');
            var next = document.createElement('break');

            var display_text = document.createTextNode(ip_address);
            var button_text = document.createTextNode("add bot");
            add_ip.setAttribute("id", i); //Use i instead of IP addresses b/c not string friendly
            add_ip.value = ip_address;
            add_ip.className = "discoverbot";

            //Append site elements
            discover_list.appendChild(bot_ip);
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
        //Get minibot's IP address
        var target = $(event.target); //$target
        var button_id = target[0]
        var bot_ip = button_id.value;
        var bot_idx = button_id.id;

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
        //Add to active_bots list
        active_bots.push(bot_ip);
        redoDiscoverList(discovered_bots);
    });
}


function listBots(){
	// lists all the bots
}

updateDiscoveredBots();

$(document).ready(function() {
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
});

/*
*   Send KV -- allows users to manually send key and value to bot (for debugging/testing
    purposes)
*/
function sendKV(){
    $.ajax({
        method:'POST',
        url:'/sendKV',
        dataType: 'json',
        data: JSON.stringify({
            key:$("#kv_key").val(),
            value:$("#kv_value").val(),
            name:getBotID()
        }),
        contentType: 'application/json'
    });
}