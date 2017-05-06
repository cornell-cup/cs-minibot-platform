from MiniBotFramework.Communication.ZMQ import ZMQExchange
from MiniBotFramework.Communication.TCP import TCP
from MiniBotFramework.Actuation.TwoWheelMovement import TwoWheelMovement
from threading import Thread
from MiniBotFramework.Sensing.ColorSensor import ColorSensor
import time

threads = []

def run(bot):
    # Sets up TCP connection between master and minions. Starts publisher-side 
    # connection.
    # always set the mediator first
    z = ZMQExchange()
    z.setMediator()
    z.setBroadcaster()
    
    TCP.tcp.send_to_basestation("SwarmIP", z.getIP("wlan0"))

    mediateThread = Thread(target=z.mediate)
    mediateThread.start()
    threads.append(mediateThread)
    
    echobot(bot,z)
    #colorbot(bot,z)
    
def colorbot(bot,z):
    speed = 10
    cs = bot.get_sensor_by_name("ColorSensor")
    cs.calibrate()
    pinkFirstTime = True
    orangeFirstTime = True
    count = {"F":0,"B":0,"L":0,"R":0}

    try:
        while(True):
            c = cs.read_color()
            if(c=="RED"):
                # stop
                msg = (0,0)
                count["F"]=0
                count["B"]=0
                count["L"]=0
                count["R"]=0
            elif(c=="GREEN"):
                # forwards
                msg = (speed,speed)
                count["F"]++
                count["B"]=0
                count["L"]=0
                count["R"]=0
                if(count["F"]>25):
                    speed = inc(speed,5)
            elif(c=="BLUE"):
                # backwards
                msg = (-speed,-speed)
                count["F"]=0
                count["B"]++
                count["L"]=0
                count["R"]=0
                if(count["B"]>25):
                    speed = inc(speed,5)
            elif(c=="YELLOW"):
                # turn left
                msg = (-speed,speed)
                count["F"]=0
                count["B"]=0
                count["L"]++
                count["R"]=0
                if(count["L"]>25):
                    speed = inc(speed,5)
            elif(c=="VIOLET"):
                # turn right
                msg = (speed,-speed)
                count["F"]=0
                count["B"]=0
                count["L"]=0
                count["R"]++
                if(count["R"]>25):
                    speed = inc(speed,5)
            # elif(c=="PINK"):
            #     # decrease speed
            #     orangeFirstTime = True
            #     if (pinkFirstTime && speed > 10):
            #         pinkFirstTime = False
            #         speed -= 5
            #         print "SLOWER! (Speed: " + str(speed) + ")"
            # elif(c=="ORANGE"):
            #     # increase speed
            #     pinkFirstTime = True
            #     if (orangeFirstTime && speed < 50):
            #         orangeFirstTime = False
            #         speed += 5
            #         print "FASTER! (Speed: " + str(speed) + ")"
            print str(msg)
            z.broadcast(msg)
            time.sleep(0.2)
    finally:
        cleanup(z)

def inc(speed, i):
    if(speed<50):
        speed += i

def echobot(bot,z):
    try:
        while(True):
            # msg is a tuple of left motor and right motor, respectively.
            msg = bot.get_actuator_by_name("two_wheel_movement").get_value()
            print "MSG: " + msg
            z.broadcast(msg)
            time.sleep(0.1)

            if not TCP.tcp.isConnected():
                break

    finally:
        cleanup(z)

def cleanup(z):
    for t in threads:
        t.join(0.1)

    z.stopZMQExchange()
