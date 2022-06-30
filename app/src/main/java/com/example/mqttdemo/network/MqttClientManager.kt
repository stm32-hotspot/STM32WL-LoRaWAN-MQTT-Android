package com.example.mqttdemo.network

import android.annotation.SuppressLint
import android.content.Context
import android.util.Base64
import com.example.mqttdemo.network.data.SubscribeTopic
import org.eclipse.paho.android.service.MqttAndroidClient
import org.eclipse.paho.client.mqttv3.DisconnectedBufferOptions
import org.eclipse.paho.client.mqttv3.IMqttActionListener
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken
import org.eclipse.paho.client.mqttv3.IMqttToken
import org.eclipse.paho.client.mqttv3.MqttCallback
import org.eclipse.paho.client.mqttv3.MqttClient
import org.eclipse.paho.client.mqttv3.MqttConnectOptions
import org.eclipse.paho.client.mqttv3.MqttException
import org.eclipse.paho.client.mqttv3.MqttMessage
import timber.log.Timber

/**
* MqttClientManager is a singleton class which manages all of
* MQTT connection, disconnection, listeners, and resources.
*/
object MqttClientManager {

	@SuppressLint("StaticFieldLeak")
	private lateinit var client: MqttAndroidClient
	private lateinit var connectOptions: MqttConnectOptions

	private val deviceIds = ArrayList<String>()

	/**
	* Connect to the MQTT server.
	*
	* @param context Application Context.
	* @param name user name.
	* @param pw user password.
	* @param uri target URI.
	* @param callback implementation of [ConnectionCallback]
	*/
	fun connect(
		context: Context,
		name: String,
		pw: String,
		uri: String,
		callback: ConnectionCallback
	) {
		// if already connected, return
		if (isConnected()) return

		// Setup MqttConnectOptions
		connectOptions = MqttConnectOptions().apply {
			userName = name
			password = pw.toCharArray()
			isAutomaticReconnect = true
			isCleanSession = false
		}

		// Setup MqttAndroidClient
		client = MqttAndroidClient(context.applicationContext, uri, MqttClient.generateClientId())

		try {
			// onServiceConnected() method has run (asynchronously)
			client.connect(connectOptions, null, object : IMqttActionListener {
				override fun onSuccess(asyncActionToken: IMqttToken) {
					// Setup DisconnectedBufferOptions
					val disconnectedBufferOptions = DisconnectedBufferOptions().apply {
						isBufferEnabled = true
						bufferSize = 100
						isPersistBuffer = false
						isDeleteOldestMessages = false
					}
					client.setBufferOpts(disconnectedBufferOptions)

					// configure MQTT callback
					mqttCallbackConfig(client)

					// Notify callback has been connected
					callback.onSuccess()
				}

				override fun onFailure(asyncActionToken: IMqttToken, exception: Throwable) {
					Timber.e("Conn Failed $exception")
				}
			})
		} catch (ex: MqttException) {
			Timber.e("Connection failed")
		}
	}

	/**
	* Checks the client is already connected or not.
	*/
	fun isConnected(): Boolean {
		return ::client.isInitialized && client.isConnected
	}

	/**
	* Add an end node.
	*
	* @param id node ID.
	*/
	fun addEndNode(id: String) {
		deviceIds.add(id)
		for (ts in topicSubscribeList) {
			val fullName = "${getTopicTemplate(id)}${ts.topic}"
			subscribeTopic(fullName)
		}
	}

	/**
	* Subscribe a specific topic.
	*
	* @param topic topic.
	*/
	private fun subscribeTopic(topic: String) {
		try {
			client.subscribe(topic, qos, null, object : IMqttActionListener {
				override fun onSuccess(asyncActionToken: IMqttToken) {
					Timber.i("MQTT Subscribed topic $topic")
				}

				override fun onFailure(asyncActionToken: IMqttToken, exception: Throwable) {
					Timber.e("MQTT Subscription failed, $exception")
				}
			})
		} catch (ex: MqttException) {
			Timber.e("Exception subscribing")
			ex.printStackTrace()
		}
	}

	/**
	* Configure MQTT callbacks.
	*
	* @param client MqttAndroidClient.
	*/
	private fun mqttCallbackConfig(client: MqttAndroidClient) {
		client.setCallback(object : MqttCallback {
			override fun connectionLost(cause: Throwable?) {
				Timber.e("MQTT Connection Lost $cause")
			}

			override fun messageArrived(
				topicFullName: String?,
				message: MqttMessage?
			) {
				// executes only when message is not NULL
				message?.let {
					val decomposed = topicFullName!!.split("/")
					val sz = decomposed.size
					val topicString: String =
						if (sz == 5) decomposed[sz - 1]
						else "${decomposed[sz - 2]}/${decomposed[sz - 1]}"

					val deviceId = decomposed[3]
					when (SubscribeTopic.from(topicString)) {
						SubscribeTopic.UP -> subscribeCallbackUP?.onMessageArrived(deviceId, it)
						SubscribeTopic.JOIN -> subscribeCallbackJoin?.onMessageArrived(deviceId, it)
						SubscribeTopic.DOWN_FAILED -> subscribeCallbackDownFailed?.onMessageArrived(
							deviceId,
							it
						)
						SubscribeTopic.SERVICE_DATA -> subscribeCallbackServiceData?.onMessageArrived(
							deviceId,
							it
						)
						SubscribeTopic.UNKNOWN -> Timber.e("UNKNOWN Subscribe topic")
					}
				}
			}

			override fun deliveryComplete(token: IMqttDeliveryToken?) {
				Timber.i("Publish Complete, $token")
			}
		})
	}

	/**
	* Sends a downlink message.
	*
	* @param message payload data
	* @param id topic ID.
	*/
	fun sendDownlinkMessage(message: ByteArray, id: String) {
		val topicTemplate = getTopicTemplate(id)
		val topic = "${topicTemplate}down/push"
		val messageBase64 = Base64.encodeToString(message, Base64.NO_WRAP)
		val messageJSON =
			"{\"downlinks\":[{\"f_port\": 15,\"frm_payload\":\"$messageBase64\",\"priority\": \"NORMAL\"}]}"
		if (MqttClientManager::client.isInitialized && client.isConnected) {
			try {
				client.publish(
					topic, messageJSON.toByteArray(), qos, false, null,
					object : IMqttActionListener {
						override fun onSuccess(asyncActionToken: IMqttToken?) {
							Timber.d("MQTT topic $topic published $messageJSON")
						}

						override fun onFailure(
							asyncActionToken: IMqttToken?,
							exception: Throwable?
						) {
							Timber.e("MQTT published failed, $exception")
						}
					})
			} catch (ex: MqttException) {
				Timber.e("Exception publishing $ex")
			}
		}
	}

	private fun getTopicTemplate(id: String): String {
		val stackVersion = "v3"
		return "$stackVersion/${connectOptions.userName}/devices/$id/"
	}

	//region subscribe callbacks & listeners
	private var subscribeCallbackUP: SubscribeCallbackUP? = null
	private var subscribeCallbackJoin: SubscribeCallbackJoin? = null
	private var subscribeCallbackDownFailed: SubscribeCallbackDownFailed? = null
	private var subscribeCallbackServiceData: SubscribeCallbackServiceData? = null

	fun setSubscribeCallbackUP(callback: SubscribeCallbackUP) {
		subscribeCallbackUP = callback
	}

	fun setSubscribeCallbackJoin(callback: SubscribeCallbackJoin) {
		subscribeCallbackJoin = callback
	}

	fun setSubscribeCallbackDownFailed(callback: SubscribeCallbackDownFailed) {
		subscribeCallbackDownFailed = callback
	}

	fun setSubscribeCallbackServiceData(callback: SubscribeCallbackServiceData) {
		subscribeCallbackServiceData = callback
	}

	fun interface SubscribeCallbackUP {
		fun onMessageArrived(deviceId: String?, message: MqttMessage?)
	}

	fun interface SubscribeCallbackJoin {
		fun onMessageArrived(deviceId: String?, message: MqttMessage?)
	}

	fun interface SubscribeCallbackDownFailed {
		fun onMessageArrived(deviceId: String?, message: MqttMessage?)
	}

	fun interface SubscribeCallbackServiceData {
		fun onMessageArrived(deviceId: String?, message: MqttMessage?)
	}

	interface ConnectionCallback {
		fun onSuccess()
		fun onFailure()
	}
	//endregion

	/**
	* Disconnect and release all resources.
	*/
	fun disconnect() {
		if (::client.isInitialized) {
			client.disconnect()
			deviceIds.clear()
			subscribeCallbackUP = null
			subscribeCallbackJoin = null
			subscribeCallbackDownFailed = null
			subscribeCallbackServiceData = null
		}
	}

	private val qos = 0
	private val topicSubscribeList = listOf(
		SubscribeTopic.JOIN,
		SubscribeTopic.UP,
		SubscribeTopic.DOWN_FAILED,
		SubscribeTopic.SERVICE_DATA,
	)
}
