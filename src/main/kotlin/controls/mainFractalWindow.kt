package controls

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.PointerMatcher
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import drawing.FractalPainter
import drawing.Painter
import drawing.SelectionRect
import drawing.convertation.Converter

@Composable
fun mainFractalWindow(fp:FractalPainter){
    Box(){
        DrawingPanel(fp){size ->
            fp.width = size.width.toInt()
            fp.height = size.height.toInt()
            fp.refresh = true
        }
        SelectionPanel{
            fp.plane?.let{ plane ->
                val xMin = Converter.xScr2Crt(it.topLeft.x, plane)
                val xMax = Converter.xScr2Crt(it.topLeft.x+it.size.width, plane)
                val yMax = Converter.yScr2Crt(it.topLeft.y, plane)
                val yMin = Converter.yScr2Crt(it.topLeft.y+it.size.height, plane)
                plane.xMin = xMin
                plane.xMax = xMax
                plane.yMin = yMin
                plane.yMax = yMax
                fp.refresh = true
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun SelectionPanel(
    onSelected: (SelectionRect)->Unit
) {
    var rect by remember { mutableStateOf(SelectionRect(Offset.Zero)) }
    Canvas(Modifier.fillMaxSize().padding(8.dp).pointerInput(Unit){
        detectDragGestures(
            onDragStart = {
                rect = SelectionRect(it)
            },
            onDrag = {
                rect.addPoint(it)
            },
            onDragEnd = {
                onSelected(rect)
                rect = SelectionRect(Offset.Zero)
            },
            matcher = PointerMatcher.Primary)
    }){
        drawRect(Color(0f, 1f, 1f, 0.3f), rect.topLeft, rect.size)
    }
}

@Composable
fun DrawingPanel(
    fp: Painter,
    onResize: (Size)-> Unit = {},
) {
    Canvas(Modifier.fillMaxSize().padding(8.dp)) {
        if(fp.width != size.width.toInt() || fp.height != size.height.toInt() )
            onResize(size)

        fp.paint(this)
    }
}