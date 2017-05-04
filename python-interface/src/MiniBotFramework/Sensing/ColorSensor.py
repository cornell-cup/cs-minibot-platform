from MiniBotFramework.Sensing.Sensor import Sensor
from MiniBotFramework.Lib.TCS34725 import TCS34725 as CSensor
import smbus
import math

def distance(p1, p2):
    """ Returns distance between two 3-tuples. 
    Used for evaluating color """
    return math.sqrt((p1[0]-p2[0])**2 + (p1[1]-p2[1])**2 + (p1[2]-p2[2])**2)

class ColorSensor(Sensor):
    """ Pre-determined set of colors and corresponding RGB 3-tuples
    that are the basis of color inputs. Sensor inputs will be compared
    with values in this dict to see what "color" it is closest to. """

    def __init__(self, bot, name, pin_number):
        Sensor.__init__(self, bot, name)
        self.pin_number = pin_number
        self.color_sensor = CSensor()
        self.bus = smbus.SMBus(1)
        self.bus.write_byte(0x29,0x80|0x12)
        ver = self.bus.read_byte(0x29)

        self.colors = {
            "RED":(7885,2631,3034),
            "GREEN":(4794,10432,8395),
            "BLUE":(14162,7582,4268),
            "ORANGE":(14162,7582,4268),
            "VIOLET":(8263,7538,9303),
            "YELLOW":(13772,12879,5783),
            "PINK":(11483,7839,8267)
        }

    def read(self):
        """ Returns a 3-tuple of RGB value """
        data = self.bus.read_i2c_block_data(0x29, 0)
        # clear = clear = data[1] << 8 | data[0]
        red = data[3] << 8 | data[2]
        green = data[5] << 8 | data[4]
        blue = data[7] << 8 | data[6]

        rgb = (red, green, blue)
        return rgb

    def read_color(self):
        """ Returns string of color """
        color_guess = ("", 999)
        color_actual = self.read()
        for c in self.colors:
            if(distance(self.colors[c],color_actual) == color_guess[1]):
                return c
            elif(distance(self.colors[c],color_actual) < color_guess[1]):
                color_guess = (c, self.colors[c])
        return color_guess[0]

