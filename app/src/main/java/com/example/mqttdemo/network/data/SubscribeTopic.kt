package com.example.mqttdemo.network.data

enum class SubscribeTopic(
	val topic: String
) {
	JOIN("join"),
	UP("up"),
	DOWN_FAILED("down/failed"),
	SERVICE_DATA("service/data"),
	UNKNOWN("UNKNOWN")
	;

	companion object{
		fun from(topic: String): SubscribeTopic {
			return when (topic){
				"join" -> JOIN
				"up" -> UP
				"down/failed" -> DOWN_FAILED
				"service/data" -> SERVICE_DATA
				else -> UNKNOWN
			}
		}
	}
}
