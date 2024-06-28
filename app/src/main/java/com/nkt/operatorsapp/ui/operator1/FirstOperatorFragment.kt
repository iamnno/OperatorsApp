package com.nkt.operatorsapp.ui.operator1

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.nkt.operatorsapp.MainActivity
import com.nkt.operatorsapp.R
import com.nkt.operatorsapp.data.RemoteParamsRepository.Companion.PARAM_1
import com.nkt.operatorsapp.data.RemoteParamsRepository.Companion.PARAM_2
import com.nkt.operatorsapp.data.RemoteParamsRepository.Companion.PARAM_3
import com.nkt.operatorsapp.databinding.FragmentFirstOperatorBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken
import org.eclipse.paho.client.mqttv3.MqttCallback
import org.eclipse.paho.client.mqttv3.MqttClient
import org.eclipse.paho.client.mqttv3.MqttConnectOptions
import org.eclipse.paho.client.mqttv3.MqttException
import org.eclipse.paho.client.mqttv3.MqttMessage

@AndroidEntryPoint
class FirstOperatorFragment : Fragment() {

    private lateinit var binding: FragmentFirstOperatorBinding
    private val viewModel by viewModels<FirstOperatorViewModel>()
    private lateinit var mqttClient: MqttClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        lifecycleScope.launch {
            withContext(Dispatchers.IO) {
                try {
                    initializeMqttClient()
                } catch (e: MqttException) {
                    e.printStackTrace()
                    Log.e("MQTT", "Помилка підключення до MQTT: ${e.message}")
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()

        (requireActivity() as MainActivity).setTopAppBarTitle(getString(R.string.operator_1))
        (requireActivity() as MainActivity).setTopAppBar()
    }

    @SuppressLint("UnsafeRepeatOnLifecycleDetector")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_first_operator, container, false)
        binding = FragmentFirstOperatorBinding.bind(view)

        setupButtonListeners()

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { state ->
                    when (state) {
                        FirstOperatorViewModel.UiState.Loading -> {}
                        is FirstOperatorViewModel.UiState.Loaded -> {
                            val params = state.params
                            with(binding) {
                                param1Text.text = params[PARAM_1]
                                param2Text.text = params[PARAM_2]
                                param3Text.text = params[PARAM_3]
                            }
                        }
                    }
                }
            }
        }

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        if (this::mqttClient.isInitialized && mqttClient.isConnected) {
            try {
                mqttClient.disconnect()
                mqttClient.close()
            } catch (e: MqttException) {
                e.printStackTrace()
                Log.e("MQTT", "Помилка відключення від MQTT: ${e.message}")
            }
        }
    }

    private suspend fun initializeMqttClient() {
        // Ініціалізація MQTT клієнта
        val serverUri = "ssl://68213335b4c74cc89fad7f471214baa5.s1.eu.hivemq.cloud:8883"
        mqttClient = MqttClient(serverUri, MqttClient.generateClientId(), null)

        val options = MqttConnectOptions().apply {
            userName = "user1pub"
            password = "05aw%1#b<j!8ZUPSYxIe".toCharArray()
            isAutomaticReconnect = true
            isCleanSession = true
        }

        Log.d("MQTT", "Підключення до MQTT брокера")
        mqttClient.connect(options)
        Log.d("MQTT", "Підключено до MQTT брокера")

        mqttClient.setCallback(object : MqttCallback {
            override fun connectionLost(cause: Throwable) {
                // Логіка обробки втрати з'єднання
                Log.e("MQTT", "З'єднання втрачено: ${cause.message}")
            }

            override fun messageArrived(topic: String, message: MqttMessage) {
                // Логіка обробки отриманих повідомлень
                Log.d(
                    "MQTT",
                    "Отримано повідомлення з теми [$topic]: ${message.payload.toString(Charsets.UTF_8)}"
                )
            }

            override fun deliveryComplete(token: IMqttDeliveryToken) {
                // Логіка обробки завершеної доставки повідомлення
                Log.d("MQTT", "Доставка повідомлення завершена для токена: ${token.message}")
            }
        })
    }

    private fun setupButtonListeners() {
        with(binding) {
            buttonMotor1Cw.setOnClickListener {
                sendCommand("motor1/cw")
            }
            buttonMotor1Ccw.setOnClickListener {
                sendCommand("motor1/ccw")
            }
            buttonMotor2Cw.setOnClickListener {
                sendCommand("motor2/cw")
            }
            buttonMotor2Ccw.setOnClickListener {
                sendCommand("motor2/ccw")
            }
        }
    }

    private fun sendCommand(command: String) {
        lifecycleScope.launch {
            withContext(Dispatchers.IO) {
                try {
                    if (this@FirstOperatorFragment::mqttClient.isInitialized && mqttClient.isConnected) {
                        val message = MqttMessage(command.toByteArray())
                        mqttClient.publish("esp32/commands", message)
                    } else {
                        Log.e("MQTT", "MQTT клієнт не підключений")
                    }
                } catch (e: MqttException) {
                    e.printStackTrace()
                    Log.e("MQTT", "Помилка відправки команди: ${e.message}")
                }
            }
        }
    }
}
