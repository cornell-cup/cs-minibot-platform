import pygame.mixer as pm
from pygame_play_tone import Note
from time import sleep

#Default volume for Notes
DEFAULT_VOLUME=0.2

# Notes that can be called on, where C4 is middle C
C0 = 16.35
C0_SHARP = 17.32
D0 = 18.35
D0_SHARP = 19.45
E0 = 20.6
F0 = 21.83
F0_SHARP = 23.12
G0 = 24.5
G0_SHARP = 25.96
A0 = 27.5
A0_SHARP = 29.14
B0 = 30.87
C1 = 32.7
C1_SHARP = 34.65
D1 = 36.71
D1_SHARP = 38.89
E1 = 41.2
F1 = 43.65
F1_SHARP = 46.25
G1 = 49
G1_SHARP = 51.91
A1 = 55
A1_SHARP = 58.27
B1 = 61.74
C2 = 65.41
C2_SHARP = 69.3
D2 = 73.42
D2_SHARP = 77.78
E2 = 82.41
F2 = 87.31
F2_SHARP = 92.5
G2 = 98
G2_SHARP = 103.83
A2 = 110
A2_SHARP = 116.54
B2 = 123.47
C3 = 130.81
C3_SHARP = 138.59
D3 = 146.83
D3_SHARP = 155.56
E3 = 164.81
F3 = 174.61
F3_SHARP = 185
G3 = 196
G3_SHARP = 207.65
A3 = 220
A3_SHARP = 233.08
B3 = 246.94
C4 = 261.63
C4_SHARP = 277.18
D4 = 293.66
D4_SHARP = 311.13
E4 = 329.63
F4 = 349.23
F4_SHARP = 369.99
G4 = 392
G4_SHARP = 415.3
A4 = 440
A4_SHARP = 466.16
B4 = 493.88
C5 = 523.25
C5_SHARP = 554.37
D5 = 587.33
D5_SHARP = 622.25
E5 = 659.25
F5 = 698.46
F5_SHARP = 739.99
G5 = 783.99
G5_SHARP = 830.61
A5 = 880
A5_SHARP = 932.33
B5 = 987.77
C6 = 1046.5
C6_SHARP = 1108.73
D6 = 1174.66
D6_SHARP = 1244.51
E6 = 1318.51
F6 = 1396.91
F6_SHARP = 1479.98
G6 = 1567.98
G6_SHARP = 1661.22
A6 = 1760
A6_SHARP = 1864.66
B6 = 1975.53

def prepPlaying():
	''' Initializes environment to play pygame noises '''
	pm.pre_init(44100, -16, 1, 1024)
	# pygame.init()
	# pm.init() #Only works for non-Windows? #TODO Research this further to confirm
	pm.init()

def playNote(note,time,volume=DEFAULT_VOLUME):
	''' Plays a sound of a given frequency [note] in Hertz for duration 
		[time] in seconds at a particular volume, where [volume] is a 
		number between 0.0 and 1.0'''
	sound = Note(note,volume)
	sound.play(-1)
	sleep(time)
	sound.stop()


def blurNote(note,time,volume=DEFAULT_VOLUME,last_note=False):
	''' Same as playNote, but will continue to play with other notes
		that are not specified to stop. In order to stop blurring a 
		selection of notes together, have the last note be a playNote or
		specify the last parameter [last_note] as True'''
	sound = Note(note,volume)
	sound.play(-1)
	sleep(time)
	if(last_note):
		sound.stop()


