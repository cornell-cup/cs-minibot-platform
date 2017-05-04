import math

def distance(p1, p2):
    """ Returns distance between two 3-tuples. 
    Used for evaluating color """
    return math.sqrt((p1[0]-p2[0])**2 + (p1[1]-p2[1])**2 + (p1[2]-p2[2])**2)

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

    print distance((0,0,0), (3,4,0))

    while len(input("GO?"))>0:
        cs = bot.get_sensor_by_name("ColorSensor")
        print "RGB: " + str(cs.read())
        print "Color: " + str(cs.read_color())