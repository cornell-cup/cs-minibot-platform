from MiniBotFramework.Communication.ZMQ import ZMQ
from Queue import Queue

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
receiveThread = threading.Thread(target=z.receive, args=(receivedQueue, ))
receiveThread.start()
threads.append(self.receiveThread)

def run(bot):
    try:
        while True:
            if (not self.receivedQueue.empty()):
                command = self.receivedQueue.get()
                
                # react to commamd
                bot.get_actuator_by_name("two_wheel_movement").move(command[0], command[1])
                
                print "running ", command

    finally:
        cleanup()


def cleanup():
    for t in threads:
        t.join(1)

    z.stopZMQExchange()