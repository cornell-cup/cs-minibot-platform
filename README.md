# cs-minibot-platform

The shared software for the MiniBot platform. Includes a web server running over the base station and python scripts to run on the minibot.

# Setup

## Base Station Web Server

Clone this repository with its submodules:

```
git clone --recursive  https://github.com/cornell-cup/cs-minibot-platform.git
```

If you have already cloned the repository, then you can use the following command to initialize the dependencies:
```
git submodule update --init
```

- Open the project in IntelliJ Idea. 
- Go to src/main/java/minibot/BaseHTTPInterface.java and run it.
- Open your web browser and navigate to localhost:8080/gui

# How to run python interface:

Console printout- input commands into testcommand.py

In the first terminal window, navigate to directory and run
```
python tcp.py
```
Open a second terminal window, navigate to directory and run
```
python tcpclient.py
```

For controlling the Cozmo, connect to the Cozmo and activate SDK, follow instructions as above, except input commands into testcommand2.py and run tcp_cozmo.py and tcpclient_cozmo.py
