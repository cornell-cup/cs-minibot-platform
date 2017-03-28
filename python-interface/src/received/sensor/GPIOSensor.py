# A sensor for reading GPIO values.
# Needs to be updated to actually work, fake for now!
# Abstract class representing the sensor interface

from .Sensor import Sensor

class GPIOSensor(Sensor):
    def __init__(self, bot, name, pin_number):
        Sensor.__init__(self, bot, name)
        self.pin_number = pin_number

    def read(self):
        return 0.5 # TODO: Actually read a value!
