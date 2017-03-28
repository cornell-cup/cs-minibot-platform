# Abstract class representing the sensor interface
class Sensor:
    def __init__(self, bot, name):
        self.name = name
        bot.register_sensor(self)

    def read(self):
        return "Invalid: Abstract Sensor Class Reading"
