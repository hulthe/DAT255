Package composition
===================

```json
{
	"type":"<type>",
	"value":"<value>"
}
```

## `<type>`
A string value describing the message type.

### Available message types:
#### `"state"`
Set the MOPED in a specific state.

Valid values:

  - `"M"` - Manual Driving
  - `"P"` - Platooning
  - `"CC"` - Cruise Control
  - `"ACC"` - Adaptive Cruise Control

___________________________

# Example
#### Set moped state to Adaptive Cruise Control
```json
{"type":"state", "value":"ACC"}
```
