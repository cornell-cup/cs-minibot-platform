#!/usr/bin/python

from socket import *
import multiprocessing, time, signal, os, sys
from multiprocessing import Process

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
                break

        script = ""
        if command.find("SCRIPT") > 0:
            begin = command.find(",") + 1
            end = command.find(">")
            script = command[begin:end]

        elif command.find("WHEELS") > 0:
            command = command[command.find(",")+1:command.find(">")]
            wheels = []
            for i in range(4):
                index = command.find(",")
                if index == -1:
                    val = str(0)
                else:
                    val = command[:command.find(",")]
                    command = command[command.find(",")+1:]
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
        if (cozmo):
            prepend_module=open("received/cozmo_minibot.py","r")
        else:
            prepend_module=open("received/cup_minibot_prepend.py","r")
        for line in prepend_module:
            received.write(line)
            newline=''
        if (cozmo):
            received.write("bot=CozmoMiniBot()\n")
        else:
            pass
            #received.write("bot=CupMiniBot()\n")
        prepend_module.close()
        received.write(script)
        received.close()
        p = spawn_script_process(p)
        #connectionSocket.close()

# Since we are using multiple processes, need to check for main.
if (__name__ == "__main__"):
    p = multiprocessing.Process(target=time.sleep, args=(1000,))
    main(p)
