package com.example.stopwatch.ui

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.stopwatch.service.StopwatchUIState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StopwatchScreen() {
    val viewModel: StopwatchViewModel = viewModel(factory = StopwatchViewModelFactory())

    val state by viewModel.timerState.collectAsState()
    val formattedTime by viewModel.formattedTime.collectAsState()

    val isRunning = state is StopwatchUIState.Running
    val elapsedTime = when (state) {
        StopwatchUIState.Idle -> 0L
        is StopwatchUIState.Paused -> (state as StopwatchUIState.Paused).elapsedTime
        is StopwatchUIState.Running -> (state as StopwatchUIState.Running).elapsedTime
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = { TopAppBar(title = { Text("Foreground stopwatch") }) }) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Circular stopwatch display
            TimerDisplay(
                elapsedTime = elapsedTime,
                formattedTime = formattedTime,
                isRunning = isRunning,
                state = state,
                modifier = Modifier
                    .weight(1f)
                    .aspectRatio(1f)
                    .padding(32.dp)
            )

            // Control Buttons
            Row(
                horizontalArrangement = Arrangement.SpaceAround,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 32.dp)
            ) {
                // Reset Button
                Button(
                    onClick = viewModel::resetTimer,
                    // Enabled if Paused or Running (since time > 0)
                    enabled = state is StopwatchUIState.Paused || state is StopwatchUIState.Running,
                    shape = CircleShape,
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Red),
                    modifier = Modifier.size(72.dp)
                ) {
                    Icon(Icons.Filled.Refresh, contentDescription = "Reset", tint = Color.White)
                }

                Spacer(Modifier.width(32.dp))

                // START/PAUSE Button
                Button(
                    onClick = {
                        if (isRunning) viewModel.pauseTimer() else viewModel.startTimer()
                    }, shape = CircleShape,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = when (state) {
                            StopwatchUIState.Idle, is StopwatchUIState.Paused -> Color.Green
                            is StopwatchUIState.Running -> Color.Red
                        }
                    ), modifier = Modifier.size(96.dp)
                ) {
                    Icon(
                        if (isRunning) Icons.Filled.Pause else Icons.Filled.PlayArrow,
                        contentDescription = if (isRunning) "Pause" else "Start",
                        modifier = Modifier.size(48.dp),
                        tint = Color.White
                    )
                }
            }
        }
    }
}

@Composable
fun TimerDisplay(
    elapsedTime: Long,
    formattedTime: String,
    isRunning: Boolean,
    state: StopwatchUIState,
    modifier: Modifier = Modifier
) {
    val progressColor by animateColorAsState(
        targetValue = if (isRunning) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.tertiary,
        label = "progressColor"
    )

    val circumferenceMs = 60_000L
    val progress = (elapsedTime % circumferenceMs) / circumferenceMs.toFloat()

    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val strokeWidth = 12.dp.toPx()
            val radius = size.minDimension / 2 - strokeWidth / 2
            val center = Offset(size.width / 2, size.height / 2)
            val canvasSize = Size(radius * 2, radius * 2)

            // Background Circle
            drawCircle(
                color = Color.LightGray.copy(alpha = 0.3f),
                radius = radius,
                style = Stroke(width = strokeWidth)
            )

            // Progress Arc
            drawArc(
                color = progressColor,
                startAngle = 270f, // Start from the top
                sweepAngle = 360f * progress,
                useCenter = false,
                topLeft = center - Offset(radius, radius),
                size = canvasSize,
                style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
            )
        }

        // Time Text
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = formattedTime,
                fontSize = 40.sp,
                fontWeight = FontWeight.ExtraBold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = when (state) {
                    StopwatchUIState.Idle -> "READY"
                    is StopwatchUIState.Running -> "RUNNING"
                    is StopwatchUIState.Paused -> "PAUSED"
                }, fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}