def run(bot):
    # while True:
    #     print(bot.get_actuator_by_name("two_wheel_movement").get_value())
    #     bot.move_forward(25)
    #     bot.wait(1)
    #     bot.turn_clockwise(25)
    #     bot.wait(1)
    #     bot.move_backward(25)
    #     bot.wait(1)
    #     bot.turn_counter_clockwise(25)
    #     bot.wait(1)

    while x in range(1,10):
        cs = bot.get_sensor_by_name("ColorSensor")
        print "RGB: " + str(cs.read())
        print "Color: " + str(cs.read_color())
