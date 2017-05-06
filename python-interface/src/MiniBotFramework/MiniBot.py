import os, time
from os import sys, path
try:
    import RPi.GPIO as GPIO
except:
    GPIO = {}
import MiniBotFramework
from Queue import Queue

class MiniBot:
    """
    Defines the base functions of the MiniBot
    """
    def __init__(self, config):
        self.sensors = {}
        self.actuators = {}
        #GPIO.setwarnings(False)
        GPIO.setmode(GPIO.BCM)

        for actuator in config["actuators"]:
            if actuator["type"] == "gpioMotor":
                name = actuator["name"]
                pinPWM = actuator["pinPWM"]
                pinHighLow = actuator["pinHighLow"]
                reversed = actuator["reversed"]
                MiniBotFramework.Actuation.GpioMotor.GpioMotor(self, name, pinPWM, pinHighLow, reversed, GPIO)
            else:
                print("ERROR: Unknown actuator in config")

        # queue for extra unrecognized commands by parser
        self.extraCMD = Queue()
        # TODO: Sensor parsing

        # Meta actuator. TODO: Make configurable
        self.left_motor = self.actuators["leftMotor"]
        self.right_motor = self.actuators["rightMotor"]
        self.two_wheel_movement = MiniBotFramework.Actuation.TwoWheelMovement.TwoWheelMovement(self, "two_wheel_movement", self.left_motor, self.right_motor)

    def stop(self):
        """
        Moves the bot forward at a percentage of its full power

        :param power The percentage of the bot's power to use from 0-100
        :return True if the action is supported
        """
        print("STOPPING")
        self.actuators["two_wheel_movement"].move(0,0)

    def move_forward(self, power):
        """
        Moves the bot forward at a percentage of its full power

        :param power The percentage of the bot's power to use from 0-100
        :return True if the action is supported
        """
        print("MOVING FORWARD")
        self.actuators["two_wheel_movement"].move(power,power)

    def move_backward(self, power):
        """
        Moves the bot backward at a percentage of its full power

        :param power The percentage of the bot's power to use from 0-100
        :return True if the action is supported
        """
        self.actuators["two_wheel_movement"].move(-power,-power)

    def turn_clockwise(self, power):
        """
        Moves the bot clockwise  at a percentage of its full power

        :param power The percentage of the bot's power to use from 0-100
        :return True if the action is supported
        """
        self.actuators["two_wheel_movement"].move(power,-power)

    def turn_counter_clockwise(self, power):
        """
        Moves the bot counter-clockwise at a percentage of its full power

        :param power The percentage of the bot's power to use from 0-100
        :return True if the action is supported
        """
        self.actuators["two_wheel_movement"].move(-power,power)

    def set_wheel_power(self, left, right):
        """
        Sets the power of the bot's wheels as a percentage from -100 to 100. If a wheel
        specified does not exist, the power for that wheel is ignored.

        :param front_left power to deliver to the front_left wheel
        :param front_right power to deliver to the front_right wheel
        :param back_left power to deliver to the back_left wheel
        :param back_right power to deliver to the back_right wheel
        :return True if the action is supported
        """
        self.actuators["two_wheel_movement"].move(left,right)

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

    # def poll_location(self):
    #     data = {}


    def register_sensor(self,sensor):
        self.sensors[sensor.name] = sensor

    def register_actuator(self,actuator):
        self.actuators[actuator.name] = actuator

    def get_actuator_by_name(self, name):
        return self.actuators[name]

    def get_all_actuators(self):
        return self.actuators.values()
