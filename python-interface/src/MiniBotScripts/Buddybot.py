import Note_Library as nl
import sample_audio as sa

TEST_EMOTION = 0;

SHOUT_THRESHOLD = 450
SPEAK_THRESHOLD = 450

# Should change at most by factor of 1 every second
HAPPY_FACTOR = 10
SAD_FACTOR = 200
SURPRISE_FACTOR = 1
ANGER_FACTOR = 50 #Currently not implemented

SAMPLE_FILE = "sample.wav"

bot_happy = 0
bot_sad = 0
bot_surprised = 0
bot_angry = 0

def neutralNoise():
    nl.playNote(nl.G5,0.15)

def neutralNoise2():
    nl.playNote(nl.G4,0.15)

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
    # bot.stop()
    # bot.wait(1)
    bot.move_backward(25)
    bot.wait(3)
    # bot.stop()
    # bot.wait(1)
    bot.turn_clockwise(25)
    bot.wait(5)
    # bot.stop()
    # bot.wait(1)
    bot.turn_counter_clockwise(25)
    bot.wait(5)
    # bot.stop()
    # bot.wait(1)

def sadSong():
    nl.playNote(nl.G6,0.25)
    nl.playNote(nl.D6_SHARP,0.25)
    nl.playNote(nl.G3,0.5)
    nl.playNote(nl.G2,1)

def showSad(bot):
    sadSong()
    bot.move_backward(10)
    bot.wait(5)
    # bot.stop()

def surpriseSong():
    nl.playNote(nl.B6,0.10)
    nl.playNote(nl.B6,0.10)
    nl.playNote(nl.B6,0.10)
    nl.playNote(nl.B6,0.10)

def showSurprise(bot):
    surpriseSong()
    bot.move_backward(80)
    bot.wait(0.75)
    #bot.stop()
    bot.move_backward(80)
    bot.wait(0.75)
    #bot.stop()
    bot.move_backward(80)
    bot.wait(0.75)
    #bot.stop()

def angerSong():
    nl.playNote(nl.G3,0.10)
    nl.playNote(nl.G6,0.10)
    nl.playNote(nl.G1,0.5)
    nl.playNote(nl.G3,0.10)
    nl.playNote(nl.G6,0.10)
    nl.playNote(nl.G1,0.5)

def showAnger(bot):
    bot.move_forward(80)
    bot.wait(1)
    bot.move_backward(80)
    bot.wait(0.75)
    bot.move_forward(80)
    bot.wait(1)
    bot.move_backward(80)
    bot.wait(0.75)
    bot.move_forward(80)
    bot.wait(1)
    bot.move_backward(80)
    bot.wait(0.75)

def emotionUpdate(bot):
    event_trigger = False

    if bot_happy>=HAPPY_FACTOR:
        print("Happy")
        # bot_happy=0
        # bot_sad=0 #If happy then isn't sad
        showHappy(bot)
        event_trigger = True
    elif bot_sad>=SAD_FACTOR:
        # bot_sad=0
        # bot_happy=0 #If sad then isn't happy
        print("Sad")
        showSad(bot)
        event_trigger = True
    elif bot_surprised>=SURPRISE_FACTOR:
        print("Surprise")
        # bot_surprised=0
        showSurprise(bot)
        event_trigger = True
    elif bot_angry>=ANGER_FACTOR:
        print("Anger")
        # bot_angry=0
        showAnger(bot)
        event_trigger = True

    # Reset emotion counters to prevent mood swings
    if event_trigger:
        bot_happy=0
        bot_sad=0
        bot_surprised=0
        bot_angry=0



def run(bot):
    nl.prepPlaying()
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

    # while True:
    #     # Get audio input
    #     sa.sampleAudio(SAMPLE_FILE)
    #     magnitude, lower_thres, upper_thres = getAvgFreq(SAMPLE_FILE)

    #     if magnitude>SHOUT_THRESHOLD:
    #         bot_surprised+=1
    #     elif upper_thres < 0:
    #         bot_sad+=1
    #     elif upper_thres <= 255 && lower_thres >= 85 && magnitude > SPEAK_THRESHOLD:
    #         bot_happy+=1

    #     emotionUpdate()
