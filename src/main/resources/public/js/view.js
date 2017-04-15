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
var bots = [            // hard-coded bots for testing
    newBot(1,1,0,"bob"), 
    newBot(3,3,0,"bobette")
    ]; 
//const X_RANGE = 11; // x range for grid, TODO: capitalize const
//const Y_RANGE = 11; // y range for grid  <---- what does this mean?????????
var viewWidth =  520;
var x_int = 520/4; // actual spacing between grid lines
var y_int =520/4;

var scale = 100; //percentage, 100% is equal to 4x4 grid displayed, with each grid being 130pixels wide
var x_offset = 0; //for purposes of adjusting viewport, this is a raw pixel value
var y_offset = 0; //same as above

var stage; // pixi elements for displaying information
var back;
var botContainer;
var gridContainer;
var grid;
var imageLoader;

var backgroundSprite;

$('#scale').on('change',function(){
    var val = $(this).val();
    scale=val;
    x_int = viewWidth/4*val/100;
    y_int = viewWidth/4*val/100;
    updateInfo(x_int, y_int);

    stage.removeChild(gridContainer);
    gridContainer = new PIXI.Container();
    botContainer.removeChildren();
    setupGridLines(scale, x_offset, y_offset);
    stage.addChild(gridContainer);
    displayBots(bots,scale, x_offset, y_offset);
    grid.render(stage);
});

/* for moving the viewport */
window.onkeydown = function (e) {

    let code = e.keyCode ? e.keyCode : e.which;

    if (code === 37) {
       //move view left
       x_offset-=1;
    } else if (code === 39) {
       //move view right
       x_offset+=1;

    } else if (code == 38) {
    	//move view up
        y_offset-=1;

    } else if (code == 40) {
    	// move view down
        y_offset+=1;
    } else {
        return;
    }
    stage.removeChild(gridContainer);
    gridContainer = new PIXI.Container();
    botContainer.removeChildren();
    setupGridLines(scale, x_offset, y_offset);
    stage.addChild(gridContainer);
    displayBots(bots, scale, x_offset, y_offset);
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

    setupGridLines(scale, x_offset, y_offset);
    displayBots(bots,scale, x_offset, y_offset);
  
    getNewVisionData();
    pollBotNames();
}

/* pseudo-constructor for a bot object */
function newBot(x, y, angle, id) {
    var bot = {
        x: x,
        y: y,
        angle: angle, // radians
        id: id
    };
    return bot;
}


///* Zoom-out function to make all active bots visible on grid. */
//$("#zoom-out").click(function() {
//    console.log("zoom out");
//    if(bots.length!==0) {
//        scaleToFit();
//        zoomclicked = true;
//        $("#reset").css("display","inline");
//        $(this).css("display","none");
//    }
//});
//
///* Reset function to return to original view (from zoom-out). */
//$("#reset").click(function(){
//console.log("reset");
//    updateInfo(x_int, y_int);
//    botContainer.removeChildren();
//    setupGridLines();
//    displayBots(bots);
//    grid.render(stage);
//    $("#zoom-out").css("display","inline");
//    $(this).css("display","none");
//});

/* Setting up a single modbot at (x, y) 
	where (0,0) is top left */
function drawBot(b, scale, x_offset, y_offset) {
    var botradius = scale/4
	var circle = new PIXI.Graphics();
	circle.beginFill(0x0EB530);

	circle.drawCircle(0, 0, botradius);

	circle.endFill();
    var cx = (b.x)*x_int+x_offset;
    var cy = (b.y)*y_int+y_offset;
	circle.x = cx;
	circle.y = cy;

    var circle2 = new PIXI.Graphics();
    circle2.beginFill(0xFF0000);

    circle2.drawCircle(0, 0, scale/10);

    circle2.endFill();

    var circle3 = new PIXI.Graphics();
        circle3.beginFill(0xFF0000);

        circle3.drawCircle(0, 0, scale/10);
        circle3.endFill();

    circle2.x = cx+botradius*Math.cos(b.angle+Math.PI/6);
    circle2.y = cy+botradius*Math.sin(b.angle+Math.PI/6);

    circle3.x = cx+botradius*Math.cos(b.angle-Math.PI/6);
    circle3.y = cy+botradius*Math.sin(b.angle-Math.PI/6);


	botContainer.addChild(circle);
	botContainer.addChild(circle2);
	botContainer.addChild(circle3);
}

/* Displays all bots given an array of bots */
function displayBots(botArray, scale, x_offset, y_offset) {
	for(var b=0; b<botArray.length;b++) {
		drawBot(botArray[b], scale, x_offset, y_offset);
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
	- 13 x 13 grid
	- x:[0, 12], y:[0, 12]
	- 
*/
function setupGridLines(scale, x_offset, y_offset) {
    var lines_y = [];
    var lines_x = [];

    for(var i=0; i<80; i=i+1){
        lines_y[i] = new PIXI.Graphics();
        lines_y[i].lineStyle(1, 0x0000FF, 1);

        lines_y[i].moveTo(0,i*65*scale/100);
        lines_y[i].lineTo(520,i*65*scale/100);
        lines_y[i].x = 0;
        lines_y[i].y =(i-40)*65*scale/100 + y_offset;

        gridContainer.addChild(lines_y[i]);

        lines_x[i] = new PIXI.Graphics();
        lines_x[i].lineStyle(1, 0x0000FF, 1);

        lines_x[i].moveTo(i*65*scale/100,0);
        lines_x[i].lineTo(i*65*scale/100,520);
        lines_x[i].x = (i-40)*65*scale/100 +x_offset;
        lines_x[i].y = 0;

        lines_x[i].moveTo(i*20,0);
        lines_x[i].lineTo(i*20,520);
        lines_x[i].x = i*20; lines_x[i].y = 0;

        gridContainer.addChild(lines_x[i]);
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
                        //console.log("YAY");
                        var bot = data[b];
                        var zz = bot.x;
                        var aa = bot.y;
                        var bb = bot.angle;
                        //console.log(bot.angle);
                        var idid = bot.id;
                        bots.push(newBot(bot.x,bot.y,bot.angle,bot.id));
                    }

                    displayBots(bots, scale, x_offset, y_offset);
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

/*
    Re-displays a list of bots so that all bots are visible
    in view.

    inv: bots is not empty.
*/
/* function scaleToFit() {
    console.log("scaled to fit");
	var botmin_x = bots[0].x;
    var botmin_y = bots[0].y;
    var botmax_x = bots[0].x;
    var botmax_y = bots[0].y;
    for (var b in bots) {
        botmin_x = Math.min(botmin_x, bots[b].x);
        botmin_y = Math.min(botmin_y, bots[b].y);
        botmax_x = Math.max(botmax_x, bots[b].x);
        botmax_y = Math.max(botmax_y, bots[b].y);
    }

    var xran = botmax_x - botmin_x;
    var yran = botmax_y - botmin_y;
    zoombots = [];

    for (var b in bots) {
        zoombots.push(newBot(
            (bots[b].x - botmin_x)*(X_RANGE/xran) + 1, // x pos
            (bots[b].y - botmin_y)*(Y_RANGE/yran) + 1, // y pos
            bots[b].angle, // angle
            bots[b].id // id
        ));
    }

    updateInfo((X_RANGE/xran), (Y_RANGE/yran));
    botContainer.removeChildren();
    setupGridLines();
    displayBots(zoombots);
    grid.render(stage);
}
*/
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

main();