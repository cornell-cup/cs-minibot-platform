import numpy as np
# import matplotlib.pyplot as plt
import pyaudio as pa
import wave
from time import sleep
from scipy.io import wavfile as wv

#Constants used for sampling audio
CHUNK = 1024
FORMAT = pa.paInt16
CHANNELS = 1
RATE = 44100 #DOUBLE CHECK ACTUAL MIC
RECORD_TIMEFRAME = 1.0 # 0.5 #Time in seconds
OUTPUT_FILE = "sample.wav"


def sampleAudio(wav_name):
	"""Samples audio from the microphone for a given period of time. 
		The output file is saved as [wav_name]

		Code here taken from the front page of: 
		< https://people.csail.mit.edu/hubert/pyaudio/ > """
	
	# Open the recording session
	rec_session = pa.PyAudio()
	stream = rec_session.open(format=FORMAT,
		channels=CHANNELS,rate=RATE,input=True,frames_per_buffer=CHUNK)
	print("Start recording")
	frames = []

	# Sample audio frames for given time period
	for i in range(0, int(RATE/CHUNK*RECORD_TIMEFRAME)):
		data = stream.read(CHUNK)
		frames.append(data)

	# Close the recording session
	stream.stop_stream()
	stream.close()
	rec_session.terminate()

	#Create the wav file
	output_wav = wave.open(wav_name,"wb")
	output_wav.setnchannels(CHANNELS)
	output_wav.setsampwidth(rec_session.get_sample_size(FORMAT))
	output_wav.setframerate(RATE)
	output_wav.writeframes(b''.join(frames))
	output_wav.close()


def getAvgFreq(wav_file):
	"""Analyzes an audio sample (must be a 16-bit WAV file with one channel) 
	   and returns its average frequency

	   Basic procedure of processing audio taken from: 
	   < http://samcarcagno.altervista.org/blog/basic-sound-processing-python/ >"""
	
	# #Open wav file for analysis
	# sound_sample = wave.open(wav_file, "rb")

	# #Get sampling frequency
	# sample_freq = sound_sample.getframerate()

	# #Extract audio frames to be analyzed
	# audio_frames = sound_sample.readframes(sound_sample.getnframes())

	#Open wav file for analysis and get sampling frequency
	sample_freq, sound_sample = wv.read(wav_file)

	#Extract sound frame values to be analyzed
	floating_samples = sound_sample/(2.**15)

	# Get amplitude of soundwave section
	freq = np.fft.fft(floating_samples)	
	amplitude = np.abs(freq)

	#Get bins/thresholds for frequencies
	freqbins = np.fft.fftfreq(CHUNK,1.0/sample_freq)

	#Get the range that the max amplitude falls in. This represents the loudest noise
	loudest = np.argmax(amplitude) #Shouldn't have to specify axis
	lower_thres = freqbins[loudest]
	upper_thres = (freqbins[1]-freqbins[1])+lower_thres
	
	sound_sample.close()

	return lower_thres, upper_thres


if __name__ == "__main__":
	# print("Wait to start...")
	# sleep(3)
	print("Recording!")
	sampleAudio(OUTPUT_FILE)
	print("Stop recording")

	print(getAvgFreq(OUTPUT_FILE))

