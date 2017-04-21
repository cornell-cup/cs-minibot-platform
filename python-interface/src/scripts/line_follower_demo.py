#COPY AND PASTE THIS CODE INTO THE GUI TERMINAL

# bot.move_forward(70)
# s = GPIOSensor(bot, 'right', 1)
# s1 = GPIOSensor(bot, 'left', 2)
# s2 = GPIOSensor(bot, 'center', 3)
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