# cs-minibot-platform ![If this does not say passing, someone really messed up](https://travis-ci.org/cornell-cup/cs-minibot-platform.svg?branch=develop)


The shared software for the MiniBot platform. Includes a web server running over the base station and python scripts to run on the minibot.

# Setup

## Base Station Web Server

Clone this repository:

```
git clone https://github.com/cornell-cup/cs-minibot-platform.git
```

Because this is a maven project, you should import it in the following way:
- Open intellij idea and select import project
- Select the pom.xml under cs-minibot-platform (the repo you just cloned)
- Go to cs-minibot-platform-src/src/main/java/minibot/BaseHTTPInterface.java and run it.
- Open your web browser and navigate to localhost:8080/gui

## How to run python interface:

If you are setting up a MiniBot for the first time, navigate to python-interface and run install_bot.sh.

Ensure that you have a proper config.json under python-interface/src/MiniBogConfig/config.json.

Run python-interface/src/init_python.sh to start the interface.

# Testing
To test, run 
```
mvn clean verify
```
