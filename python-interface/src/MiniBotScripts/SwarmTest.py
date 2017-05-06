import math

def run(bot):

    cs = bot.get_sensor_by_name("ColorSensor")
    cs.calibrate()

    print "================== TESTING =================="

    while len(raw_input("GO?"))>0:
        print "RGB: " + str(cs.read())
        print "Color: " + str(cs.read_color())