*A design rationale is the explicit listing of decisions made during a design process, and the reasons why those decisions were made. Its primary goal is to support designers by providing a means to record and communicate the argumentation and reasoning behind the design process. It should therefore include:*

- *... the reasons behind a design decision,*
- *... the justification for it,*
- *... the other alternatives considered,*
- *... the trade offs evaluated, and*
- *... the argumentation that led to the decision.*
---
# MOPED design rationale
## System Architecture
The system consists of an Android application and a server.
The server sits on a RC car and has a TCP and a UDP port open.
When the application wants to connect to the server it does this by connecting to a predefined port on the server over TCP.
After the connection has been established the application can begin to send [control messages](##Connection-protocols) to the predefined UDP port on the server.
Messages that require the server to acknowledge that the command reached the server -- typically more important messages -- are sent over TCP.
Similarly, for some messages we don't require an acknowledgment from the server since the application doesn't care whether the server received the command or not.
These messages are typically the manual control messages of the car.

### Connection protocols

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

## State management
The state system consists of one `state holder`, multiple `controllers` and an equal amount of `messages filters`.
When a message is received it is sent to all `controllers` where a decision on what to do with the message is made.
The filters then check which state is active in the `state holder` and chooses whether to send the commands along to the `driver` where the commands are executed.
The process is a bit flexible however since each filter specifies what state it lets through messages on.
This allows i.e. the `manual filter` to send messages along even though `ACC` state is active.

## Hardware communication
A way to communicate with the CAN-bus directly from the Java program was desired.
Therefore an external library was developed based upon some code from another group.
The library was designed in such a way as to be completely separate from the rest of the project.

## Concurrency
*TODO*
### Input/Output
### Actors
