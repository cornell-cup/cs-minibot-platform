import math

def run(bot):

    cs = bot.get_sensor_by_name("ColorSensor")
    cs.calibrate()

    print "================== TESTING =================="

    while len(raw_input("GO?"))>-1:
        print "RGB: " + str(cs.read())
        color = str(cs.read_color())
        print "Color: " + color

        if(color=="RED"):
            # stop
        else:
            # go

