# Abstract class representing a sensor
class Sensor:
    def __init__(self, bot, name):
        self.name = name
        print "Sensor init called! Name: " + name
        bot.register_sensor(self)

    def read(self):
        return "Invalid: Abstract Sensor Class Reading"

    def get_name(self):
        return self.name
