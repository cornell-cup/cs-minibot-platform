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

    try:
        while(True):
            c = cs.read_color()
            if(c=="RED"):
                # stop
                msg = (0,0)
                pinkFirstTime = True
                orangeFirstTime = True
            elif(c=="GREEN"):
                # forwards
                msg = (speed,speed)
                pinkFirstTime = True
                orangeFirstTime = True
            elif(c=="BLUE"):
                # backwards
                msg = (-speed,-speed)
                pinkFirstTime = True
                orangeFirstTime = True
            elif(c=="YELLOW"):
                # turn left
                msg = (-speed,speed)
                pinkFirstTime = True
                orangeFirstTime = True
            elif(c=="VIOLET"):
                # turn right
                msg = (speed,-speed)
                pinkFirstTime = True
                orangeFirstTime = True
            elif(c=="PINK"):
                # increase speed
                orangeFirstTime = True
                if (pinkFirstTime && speed < 50):
                    pinkFirstTime = False
                    speed += 5
            elif(c=="ORANGE"):
                # decrease speed
                pinkFirstTime = True
                if (orangeFirstTime && speed > 10):
                    orangeFirstTime = False
                    speed -= 5
            print str(msg)
            z.broadcast(msg)
            time.sleep(0.2)
    finally:
        cleanup(z)

def echobot(bot,z):
    try:
        while(True):
            # msg is a tuple of left motor and right motor, respectively.
            msg = bot.get_actuator_by_name("two_wheel_movement").get_value()
            print "MSG: " + Mmsg
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
