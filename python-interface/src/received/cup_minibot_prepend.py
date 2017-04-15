import os
from os import sys, path
sys.path.append(os.path.join(os.path.dirname('sensor'), '..'))
from sensor import Sensor
from sensor import GPIOSensor


class CupMiniBot:
    """
    Abstract class defining the base functions of the MiniBot. More customized MiniBots may
    subclass this.
    """
    def __init__(self):
        self.sensors = {}

    def move_forward(self, power):
        """
        Moves the bot forward at a percentage of its full power

        :param power The percentage of the bot's power to use from 0-100
        :return True if the action is supported
        """
        print("Unimplemented: Moving forward "+str(power))

    def move_backward(self, power):
        """
        Moves the bot backward at a percentage of its full power

        :param power The percentage of the bot's power to use from 0-100
        :return True if the action is supported
        """
        print("Unimplemented: Moving backward "+str(power))

    def turn_clockwise(self, power):
        """
        Moves the bot clockwise  at a percentage of its full power

        :param power The percentage of the bot's power to use from 0-100
        :return True if the action is supported
        """
        print("Unimplemented: Turning clockwise "+str(power))

    def turn_counter_clockwise(self, power):
        """
        Moves the bot counter-clockwise at a percentage of its full power

        :param power The percentage of the bot's power to use from 0-100
        :return True if the action is supported
        """
        print("Unimplemented: Turning counter clockwise "+str(power))

    def set_wheel_power(self, front_left, front_right, back_left, back_right):
        """
        Sets the power of the bot's wheels as a percentage from -100 to 100. If a wheel
        specified does not exist, the power for that wheel is ignored.

        :param front_left power to deliver to the front_left wheel
        :param front_right power to deliver to the front_right wheel
        :param back_left power to deliver to the back_left wheel
        :param back_right power to deliver to the back_right wheel
        :return True if the action is supported
        """
        print("Unimplemented: Setting wheel power to %d,%d,%d,%d" % (front_left, front_right, back_left, back_right))

    def wait(self, t):
        """
        Waits for a duration in seconds.

        :param t The duration in seconds
        """
        time.sleep(t)

    def get_all_sensors(self):
        return self.sensors.values()

    def get_sensor_by_name(self, name):
        return self.sensors[name]

    def poll_sensors(self):
        data = {}
        for sensor in self.sensors:
            data[sensor] = self.sensors[sensor].read()
        return data

    def register_sensor(self,sensor):
        self.sensors[sensor.name] = sensor


bot = CupMiniBot()
