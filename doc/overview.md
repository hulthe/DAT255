# Overview
## System Architecture
The system consists of an Android application and a server.
The server sits on a RC car and has a TCP and a UDP port open.
When the application wants to connect to the server it does this by connecting to a predefined port on the server over TCP.
After the connection has been established the application can begin to send [control messages](##Connection-protocols) to the predefined UDP port on the server.
Messages that require the server to acknowledge that the command reached the server -- typically more important messages -- are sent over TCP.
Similarly, for some messages we don't require an acknowledgment from the server since the application doesn't care whether the server received the command or not.
These messages are typically the manual control messages of the car.

## Connection protocols

#### UDP
Messages sent over [this protocol](https://github.com/hulthe/DAT255//tree/master/doc/udp_protocol.md) consists of motor and steering commands.
We care less about the package loss and more about the correctness of the messages that are actually received.
To do this a `starter` and an `terminator` byte begin and end respectively the message.
In addition to this a checksum is used to verify the integrity of the payload.
A complete message is only 7 bytes long, thus we can have a very high send rate.

#### TCP
Messages sent over [this protocol](https://github.com/hulthe/DAT255//tree/master/doc/tcp_protocol.md) require the server to acknowledge that it was received correctly since the application need to know which state the server is in.
This makes messages over this protocol more reliable but also slower.
Therefore only state changes is sent over this protocol.

## onTruckPlugin
The code running on the M.O.P.E.D platform

See more in the [design rationale](https://github.com/hulthe/DAT255/blob/master/doc/onTruckPlugin/designRationale.md).

## onTruckConnector
The app.

See more in the [design rationale](https://github.com/hulthe/DAT255/blob/master/doc/onTruckConnector/designRationale.md).
