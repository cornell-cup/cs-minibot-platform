
#Reads xbox inputs and returns desired l/r wheel powers
from MiniBotFramework.Lib.legopi.lib import xbox_read
from threading import Thread

class Xbox(object):

    updated = False
    left = 0
    right = 0

    def __init__(self):
        self.thread_xbox = Thread(target = read_xbox)
        self.thread_xbox.start()

def read_xbox():
    left = 0
    right = 0

    for event in xbox_read.event_stream(deadzone=12000):
        # Convert input event into a string so we can parse it
        event_triggered = str(event)

        # Extracts the button pressed and value (0 or 1 depending on pressed or unpressed)
        button = event_triggered[event_triggered.find("(")+1:event_triggered.find(",")]
        value = event_triggered[event_triggered.find(",")+1:event_triggered.rfind(",")]

        if (button == "Y0" or button == "Y1" or wow):
            wow=False
            if button == "Y0":
                right = ((int(value)) / 32766)
            if button == "Y1":
                left = ((int(value)) / 32766)
            Xbox.left = left*100
            Xbox.right = right*100
            Xbox.updated = True
