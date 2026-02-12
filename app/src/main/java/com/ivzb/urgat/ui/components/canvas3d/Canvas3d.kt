package com.ivzb.urgat.ui.components.canvas3d

import android.content.Context
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.tooling.preview.Preview
import com.ivzb.urgat.ui.theme.UrgatTheme
import java.io.BufferedReader
import java.io.InputStreamReader
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun Canvas3d(
    text: String,
    textStyle: TextStyle,
    modifier: Modifier = Modifier,
) {

    Canvas3dContent(
        model = CUBE_MODEL,
        text = text,
        textStyle = textStyle,
        modifier = modifier
    )
}

fun read3dModel(context: Context, fileName: String): Model3d {
    val modelContent = readObjFromAssets(context, "models_3d/$fileName")
        .split("\n")
        .filter { it.isNotBlank() }
        .map { it.split(" ") }

    val vertices = modelContent
        .filter {
            it[0] == "v"
        }
        .map {
            val (x, y, z) = it.drop(1).map { it.toFloat() }
            Point3d(x, y, z)
        }

    val faces = modelContent
        .filter {
            it[0] == "f"
        }
        .map {
            it.drop(1).map { it.split('/')[0].toInt() - 1 }.take(3)
        }

    return Model3d(
        vertices = vertices,
        faces = faces,
    )
}

fun readObjFromAssets(context: Context, fileName: String): String {
    return context.assets.open(fileName).use { inputStream ->
        BufferedReader(InputStreamReader(inputStream)).use { reader ->
            reader.readText()
        }
    }
}

@Composable
private fun Canvas3dContent(
    model: Model3d,
    text: String,
    textStyle: TextStyle,
    modifier: Modifier = Modifier,
) {
    var dz by remember { mutableFloatStateOf(1f) }
    var angle by remember { mutableFloatStateOf(0f) }
    val fps = 120f

    val backgroundColor = MaterialTheme.colorScheme.background
    val foregroundColor = MaterialTheme.colorScheme.onBackground
    val primaryColor = MaterialTheme.colorScheme.primary

    val textMeasurer = rememberTextMeasurer()

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
        val size = 20f

        drawRect(
            color = foregroundColor,
            topLeft = Offset(
                x = point.x - size / 2,
                y = point.y - size / 2,
            ),
            size = Size(size, size),
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
    
    fun DrawScope.fillFace(face: List<Int>, model: Model3d, dz: Float, angle: Float, color: Color) {
        val path = Path().apply {
            val p1 = model.vertices[face[0]].transform(angle, dz, size)
            moveTo(p1.x, p1.y)

            for (i in 0 until face.size) {
                val p2 = model.vertices[face[(i + 1) % face.size]].transform(angle, dz, size)
                lineTo(p2.x, p2.y)
            }

            close()
        }

        drawPath(
            path = path,
            color = color.copy(alpha = 0.5f)
        )
    }

    fun DrawScope.frame(dz: Float, angle: Float) {
        clear()

        model.faces.forEach { face ->
            fillFace(face, model, dz, angle, primaryColor)
        }

        model.vertices.forEach { p ->
//            point(p.transform(angle, dz, size))
        }

        model.faces.forEach { face ->
            for (i in 0 until face.size) {
                val p1 = model.vertices[face[i]].transform(angle, dz, size)
                val p2 = model.vertices[face[(i + 1) % face.size]].transform(angle, dz, size)

                line(
                    p1 = p1,
                    p2 = p2,
                )
            }
        }

        val textLayoutResult = textMeasurer.measure(
            text = text,
            style = textStyle,
        )

        drawText(
            textLayoutResult = textLayoutResult,
            topLeft = Offset(
                x = (size.width / 2) - (textLayoutResult.size.width / 2),
                y = (size.height * 0.8f),
            )
        )
    }

    Canvas(
        modifier = modifier
            .fillMaxWidth()
            .background(backgroundColor)
            .aspectRatio(1f)
    ) {
        frame(dz, angle)
    }

}

data class Model3d(val vertices: List<Point3d>, val faces: List<List<Int>>)

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

    fun transform(angle: Float, dz: Float, size: Size): Point2d {
        return rotateXZ(angle)
//            .rotateXY(angle)
            .rotateYZ(-angle)
            .translateZ(dz)
            .project()
            .screen(size)
    }

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
        val cos = cos(angle)
        val sin = sin(angle)

        return Point3d(
            x = x * cos - z * sin,
            y = y,
            z = x * sin + z * cos,
        )
    }

    fun rotateXY(angle: Float): Point3d {
        val cos = cos(angle)
        val sin = sin(angle)

        return Point3d(
            x = x * cos - y * sin,
            y = x * sin + y * cos,
            z = z,
        )
    }

    fun rotateYZ(angle: Float): Point3d {
        val cos = cos(angle)
        val sin = sin(angle)

        return Point3d(
            x = x,
            y = y * cos - z * sin,
            z = y * sin + z * cos,
        )
    }
}

val CUBE_MODEL = Model3d(
    vertices = listOf(
        // cube
        Point3d(0.25f, 0.25f, 0.25f),
        Point3d(-0.25f, 0.25f, 0.25f),
        Point3d(-0.25f, -0.25f, 0.25f),
        Point3d(0.25f, -0.25f, 0.25f),

        Point3d(0.25f, 0.25f, -0.25f),
        Point3d(-0.25f, 0.25f, -0.25f),
        Point3d(-0.25f, -0.25f, -0.25f),
        Point3d(0.25f, -0.25f, -0.25f),

        // U letter
//        Point3d(-0.15f, 0.15f, 0.05f),
//        Point3d(-0.05f, 0.15f, 0.05f),
//        Point3d(-0.15f, -0.15f, 0.05f),
//        Point3d(-0.05f, -0.05f, 0.05f),
//
//        Point3d(0.15f, 0.15f, 0.05f),
//        Point3d(0.05f, 0.15f, 0.05f),
//        Point3d(0.15f, -0.15f, 0.05f),
//        Point3d(0.05f, -0.05f, 0.05f),
//
//        Point3d(-0.15f, 0.15f, -0.05f),
//        Point3d(-0.05f, 0.15f, -0.05f),
//        Point3d(-0.15f, -0.15f, -0.05f),
//        Point3d(-0.05f, -0.05f, -0.05f),
//
//        Point3d(0.15f, 0.15f, -0.05f),
//        Point3d(0.05f, 0.15f, -0.05f),
//        Point3d(0.15f, -0.15f, -0.05f),
//        Point3d(0.05f, -0.05f, -0.05f),

    ),

    faces = listOf(
        // cube
        listOf(0, 1, 2, 3),
        listOf(4, 5, 6, 7),

        listOf(0, 1, 5, 4),
        listOf(0, 3, 7, 4),
        listOf(2, 3, 7, 6),
        listOf(2, 6, 5, 1),

        // U letter
//        listOf(8, 9, 11, 15, 13, 12, 14, 10),
//        listOf(16, 17, 19, 23, 21, 20, 22, 18),
//
//        listOf(8, 9, 17, 16),
//        listOf(8, 10, 18, 16),
//        listOf(14, 10, 18, 22),
//        listOf(14, 22, 20, 12),
//        listOf(12, 20, 21, 13),
//        listOf(13, 21, 23, 15),
//        listOf(15, 23, 19, 11),
//        listOf(11, 19, 17, 9),

//        listOf(0, 4),
//        listOf(1, 5),
//        listOf(2, 6),
//        listOf(3, 7),
    ),
)

@Preview
@Composable
fun Canvas3dContentPreview() {
    UrgatTheme {
        Canvas3dContent(
            model = CUBE_MODEL,
            text = "Loading",
            textStyle = MaterialTheme.typography.titleMedium,
            modifier = Modifier
        )
    }
}