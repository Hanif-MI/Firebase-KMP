package org.hanif.firebase

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application

fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
        title = "Firebase-KMP",
    ) {
        App()
    }
}