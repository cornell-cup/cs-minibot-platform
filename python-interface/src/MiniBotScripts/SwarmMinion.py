from MiniBotFramework.Communication.ZMQ import ZMQExchange
from Queue import Queue
import time
from threading import Thread

z = ZMQExchange()

"""
Sets up TCP connection between master and minions. Starts subscriber-side 
connection.
"""
z.setReceiver()
receivedQueue = Queue()

# the broadcastingQueue holds messages temporarily and then puts them
# into the broadcaster

threads = []
receiveThread = Thread(target=z.receive, args=(receivedQueue, ))
receiveThread.start()
threads.append(receiveThread)

def run(bot):
    try:
        while True:
            if (not receivedQueue.empty()):
                command = receivedQueue.get()

                print("receviging: " + str(command))
                
                # react to commamd
                bot.get_actuator_by_name("two_wheel_movement").move(command[0], command[1])
                
                #print "running ", command
                time.sleep(0.01)

    finally:
        cleanup()


def cleanup():
    for t in threads:
        t.join(1)

    z.stopZMQExchange()
