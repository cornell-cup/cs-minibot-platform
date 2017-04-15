import RPi.GPIO as GPIO
import sys, threading, time, os

class CupMiniBot:
    def __init__(self):
        GPIO.setmode(GPIO.BCM)
        GPIO.setup(13, GPIO.OUT)
        GPIO.setup(19, GPIO.OUT)
        GPIO.setup(16, GPIO.OUT)
        GPIO.setup(20, GPIO.OUT)

        GPIO.output(13, GPIO.LOW)
        GPIO.output(19, GPIO.HIGH)
        GPIO.output(16, GPIO.LOW)
        GPIO.output(20, GPIO.HIGH)
    def move_forward(self, power):
        """
        Moves the bot forward at a percentage of its full power

        :param power The percentage of the bot's power to use from 0-100
        :return True if the action is supported
        """
        GPIO.output(13, GPIO.LOW)
        GPIO.output(19, GPIO.LOW)
        GPIO.output(16, GPIO.HIGH)
        GPIO.output(20, GPIO.HIGH)
    def move_backward(self, power):
        """
        Moves the bot backward at a percentage of its full power

        :param power The percentage of the bot's power to use from 0-100
        :return True if the action is supported
        """
        GPIO.output(13, GPIO.HIGH)
        GPIO.output(19, GPIO.HIGH)
        GPIO.output(16, GPIO.LOW)
        GPIO.output(20, GPIO.LOW)
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

bot = CupMiniBot()
