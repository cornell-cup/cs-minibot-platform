#!/usr/bin/python

from socket import *
import RPi.GPIO as GPIO
import multiprocessing, time, signal, os, sys, math

#From https//github.com/zephod/legopi
from lib2.legopi.lib import xbox_read

from multiprocessing import Process
from threading import Thread
from time import sleep

p = multiprocessing.Process(target=time.sleep, args=(1000,))
prepend_script="cup_minibot_prepend.py"
if (len(sys.argv[1]) > 0):
    prepend_script = sys.argv[1]
    print(prepend_script)

# Initialize our process
def run_script():
    from received import received

# https://docs.python.org/3/library/multiprocessing.html#multiprocessing-programming
def spawn_script_process(p):
    if (p.is_alive()):
        p.terminate()
    time.sleep(0.1)
    p = Process(target=run_script)
    p.start()
    # Return control to main after .1 seconds
    p.join(0.1)
    return p

# Prints and flushes so we see it right away
def print_flush(msg):
    print(msg)
    sys.stdout.flush()

# Parses cmd and creates a received.py consisting of the semantics of cmd with custom modules prepended
def runScript(cmd,coz):
    global p
    script = ""
    if cmd.find("SCRIPT") > 0:
        begin = cmd.find(",") + 1
        end = cmd.find(">")
        script = cmd[begin:end]
    elif cmd.find("WHEELS") > 0:
        cmd = cmd[cmd.find(",")+1:cmd.find(">")]
        wheels = []
        for i in range(4):
            index = cmd.find(",")
            if index == -1:
                val = str(0)
            else:
                val = cmd[:cmd.find(",")]
                cmd = cmd[cmd.find(",")+1:]
            wheels.append(val)
        script = "bot.set_wheel_power(" + wheels[0] + ","+wheels[1]+","+wheels[2] + ","+ wheels[3] + ")\nGPIO.cleanup()"
    else:
        print("Bad Input, please send SCRIPT or WHEELS command")

    try:
        os.mkdir("received")
    except:
        pass
    try:
        os.remove("received/received.py")
    except:
        pass
    received = open("received/received.py",'w')
    if (coz):
        prepend_module=open("received/cozmo_minibot.py","r")
    else:
        prepend_module=open("received/"+prepend_script,"r")
    for line in prepend_module:
        received.write(line)
        newline=''
    prepend_module.close()
    if (coz):
        for line in script:
            if line!=('\n') and line!=('\r') and line!=(')'):
                newline+=line
            if line==(")"):
                newline+=', bot)'
                received.write("	"+newline+'\n')
                newline=''
    else:
        received.write(script)
    if (coz):
        received.write('\n'+'cozmo.run_program(cozmo_program)')
    
    received.close()
    p = spawn_script_process(p)

# Defines WHEEL powers depending on button that is pressed 
def move_command(b):
    if b == "A":
        print("yes")
        runScript("<<<<WHEELS,0,0,0,0>>>>",False)
    elif b == "dd":
        runScript("<<<<WHEELS,-100,-100,-100,-100>>>>",False) 
    elif b == "dr":
        runScript("<<<<WHEELS,100,-100,100,-100>>>>",False)
    elif b == "dl":
        runScript("<<<<WHEELS,-100,100,-100,100>>>>",False)
    else:
        runScript("<<<<WHEELS,100,100,100,100>>>>",False)

def move_command(fl,fr,bl,br):
    runScript("<<<<WHEELS,"+fl+","+fr+","+bl+","+br+">>>>",False)


# Reads in xbox button inputs from controller directly attached to RPi
def xbox():
    lastX = 0
    lastY = 0
    print("running xbox")
    print("Setting up GPIO")
    GPIO.setmode(GPIO.BCM)
    GPIO.setup(13, GPIO.OUT)
    GPIO.setup(16, GPIO.OUT)
    GPIO.setup(19, GPIO.OUT)
    GPIO.setup(20, GPIO.OUT)

    pwmFrequency = 30 # Hz
    # 13 16 left
    #pwnRightA = GPIO.PWM(20,pwmFrequency)
    #pwnLeftA = GPIO.PWM(16,pwmFrequency)
    #pwnLeftB = GPIO.PWM(13,pwmFrequency)
    #pwnRightB = GPIO.PWM(19,pwmFrequency)

    #pwnLeftA.start(0)
    #pwnRightA.start(0)
    #pwnLeftB.start(0)
    #pwnRightB.start(0)

    pwn20 = GPIO.PWM(20, pwmFrequency)
    pwn16 = GPIO.PWM(16, pwmFrequency)
    pwn13 = GPIO.PWM(13, pwmFrequency)
    pwn19 = GPIO.PWM(19, pwmFrequency)

    pwn20.start(0)
    pwn16.start(0)
    pwn13.start(0)
    pwn19.start(0)

    wow=True


    for event in xbox_read.event_stream(deadzone=12000):

        #print("yay")
        
        # Convert input event into a string so we can parse it
        event_triggered = str(event)
        
        # Extracts the button pressed and value (0 or 1 depending on pressed or unpressed)
        button = event_triggered[event_triggered.find("(")+1:event_triggered.find(",")]
        value = event_triggered[event_triggered.find(",")+1:event_triggered.rfind(",")]
        #print(button)
        #print(value)

        if (button == "X1" or button == "Y1" or wow):
            wow=False
            if button == "X1":
                lastX = ((int(value)) / 32766)
            if button == "Y1":
                lastY = ((int(value)) / 32766)
            radius = math.sqrt(lastX*lastX + lastY*lastY)  
            angle = math.atan2(lastY,lastX)
            if (radius < 0):
                radius = 0
            radius = radius * 100 # Scaling
            if radius > 100:
                radius = 100
            
            if angle >=0 and angle <= math.pi/2:
                fl = radius
            elif angle > math.pi/2 and angle < math.pi:
                fl = radius * math.cos(angle*2 - math.pi)
            elif angle >= -math.pi and angle <= -math.pi/2:
                fl = -radius
            else:
                fl = radius * math.sin(angle)
            
            if angle > 0 and angle <= math.pi/2:
                fr = radius * math.sin(angle)
            elif angle >= math.pi/2 and angle <= math.pi:
                fr = radius
            elif angle < -math.pi/2 and angle > -math.pi:
                fr = radius * -math.cos(angle*2 + math.pi)             
            else:
                fr = -radius

            front_left = int(fl)
            front_right = int(fr)
            print("Setting the power to: " + str(front_left) + "," + str(front_right))
            if front_right < 0:
                front_right = -front_right
                BR = 100 - front_right
            if front_left < 0:
                front_left = -front_left
                BL = 100 - front_left
            if front_left > 100:
                front_left = 100
            if front_right > 100:
                front_right = 100
            BL=100-front_left
            BR=100-front_right
            print(BL)
            print(BR)
            #pwnRightB.ChangeDutyCycle(int(BR))
            #pwnLeftB.ChangeDutyCycle(int(BL))
            #pwnRightA.ChangeDutyCycle(int(front_right))
            #pwnLeftA.ChangeDutyCycle(int(front_left))
            

            print(angle)
            if radius > 10:
                if angle < 2 * math.pi / 3 and angle > math.pi / 3:
                    print("forwarz")
                    pwn20.ChangeDutyCycle(front_right)
                    pwn19.ChangeDutyCycle(BR)
                    pwn16.ChangeDutyCycle(front_left)
                    pwn13.ChangeDutyCycle(BL)
                elif angle > -(2 * math.pi / 3) and (angle < -math.pi / 3):
                    #backwardz
                    print("backz")
                    pwn20.ChangeDutyCycle(BR)
                    pwn19.ChangeDutyCycle(front_right)
                    pwn16.ChangeDutyCycle(BL)
                    pwn13.ChangeDutyCycle(front_left)
                elif angle < math.pi/2:
                    pwn20.ChangeDutyCycle(BR)
                    pwn19.ChangeDutyCycle(BR)
                    pwn16.ChangeDutyCycle(BL)
                    pwn13.ChangeDutyCycle(BL)
                elif angle < math.pi:
                    pwn20.ChangeDutyCycle(front_right)
                    pwn19.ChangeDutyCycle(front_right)
                    pwn16.ChangeDutyCycle(front_left)
                    pwn13.ChangeDutyCycle(front_left)
                elif angle < 3 * math.pi / 2:
                    pwn20.ChangeDutyCycle(BL)
                    pwn19.ChangeDutyCycle(BL)
                    pwn16.ChangeDutyCycle(BR)
                    pwn13.ChangeDutyCycle(BR)
                else:
                    pwn20.ChangeDutyCycle(BL)
                    pwn19.ChangeDutyCycle(BL)
                    pwn16.ChangeDutyCycle(BR)
                    pwn13.ChangeDutyCycle(BR)
            else:
                print("stahp")
                pwn20.ChangeDutyCycle(100)
                pwn19.ChangeDutyCycle(100)
                pwn13.ChangeDutyCycle(0)
                pwn16.ChangeDutyCycle(0)

        # Button is 1 when it is pressed
        #if value == "1":
            #move_command(button)            

def main(p):
    print("Script started")
    # Process Arguments
    cozmo=False
    if (len(sys.argv) > 0):
        # are we a cozmo? TODO: Make cleaner.
        if (str(sys.argv).find("cozmo") != -1):
            print_flush("Becoming a cozmo")
            cozmo=True
    serverPort = 10000
    serverSocket = socket(AF_INET, SOCK_STREAM)
    ip = "127.0.0.1"
    serverSocket.bind( (ip, serverPort) )
    serverSocket.listen(1)
    print("Waiting for connections!")
    connectionSocket, addr = serverSocket.accept()
    while True:
        print("Connection accepted")
        command = ""
        while True:
            command += connectionSocket.recv(1024).decode()
            if command.find(">>>>") > 0:
                runScript(command,cozmo)
                command = ""

# Since we are using multiple processes, need to check for main.
if (__name__ == "__main__"):
    threadxbox = Thread(target = xbox())
    threadxbox.start()
    main(p)
