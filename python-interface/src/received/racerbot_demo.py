import RPi.GPIO as GPIO
import sys, threading, time, os

class CupMiniBot:
    def __init__(self):
        print("Setting up GPIO")
        GPIO.setmode(GPIO.BCM)
        GPIO.setup(13, GPIO.OUT)
        GPIO.setup(19, GPIO.OUT)
        GPIO.setup(16, GPIO.OUT)
        GPIO.setup(20, GPIO.OUT)

        #GPIO.output(13, GPIO.LOW)
        #GPIO.output(19, GPIO.HIGH)
        #GPIO.output(16, GPIO.LOW)
        #GPIO.output(20, GPIO.HIGH)

        # Setup PWM
        pwmFrequency = 60 # Hz
        self.pwnLeftA = GPIO.PWM(13,pwmFrequency)
        self.pwnRightB = GPIO.PWM(19,pwmFrequency)
        self.pwnRightA = GPIO.PWM(16,pwmFrequency)
        self.pwnLeftB = GPIO.PWM(20,pwmFrequency)

    def stop(self):
        GPIO.output(13, GPIO.LOW)
        GPIO.output(16, GPIO.LOW)
        GPIO.output(19, GPIO.HIGH)
        GPIO.output(20, GPIO.HIGH)

    def move_forward(self, power):
        """
        Moves the bot forward at a percentage of its full power

        :param power The percentage of the bot's power to use from 0-100
        :return True if the action is supported
        """
        GPIO.output(13, GPIO.LOW)
        GPIO.output(16, GPIO.HIGH)
        GPIO.output(19, GPIO.LOW)
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
        GPIO.output(13, GPIO.LOW)
        GPIO.output(19, GPIO.LOW)
        GPIO.output(16, GPIO.LOW)
        GPIO.output(20, GPIO.LOW)
    def turn_counter_clockwise(self, power):
        """
        Moves the bot counter-clockwise at a percentage of its full power

        :param power The percentage of the bot's power to use from 0-100
        :return True if the action is supported
        """
        GPIO.output(13, GPIO.HIGH)
        GPIO.output(19, GPIO.HIGH)
        GPIO.output(16, GPIO.HIGH)
        GPIO.output(20, GPIO.HIGH)
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
        front_left = int(front_left)
        front_right = int(front_right)
        if front_right < 0:
            front_right = -front_right
            BR = 100 - front_right
        if front_left < 0:
            front_left = -front_left
            BL = 100 - front_left
        print("Setting the power to: " + str(front_left) + "," + str(front_right))
        BL=100-front_right
        BR=100-front_left
        self.pwnRightA.ChangeDutyCycle(int(front_right))
        self.pwnRightB.ChangeDutyCycle(int(BR))
        self.pwnLeftA.ChangeDutyCycle(int(front_left))
        self.pwnLeftB.ChangeDutyCycle(int(BL))

        if (front_left > 0 and front_right > 0):
             pass
             #self.move_forward(10)
        elif (front_left < 0 and front_right < 0):
             pass
            #self.move_backward(10)
        elif (front_left < 0 and front_right > 0):
             pass
            #self.turn_counter_clockwise(10)
        elif (front_left > 0 and front_right < 0):
             pass
            #self.turn_clockwise(10)
        else:
            pass
            #self.stop()
            
    def wait(self, t):
        """
        Waits for a duration in seconds.

        :param t The duration in seconds
        """
        time.sleep(t)

bot = CupMiniBot()
