package co.edu.iudigital.radio.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.unit.dp
import kotlin.math.sin
import kotlin.random.Random

@Composable
fun SpectrumVisualizer(
    modifier: Modifier = Modifier,
    barCount: Int = 32,
    isPlaying: Boolean = false
) {
    val primaryColor = MaterialTheme.colorScheme.primary
    val animatedBars = remember { mutableStateListOf<Float>() }
    
    // Initialize bar heights
    LaunchedEffect(Unit) {
        repeat(barCount) {
            animatedBars.add(0f)
        }
    }
    
    // Animate bars when playing
    val infiniteTransition = rememberInfiniteTransition()
    val waveOffset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 2 * kotlin.math.PI.toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = LinearEasing)
        )
    )
    
    LaunchedEffect(isPlaying, waveOffset) {
        if (isPlaying) {
            for (i in animatedBars.indices) {
                // Create a wave pattern with some randomness
                val waveValue = sin(waveOffset + i * 0.5f)
                val randomFactor = Random.nextFloat() * 0.3f
                animatedBars[i] = (0.5f + 0.5f * waveValue + randomFactor).coerceIn(0f, 1f)
            }
        } else {
            // Reset heights when not playing
            for (i in animatedBars.indices) {
                animatedBars[i] = 0f
            }
        }
    }
    
    Canvas(
        modifier = modifier
            .fillMaxWidth()
            .height(120.dp)
            .padding(horizontal = 16.dp)
    ) {
        val barWidth = (size.width - 31 * 4.dp.toPx()) / barCount
        val spacing = 4.dp.toPx()
        
        animatedBars.forEachIndexed { index, height ->
            val x = index * (barWidth + spacing) + barWidth / 2
            val barHeight = height * size.height * 0.8f
            val topY = (size.height - barHeight) / 2
            
            // Draw gradient effect
            val gradientColor = primaryColor.copy(alpha = 0.3f + 0.7f * height)
            
            drawLine(
                color = gradientColor,
                start = Offset(x, topY),
                end = Offset(x, topY + barHeight),
                strokeWidth = barWidth,
                cap = StrokeCap.Round
            )
        }
        
        // Draw connecting wave
        if (isPlaying) {
            val path = Path()
            var firstPoint = true
            
            animatedBars.forEachIndexed { index, height ->
                val x = index * (barWidth + spacing) + barWidth / 2
                val barHeight = height * size.height * 0.8f
                val y = (size.height - barHeight) / 2 + barHeight / 2
                
                if (firstPoint) {
                    path.moveTo(x, y)
                    firstPoint = false
                } else {
                    path.lineTo(x, y)
                }
            }
            
            drawPath(
                path = path,
                color = primaryColor.copy(alpha = 0.5f),
                style = androidx.compose.ui.graphics.drawscope.Stroke(width = 3.dp.toPx())
            )
        }
    }
}
