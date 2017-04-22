from MiniBotFramework.Actuation.Actuator import Actuator

class GpioMotor(Actuator):

    PWM_FREQUENCY=30

    def __init__(self, bot, name, pin_pwm, pin_hl, GPIO):
        Actuator.__init__(self, bot, name)
        self.pin_pwm = pin_pwm
        self.pin_hl = pin_hl
        GPIO.setup(self.pin_pwm, GPIO.OUT)
        GPIO.setup(self.pin_hl, GPIO.OUT)

        self.pwm = GPIO.PWM(self.pin_pwm, GpioMotor.PWM_FREQUENCY)

        # Stop the bot
        GPIO.output(self.pin_pwm, GPIO.HIGH)
        GPIO.output(self.pin_hl, GPIO.LOW)
        self.value_pwm_pin = self.pwm #Duty cycle
        self.value_hl_pin = true #returns if is_Low
        self.state_stop = True
        self.state_forward = False

    def read(self):
        return "TODO"

    def rotate_forward(self,power):
        self.set(False,True)
        self.pin_pwm.ChangeDutyCycle(power)
        if self.pin_hl == 20:
            GPIO.output(self.pin_hl, GPIO.HIGH)
        else:
            GPIO.output(self.pin_hl, GPIO.LOW)

    def rotate_backward(self,power):
        self.set(False,False)
        self.pin_pwm.ChangeDutyCycle(power)
        if self.pin_hl == 20:
            GPIO.output(self.pin_hl, GPIO.LOW)
        else:
            GPIO.output(self.pin_hl, GPIO.HIGH)

    def stop(self):
        self.set(True,False)
        GPIO.output(self.pin_pwm, GPIO.HIGH)
        GPIO.output(self.pin_hl, GPIO.LOW)        

    def set(self, state_stop, state_forward):
        self.state_stop = state_stop
        self.state_forward = state_forward
