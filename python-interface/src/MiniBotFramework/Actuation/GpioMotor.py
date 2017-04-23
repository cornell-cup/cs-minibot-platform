from MiniBotFramework.Actuation.Actuator import Actuator

class GpioMotor(Actuator):

    PWM_FREQUENCY=100

    def __init__(self, bot, name, pin_pwm, pin_hl, GPIO):
        Actuator.__init__(self, bot, name)
        self.pin_pwm = pin_pwm
        self.pin_hl = pin_hl
        self.GPIO = GPIO
        GPIO.setup(self.pin_pwm, GPIO.OUT)
        GPIO.setup(self.pin_hl, GPIO.OUT)

        # Stop the bot
        GPIO.output(self.pin_pwm, GPIO.HIGH)
        GPIO.output(self.pin_hl, GPIO.LOW)
        self.pwm = GPIO.PWM(self.pin_pwm, GpioMotor.PWM_FREQUENCY)
        self.pwm.start(100)
        self.speed = 0
        self.forward = True

    def read(self):
        return (self.speed, self.forward)

    def rotate_forward(self,power):
        """ Requires 0 <= power <= 100 """
        self.set(power,True)
        self.pwm.ChangeDutyCycle(power)
        self.GPIO.output(self.pin_hl, self.GPIO.HIGH)

    def rotate_backward(self,power):
        """ Requires 0 <= power <= 100 """
        self.set(power,False)
        self.pwm.ChangeDutyCycle(100-power)
        self.GPIO.output(self.pin_hl, self.GPIO.LOW)

    def stop(self):
        self.set(0,True)
        #self.GPIO.output(self.pin_pwm, self.GPIO.HIGH)
        self.pwm.ChangeDutyCycle(100)
        self.GPIO.output(self.pin_hl, self.GPIO.LOW)

    def set(self, speed, forward):
        self.speed = speed
        self.forward = forward
