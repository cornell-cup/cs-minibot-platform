import json, time
import MiniBotFramework

# Constants
CONFIG_LOCATION = "MiniBotConfig/config.json"

def main():
    print("Initializing MiniBot Software")
    # Load config
    config_file = open(CONFIG_LOCATION)
    config = json.loads(config_file.read())

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
            print(tcpCmd)
        # Check on the main code
        time.sleep(0.01)

if (__name__ == "__main__"):
    main()
