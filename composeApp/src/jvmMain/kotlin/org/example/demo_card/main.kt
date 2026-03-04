package org.example.demo_card

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import org.example.demo_card.presentation.CardScreen

fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
        title = "demo_card",
    ) {
        CardScreen()
    }
}

