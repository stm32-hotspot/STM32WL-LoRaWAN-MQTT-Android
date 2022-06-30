package com.example.mqttdemo.view.dashboard.sensor

import androidx.annotation.DrawableRes
import com.example.mqttdemo.R
import kotlin.math.log10
import kotlin.math.pow

sealed class SensorItem constructor(
	val title: String,
	@DrawableRes val iconResource: Int,
	var dataValue: Float,
	var unit: String,
	var defaultUnit: String,
	var convertedUnit: String,
	var convert: Boolean = false
) {
    abstract fun convertToDefaultUnit(dataValue: Float): Float
    abstract fun convertToConvertedUnit(dataValue: Float): Float

    class HUMIDITY(
		title: String = "Humidity",
		iconResource: Int = R.drawable.ic_water_drop,
		dataValue: Float = 55.00f,
		unit: String = "%",
		defaultUnit: String = unit,
        convertedUnit: String = unit,
		convert: Boolean = false
	) : SensorItem(title, iconResource, dataValue, unit, defaultUnit, convertedUnit, convert) {
        override fun convertToDefaultUnit(dataValue: Float): Float {
            return dataValue
        }
        override fun convertToConvertedUnit(dataValue: Float): Float {
            return dataValue
        }
	}

	class RSSI(
		title: String = "RSSI",
		iconResource: Int = R.drawable.ic_signal,
		dataValue: Float = -85.00f,
		unit: String = "dBm",
		defaultUnit: String = unit,
        convertedUnit: String = "mW",
		convert: Boolean = false
	) : SensorItem(title, iconResource, dataValue, unit, defaultUnit, convertedUnit, convert) {
	    override fun convertToDefaultUnit(dataValue: Float): Float { // mW -> dBm
	        return 10 * log10(dataValue)
	    }
        override fun convertToConvertedUnit(dataValue: Float): Float { // dBm -> mW
            return 10f.pow(dataValue / 10f)
        }
	}

	class BAROMETER(
		title: String = "Barometer",
		iconResource: Int = R.drawable.ic_air,
		dataValue: Float = 986.00f,
		unit: String = "hPa",
		defaultUnit: String = unit,
        convertedUnit: String = "inHg",
		convert: Boolean = false
	) : SensorItem(title, iconResource, dataValue, unit, defaultUnit, convertedUnit, convert) {
        override fun convertToDefaultUnit(dataValue: Float): Float { // inHg -> hPa
            return dataValue * 33.86f
        }
        override fun convertToConvertedUnit(dataValue: Float): Float { // hPa -> inHg
            return dataValue / 33.86f
        }
	}

	class SNR(
		title: String = "SNR",
		iconResource: Int = R.drawable.ic_signal,
		dataValue: Float = 9.30f,
		unit: String = "dB",
		defaultUnit: String = unit,
        convertedUnit: String = unit,
		convert: Boolean = false
	) : SensorItem(title, iconResource, dataValue, unit, defaultUnit, convertedUnit, convert) {
        override fun convertToDefaultUnit(dataValue: Float): Float {
            return dataValue
        }
        override fun convertToConvertedUnit(dataValue: Float): Float {
            return dataValue
        }
	}

	class TEMPERATURE(
		title: String = "Temperature",
		iconResource: Int = R.drawable.ic_cold,
		dataValue: Float = 82.40f,
		unit: String = "°C",
        defaultUnit: String = unit,
        convertedUnit: String = "°F",
		convert: Boolean = false
	) : SensorItem(title, iconResource, dataValue, unit, defaultUnit, convertedUnit, convert) {
        override fun convertToDefaultUnit(dataValue: Float): Float { // °F -> °C
            return (dataValue - 32f) * (5f / 9f)
        }
        override fun convertToConvertedUnit(dataValue: Float): Float { // °C -> °F
            return (dataValue * (9f / 5f)) + 32f
        }
	}

	companion object {
		fun itemList(): List<SensorItem> {
			return listOf(
				HUMIDITY(),
				RSSI(),
				BAROMETER(),
				SNR(),
				TEMPERATURE()
			)
		}
	}
}
