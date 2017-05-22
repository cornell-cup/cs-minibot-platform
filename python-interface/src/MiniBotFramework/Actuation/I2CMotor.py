from MiniBotFramework.Actuation.Actuator import Actuator
import smbus, time

class I2CMotor(Actuator):

    def __init__(self, bot, name, address, id_number, reversed):
        # 1 for right, 2 for left
        Actuator.__init__(self, bot, name)
        self.bus = smbus.SMBus(1)
        self.address = address
        self.reversed = reversed
        self.id_number = id_number

        # Stop the bot
        self.speed = 0
        self.forward = True
        self.i2cmove(0)

    def writeNumber(self, power):
         self.bus.write_byte_data(self.address,self.id_number,power)
    
    def i2cmove(self,power):
        power = int((power / 100.0) * 127.0)
        power = power & 0x00FF
        self.writeNumber(power)

    def read(self):
        return (self.speed, self.forward)

    def rotate_forward(self,power):
        if self.reversed:
            self.rotate_backward_true(power)
        else:
            self.rotate_forward_true(power)

    def rotate_backward(self,power):
        if not self.reversed:
            self.rotate_backward_true(power)
        else:
            self.rotate_forward_true(power)

    def rotate_forward_true(self,power):
        """ Requires 0 <= power <= 100 """
        self.set(power,True)
        self.i2cmove(power)

    def rotate_backward_true(self,power):
        """ Requires 0 <= power <= 100 """
        self.set(power,False)
        self.i2cmove(-power)

    def stop(self):
        self.set(0,True)
        self.i2cmove(0)

    def set(self, speed, forward):
        self.speed = speed
        self.forward = forward

