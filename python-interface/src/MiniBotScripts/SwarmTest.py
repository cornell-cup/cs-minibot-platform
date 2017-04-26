def run(bot):
    while True:
        print(bot.get_actuator_by_name("two_wheel_movement").get_value())
        bot.move_forward(25)
        bot.wait(1)
        bot.turn_clockwise(25)
        bot.wait(1)
        bot.move_backward(25)
        bot.wait(1)
        bot.turn_counter_clockwise(25)
        bot.wait(1)
