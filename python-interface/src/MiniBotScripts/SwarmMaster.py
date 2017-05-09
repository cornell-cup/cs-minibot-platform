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
    
    #echobot(bot,z)
    colorbot(bot,z)
    
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
                speed = 10
            elif(c=="GREEN"):
                # forwards
                count["F"]+=1
                count["B"]=0
                count["L"]=0
                count["R"]=0
                if(count["F"]>15):
                    count["F"]=0
                    speed = inc(speed,15)
                msg = (speed,speed)
            elif(c=="BLUE"):
                # backwards
                count["F"]=0
                count["B"]+=1
                count["L"]=0
                count["R"]=0
                if(count["B"]>15):
                    count["B"]=0
                    speed = inc(speed,15)
                msg = (-speed,-speed)
            elif(c=="YELLOW"):
                # turn left
                count["F"]=0
                count["B"]=0
                count["L"]+=1
                count["R"]=0
                if(count["L"]>15):
                    count["L"]=0
                    speed = inc(speed,15)
                msg = (-speed,speed)
            elif(c=="VIOLET"):
                # turn right
                count["F"]=0
                count["B"]=0
                count["L"]=0
                count["R"]+=1
                if(count["R"]>15):
                    count["R"]=0
                    speed = inc(speed,15)
                msg = (speed,-speed)
            z.broadcast(msg)
            time.sleep(0.2)
    finally:
        cleanup(z)

def inc(speed, i):
    if(speed<50):
        speed += i
    print "Speed increased: " + str(speed)
    return speed

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
