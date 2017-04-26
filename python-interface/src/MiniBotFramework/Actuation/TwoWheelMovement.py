#Given two GPIO motors and desired wheel speeds for left and right wheel, sets the GPIO signals
from MiniBotFramework.Actuation.Actuator import Actuator

class TwoWheelMovement(Actuator):

    def __init__(self, bot, name, motorL, motorR):
        Actuator.__init__(self, bot, name)
        self.motor_left = motorL
        self.motor_right = motorR
        self.left = 0
        self.right = 0

    def get_value(self):
        return (self.left,self.right)

    def move(self, speedL, speedR):
        limiter = 50

        # Normalize speeds
        if speedL < -limiter:
            speedL = -limiter
        if speedR < -limiter:
            speedR = -limiter
        if speedL > limiter:
            speedL = limiter
        if speedR > limiter:
            speedR = limiter

        self.left = speedL
        self.right = speedR

        # Apply speeds to motors
        # Left
        if speedL > 0:
            self.motor_left.rotate_backward(speedL)
        elif speedL < 0:
            self.motor_left.rotate_forward(-speedL)
        else:
            self.motor_left.stop()

        # Right
        if speedR > 0:
            self.motor_right.rotate_forward(speedR)
        elif speedR < 0:
            self.motor_right.rotate_backward(-speedR)
        else:
            self.motor_right.stop()
