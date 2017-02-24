import cozmo
import time
from cozmo.util import distance_mm, speed_mmps, degrees

#TODO: Refactor this to subclass the baseminibot abstract class


def move_forward(dist, robot: cozmo.robot.Robot):
  robot.drive_straight(distance=distance_mm(dist), speed=speed_mmps(100)).wait_for_completed()

def move_backward(dist, robot: cozmo.robot.Robot):
  robot.drive_straight(distance=distance_mm(-dist), speed=speed_mmps(100)).wait_for_completed()

def turn_left(angle, robot: cozmo.robot.Robot):
  robot.turn_in_place(angle=degrees(-angle)).wait_for_completed()

def turn_right(angle, robot: cozmo.robot.Robot):
  robot.turn_in_place(angle=degrees(angle)).wait_for_completed()

def wait(t, robot: cozmo.robot.Robot):
    time.sleep(t)

def cozmo_program(robot: cozmo.robot.Robot):
