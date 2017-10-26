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
As the software would have to handle multiple inputs, tasks and algorithms at once a certain degree of concurrency was unavoidable.
The concurrent structures in the application can be divided into two categories; Input/Output and Actors.

### Input/Output
Regarding I/O we had to both send and receive data at the same time without halting the application.
Furthermore this had to be done in regard to multiple clients such as the mobile apps multiple communication protocols and the CAN-network controlling the M.O.P.E.D. itself.

Every I/O connection was setup with a input-worker that continuously tries to receive new input and process it trough a list of registered data-processors.
It's of outermost importance that the code in each registered data-processor is swift so that the input-worker can move on to the next data-processor and eventually the next input message as fast as possible.
Therefor the data-processors mostly just saves the data to somewhere accessible to an [actor](###Actors) that can process it.

Apart from the input-worker a output-worker was also setup for every I/O connection that could be used to send messages.
This was done because in a real scenario it might take up to several seconds for a message to send, a pause not acceptable when dealing with real time planning.
Instead of waiting for the message to send, the responsibility for sending the message was shifted to a output worker and the algorithm can start it’s next iteration right away while the output-worker handles the message.
The output-worker itself simply tries to send all messages sent to it and sleeps if there is no message to send until it’s woken up by a new message.

### Actors
Actors act based on a set of data.
The data used by the Actor is often delivered to it from a input-worker trough a data-processor, thus separating the Actor from all concern regarding the collection of input data.
The act of an Actor is often a message to be processed by a output-worker, as the output worker takes over the responsibility for sending the message the Actor can right away start working on the next act.
This allows the Actor to always work on finding the appropriate action regarding to the current input data.
Multiple Actors usually execute simultaneously as they act based on different input, for example manual control signals and sensor data for autonomous behavior.
In the section [State Management](##State-Management) you can read about how the commands from the actors are being filtered depending on which state the software is in.
