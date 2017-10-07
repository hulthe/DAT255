Package composition:
====================
`<starter><type><payload><state_group><checksum><terminator>`

### `<starter>`
Size: 1 byte

Content: `0x01` [(STH)](http://www.asciitable.com)

### `<type>`
Size: 1 byte

Content:

  - 'P' `0x50`
  - 'S' `0x53`
  - 'B' `0x42`

See: [Event types](#event-types)

### `<payload>`
Size: 1 byte

##### Event types

###### 'P': Power
Payload: signed integer

Positive values maps to forward, negative to backward.
Value further from 0 means greater power.

###### 'S': Steering
Payload: signed integer

Positive values maps to right steering, negative to left
Value further from 0 means greater angle.

###### 'B': Braking
Payload: unsigned integer

Greater value means greater stopping power.

### `<state_group>`
Size: 1 byte

Content: Abstract identifier for the state to which this event belongs.

This value is used to invalidate late messages that belonged to the previous state.

### `<checksum>`
Size: 2 bytes

Content: First two bytes of `<type><payload><state_group>`:s md5-hash.

### `<terminator>`
Size: 1 byte

Content: 0x04 [(EOT)](http://www.asciitable.com)


# Example
#### Set max power forwards:
`01` `50` `7F` `00` `F381` `04`

#### Apply 25% braking:
`01` `42` `3F` `01` `88DB` `04`

#### Steer 50% to the left:
`01` `53` `C1` `02` `182F` `04`
