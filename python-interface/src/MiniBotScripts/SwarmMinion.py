from MiniBotFramework.Communication.ZMQ import ZMQExchange
from MiniBotFramework.Communication.TCP import TCP
from Queue import Queue
import time
from threading import Thread

z = ZMQExchange()

threads = []

def run(bot):

    # get the IP of the mediator in the ZMQ network
    MEDIATOR_IP = getMediatorIP()
    # Sets up ZMQ connection between master and minions. Starts subscriber-side 
    # connection
    
    z.setReceiver()
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

    finally:
        cleanup()

def getMediatorIP():
    if TCP.tcp is not None:
        MediatorIP = TCP.tcp.requestIP()

def cleanup():
    for t in threads:
        t.join(1)

    z.stopZMQExchange()
