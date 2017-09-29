OnTruck MOPED Plugin
====================

This software acts as a server to a controller application.

## Building

Using maven: `mvn package`

## Running

The software will try to connect to a can network on the interface
specified by the environment variable `CAN_INTERFACE`.

The machine will need to have `can-utils` installed for the program to
run.

## How to setup a local virtual CAN interface for testing

make install will install to system and require root permissions.
Only tested on linux will not work on windows but might work on mac


```shell
git clone https://github.com/linux-can/can-utils
cd can-utils
./autogen.sh
make
make install
```

from [StackOverflow](https://stackoverflow.com/questions/21022749/how-to-create-virtual-can-port-on-linux-c)

```shell
modprobe can
ip link add dev vcan0 type vcan
ip link set up vcan0
```

If everything worked as intended it is now possible to interact with vcan0 as if it were a can bus using can-utils
like so:

```shell
cangen vcan0 &
candump vcan0
```


