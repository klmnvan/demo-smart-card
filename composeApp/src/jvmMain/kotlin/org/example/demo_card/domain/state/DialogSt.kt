package org.example.demo_card.domain.state

data class DialogSt(
    val title: String = "",
    val description: String = "",
    val button: String = "",
    val dialogIsOpen: Boolean = false,
)