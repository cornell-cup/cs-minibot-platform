import MiniBotFramework.Sound.note_library as nl
import MiniBotFramework.Sound.live_audio_sample as las
import random

#Used for individual emotion testing 
TEST_EMOTION = 0 #TODO remove once emotions finalized

#Noise frequency thresholds #TODO check if correct
SHOUT_THRESHOLD = 850
SPEAK_THRESHOLD = 450

#Threshold for emotion values being triggered by their respetive counters
HAPPY_FACTOR = 10
SAD_FACTOR = 200
SURPRISE_FACTOR = 1
ANGER_FACTOR = 50 #Currently not implemented

#Name for file used in live audio sampling
SAMPLE_FILE = "sample.wav"

#Global emotion counters
#Incremented if a particular event causes Buddybot to feel a particular emotion
#Once a counter reaches its respective threshold, its corresponding emotion is triggered
#Should change at most by factor of 1 every second
bot_happy = 0
bot_sad = 0
bot_surprised = 0
bot_angry = 0

def randomEmotion():
    return random.randint(0,3)

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
    bot.move_backward(25)
    bot.wait(3)
    bot.turn_clockwise(25)
    bot.wait(5)
    bot.turn_counter_clockwise(25)
    bot.wait(5)    

def sadSong():
    nl.playNote(nl.G6,0.25)
    nl.playNote(nl.D6_SHARP,0.25)
    nl.playNote(nl.G3,0.5)
    nl.playNote(nl.G2,1)

def showSad(bot):
    sadSong()
    bot.move_backward(10)
    bot.wait(5)

def surpriseSong():
    nl.playNote(nl.B6,0.10)
    nl.playNote(nl.B6,0.10)
    nl.playNote(nl.B6,0.10)
    nl.playNote(nl.B6,0.10)

def showSurprise(bot):
    surpriseSong()
    bot.move_backward(80)
    bot.wait(0.75)
    bot.move_backward(80)
    bot.wait(0.75)
    bot.move_backward(80)
    bot.wait(0.75)

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
    global bot_happy
    global bot_sad
    global bot_surprised
    global bot_angry

    #Allow for Buddybot to make noise
    nl.prepPlaying()

    #Test movement
    bot.move_forward(20)
    bot.wait(3)
    bot.stop()    

    #Test the given emotion #TODO Remove when done testing
    while True:

        bot.wait(5)
        TEST_EMOTION = randomEmotion()

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

    #TODO Fully integrate audio code below with emotions
    # while True:
    #     # Get audio input
    #     las.sampleAudio(SAMPLE_FILE)
    #     magnitude, lower_thres, upper_thres = getAvgFreq(SAMPLE_FILE)

    #     if magnitude>SHOUT_THRESHOLD:
    #         bot_surprised+=1
    #     elif upper_thres < 0:
    #         bot_sad+=1
    #     elif upper_thres <= 255 && lower_thres >= 85 && magnitude > SPEAK_THRESHOLD:
    #         bot_happy+=1

    #TODO Replace TEST_EMOTION with this
    #     emotionUpdate()

