import smbus

VL6180X_DEFAULT_I2C_ADDR = 0x29

VL6180X_REG_IDENTIFICATION_MODEL_ID = 0x000
VL6180X_REG_SYSTEM_INTERRUPT_CONFIG = 0x014
VL6180X_REG_SYSTEM_INTERRUPT_CLEAR = 0x015
VL6180X_REG_SYSTEM_FRESH_OUT_OF_RESET = 0x016
VL6180X_REG_SYSRANGE_START = 0x018
VL6180X_REG_SYSALS_START = 0x038
VL6180X_REG_SYSALS_ANALOGUE_GAIN = 0x03F
VL6180X_REG_SYSALS_INTEGRATION_PERIOD_HI = 0x040
VL6180X_REG_SYSALS_INTEGRATION_PERIOD_LO = 0x041
VL6180X_REG_RESULT_ALS_VAL = 0x050
VL6180X_REG_RESULT_RANGE_VAL = 0x062
VL6180X_REG_RESULT_RANGE_STATUS = 0x04d
VL6180X_REG_RESULT_INTERRUPT_STATUS_GPIO = 0x04f


VL6180X_ALS_GAIN_1 = 0x06
VL6180X_ALS_GAIN_1_25 = 0x05
VL6180X_ALS_GAIN_1_67 = 0x04
VL6180X_ALS_GAIN_2_5 = 0x03
VL6180X_ALS_GAIN_5 = 0x02
VL6180X_ALS_GAIN_10 = 0x01
VL6180X_ALS_GAIN_20 = 0x00
VL6180X_ALS_GAIN_40 = 0x07

VL6180X_ERROR_NONE = 0
VL6180X_ERROR_SYSERR_1 = 1
VL6180X_ERROR_SYSERR_5 = 5
VL6180X_ERROR_ECEFAIL = 6
VL6180X_ERROR_NOCONVERGE = 7
VL6180X_ERROR_RANGEIGNORE = 8
VL6180X_ERROR_SNR = 11
VL6180X_ERROR_RAWUFLOW = 12
VL6180X_ERROR_RAWOFLOW = 13
VL6180X_ERROR_RANGEUFLOW = 14
VL6180X_ERROR_RANGEOFLOW = 15


class VL6180X(object):
    def __init__(self):
        self._addr = VL6180X_DEFAULT_I2C_ADDR
        self.b = smbus.SMBus(1)

    def setup(self):
        if self.b.read_byte_data(self._addr, VL6180X_REG_IDENTIFICATION_MODEL_ID) != 0xB4:
            return False

        self.load_settings()

        self.b.write_byte_data(self._addr, VL6180X_REG_SYSTEM_FRESH_OUT_OF_RESET, 0x00)

        return True

    def load_settings(self):
        # private settings from page 24 of app note
        self.b.write_byte_data(self._addr, 0x0207, 0x01)
        self.b.write_byte_data(self._addr, 0x0208, 0x01)
        self.b.write_byte_data(self._addr, 0x0097, 0xfd)
        self.b.write_byte_data(self._addr, 0x0096, 0x00)
        self.b.write_byte_data(self._addr, 0x00e3, 0x00)
        self.b.write_byte_data(self._addr, 0x00e4, 0x04)
        self.b.write_byte_data(self._addr, 0x00e5, 0x02)
        self.b.write_byte_data(self._addr, 0x00e6, 0x01)
        self.b.write_byte_data(self._addr, 0x00e7, 0x03)
        self.b.write_byte_data(self._addr, 0x00f5, 0x02)
        self.b.write_byte_data(self._addr, 0x00d9, 0x05)
        self.b.write_byte_data(self._addr, 0x00db, 0xce)
        self.b.write_byte_data(self._addr, 0x00dc, 0x03)
        self.b.write_byte_data(self._addr, 0x00dd, 0xf8)
        self.b.write_byte_data(self._addr, 0x009f, 0x00)
        self.b.write_byte_data(self._addr, 0x00a3, 0x3c)
        self.b.write_byte_data(self._addr, 0x00b7, 0x00)
        self.b.write_byte_data(self._addr, 0x00bb, 0x3c)
        self.b.write_byte_data(self._addr, 0x00b2, 0x09)
        self.b.write_byte_data(self._addr, 0x00ca, 0x09)
        self.b.write_byte_data(self._addr, 0x0198, 0x01)
        self.b.write_byte_data(self._addr, 0x01b0, 0x17)
        self.b.write_byte_data(self._addr, 0x01ad, 0x00)
        self.b.write_byte_data(self._addr, 0x00ff, 0x05)
        self.b.write_byte_data(self._addr, 0x0100, 0x05)
        self.b.write_byte_data(self._addr, 0x0199, 0x05)
        self.b.write_byte_data(self._addr, 0x01a6, 0x1b)
        self.b.write_byte_data(self._addr, 0x01ac, 0x3e)
        self.b.write_byte_data(self._addr, 0x01a7, 0x1f)
        self.b.write_byte_data(self._addr, 0x0030, 0x00)

        # Recommended : Public registers - See data sheet for more detail
        self.b.write_byte_data(self._addr, 0x0011, 0x10)  # Enables polling for 'New Sample ready' when measurement
        # completes
        self.b.write_byte_data(self._addr, 0x010a, 0x30)  # Set the averaging sample period (compromise between
        # lower noise and increased execution time)
        self.b.write_byte_data(self._addr, 0x003f, 0x46)  # Sets the light and dark gain (upper nibble). Dark gain
        # should not be changed.
        self.b.write_byte_data(self._addr, 0x0031, 0xFF)  # sets the # of range measurements after which auto
        # calibration of system is performed
        self.b.write_byte_data(self._addr, 0x0040, 0x63)  # Set ALS integration time to 100ms
        self.b.write_byte_data(self._addr, 0x002e, 0x01)  # perform a single temperature calibration of the ranging
        # sensor

        # Optional: Public registers - See data sheet for more detail
        self.b.write_byte_data(self._addr, 0x001b, 0x09)  # Set default ranging inter-measurement period to 100ms
        self.b.write_byte_data(self._addr, 0x003e, 0x31)  # Set default ALS inter-measurement period to 500ms
        self.b.write_byte_data(self._addr, 0x0014, 0x24)  # Configures interrupt on 'New Sample Ready threshold event'


    def read_range(self):
        # wait for device to be ready for range measurement
        while not (self.b.read_byte_data(self._addr, VL6180X_REG_RESULT_RANGE_STATUS) & 0x01): pass

        # Start a range measurement
        self.b.write_byte_data(self._addr, VL6180X_REG_SYSRANGE_START, 0x01)

        # Poll until bit 2 is set
        while not (self.b.read_byte_data(self._addr, VL6180X_REG_RESULT_INTERRUPT_STATUS_GPIO) & 0x04): pass

        # read range in mm
        range = self.b.read_byte_data(self._addr, VL6180X_REG_RESULT_RANGE_VAL)

        # clear interrupt
        self.b.write_byte_data(self._addr, VL6180X_REG_SYSTEM_INTERRUPT_CLEAR, 0x07)

        return range

    def read_range_status(self):
        return self.b.read_byte_data(self._addr, VL6180X_REG_RESULT_RANGE_STATUS) >> 4


    def read_lux(self, gain):
        reg = self.b.read_byte_data(self._addr, VL6180X_REG_SYSTEM_INTERRUPT_CONFIG)
        reg &= ~0x38
        reg |= (0x4 << 3)  # IRQ on ALS ready
        self.b.write_byte_data(self._addr, VL6180X_REG_SYSTEM_INTERRUPT_CONFIG, reg)

        # 100 ms integration period
        self.b.write_byte_data(self._addr, VL6180X_REG_SYSALS_INTEGRATION_PERIOD_HI, 0)
        self.b.write_byte_data(self._addr, VL6180X_REG_SYSALS_INTEGRATION_PERIOD_LO, 100)

        # analog gain
        if gain > VL6180X_ALS_GAIN_40:
            gain = VL6180X_ALS_GAIN_40

        self.b.write_byte_data(self._addr, VL6180X_REG_SYSALS_ANALOGUE_GAIN, 0x40 | gain)

        # start ALS
        self.b.write_byte_data(self._addr, VL6180X_REG_SYSALS_START, 0x1)

        # Poll until "New Sample Ready threshold event" is set
        while 4 != ((self.b.read_byte_data(self._addr, VL6180X_REG_RESULT_INTERRUPT_STATUS_GPIO) >> 3) & 0x7): pass

        # read lux!
        lux = self.b.read_word_data(self._addr, VL6180X_REG_RESULT_ALS_VAL)

        # clear interrupt
        self.b.write_byte_data(self._addr, VL6180X_REG_SYSTEM_INTERRUPT_CLEAR, 0x07)

        lux *= 0.32  # calibrated count/lux
        if gain == VL6180X_ALS_GAIN_1:
            pass
        elif gain == VL6180X_ALS_GAIN_1_25:
            lux /= 1.25
        elif gain == VL6180X_ALS_GAIN_1_67:
            lux /= 1.76
        elif gain == VL6180X_ALS_GAIN_2_5:
            lux /= 2.5
        elif gain == VL6180X_ALS_GAIN_5:
            lux /= 5
        elif gain == VL6180X_ALS_GAIN_10:
            lux /= 10
        elif gain == VL6180X_ALS_GAIN_20:
            lux /= 20
        elif gain == VL6180X_ALS_GAIN_40:
            lux /= 20

        lux *= 100
        lux /= 100  # integration time in ms

        return lux
