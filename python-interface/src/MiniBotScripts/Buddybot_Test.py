import Note_Library
from pygame_play_tone import Note

import pygame
from pygame.mixer import Sound, get_init, pre_init

def happySong(bot):
    print("First note")
    test = Note(440)
    test.play(0)
    bot.wait(1)
    print("Second note")
    test2 = Note(523)
    test2.play(0)
    bot.wait(1)
    print("Third note")
    test3 = Note(659)
    test3.play(0)
    bot.wait(2)
    # nt.Note(Note_Library.NOTE_C0).play(0)
    # bot.wait(1)
    # nt.Note(Note_Library.NOTE_E0).play(0)
    # bot.wait(1)
    # nt.Note(Note_Library.NOTE_G0).play(0)
    # bot.wait(2)

def showHappy(bot):
    happySong(bot)
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

def showSad(bot):
    pass

def showSurprise(bot):
    pass

def showAnger(bot):
    pass

def run(bot):
    # pre_init(44100, -16, 1, 1024)
    # pygame.init()
    # bot.wait(1)
    bot.move_forward(20)
    bot.wait(3)
    bot.stop()
    # bot.wait(3)
    #print("Done FD")
    #bot.move_backward(20)
    #bot.wait(3)
    #bot.stop()
    # bot.wait(3)
    print("Done BK")

    test_emotion = 0

    if test_emotion == 0:
        print("Happy")
        showHappy(bot)
    elif test_emotion == 1:
        print("Sad")
        showSad(bot)
    elif test_emotion == 2:
        print("Surprise")
        showSurprise(bot)
    elif test_emotion == 3:
        print("Anger")
        showAnger(bot)

    bot.stop()
