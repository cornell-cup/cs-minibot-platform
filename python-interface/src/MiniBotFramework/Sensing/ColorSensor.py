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
        self.bus.write_byte(0x29, 0x80|0x00) # 0x00 = ENABLE register
        self.bus.write_byte(0x29, 0x01|0x02) # 0x01 = Power on, 0x02 RGB sensors enabled
        self.bus.write_byte(0x29, 0x80|0x14) # Reading results start register 14, LSB then MSB

        self.colors = {
            # TRIAL 1
            # "RED":(7885,2631,3034),
            # "GREEN":(4794,10432,8395),
            # "BLUE":(14162,7582,4268),
            # "ORANGE":(14162,7582,4268),
            # "VIOLET":(8263,7538,9303),
            # "YELLOW":(13772,12879,5783),
            # "PINK":(11483,7839,8267)

            # TRIAL 2 - rugged side of color mat, stationary bot, 100 avg
            # "RED":(151,49,58),
            # "GREEN":(160,357,286),
            # "BLUE":(76,158,198),
            # "ORANGE":(250,134,76),
            # "VIOLET":(137,128,156),
            # "YELLOW":(245,229,103),
            # "PINK":(236,165,172)

            # TRIAL 3 - flat side of color mat, moving bot, 100 avg
            "RED":(155,68,70),
            "GREEN":(81,170,139),
            "BLUE":(73,139,167),
            "ORANGE":(231,138,83),
            "VIOLET":(140,138,158),
            "YELLOW":(241,234,113),
            "PINK":(187,150,150)
        }

        self.colors_normalized = self.normalize(self.colors)

    def normalize(self, color_dict):
        norm = {}
        for color in color_dict:
            sum = color_dict[color][0]+color_dict[color][1]+color_dict[color][2]
            norm[color] = (color_dict[color][0]/sum,color_dict[color][1]/sum,color_dict[color][2]/sum)
        return norm

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
        color_guess = ("", 99999999999999999999999999) #tuple of (color, distance from color to input)
        color_actual = self.read()
        for c in self.colors_normalized:
            dist = distance(self.colors_normalized[c],color_actual)
            print "    " + c+ " dist: " + str(dist)
            if(dist < color_guess[1]):
                color_guess = (c, dist)
                print "    new guess is " + color_guess[0]
        return color_guess[0]

