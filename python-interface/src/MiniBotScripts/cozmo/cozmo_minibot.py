from received.base_minibot import BaseMiniBot
import cozmo
import time
from cozmo.util import distance_mm, speed_mmps, degrees

class CozmoMiniBot(BaseMiniBot):
    def __init__(self):
        BaseMiniBot.__init__(self)
        self.robot=cozmo.robot.Robot
        self.action=None

def move_forward(self, power):
    if self.action is not None:
        self.action.abort()
        self.robot.stop_all_motors()
        self.action=None
    self.action = self.robot.drive_straight(distance=distance_mm(10000), speed=speed_mmps(power))

def move_backward(self,power):
    if self.action is not None:
        self.action.abort()
    self.action =   self.robot.drive_straight(distance=distance_mm(-10000), speed=speed_mmps(power))

def turn_left(self):
    if self.action is not None:
        self.action.abort()
    self.action =   self.robot.turn_in_place(angle=-10000)

def turn_right(self):
    if self.action is not None:
        self.action.abort()
    self.action =  self.robot.turn_in_place(angle=10000)

def wait(self, t):
    time.sleep(t)

def cozmo_program(bot):
