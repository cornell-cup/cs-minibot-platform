#Given two GPIO motors and desired wheel speeds for left and right wheel, sets the GPIO signals
from MiniBotFramework.Actuation.Actuator import Actuator

class TwoWheelMovement(Actuator):

    def __init__(self, bot, name, motorL, motorR):
        Actuator.__init__(self, bot, name)
        self.motor_left = motorL
        self.motor_right = motorR

    def move(self, speedL, speedR):
        # Normalize speeds
        if speedL < -100:
            speedL = -100
        if speedR < -100:
            speedR = -100
        if speedL > 100:
            speedL = 100
        if speedR > 100:
            speedR = 100

        # Apply speeds to motors
        # Left
        if speedL > 0:
            self.motor_left.rotate_forward(speedL)
        elif speelL < 0:
            self.motor_left.rotate_backward(-speedL)
        else:
            self.motor_left.stop()

        # Right
        if speedR > 0:
            self.motor_right.rotate_backward(speedR)
        elif speedR < 0:
            self.motor_right.rotate_forward(-speedR)
        else:
            self.motor_right.stop()
