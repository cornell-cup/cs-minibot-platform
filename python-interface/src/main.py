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

    print("Entering main loop")
    # Main loop
    while True:
        # Poll TCP Connection
        tcpCmd = tcpInstance.get_command()
        if tcpCmd != "":
            parse_command(tcpCmd, bot)
        # Check on the main code
        time.sleep(0.01)

def parse_command(cmd, bot):
    comma = cmd.find(",")
    end = cmd.find(">>>>")
    key = cmd[4:comma]
    value = cmd[comma+1:end]
    if key == "GPIO":
        # [debug] send pwm duty cycles to motors directly
        # A;B;C;D
        values = value.split(";")
        print(values)
        print(bot)
        left=bot.get_actuator_by_name("leftMotor")
        right=bot.get_actuator_by_name("rightMotor")
        left.get_in_pwm().ChangeDutyCycle(int(values[0]))
        left.get_out_pwm().ChangeDutyCycle(int(values[1]))
        right.get_in_pwm().ChangeDutyCycle(int(values[2]))
        right.get_out_pwm().ChangeDutyCycle(int(values[3]))
    elif key == "WHEELS":
        print("TODO: Handle wheels")
    elif key == "SCRIPT":
        print("TODO: Handle script")
    else:
        print("Unknown key: " + key)

if (__name__ == "__main__"):
    main()
