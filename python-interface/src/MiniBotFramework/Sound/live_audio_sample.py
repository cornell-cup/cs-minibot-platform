import numpy as np
import matplotlib.pyplot as plt #Used for graphing audio tests
import pyaudio as pa
import wave
from time import sleep

#Constants used for sampling audio
CHUNK = 1024
FORMAT = pa.paInt16
CHANNELS = 1
RATE = 44100 # Must match rate at which mic actually samples sound
RECORD_TIMEFRAME = 1.0 #Time in seconds
OUTPUT_FILE = "sample.wav"

#Flag for plotting sound input waves for debugging and implementation purposes
TESTING_GRAPHS = True

def sampleAudio(wav_name=OUTPUT_FILE):
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

	#Create the wav file for analysis
	output_wav = wave.open(wav_name,"wb")
	output_wav.setnchannels(CHANNELS)
	output_wav.setsampwidth(rec_session.get_sample_size(FORMAT))
	output_wav.setframerate(RATE)
	output_wav.writeframes(b''.join(frames))
	output_wav.close()


def getAvgFreq(wav_file=OUTPUT_FILE):
	"""Analyzes the audio sample [wav_file] (must be a 16-bit WAV file with 
	   one channel) and returns maximum magnitude of the most prominent sound 
	   and the frequency thresholds it falls between.

	   Basic procedure of processing audio taken from: 
	   < http://samcarcagno.altervista.org/blog/basic-sound-processing-python/ >"""
	
	#Open wav file for analysis
	sound_sample = wave.open(wav_file, "rb")

	#Get sampling frequency
	sample_freq = sound_sample.getframerate()

	#Extract audio frames to be analyzed
	# audio_frames = sound_sample.readframes(sound_sample.getnframes())
	audio_frames = sound_sample.readframes(1024)

	converted_val = []

	#COnvert byte objects into frequency values per frame
	for i in range(0,len(audio_frames),2):
		if ord(audio_frames[i+1])>127:
			converted_val.append(-(ord(audio_frames[i])+(256*(255-ord(audio_frames[i+1])))))
		else:
			converted_val.append(ord(audio_frames[i])+(256*ord(audio_frames[i+1])))
	
	#Fit into numpy array for FFT analysis
	freq_per_frame = np.array(converted_val)


	# Get amplitude of soundwave section
	freq = np.fft.fft(freq_per_frame)
	amplitude = np.abs(freq)
	amplitude = amplitude/float(len(freq_per_frame))
	amplitude = amplitude**2

	#Get bins/thresholds for frequencies
	freqbins = np.fft.fftfreq(CHUNK,1.0/sample_freq)

	x = np.linspace(0.0,1.0,1024)

	# Plot data if need visualization
	if(TESTING_GRAPHS):
		#Plot raw data
		plt.plot(converted_val)
		plt.title("Raw Data")
		plt.xlabel("Time (ms)")
		plt.ylabel("Frequency (Hz)")
		plt.show()

		#Plot frequency histogram
		plt.plot(freqbins[:16],amplitude[:16])
		plt.title("Processed Data")
		plt.xlabel("Frequency Bins")
		plt.ylabel("Magnitude")
		plt.show()
	

	#Get the range that the max amplitude falls in. This represents the loudest noise
	magnitude = np.amax(amplitude) 
	loudest = np.argmax(amplitude) 
	lower_thres = freqbins[loudest]
	upper_thres = (freqbins[1]-freqbins[0])+lower_thres
	
	#Close wav file 
	sound_sample.close()

	#Return the magnitude of the sound wave and its frequency threshold for analysis
	return magnitude, lower_thres, upper_thres

#Use for testing microphone input
if __name__ == "__main__":
	# print("Wait 3 seconds to start...")
	# sleep(3)
	print("Recording!")
	sampleAudio(OUTPUT_FILE)
	print("Stop recording!")
	print("Analyzing...")
	mag, lower, upper = getAvgFreq(OUTPUT_FILE)
	print("Magnitude is "+str(mag))
	print("Lower bin threshold is "+str(lower))
	print("Upper bin threshold is "+str(upper))

