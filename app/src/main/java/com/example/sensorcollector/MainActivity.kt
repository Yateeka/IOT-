package com.example.sensorcollector

import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.sensorcollector.ui.theme.SensorCollectorTheme
import java.util.Locale

private val POSES = listOf(
    "Screen Up", "Screen Down", "Portrait Up",
    "Portrait Down", "Landscape Left", "Landscape Right"
)

private data class PoseWindow(
    val elapsedMs: Long,
    val features: List<Float>,
    val label: String,
    val clientId: String
)

class MainActivity : ComponentActivity(), SensorEventListener {
    private lateinit var sensorManager: SensorManager
    private var accelerometer: Sensor? = null
    private var gravity: Sensor? = null
    private val handler = Handler(Looper.getMainLooper())
    private val windows = mutableStateListOf<PoseWindow>()
    private val accelerationSamples = mutableListOf<FloatArray>()
    private val gravitySamples = mutableListOf<FloatArray>()

    private var isRecording by mutableStateOf(false)
    private var selectedPose by mutableStateOf(POSES.first())
    private var clientId by mutableStateOf("client_a")
    private var status by mutableStateOf("Ready. Select a pose, then capture a 1-second window.")
    private var elapsedMs by mutableStateOf(0L)
    private var collectionStartedNs = 0L

    private val finishWindow = Runnable { completeWindow() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        gravity = sensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY)
        if (accelerometer == null || gravity == null) {
            status = "This phone must provide accelerometer and gravity sensors."
        }
        setContent {
            SensorCollectorTheme { PoseCollectorScreen() }
        }
    }

    private fun startWindow() {
        if (isRecording) return
        if (accelerometer == null || gravity == null) {
            status = "Required accelerometer or gravity sensor is unavailable."
            return
        }
        if (clientId.isBlank()) {
            status = "Enter a client ID before recording."
            return
        }
        accelerationSamples.clear()
        gravitySamples.clear()
        collectionStartedNs = System.nanoTime()
        val accelStarted = sensorManager.registerListener(this, accelerometer, 20_000)
        val gravityStarted = sensorManager.registerListener(this, gravity, 20_000)
        if (!accelStarted || !gravityStarted) {
            sensorManager.unregisterListener(this)
            status = "Could not start both sensor streams."
            return
        }
        isRecording = true
        elapsedMs = 0L
        status = "Capturing $selectedPose… keep the phone still."
        handler.postDelayed(finishWindow, 1_000)
    }

    private fun completeWindow() {
        if (!isRecording) return
        sensorManager.unregisterListener(this)
        isRecording = false
        elapsedMs = (System.nanoTime() - collectionStartedNs) / 1_000_000
        if (accelerationSamples.isEmpty() || gravitySamples.isEmpty()) {
            status = "No sensor samples received; window discarded. Try again."
            return
        }
        val features = means(accelerationSamples) + means(gravitySamples)
        windows.add(PoseWindow(elapsedMs, features, selectedPose, clientId.trim()))
        status = "Saved 1 window (${accelerationSamples.size} accelerometer samples). Total: ${windows.size}."
    }

    private fun means(samples: List<FloatArray>): List<Float> = (0..2).map { axis ->
        samples.sumOf { it[axis].toDouble() }.div(samples.size).toFloat()
    }

    override fun onSensorChanged(event: SensorEvent) {
        if (!isRecording) return
        when (event.sensor.type) {
            Sensor.TYPE_ACCELEROMETER -> accelerationSamples.add(event.values.copyOf())
            Sensor.TYPE_GRAVITY -> gravitySamples.add(event.values.copyOf())
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) = Unit

    override fun onPause() {
        if (isRecording) {
            handler.removeCallbacks(finishWindow)
            sensorManager.unregisterListener(this)
            isRecording = false
            status = "Capture interrupted when the app left the foreground; window discarded."
        }
        super.onPause()
    }

    private fun exportCsv(uri: android.net.Uri) {
        try {
            contentResolver.openOutputStream(uri)?.bufferedWriter()?.use { writer ->
                writer.appendLine("client_id,elapsed_ms,accel_x_mean,accel_y_mean,accel_z_mean,gravity_x_mean,gravity_y_mean,gravity_z_mean,pose")
                windows.forEach { row ->
                    val featureText = row.features.joinToString(",") { String.format(Locale.US, "%.6f", it) }
                    writer.appendLine("${csv(row.clientId)},${row.elapsedMs},$featureText,${csv(row.label)}")
                }
            } ?: error("Could not open the selected file.")
            status = "Exported ${windows.size} labeled windows."
        } catch (e: Exception) {
            status = "Export failed: ${e.message ?: "unknown error"}"
        }
    }

    private fun csv(value: String) = "\"${value.replace("\"", "\"\"")}\""

    @Composable
    private fun PoseCollectorScreen() {
        val csvLauncher = rememberLauncherForActivityResult(
            ActivityResultContracts.CreateDocument("text/csv")
        ) { uri -> if (uri != null) exportCsv(uri) }

        Scaffold { padding ->
            Column(
                modifier = Modifier.fillMaxSize().padding(padding).padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text("PoseFL Client", style = MaterialTheme.typography.headlineMedium)
                Text("CSC 8223 · local sensor collection")
                OutlinedTextField(
                    value = clientId,
                    onValueChange = { if (!isRecording) clientId = it },
                    enabled = !isRecording,
                    label = { Text("Client ID (e.g. client_a)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                Text("Choose the phone posture to label this window:", style = MaterialTheme.typography.titleMedium)
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.height(150.dp)
                ) {
                    items(POSES) { pose ->
                        Button(
                            onClick = { selectedPose = pose },
                            enabled = !isRecording,
                            modifier = Modifier.fillMaxWidth()
                        ) { Text(if (pose == selectedPose) "✓ $pose" else pose) }
                    }
                }
                Text("Selected: $selectedPose", style = MaterialTheme.typography.titleMedium)
                HorizontalDivider()
                Text("Window: 1 second · requested sampling: 50 Hz")
                Text("Features: mean accelerometer XYZ + mean gravity XYZ")
                Text("Collected windows: ${windows.size} · elapsed: ${elapsedMs} ms")
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    Button(onClick = { startWindow() }, enabled = !isRecording && accelerometer != null && gravity != null) {
                        Text(if (isRecording) "Capturing…" else "Capture window")
                    }
                    Button(onClick = { csvLauncher.launch("${clientId}_pose_windows.csv") }, enabled = !isRecording && windows.isNotEmpty()) {
                        Text("Export CSV")
                    }
                }
                Card(modifier = Modifier.fillMaxWidth()) {
                    Text(status, modifier = Modifier.padding(14.dp), style = MaterialTheme.typography.bodyMedium)
                }
                Spacer(Modifier.height(2.dp))
            }
        }
    }
}
