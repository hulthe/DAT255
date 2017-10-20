Package composition
===================

```json
{
	"type":"<type>",
	"value":"<value>"
}
```

A message must end with a byte value of `0x04` [(EOT)](http://www.asciitable.com).

## `<type>`
A string value describing the message type.

### Available message types:
#### `"state"`
Type: string

Set the MOPED in a specific state.

Valid values:

  - `"M"` - Manual Driving
  - `"P"` - Platooning
  - `"CC"` - Cruise Control
  - `"ACC"` - Adaptive Cruise Control

#### `"event_group"`
Type: byte

Abstract identifier for the state to which this event belongs.

This value is used to invalidate late messages that belonged to the previous state.

___________________________

# Example
#### Set moped state to Adaptive Cruise Control
```json
{"type":"state", "value":"ACC"}
```
