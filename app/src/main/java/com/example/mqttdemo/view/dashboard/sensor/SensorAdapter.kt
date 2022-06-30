package com.example.mqttdemo.view.dashboard.sensor

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.mqttdemo.R
import com.example.mqttdemo.databinding.SensorItemBinding
import com.example.mqttdemo.extension.roundOffDecimal
import com.example.mqttdemo.view.dashboard.DeviceItem

@SuppressLint("NotifyDataSetChanged")
class SensorAdapter : RecyclerView.Adapter<SensorAdapter.ViewHolder>() {

	private val mSensorList: ArrayList<SensorItem> = arrayListOf()

	inner class ViewHolder(val binding: SensorItemBinding) : RecyclerView.ViewHolder(binding.root) {

		init {
			binding.settingsButton.setOnClickListener {
				val sensorItem = mSensorList[adapterPosition]
				onButtonClick(sensorItem)
			}
		}
	}

	override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
		val binding = DataBindingUtil.inflate<SensorItemBinding>(
			LayoutInflater.from(parent.context),
			R.layout.sensor_item,
			parent,
			false
		)
		return ViewHolder(binding)
	}

	override fun onBindViewHolder(holder: ViewHolder, position: Int) {
		holder.binding.sensorItem = mSensorList[position]
		holder.binding.executePendingBindings()
	}

	override fun getItemCount(): Int {
		return mSensorList.size
	}

	fun setItem(deviceItem: DeviceItem) {
		mSensorList.clear()
		mSensorList.addAll(deviceItem.sensorList)
		notifyDataSetChanged()
	}

	// Sensor Settings Button Clicked
	private fun onButtonClick(item: SensorItem) {
        var dataValue = item.dataValue
        val unit: String

        if (item is SensorItem.HUMIDITY || item is SensorItem.SNR) {
            return
        }

        if (!item.convert) {
            // Convert C -> F | hPa -> inHg | dBm -> mW
            dataValue = item.convertToConvertedUnit(dataValue)
            unit = item.convertedUnit
            item.convert = true
        } else {
            // Convert F -> C | inHg -> hPa | mW -> dBm
            dataValue = item.convertToDefaultUnit(dataValue)
            unit = item.defaultUnit
            item.convert = false
        }

		dataValue = dataValue.roundOffDecimal()

        item.dataValue = dataValue
        item.unit = unit

		notifyItemChanged(mSensorList.indexOf(item))
	}

}
