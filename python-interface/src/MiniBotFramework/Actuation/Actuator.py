# Abstract class representing an actuator
class Actuator(object):
    def __init__(self, bot, name):
        self.name = name
        bot.register_actuator(self)
        print "Actuator being registered: " + str(self.name)

    def read(self):
        return "Invalid: Abstract Class"

    def set(self, value):
        return "Invalid: Abstract Class"

    def get_name(self):
        return self.name
