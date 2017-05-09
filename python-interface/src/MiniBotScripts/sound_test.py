import Note_Library as nl

TEST_EMOTION = 3;

def showHappy():
	nl.playNote(nl.G6,0.15)
	nl.playNote(nl.A6,0.15)
	nl.playNote(nl.G6,0.15)
	nl.playNote(nl.A6,0.15)
	nl.playNote(nl.B6,0.25)

def showSad():
	nl.playNote(nl.G6,0.25)
	nl.playNote(nl.D6_SHARP,0.25)
	nl.playNote(nl.G3,0.5)
	nl.playNote(nl.G2,1)

def showSurprise():
	nl.playNote(nl.B6,0.10)
	nl.playNote(nl.B6,0.10)
	nl.playNote(nl.B6,0.10)
	nl.playNote(nl.B6,0.10)

def showAnger():
	nl.playNote(nl.G3,0.10)
	nl.playNote(nl.G6,0.10)
	nl.playNote(nl.G1,0.5)
	nl.playNote(nl.G3,0.10)
	nl.playNote(nl.G6,0.10)
	nl.playNote(nl.G1,0.5)

if __name__ == "__main__":
	nl.prepPlaying()

	if(TEST_EMOTION==0):
		showHappy()
	elif(TEST_EMOTION==1):
		showSad()
	elif(TEST_EMOTION==2):
		showSurprise()
	elif(TEST_EMOTION==3):
		showAnger()
