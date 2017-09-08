# Package composition:
`<starter><type><payload><checksum><terminator>`

### `<starter>`
Size: 1 byte

Content: `0x01` [(STH)](http://www.aciitable.com)

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

### `<checksum>`
Size: 2 bytes

Content: First two bytes of `<type><payload>`:s md5-hash.

### `<terminator>`
Size: 1 byte

Content: 0x04 [(EOT)](http://www.aciitable.com)


# Example
#### Set max power forwards:
`01` `50` `7F` `0791` `04`

#### Apply 25% braking:
`01` `42` `3F` `035D` `04`

#### Steer 50% to the left:
`01` `53` `C1` `2997` `04`
