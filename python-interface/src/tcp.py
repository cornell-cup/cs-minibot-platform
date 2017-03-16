#!/usr/bin/python

from socket import *
import multiprocessing, time, signal, os, sys

#From https//github.com/zephod/legopi
from lib.legopi.lib import xbox_read

from multiprocessing import Process
from threading import Thread
from time import sleep

p = multiprocessing.Process(target=time.sleep, args=(1000,))

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
        script = "bot.set_wheel_power(" + wheels[0] + ","+wheels[1]+","+wheels[2] + ","+ wheels[3] + ")"
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
        prepend_module=open("received/cup_minibot_prepend.py","r")
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
    print("works")
    p = spawn_script_process(p)
#connectionSocket.close()

def move_command(b):
    if b == "A":
        runScript("<<<<WHEELS,0,0,0,0>>>>",False)
    if b == "dd":
        runScript("<<<<WHEELS,-100,-100,-100,-100>>>>",False) 
        print("yes")
    elif b == "dr":
        runScript("<<<<WHEELS,100,-100,100,-100>>>>",False)
    elif b == "dl":
        runScript("<<<<WHEELS,-100,100,-100,100>>>>",False)
    else:
        runScript("<<<<WHEELS,100,100,100,100>>>>",False)

def xbox():
    for event in xbox_read.event_stream(deadzone=12000):
        event_triggered = str(event)
        button = event_triggered[event_triggered.find("(")+1:event_triggered.find(",")]
        value = event_triggered[event_triggered.find(",")+1:event_triggered.rfind(",")]
        if value == "1":
            move_command(button)            

def main(p):
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
    connectionSocket, addr = serverSocket.accept()
    while True:
        command = ""
        while True:
            command += connectionSocket.recv(1024).decode()
            if command.find(">>>>") > 0:
                runScript(command,cozmo)

# Since we are using multiple processes, need to check for main.
if (__name__ == "__main__"):
    threadxbox = Thread(target = xbox())
    threadxbox.start()
    main(p)
