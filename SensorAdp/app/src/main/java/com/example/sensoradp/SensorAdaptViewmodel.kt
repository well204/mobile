package com.example.sensoradp

import android.app.Application
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.location.Location
import android.location.LocationManager
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import androidx.core.content.ContextCompat
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

enum class NetworkType {NONE, WIFI, CELLULAR, OTHER}

class SensorAdaptViewModel(
    app: Application
) : AndroidViewModel(app), SensorEventListener {

    private val sensorManager = app.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    private val lightSensor: Sensor? = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT)

    private val _lux = MutableStateFlow(0)
    val lux: StateFlow<Int> = _lux

    private val _batteryPct = MutableStateFlow(100)
    val batteryPct: StateFlow<Int> = _batteryPct

    private val _time = MutableStateFlow("--:--")
    val time: StateFlow<String> = _time

    private val _networkType = MutableStateFlow(NetworkType.NONE)
    val networkType: StateFlow<NetworkType> = _networkType

    private val _isUFCQuixada = MutableStateFlow(false)
    val isUFCQuixada: StateFlow<Boolean> = _isUFCQuixada

    init {
        // Registra o listener para o sensor de luz, se ele existir
        lightSensor?.let {
            sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_UI)
        }

        // Cria um loop que atualiza os dados a cada 10 segundos
        viewModelScope.launch {
            while (true) {
                updateBattery()
                updateTime()
                updateNetworkType()
                checkIfUFCQuixada()
                delay(10000) // Atraso de 10 segundos
            }
        }
    }

    private fun updateBattery() {
        val intentFilter = IntentFilter(Intent.ACTION_BATTERY_CHANGED)
        val batteryStatus = getApplication<Application>().registerReceiver(null, intentFilter)
        val level = batteryStatus?.getIntExtra("level", -1) ?: -1
        val scale = batteryStatus?.getIntExtra("scale", -1) ?: -1
        _batteryPct.value = if (level >= 0 && scale > 0) ((level * 100) / scale) else 100
    }

    private fun updateTime() {
        val formatter = SimpleDateFormat("HH:mm", Locale.getDefault())
        _time.value = formatter.format(Calendar.getInstance().time)
    }

    fun updateNetworkType() {
        val cm = getApplication<Application>().getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val active = cm.activeNetwork ?: run {
            _networkType.value = NetworkType.NONE
            return
        }
        val caps = cm.getNetworkCapabilities(active) ?: run {
            _networkType.value = NetworkType.NONE
            return
        }
        _networkType.value = when {
            caps.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> NetworkType.WIFI
            caps.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> NetworkType.CELLULAR
            else -> NetworkType.OTHER
        }
    }

    fun checkIfUFCQuixada() {
        val lm = getApplication<Application>().getSystemService(Context.LOCATION_SERVICE) as LocationManager
        val perm = ContextCompat.checkSelfPermission(getApplication(), android.Manifest.permission.ACCESS_FINE_LOCATION)
        if (perm != PackageManager.PERMISSION_GRANTED) {
            _isUFCQuixada.value = false
            return
        }
        try {
            val location: Location? = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER)
                ?: lm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)

            location?.let {
                val LAT = -4.978499 // Coordenadas do campus da UFC em Quixadá
                val LON = -39.052472
                val dist = FloatArray(1)
                Location.distanceBetween(it.latitude, it.longitude, LAT, LON, dist)
                _isUFCQuixada.value = dist[0] < 300.0 // Raio de 300 metros
            } ?: run {
                _isUFCQuixada.value = false
            }
        } catch (e: SecurityException) {
            _isUFCQuixada.value = false
        }
    }

    override fun onAccuracyChanged(p0: Sensor?, p1: Int) {
        // Não precisamos implementar isso para este caso, mas a função é obrigatória.
    }

    override fun onSensorChanged(event: SensorEvent?) {
        if (event?.sensor?.type == Sensor.TYPE_LIGHT) {
            _lux.value = event.values[0].toInt()
        }
    }

    override fun onCleared() {
        super.onCleared()
        sensorManager.unregisterListener(this)
    }
}