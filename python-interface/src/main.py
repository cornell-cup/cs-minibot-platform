import json, time, multiprocessing
import MiniBotFramework
import MiniBotScripts.base
from threading import Thread
from multiprocessing import Process

# Constants
CONFIG_LOCATION = "MiniBotConfig/config.json"
bot = None

def main():
    global bot
    p = multiprocessing.Process(target=time.sleep, args=(1000,))
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
    end = cmd.find(">>>>")
    key = cmd[4:comma]
    value = cmd[comma+1:end]
    if key == "WHEELS":
        values = value.split(",")
        bot.get_actuator_by_name("two_wheel_movement").move(int(float(values[0])),int(float(values[1])))
        print(int(float(values[0])))
    elif key == "SCRIPT":
        user_script_file = open("MiniBotScripts/UserScript.py",'w')
        user_script_file.write(value)
        user_script_file.close()
        p = spawn_script_process(p)
        return p
    else:
        print("Unknown key: " + key)
    return None

def spawn_script_process(p):
    if (p.is_alive()):
        p.terminate()
    time.sleep(0.1)
    p = Process(target=run_script)
    p.start()
    # Return control to main after .1 seconds
    p.join(0.1)
    return p

def run_script():
    global bot
    from MiniBotScripts import UserScript
    UserScript.run(bot)


if (__name__ == "__main__"):
    main()
