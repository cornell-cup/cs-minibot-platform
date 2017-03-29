from cup_minibot_prepend import CupMiniBot
from .sensor.Sensor import Sensor
bot = CupMiniBot()
left = Sensor(bot, 'left')
right = Sensor(bot, 'right')
center = Sensor(bot, 'center')
# redundant? bot.register_sensor(left)
# redundant? bot.register_sensor(right)
# redundant? bot.register_sensor(center)

end = False
while(not end):

    while(center.read()==1):
        bot.move_foward(100)
    while(center.read() != 1):
        if(left.read() == 1):
            bot.turn_clockwise(100)
        else if(right.read() == 1):
            bot.turn_counter_clockwise(100)
        else:
            end = True
            break
