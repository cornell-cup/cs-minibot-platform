# Allows for Note objects to initialized via a given frequency and volume
# Imported for use in the module Note_Library.py
# Taken from < https://gist.github.com/ohsqueezy/6540433 >

##############################################################

# Generate a 440 Hz square waveform in Pygame by building an array of samples and play
# it for 5 seconds.  Change the hard-coded 440 to another value to generate a different
# pitch.
#
# Run with the following command:
#   python pygame-play-tone.py

from array import array
from time import sleep

import pygame
import pygame.mixer as pm
from pygame.mixer import Sound

class Note(Sound):

    def __init__(self, frequency, volume=0.1):
        self.frequency = frequency
        Sound.__init__(self, buffer=self.build_samples())
        self.set_volume(volume)    

    def build_samples(self):
        period = int(round(pm.get_init()[0] / self.frequency))
        samples = array("h", [0] * period)
        amplitude = 2 ** (abs(pm.get_init()[1]) - 1) - 1
        for time in range(period): #Originally xrange
            if time < period / 2:
                samples[time] = amplitude
            else:
                samples[time] = -amplitude
        return samples

#An example of the Note object being used
if __name__ == "__main__":
    pm.pre_init(44100, -16, 1, 1024) #Required init for Windows 10
    # pm.init() #Required init for other OS's? #TODO Research this further to confirm
    test = Note(440)
    test.play(-1)
    sleep(5)
