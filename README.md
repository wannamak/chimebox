# Chimebox

## About

Chimebox is Raspberry PI software which controls a tubular chime set.  
The chimes I have are 21-note Maas-Rowe.  AC voltage drives solenoids
which strike the tubes.  An AC power supply provides different voltage
levels; the magnitude of the current determines the strength of the strike.

The controller has three phases of operation:

1) Play the chimes in response to MIDI messages, at the user-selectable
volume determined by a rotary switch.
2) Play hourly or quarterly chimes on the softest setting.  The selection
of changes to play is programmable via ruleset and can be randomized.  Times
can be included or excluded.
3) Play doorbell chimes on the loudest setting.

## Components

* Raspberry PI 4B
* Waveshare MCP23017 IO expansion board
* 3x ELEGO 8 channel relay modules
* DS3231 Precision RTC Breakout (a reliable clock)
* <strike>OSA Electronics MIDI Breakout</strike> (Never could get this to work)
* M-AUDIO Midisport Uno USB Midi interface
* Standard tubular chime power supply

## Additional Features

* The chime power supply is switched by one of the relays.  The PI runs continuously,
but it turns on and off the chime power supply as needed.
* A toggle switch on the unit will squelch the hourly chimes.
* Drawing the Cloches stop on the organ will squelch the hourly chimes.

## Relay assignments

* 0: 110AC for chime power supply
* 1: low AC voltage common (overrides rotary)
* 2: high AC voltage common (overrides rotary)
* 3-23: 21 notes

## Setup notes

* Ubuntu 20.4.3 LTS, not Rasbian
* As root, <code>timedatectl</code> to verify hwclock
* Config in <code>/boot/firmware/usercfg.txt</code>
```
# DS3231 RTC clock chip.
# https://askubuntu.com/questions/1260403/rapsberry-pi-4-with-rtc-and-ubuntu-20-04
dtoverlay=i2c-rtc,ds3231
```
* Install MAudio driver package <code>sudo apt-get install midisport-firmware</code>

## Ecosystem notes

### WiringPI

Grumpy old man?  Unwilling to host source on a standard platform.
Nice C library, though.

### PI4J

Designed by an engineer.  Engineers love to code... layers upon layers of abstrations and
abstractions of abstrations, builders to configure the contexts for the abstractions, and to dress it 
all up an put a bow on it, PI4J uses Maven.  Waste of time.