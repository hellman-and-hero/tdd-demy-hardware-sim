# tdd-demy-hardware-sim
[![Build Status](https://travis-ci.org/hellman-and-hero/tdd-demy-hardware-sim.svg?branch=master)](https://travis-ci.org/hellman-and-hero/tdd-demy-hardware-sim)
[![Dependabot Status](https://api.dependabot.com/badges/status?host=github&repo=hellman-and-hero/tdd-demy-hardware-sim)](https://dependabot.com)

A software simulation of the "TDD demystified" hardware.
run ```./mvnw package exec:java``` for running with defaults or run ```./mvnw package exec:java -Dexec.args="-h"``` to show help listing possible options. 

Like the real hardware, the simulated hardware connects to the MQTT broker (see help on how broker/port can be configured). The individual leds can then be switched with an MQTT message with the topic ```"some/led/<insert led number>/rgb"``` and messe payload contatining the color in hex representation, e.g. ```#FF0000``` for red and ```#000000``` for black (=off)

You can test the running simulator (as well as the real hardware) by sending MQTT messages to the broker, e.g. by using mosquitto's command line clients ```mosquitto_pub -h localhost -t 'some/led/0/rgb' -m '#FFCC22'```

