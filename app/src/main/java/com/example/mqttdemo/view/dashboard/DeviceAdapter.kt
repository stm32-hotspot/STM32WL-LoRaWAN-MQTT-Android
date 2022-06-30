package com.example.mqttdemo.view.dashboard

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.mqttdemo.R
import com.example.mqttdemo.databinding.DeviceItemBinding
import com.example.mqttdemo.extension.roundOffDecimal
import com.example.mqttdemo.view.dashboard.sensor.SensorAdapter
import com.example.mqttdemo.view.dashboard.sensor.SensorItem

@SuppressLint("NotifyDataSetChanged")
class DeviceAdapter constructor(
	private val delegate: Delegate
) : RecyclerView.Adapter<DeviceAdapter.ViewHolder>() {

	private val mDeviceList: ArrayList<DeviceItem> = arrayListOf()

	fun interface Delegate {
		fun onButtonClick(deviceId: String, message: String)
	}

	inner class ViewHolder(val binding: DeviceItemBinding) : RecyclerView.ViewHolder(binding.root) {

		init {
			with(binding) {
				// Setup RecyclerView
				sensorAdapter = SensorAdapter()

				// Send Pub Message Button Clicked
				pubMessageButton.setOnClickListener {
					val currentItem = mDeviceList[adapterPosition]
					delegate.onButtonClick(currentItem.deviceId, pubMessageInput.text.toString())
				}

				// Down Arrow Button Clicked
				downArrow.setOnClickListener {
					val currentItem = mDeviceList[adapterPosition]
					currentItem.visibility = !currentItem.visibility
					notifyItemChanged(mDeviceList.indexOf(currentItem))
				}
			}
		}
	}

	override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
		val binding = DataBindingUtil.inflate<DeviceItemBinding>(
			LayoutInflater.from(parent.context),
			R.layout.device_item,
			parent,
			false
		)
		return ViewHolder(binding)
	}

	override fun onBindViewHolder(holder: ViewHolder, position: Int) {
		val currentItem: DeviceItem = mDeviceList[position]

		with(holder.binding) {
			deviceItem = currentItem
			sensorAdapter?.setItem(currentItem)
			executePendingBindings()
		}
	}

	override fun getItemCount(): Int {
		return mDeviceList.size
	}

	fun addItem(item: DeviceItem) {
		mDeviceList.add(item)
		notifyDataSetChanged()
		notifyItemChanged(mDeviceList.lastIndex)
	}

	fun checkItemExists(item: DeviceItem): Boolean {
		mDeviceList.find { it.deviceId == item.deviceId } ?: return false
		return true
	}

	fun updateData(deviceId: String, dData: DashboardData) {
		val device = mDeviceList.find { it.deviceId == deviceId } ?: return
		val sensorList = device.sensorList
		sensorList[0].dataValue = dData.sensorData.humidity
		sensorList[1].dataValue = updateConversion(sensorList[1], dData.rssi)
		sensorList[2].dataValue = updateConversion(sensorList[2], dData.sensorData.pressure)
		sensorList[3].dataValue = dData.snr
		sensorList[4].dataValue = updateConversion(sensorList[4], dData.sensorData.temp)
		notifyDataSetChanged()
	}

	// Converts Updated Data
	private fun updateConversion(item: SensorItem, dataValue: Float): Float {
		if (!item.convert) return dataValue
        return item.convertToConvertedUnit(dataValue).roundOffDecimal()
	}

}
