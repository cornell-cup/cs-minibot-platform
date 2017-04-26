from MiniBotFramework.Communication.ZMQ import ZMQExchange
from MiniBotFramework.Actuation.TwoWheelMovement import TwoWheelMovement

z = ZMQExchange()

"""
Sets up TCP connection between master and minions. Starts publisher-side 
connection.
"""
z.setBroadcaster()
z.setMediator()

# the broadcastingQueue holds messages temporarily and then puts them
# into the broadcaster

threads = []
mediateThread = threading.Thread(target=z.mediate)
mediateThread.start()
threads.append(mediateThread)
# commands for bot movement itself

def run(bot):
    while(True):
        # msg is a tuple of left motor and right motor, respectively.
        msg = bot.get_actuator_by_name("two_wheel_movement").get_value()
        z.broadcast(msg)