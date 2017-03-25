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

#
# bot.move_forward(70)
# s = GPIOSensor(bot, 'bot1', 1)
# state = 0
# while(True):
#     data = s.readAll()
#     j = json.loads(data)
#     center = j['center']['data']
#     right = j['right']['data']
#     left = j['left']['data']
#
#     if left != 1 and right != 1:
#         bot.move_forward(70)
#     else:
#         if left and right:
#             bot.move_forward(70)
#             state = 1
#         elif right:
#             bot.turn_clockwise(70)
#         elif left:
#             bot.turn_counter_clockwise(70)
