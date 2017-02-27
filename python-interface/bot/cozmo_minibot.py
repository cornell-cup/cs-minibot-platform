from received.base_minibot import BaseMiniBot
import cozmo
import time
from cozmo.util import distance_mm, speed_mmps, degrees

class CozmoMiniBot(BaseMiniBot):
    def __init__(self):
        BaseMiniBot.__init__(self)
        self.robot=cozmo.robot.Robot

    def move_forward(self, power):
        #TODO: FIX  
        self.robot.drive_straight(distance=distance_mm(power), speed=speed_mmps(100)).wait_for_completed()

    def move_backward(self, power):
        #TODO: FIX
        self.robot.drive_straight(distance=distance_mm(-power), speed=speed_mmps(100)).wait_for_completed()

    def turn_clockwise(self, power):
                #TODO: FIX
        self.robot.turn_in_place(angle=degrees(-power)).wait_for_completed()

    def turn_counter_clockwise(self, power):
        #TODO: FIX
        self.robot.turn_in_place(angle=degrees(power)).wait_for_completed()
