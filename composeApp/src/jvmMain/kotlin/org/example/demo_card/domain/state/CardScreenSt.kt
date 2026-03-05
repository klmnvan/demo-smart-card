package org.example.demo_card.domain.state

import javax.smartcardio.CardTerminal

data class CardScreenSt(

    val cardUID_HEX: String = "",
    val cardUID_DEC: String = "",
    val cardUID_W26: String = "",
    val listCardTerminal: List<CardTerminal> = listOf(),
    val selTerminalID: Int = -1,
    val cardIsAttached: Boolean = false,
    val reverseOrder: Boolean = false,
)



