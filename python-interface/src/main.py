import json, time
import MiniBotFramework
import MiniBotScripts.base

# Constants
CONFIG_LOCATION = "MiniBotConfig/config.json"
bot = None

def main():
    print("Initializing MiniBot Software")
    # Load config
    config_file = open(CONFIG_LOCATION)
    config = json.loads(config_file.read())
    bot = MiniBotScripts.base.MiniBot.MiniBot(config)

    # Initialize TCP
    tcpInstance = None
    if config["acceptTcp"]:
        tcpInstance = MiniBotFramework.Communication.TCP.TCP()

    # Initialize UDP broadcast
    if config["discoverable"]:
        # TODO
        pass

    # If startup script specified, run it
    if config["startupScript"] != "":
        # TODO
        pass

    if config["acceptXbox"]:
        xboxInstance = MiniBotFramework.Controls.Xbox.Xbox()

    print("Entering main loop")
    # Main loop
    while True:
        # Poll TCP Connection
        tcpCmd = tcpInstance.get_command()
        if tcpCmd != "":
            parse_command(tcpCmd, bot)

        # Poll Xbox
        if MiniBotFramework.Controls.Xbox.updated:
            MiniBotFramework.Controls.Xbox.updated = False
            x_left = MiniBotFramework.Controls.Xbox.left
            x_right = MiniBotFramework.Controls.Xbox.right
            bot.get_actuator_by_name("two_wheel_movement").move(x_left,x_right)
        # Check on the main code
        time.sleep(0.001)

def parse_command(cmd, bot):
    comma = cmd.find(",")
    end = cmd.find(">>>>")
    key = cmd[4:comma]
    value = cmd[comma+1:end]
    if key == "WHEELS":
        values = value.split(",")
        bot.get_actuator_by_name("two_wheel_movement").move(int(values[0]),int(values[1]))
        print(values[0])
        print(values[1])
    elif key == "SCRIPT":
        print("TODO: Handle script")
    else:
        print("Unknown key: " + key)

if (__name__ == "__main__"):
    main()
