/* 
VIEW.JS

All code associated with the view portion of the modbot web GUI. The view uses
pixi.js to display the bots.

Bots have coordinates (x,y) in which (0,0) is at the top left. Clicking on "zoom out"
will display the bots such that all bots are visible on the view (in case some move beyond
the current vision).

Bots have four fields: x coordinate, y coordinate, angle, and id.
*/

const MILLIS_PER_VISION_UPDATE = 33; // modbot update interval in ms
var bots = [];           // hard-coded bots for testing];

const VIEW_WIDTH =  520; //size of simulator display in pixels
const START_SCALE = 4; //number of meters displayed by simulator at start, 4 is 4x4 meters

var x_int = VIEW_WIDTH/START_SCALE; // actual spacing between grid lines
var y_int = VIEW_WIDTH/START_SCALE;

var scale = 100; //percentage, 100% is equal to 4x4 grid displayed, with each grid being 130pixels wide
var xOffset = 0; //for purposes of adjusting viewport, this is a raw pixel value
var yOffset = 0; //same as above

var stage; // pixi elements for displaying information
var back;
var botContainer;
var gridContainer;
var grid;
var imageLoader;

var listBots = [];
var occupancyMatrix = null;
var path = null;
var foundPath = false;
var omPresent = false;

var backgroundSprite;

$('#scale').on('change',function(){
    var val = $(this).val();
    scale=val;
    x_int = VIEW_WIDTH/START_SCALE*val/100;
    y_int = VIEW_WIDTH/START_SCALE*val/100;
    updateInfo(x_int, y_int);


    stage.removeChild(gridContainer);
    gridContainer = new PIXI.Container();
    botContainer.removeChildren();
    setupGridLines(scale, xOffset, yOffset);

    stage.addChild(gridContainer);
//    if(occupancyMatrix !== null) {
//        displayOccupancyMatrix(40, 40, 1.0);
//
//    }
displayBots(bots,scale, xOffset, yOffset);
    fillOccupancyMatrix(scale, xOffset, yOffset);

    grid.render(stage);
});

/* for moving the viewport */
document.onkeydown = function (e) {
    let code = e.keyCode ? e.keyCode : e.which;

    if (code === 37) {
       //move view left
       xOffset+=3;
    } else if (code === 39) {
       //move view right
       xOffset-=3;

    } else if (code == 38) {
    	//move view up
        yOffset+=3;

    } else if (code == 40) {
    	// move view down
        yOffset-=3;
    } else {
        return;
    }

    gridContainer.removeChildren();
    botContainer.removeChildren();
    setupGridLines(scale, xOffset, yOffset);
//    displayOccupancyMatrix(40, 40, 1.0);

    displayBots(bots, scale, xOffset, yOffset);
    fillOccupancyMatrix(scale, xOffset, yOffset);

    grid.render(stage);
};


function main() {
    updateInfo(x_int, y_int);

    botContainer = new PIXI.Container();
    gridContainer = new PIXI.Container();

    stage = new PIXI.Container();
    back = new PIXI.Container();
    grid = PIXI.autoDetectRenderer(520, 520);

    $("#view").append(grid.view);

    var loadUrl = 'img/line.png';
    imageLoader = PIXI.loader;
    imageLoader.add('background', loadUrl);
    imageLoader.once("complete", imageLoaded);
    imageLoader.load();
}

function imageLoaded(){
    var background = PIXI.Texture.fromImage('/img/line.png');
    var backgroundTexture =  background;
    backgroundSprite = new PIXI.Sprite(backgroundTexture);
    backgroundSprite.scale.x =  1300*scale/100;
    backgroundSprite.scale.y =  1300*scale/100;

    backgroundSprite.position.x = 0;
    backgroundSprite.position.y = 0;

    back.addChild(backgroundSprite);
    stage.addChild(back);
    stage.addChild(botContainer);
    stage.addChild(gridContainer);


    grid.view.style.border = "1px dashed black";
    grid.view.style.position = "absolute";
    grid.view.style.display = "block";
    grid.render(stage);

    setupGridLines(scale, xOffset, yOffset);
    displayBots(bots,scale, xOffset, yOffset);
  
    getNewVisionData();
    pollBotNames();
}

/* pseudo-constructor for a bot object */
function newBot(x, y, angle, id, size) {
    var bot = {
        x: x,
        y: y,
        angle: angle, // radians
        id: id,
        size: size
    };
    /*in the future add something to identify bots vs objects*/
    if (size==0.15){
    bot.type = 'bot';}
    else{
    bot.type = 'scenario_obj';}
    return bot;
}

function toDegrees(radians) {
    return 180 * radians / Math.PI;
}

/* Setting up a single modbot at (x, y) 
	where (0,0) is top left */
function drawBot(b, scale, xOffset, yOffset) {
    if (b.size == 0) b.size = 10;
    var size = b.size*x_int;
    var bot = new PIXI.Graphics();
    	bot.beginFill(0x0EB530); //green
    	bot.drawRect(0, 0, size, size);
    	bot.pivot = new PIXI.Point(size/2, size/2);
    	bot.rotation = -b.angle;
    	bot.endFill();

    var cx = (b.x)*x_int+xOffset;
    var cy = VIEW_WIDTH - ((b.y)*y_int)+yOffset;
        bot.x = cx;
        bot.y = cy;

    // draw bot coordinate text
    let botCoordText = new PIXI.Text('(' + b.x.toFixed(2) + ',' + b.y.toFixed(2) + ',' + toDegrees(b.angle).toFixed(2) + ')',{fontFamily : 'Arial', fontSize: 11, fill : 0xff1010, align : 'center'});
    botCoordText.x = cx;
    botCoordText.y = cy + 14; //arbitrary constant for offset

    if (document.querySelector("#debug") == null) {
        let debug = document.createElement("div");
        debug.id = "debug";
        let style = {
            "position": "absolute",
            "top": "10px",
            "left": "10px",
            "width": "400px",
            "height": "200px",
            "border": "1px solid red",
        };
        for (k in styles) {
            debug.style[k] = styles[k];
        }
    }
    else {
        let debug = document.querySelector("#debug");
        debug.innerHTML = b.x + " " + b.y + " " + cx + " " + cy;
    }

    var sensor1 = new PIXI.Graphics();
        sensor1.beginFill(0xFF0000); //red
        sensor1.drawCircle(0, 0, size/10);
        sensor1.endFill();

    var sensor2 = new PIXI.Graphics();
        sensor2.beginFill(0xFF0000); //red
        sensor2.drawCircle(0, 0, size/10);
        sensor2.endFill();

    sensor1.x = cx+size/Math.sqrt(3)*Math.cos(b.angle+Math.PI/6);
    sensor1.y = cy-size/Math.sqrt(3)*Math.sin(b.angle+Math.PI/6);

    sensor2.x = cx+size/Math.sqrt(3)*Math.cos(b.angle-Math.PI/6);
    sensor2.y = cy-size/Math.sqrt(3)*Math.sin(b.angle-Math.PI/6);

	botContainer.addChild(bot);
	botContainer.addChild(sensor1);
	botContainer.addChild(sensor2);
	botContainer.addChild(botCoordText);
}

function drawScenarioObject(b, scale, xOffset, yOffset) {
    var size = b.size*x_int;
	var scenarioObject = new PIXI.Graphics();

	scenarioObject.beginFill(0x0EB530);
	scenarioObject.drawRect(0, 0, size, size);
	scenarioObject.pivot = new PIXI.Point(size/2, size/2);
    scenarioObject.rotation = b.angle;
	scenarioObject.endFill();

    var cx = (b.x)*x_int;
    var cy = VIEW_WIDTH - ((b.y)*y_int);
    scenarioObject.x = cx+xOffset;
    scenarioObject.y = cy+yOffset;

	botContainer.addChild(scenarioObject);
}

/* Displays all bots given an array of bots */
function displayBots(botArray, scale, xOffset, yOffset) {
	for(var b=0; b<botArray.length;b++) {
        if (botArray[b].type=='bot'){
            drawBot(botArray[b], scale, xOffset, yOffset);
        } else {
            drawScenarioObject(botArray[b], scale, xOffset, yOffset);
        }
	}
}

/* Helper function to update HTML text indicating
   spacing intervals on view */
function updateInfo(xint, yint){
    $("#x-int").text(xint);
    $("#y-int").text(yint);
}

/*
	Sets up grid lines within view.
	- 40x40 grid, 4x4 initially visible
	- 
*/
function setupGridLines(scale, xOffset, yOffset) {
    var lines_y = [];
    var lines_x = [];

    for(var i=0; i<40; i=i+1){
        lines_y[i] = new PIXI.Graphics();
        //lines_y[i].lineStyle(1, 0x0000FF, 1);
        lines_y[i].lineStyle(1, 0x000000, 1);

        lines_y[i].moveTo(0,i*65*scale/100);
        lines_y[i].lineTo(VIEW_WIDTH,i*65*scale/100);
        lines_y[i].x = 0;
        lines_y[i].y =(i-20)*65*scale/100 + yOffset;

        gridContainer.addChild(lines_y[i]);

        lines_x[i] = new PIXI.Graphics();
        lines_x[i].lineStyle(1, 0x0000FF, 1);

        lines_x[i].moveTo(i*65*scale/100,0);
        lines_x[i].lineTo(i*65*scale/100,VIEW_WIDTH);
        lines_x[i].x = (i-20)*65*scale/100 + xOffset;
        lines_x[i].y = 0;

        gridContainer.addChild(lines_x[i]);
    }
}

// Populates the PIXI view with the squares corresponding to the occupancy matrix and path
function fillOccupancyMatrix(scale, xOffset, yOffset) {
    for(var i = 0; i < occupancyMatrix.length; i++) {
        for(var j = 0; j < occupancyMatrix[0].length; j++) {
                //If this cell corresponds to a cell on path to the occupancy matrix, color it purple
                if(path[i][j] == 1) {

                    var scenarioObject = new PIXI.Graphics();

                    scenarioObject.beginFill(0x8822A4);
                    scenarioObject.drawRect(0, 0, x_int, y_int);
                    scenarioObject.endFill();

                    var cx = (i)*x_int+xOffset;
                    var cy = (j)*y_int+yOffset;
                    scenarioObject.x = cx;
                    scenarioObject.y = cy;
                    botContainer.addChild(scenarioObject);
                }
                //If this cell corresponds to a cell that is filled in in the occupancy matrix, color it blackish
                else if(occupancyMatrix[i][j] == 1) {
                    var size = 65;
                    var scenarioObject = new PIXI.Graphics();

                    scenarioObject.beginFill(0x123212);
                    scenarioObject.drawRect(0, 0, x_int, y_int);
                    scenarioObject.endFill();

                    var cx = (i)*x_int+xOffset;
                    var cy = (j)*y_int+yOffset;
                    scenarioObject.x = cx;
                    scenarioObject.y = cy;
                    botContainer.addChild(scenarioObject);
                }
        }
    }
}

/*
	Updating location of bots on grid.
*/
lock = false;
lastTime = new Date();
function getNewVisionData() {
    if (document.getElementById('vision-poll').checked) {
        $.ajax({
            url: '/updateloc',
            type: 'GET',
            dataType: 'json',
            success: function visionDataGot(data) {
                currentTime = new Date();
                elapsed = (currentTime - lastTime);
                timeout = MILLIS_PER_VISION_UPDATE;
                if (elapsed > MILLIS_PER_VISION_UPDATE) {
                    timeout = 2*MILLIS_PER_VISION_UPDATE - elapsed;
                    if (timeout < 0) {
                        timeout = 0;
                    }
                }

                setTimeout(getNewVisionData,timeout);
                if (!lock) {
                    lock = true;
                    bots = [];
                    botContainer.removeChildren();
                    for (var b in data) {
                        var bot = data[b];
                        var botX = bot.x;
                        var botY = bot.y;
                        var botAngle = bot.angle;
                        var botSize = bot.size;
                        if (!botSize) bot.size = 0.15; // TODO: Fix this
                        // really bad hack
                        var botId = bot.id;
                        bots.push(newBot(bot.x, bot.y, bot.angle, bot.id, bot
                        .size));
                    }

                    stage.removeChild(gridContainer);
                    gridContainer = new PIXI.Container();
                    botContainer.removeChildren();

                    setupGridLines(scale, xOffset, yOffset);

                    stage.addChild(gridContainer);

                    displayBots(bots,scale, xOffset, yOffset);

                    displayBots(bots, scale, xOffset, yOffset);
                    grid.render(stage);
                    lock = false;
                }
                          },
            error: () => {
            console.log("oh no error");
            setTimeout(getNewVisionData,MILLIS_PER_VISION_UPDATE*10);}
        });
    } else {
        setTimeout(getNewVisionData,MILLIS_PER_VISION_UPDATE * 10);
    }
}

function pollBotNames() {
    $.ajax({
        url: '/trackedBots',
        type: 'GET',
        dataType: 'json',
        success: function visionDataGot(data) {
            listBotsPrev = listBots;
            listBots = [];
            for (var b in data) {
                    var bot = data[b];
                    listBots.push({name: bot.name});
            }

            if (listBots.length !== listBotsPrev.length) {
                redoDropdown(listBots);
            } else {
                for(let i = 0; i < listBots.length; i=i+1) {
                    if (listBots[i].name !== listBotsPrev[i].name) {
                        redoDropdown(listBots);
                        break;            
                    }
                }
            }

            setTimeout(pollBotNames,2000); // Try again in 2 sec
        },
        error: function() {
            setTimeout(pollBotNames,2000); // Try again in 2 sec
        }
    });
}

window.addEventListener("keydown", function(e) {
    // space and arrow keys
    if([32, 37, 38, 39, 40].indexOf(e.keyCode) > -1) {
        e.preventDefault();
    }
}, false);

function displayOccupancyMatrix(height, width, size) {

    $.ajax({
        method: "POST",
        url: '/postOccupancyMatrix',
        dataType: 'json',
        data: JSON.stringify({
            height:height,
            width: width,
            size: size}),
        contentType: 'application/json',
        success: function(data) {
            console.log("Occupancy matrix post successful");
            omPresent = true;
            occupancyMatrix = data;
            occupancyMatrix = padOccupancyMatrix();

            $.ajax({
                method: "POST",
                url: '/postDijkstras',
                dataType: 'json',
                data: JSON.stringify({
                    matrix: occupancyMatrix}),
                contentType: 'application/json',
                success: function(data) {
                    path = data;
                    fillOccupancyMatrix(scale, xOffset, yOffset);
                }
            });
        }
    });
}

/* Iterates through the occupancy matrix. When a 1 is encountered in a cell, all of the
   adjacent cells will be marked by a 1. This is to increase the margin so that any path
   planning algorithm will not choose a path too close to an obstacle.
*/
function padOccupancyMatrix() {

    var temp = [];
    for(var i = 0; i < occupancyMatrix.length; i++) {
        temp.push([]);
        for(var j = 0; j < occupancyMatrix[0].length; j++) {
            temp[i].push(occupancyMatrix[i][j]);
        }
    }
    for(var i = 0; i < occupancyMatrix.length; i++) {
        for(var j = 0; j < occupancyMatrix.length; j++) {
            if(occupancyMatrix[i][j] === 1) {
                if(i-1 >= 0) {
                    temp[i-1][j] = 1;
                }
                if(i+1 < occupancyMatrix.length) {
                    temp[i+1][j] = 1;
                }
                if(j-1 >= 0) {
                    temp[i][j-1] = 1;
                }
                if(j+1 < occupancyMatrix[0].length) {
                    temp[i][j+1] = 1;
                }
                if(j-1 >= 0 && i-1 >= 0) {
                    temp[i-1][j-1] = 1;
                }
                if(j+1 < occupancyMatrix[0].length && i+1 < occupancyMatrix.length) {
                    temp[i+1][j+1] = 1;
                }
                if(j+1 < occupancyMatrix[0].length && i-1 > 0) {
                    temp[i-1][j+1] = 1;
                }
                if(j-1 > 0 && i+1 < occupancyMatrix.length) {
                    temp[i+1][j-1] = 1;
                }
            }
        }
    }
    return temp;
}

$("#showOccupancyMatrix").click( function() {
        displayOccupancyMatrix(40, 40, 1.0);
        //Wait until the occupancy matrix has been returned from POST
        //while(!omPresent){}
        setTimeout( function() {
            displayBots(bots,scale, xOffset, yOffset);
                fillOccupancyMatrix(scale, xOffset, yOffset);

                grid.render(stage);
                }, 4000);
        //TODO. The path does not show up correctly. It is wrong because the post request to get the occupancy matrix seems to return an empty matrix
});

main();