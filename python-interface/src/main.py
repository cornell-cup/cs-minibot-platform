import json, time, multiprocessing, importlib, sys
import MiniBotFramework
from threading import Thread
from multiprocessing import Process
from multiprocessing.managers import BaseManager
from Queue import Queue

# Constants
CONFIG_LOCATION = "MiniBotConfig/config.json"

def main():
    #BaseManager.register('MiniBot',MiniBotFramework.MiniBot.MiniBot)
    #manager = BaseManager()
    #manager.start()

    #p = multiprocessing.Process(target=time.sleep, args=(1000,))
    p = None
    print("Initializing MiniBot Software")
    # Load config
    config_file = open(CONFIG_LOCATION)
    config = json.loads(config_file.read())
    bot = MiniBotFramework.MiniBot.MiniBot(config)

    # Initialize TCP
    tcpInstance = None
    if config["acceptTcp"]:
        tcpInstance = MiniBotFramework.Communication.TCP.TCP()

    # Initialize UDP broadcast
    if config["discoverable"]:
        thread_udp = Thread(target = MiniBotFramework.Communication.UDP.udpBeacon)
        thread_udp.start()

    # If startup script specified, run it
    if config["startupScript"] != "":
        # TODO Allow uploading startup scripts!
        pass

    accept_xbox = False
    if config["acceptXbox"]:
        accept_xbox = True
        xboxInstance = MiniBotFramework.Controls.Xbox.Xbox()

    print("Entering main loop")
    # Main loop
    while True:
        # Poll TCP Connection
        tcpCmd = tcpInstance.get_command()
        if tcpCmd != "":
            x = parse_command(tcpCmd, bot, p)
            if x is not None:
                p = x
        # Poll Xbox
        if accept_xbox and MiniBotFramework.Controls.Xbox.Xbox.updated:
            MiniBotFramework.Controls.Xbox.Xbox.updated = False
            x_left = MiniBotFramework.Controls.Xbox.Xbox.left
            x_right = MiniBotFramework.Controls.Xbox.Xbox.right
            bot.get_actuator_by_name("two_wheel_movement").move(x_left,x_right)
        # Check on the main code
        time.sleep(0.001)

def parse_command(cmd, bot, p):
    comma = cmd.find(",")
    start = cmd.find("<<<<")
    end = cmd.find(">>>>")
    key = cmd[start+4:comma]
    value = cmd[comma+1:end]
    if key == "WHEELS":
        try:
            values = value.split(",")
            bot.get_actuator_by_name("two_wheel_movement").move(int(float(values[0])),int(float(values[1])))
        except:
            print("oh no!")
            pass
    elif key == "SCRIPT":
        user_script_file = open("MiniBotScripts/UserScript.py",'w')
        user_script_file.write(value)
        user_script_file.close()
        p = spawn_script_process(p, bot)
        return p
    elif key == "RUN":
        p = spawn_named_script_process(p, bot, value)
    else:
        bot.extraCMD.put( (key, cmd) )
        print("Unknown key: " + key)
        print("Cmd: " + cmd)
    return None

def spawn_script_process(p,bot):
    if (p is not None and p.is_alive()):
        p.terminate()
    time.sleep(0.1)
    p = Thread(target=run_script, args=[bot])
    p.start()
    # Return control to main after .1 seconds
    return p

def spawn_named_script_process(p,bot,name):
    if (p is not None and p.is_alive()):
        p.terminate()
    time.sleep(0.1)
    if "minion" in name or "Minion" in name:

    p = Thread(target=run_script_with_name, args=[bot,name])
    p.start()
    # Return control to main after .1 seconds
    return p

def run_script_with_name(bot,script_name):
    sys.path.insert(0, './lib')
    UserScript = importlib.import_module("MiniBotScripts." + script_name)
    UserScript.run(bot)

def run_script(bot):
    from MiniBotScripts import UserScript
    UserScript.run(bot)


if (__name__ == "__main__"):
    main()
