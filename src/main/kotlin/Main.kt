import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.awt.ComposeWindow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.*
import controls.mainFractalWindow
import controls.menu
import drawing.FractalPainter
import drawing.convertation.Plane
import math.fractals.Mandelbrot
import java.io.File
import java.util.*
import kotlin.math.absoluteValue
import kotlin.math.cos
import kotlin.math.log2
import kotlin.math.sin

fun openFileDialog(window: ComposeWindow, title: String, allowedExtensions: List<String>, allowMultiSelection: Boolean = true): Set<File> {
    return FileDialog(window, title, FileDialog.LOAD).apply {
        isMultipleMode = allowMultiSelection

        file = allowedExtensions.joinToString(";") { "*$it" } // e.g. '*.jpg'

        setFilenameFilter { _, name ->
            allowedExtensions.any {
                name.endsWith(it)
            }
        }
        isVisible = true
    }.files.toSet()
}


@Composable
@Preview
fun App(){
    val fileDialogSaver = remember {  FileDialog(ComposeWindow(), "Сохранить фрактал", FileDialog.SAVE).apply {
        isMultipleMode = false
        setFilenameFilter { _, filename ->
            val extension = File(filename).extension.lowercase(Locale.getDefault())
            extension == "fractal"
        }
    }}
    val fileDialogLoader = remember {  FileDialog(ComposeWindow(), "Открыть фрактал", FileDialog.LOAD).apply {
        isMultipleMode = false
        setFilenameFilter { _, filename ->
            val extension = File(filename).extension.lowercase(Locale.getDefault())
            extension == "fractal"
        }
    }}
        if (it == 1f) Color.Black
        else {
            val r = sin(it*15f).absoluteValue
            val g = (sin(-8f*it)* cos(it*5f+12f)).absoluteValue
            val b = log2(2f - cos(sin(18*-it)))
            Color(r, g, b)
        }
    }}

    fp.plane = Plane(-2.0, 1.0, -1.0, 1.0, 0f, 0f)
    MaterialTheme{
        Scaffold(
            topBar = {
                var dynamicIterationsCheck by remember { mutableStateOf(false) }
                var isVideoDialogVisible by remember { mutableStateOf(false) }
                menu(
                    saveImage = { TODO("ПЕРЕДАТЬ ФУНКЦИЮ ДЛЯ СОХРАНЕНИЯ КАК КАРТИНКИ")},
                    saveFractal = {
                        fileDialogSaver.isVisible = true
                        val selectedFile = fileDialogSaver.file
                        val filePath = fileDialogSaver.directory + selectedFile
                        fp.plane?.let{
                            val fractalData = FractalData(it.xMin,it.xMax,it.yMin,it.yMax, 1)
                            FractalDataProcessor.saveFractalDataToFile(fractalData,filePath)
                        }
                    },
                    openFractal = {
                        fileDialogLoader.isVisible = true
                        val selectedFile = fileDialogLoader.file
                        val filePath = fileDialogLoader.directory + selectedFile
                        val resData = FractalDataProcessor.readFractalDataFromFile(filePath)
                        println(resData?.xMax ?: "null")
                        resData?.let {fd ->
                            fp.plane?.let {plane ->
                                fp.plane = Plane(fd.xMin,fd.xMax,fd.yMin,fd.yMax,plane.width,plane.height)
                                fp.refresh = true
                            }

                    },
                    back = { TODO("ОТМЕНА ДЕЙСТВИЯ")},
                    showVideoDialog = {},
                    addFrames = fp.img,
                    //ТУТ ПЕРЕДАЕТСЯ КАРТА {НАЗВАНИЕ -> ФУНКЦИЯ}, в неё мохно передавать цветовые схемы, сколько угодно.
                    //т.е когда пользователь будет нажимать на название, то вызывается функция, которая меняет фрактал
                    themesMap = mapOf(),
                    //Это Boolean значение для динамических итераций, Переключатель True/False. Тут менять ничего не нужно.
                    //Нужно просто реализовать логику изменения
                    dynamicIterationsCheck = dynamicIterationsCheck,
                    dynamicIterationsCheckChange =  {dynamicIterationsCheck = it},
                )
            },
            modifier = Modifier.fillMaxSize()){
            Box(
                Modifier.fillMaxSize()
            ){
                mainFractalWindow(fp)
            }
        }
    }
}

fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
        title = "Множество Мандельброта",
        state = rememberWindowState(
            width = 800.dp,
            height = 600.dp,
            placement = WindowPlacement.Floating,
            position = WindowPosition(100.dp, 100.dp),
            isMinimized = false
        ),
    ) {
        App()
    }
}

fun testBranch(){
    val fileChooser = JFileChooser()
    fileChooser.fileFilter = FileNameExtensionFilter("Текстовые файлы", "jpg") // Фильтр по расширению файла

    val result = fileChooser.showOpenDialog(null)

    if (result == JFileChooser.APPROVE_OPTION) {
        val selectedFile = fileChooser.selectedFile
        println("Выбранный файл: ${selectedFile.absolutePath}")
    } else {
        println("Выбор файла отменен")
    }
}
