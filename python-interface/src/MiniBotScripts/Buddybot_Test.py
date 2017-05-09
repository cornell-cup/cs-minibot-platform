import Note_Library as nl
#import pygame
import sample_audio as sa

TEST_EMOTION = 3;

def happySong():
    print("First note")        
    nl.playNote(nl.G6,0.15)
    print("Second note")
    nl.playNote(nl.A6,0.15)
    print("Third note")
    nl.playNote(nl.G6,0.15)
    nl.playNote(nl.A6,0.15)
    nl.playNote(nl.B6,0.25)

def showHappy(bot):
    happySong()
    bot.move_forward(25)
    bot.wait(3)
    bot.stop()
    # bot.wait(1)
    bot.move_backward(25)
    bot.wait(3)
    bot.stop()
    # bot.wait(1)
    bot.turn_clockwise(25)
    bot.wait(5)
    bot.stop()
    # bot.wait(1)
    bot.turn_counter_clockwise(25)
    bot.wait(5)
    bot.stop()
    # bot.wait(1)

def sadSong():
    nl.playNote(nl.G6,0.25)
    nl.playNote(nl.D6_SHARP,0.25)
    nl.playNote(nl.G3,0.5)
    nl.playNote(nl.G2,1)

def showSad(bot):
    pass

def surpriseSong():
    nl.playNote(nl.B6,0.10)
    nl.playNote(nl.B6,0.10)
    nl.playNote(nl.B6,0.10)
    nl.playNote(nl.B6,0.10)

def showSurprise(bot):
    pass

def angerSong():
    nl.playNote(nl.G3,0.10)
    nl.playNote(nl.G6,0.10)
    nl.playNote(nl.G1,0.5)
    nl.playNote(nl.G3,0.10)
    nl.playNote(nl.G6,0.10)
    nl.playNote(nl.G1,0.5)

def showAnger(bot):
    pass

def run(bot):
    bot.move_forward(20)
    bot.wait(3)
    bot.stop()
    # bot.wait(3)
    #print("Done FD")
    #bot.move_backward(20)
    #bot.wait(3)
    #bot.stop()
    # bot.wait(3)
    # print("Done BK")

    print("Emotion Time")
    if TEST_EMOTION == 0:
        print("Happy")
        showHappy(bot)
    elif TEST_EMOTION == 1:
        print("Sad")
        showSad(bot)
    elif TEST_EMOTION == 2:
        print("Surprise")
        showSurprise(bot)
    elif TEST_EMOTION == 3:
        print("Anger")
        showAnger(bot)

    bot.stop()
