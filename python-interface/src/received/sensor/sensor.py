# Abstract class representing the sensor interface
class Sensor:
    def __init__(self, bot, name):
        bot.register_sensor(self)
        self.name=name
    def read(self):
        return "Invalid: Abstract Sensor Class Reading"
    def name(self):
        return self.name
