package org.example.demo_card.domain.state

import javax.smartcardio.CardTerminal

data class CardScreenSt(

    val cardUID: String = "",
    val listCardTerminal: List<CardTerminal> = listOf(),
    val selTerminalID: Int = -1
)

class TestClass (val test: String) {

}



