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
            parse_command(tcpCmd, bot)

        # Poll Xbox
        if accept_xbox and MiniBotFramework.Controls.Xbox.Xbox.updated:
            MiniBotFramework.Controls.Xbox.Xbox.updated = False
            x_left = MiniBotFramework.Controls.Xbox.Xbox.left
            x_right = MiniBotFramework.Controls.Xbox.Xbox.right
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
        bot.get_actuator_by_name("two_wheel_movement").move(int(float(values[0])),int(float(values[1])))
        print(int(float(values[0])))
    elif key == "SCRIPT":
        print("TODO: Handle script")
    else:
        print("Unknown key: " + key)

if (__name__ == "__main__"):
    main()
