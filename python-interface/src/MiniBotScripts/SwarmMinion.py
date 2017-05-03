from MiniBotFramework.Communication.ZMQ import ZMQExchange
from MiniBotFramework.Communication.TCP import TCP
from Queue import Queue
import time
from threading import Thread

z = ZMQExchange()

threads = []

def run(bot):

    # get the IP of the mediator (hosted on the master) from the basestation
    TCP.tcp.send_to_basestation("GET_IP", "Swarm")

    # look in the queue for info
    (key, value) = bot.extraCMD.get()
    while key != "GET_IPSwarmMaster":
        # keep searching the queue
        (key, value) = bot.extraCMD.get()

    assert key == "GET_IPSwarmMaster"
    MEDIATOR_IP = value

    # Sets up ZMQ connection between master and minions. Starts subscriber-side 
    # connection
    
    z.setReceiver(MEDIATOR_IP)
    receivedQueue = Queue()
    
    receiveThread = Thread(target=z.receive, args=(receivedQueue, ))
    receiveThread.start()
    threads.append(receiveThread)

    try:
        while True:
            if (not receivedQueue.empty()):
                command = receivedQueue.get()

                print("receiving: " + str(command))
                
                # react to commamd
                bot.get_actuator_by_name("two_wheel_movement").move(command[0], command[1])
                
                #print "running ", command
            time.sleep(0.01)

            if not TCP.tcp.isConnected():
                print "zmq ending"
                break

    finally:
        cleanup()

def cleanup():
    for t in threads:
        t.join(1)

    z.stopZMQExchange()
