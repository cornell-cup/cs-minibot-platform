from MiniBotFramework.Communication.ZMQ import ZMQExchange
from MiniBotFramework.Communication.TCP import TCP
from MiniBotFramework.Actuation.TwoWheelMovement import TwoWheelMovement
from threading import Thread
import time

z = ZMQExchange()
threads = []

def run(bot):
    # Sets up TCP connection between master and minions. Starts publisher-side 
    # connection.
    # always set the mediator first
    z.setMediator()
    z.setBroadcaster()
    
    TCP.tcp.send_to_basestation("PUT_IP", "SwarmMaster," + z.getIP("wlan1"))

    mediateThread = Thread(target=z.mediate)
    mediateThread.start()
    threads.append(mediateThread)
    # commands for bot movement itself
    try:
        while(True):
            # msg is a tuple of left motor and right motor, respectively.
            msg = bot.get_actuator_by_name("two_wheel_movement").get_value()
            z.broadcast(msg)
            time.sleep(0.01)

    finally:
        cleanup()

def cleanup():
    for t in threads:
        t.join(0.1)

    z.stopZMQBroadcast()
