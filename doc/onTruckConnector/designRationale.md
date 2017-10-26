# App design rationale

## System Architecture
![Image of Yaktocat](<insert dataflow picture here>)
The android application consists of a `UDPSender`, a `TCPSender`, a `JoystickPosition` and a `GUIHolder`.
In the beginning of the program the main thread starts up one `UDPSender` and one `TCPSender`, each in its own thread.
The two senders then continuously start to ask the `JoystickPosition` class, which holds the live position of the joystick on the UI (in a format which is ready to be sent over the protocol), for data to send.
This position is updated directly from the main thread which listens on the GUI for changes.
The GUI itself is contained in a separate holder, the `GUIHolder`, to allow all parts of the program to change the GUI without a reference to the main thread.
When a state change is to be sent the main thread receives an event which tells it to send data for that specified event to to the `TCPSender`.

## Design decisions

### GUI
In the beginning of the lifetime of the application the GUI was all handled in the main thread.
However, when the program grew more complex and asynchronous, the GUI could not be contained in the main thread since it would cause circular dependencies between every other part that used the GUI and the main thread.
The decision was therefore made to break out the GUI from the main thread and keep it separate and 'dumb'.

### Joystick position
When the time for implementing the joystick came around a problem arose; how would the `UDPSender` know what data to send and when the data had changed?
To solve this problem the decision to break out the data storing of the joystick position into a separate class.
It would receive information from the main thread when a change event sent and holds the current data for the `UDPSender` to retrieve needed.
The `UDPSender` just continuously asks it for information.

## Concurrency
(See **Concurrency > I/O** in [*MOPED design rationale*](https://github.com/hulthe/DAT255/blob/misc/design-rationale/doc/design-rationale/moped-design-rationale.md))
