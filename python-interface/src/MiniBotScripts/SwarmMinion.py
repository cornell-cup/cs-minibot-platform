from ..MiniBotFramework.Communication.ZMQ import ZMQ

z = ZMQ()

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
                    comm = self.receivedQueue.get()
                    react(comm)
                    print "running ", comm
                else:
                    print "nothing in queue"
                    time.sleep(1)
    finally:
        self.cleanup()


def react(command):
    bot.get_actuator_by_name("two_wheel_movement").move(command[0], command[1])