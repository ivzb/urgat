package com.ivzb.urgat.ui.splash

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ivzb.urgat.ui.theme.UrgatTheme

@Composable
fun SplashScreen(
    viewModel: SplashViewModel,
    onComplete: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    SplashScreenContent(modifier)

    viewModel.onIntent(SplashIntent.Initialize)

    LaunchedEffect(uiState.isComplete) {
        if (uiState.isComplete) {
            onComplete()
        }
    }
}

@Composable
private fun SplashScreenContent(
    modifier: Modifier = Modifier,
) {
    var dz by remember { mutableFloatStateOf(1f) }
    var angle by remember { mutableFloatStateOf(0f) }
    val fps = 120f

    val backgroundColor = MaterialTheme.colorScheme.background
    val foregroundColor = MaterialTheme.colorScheme.onBackground

    LaunchedEffect(Unit) {
        val dt = 1f / fps

        while (true) {
            withFrameNanos {
//                dz += 1f * dt
                angle += Math.PI.toFloat() * dt
            }
        }
    }

    fun DrawScope.clear() {
        drawRect(
            color = backgroundColor,
            size = size,
        )
    }

    fun DrawScope.point(point: Point2d) {
        val SIZE = 20f

        drawRect(
            color = foregroundColor,
            topLeft = Offset(
                x = point.x - SIZE / 2,
                y = point.y - SIZE / 2,
            ),
            size = Size(SIZE, SIZE),
        )
    }

    fun DrawScope.line(p1: Point2d, p2: Point2d) {
        drawLine(
            color = foregroundColor,
            start = Offset(p1.x, p1.y),
            end = Offset(p2.x, p2.y),
            strokeWidth = 3f
        )
    }

    fun DrawScope.frame(dz: Float, angle: Float) {
        clear()

        vertices.forEach { p ->
//            point(p
//                .rotate_xz(angle)
//                .translate_z(dz)
//                .project()
//                .screen(size)
//            )
        }

        faces.forEach { face ->
            for (i in 0 until face.size) {
                val p1 = vertices[face[i]]
                    .rotateXZ(angle)
                    .translateZ(dz)
                    .project()
                    .screen(size)

                val p2 = vertices[face[(i + 1) % face.size]]
                    .rotateXZ(angle)
                    .translateZ(dz)
                    .project()
                    .screen(size)

                line(
                    p1 = p1,
                    p2 = p2,
                )
            }
        }
    }

    Canvas(
        modifier = modifier
            .fillMaxSize()
            .background(backgroundColor)
            .aspectRatio(1f)
    ) {
        frame(dz, angle)
    }

}


data class Point2d(val x: Float, val y: Float) {

    fun screen(size: Size): Point2d {
        // -1..1 => 0..2 => 0..1 => 0..size
        return Point2d(
            x = (x + 1) / 2 * size.width,
            y = (1 - (y + 1) / 2) * size.height,
        )
    }

}

data class Point3d(val x: Float, val y: Float, val z: Float) {

    fun project(): Point2d {
        return Point2d(
            x = x / z,
            y = y / z,
        )
    }

    fun translateZ(dz: Float): Point3d {
        return copy(
            z = z + dz,
        )
    }

    fun rotateXZ(angle: Float): Point3d {
        val cos = kotlin.math.cos(angle)
        val sin = kotlin.math.sin(angle)

        return Point3d(
            x = x * cos - z * sin,
            y = y,
            z = x * sin + z * cos,
        )
    }
}

val vertices = listOf(
    Point3d(0.25f, 0.25f, 0.25f),
    Point3d(-0.25f, 0.25f, 0.25f),
    Point3d(-0.25f, -0.25f, 0.25f),
    Point3d(0.25f, -0.25f, 0.25f),

    Point3d(0.25f, 0.25f, -0.25f),
    Point3d(-0.25f, 0.25f, -0.25f),
    Point3d(-0.25f, -0.25f, -0.25f),
    Point3d(0.25f, -0.25f, -0.25f),
)

val faces = listOf(
    listOf(0, 1, 2, 3),
    listOf(4, 5, 6, 7),
    listOf(0, 4),
    listOf(1, 5),
    listOf(2, 6),
    listOf(3, 7),
)

@Preview
@Composable
fun CanvasScreenPreview() {
    UrgatTheme {
        SplashScreenContent()
    }
}