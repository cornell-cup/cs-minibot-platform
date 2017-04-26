import os
from os import sys, path
sys.path.append(os.path.dirname('../src/received/'))
sys.path.append(os.path.join('../src/received/sensor/'))
#sys.path.append(os.path.join(os.path.dirname('../src/received/'), '..'))
from sensor import Sensor
from cup_minibot_prepend import CupMiniBot

bot = CupMiniBot()
left = Sensor(bot, 'left')
right = Sensor(bot, 'right')
center = Sensor(bot, 'center')

end = False
while(not end):

    while(center.read()==1):
        bot.move_foward(100)
    while(center.read() != 1):
        if(left.read() == 1):
            bot.turn_clockwise(100)
        elif(right.read() == 1):
            bot.turn_counter_clockwise(100)
        else:
            end = True
            break
