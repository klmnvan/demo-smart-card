package org.example.demo_card.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import org.example.demo_card.domain.state.CardScreenSt
import javax.smartcardio.CardChannel
import javax.smartcardio.CardException
import javax.smartcardio.CommandAPDU
import javax.smartcardio.TerminalFactory


class CardScreenVM: ViewModel() {

    private val _dataSt = MutableStateFlow(CardScreenSt())
    val dataSt: StateFlow<CardScreenSt> = _dataSt.asStateFlow()

    fun updData(value: CardScreenSt) { _dataSt.value = value }

    private var cardTerminals = TerminalFactory.getDefault().terminals()

    init {
        loadTerminals()
        observeTerminals()
    }

    private fun loadTerminals() {
        viewModelScope.launch(Dispatchers.IO) {
            val list = cardTerminals.list()
            updData(dataSt.value.copy(listCardTerminal = list))
        }
    }

    private fun observeTerminals() {
        viewModelScope.launch(Dispatchers.IO) {
            while (isActive) {
                try {
                    cardTerminals.waitForChange() // блокирует до изменения
                    val updatedList = cardTerminals.list()
                    updData(dataSt.value.copy(listCardTerminal = updatedList))
                } catch (e: CardException) {
                    updData(dataSt.value.copy(listCardTerminal = emptyList(), selTerminalID = -1))
                }
            }
        }
    }

    fun getUIDCard() {
        viewModelScope.launch(Dispatchers.IO) {
            val idTerminal = dataSt.value.selTerminalID
            if(idTerminal != -1 && dataSt.value.listCardTerminal.isNotEmpty()) {
                //соединение со считкой
                val cardIsAppeared = cardTerminals.list()[idTerminal].waitForCardPresent(2_000)
                if (cardIsAppeared) {
                    val card = cardTerminals.list()[idTerminal].connect("*")
                    val channel = card.basicChannel
                    val getUidApdu = CommandAPDU(
                        byteArrayOf(0xFF.toByte(), 0xCA.toByte(), 0x00, 0x00, 0x00)
                    )
                    val response = channel.transmit(getUidApdu)

                    if (response.sw == 0x9000) {
                        // Успех — данные это и есть UID
                        val uid = response.data
                        val uidHex = uid.joinToString("") { "%02X".format(it) }
                        updData(dataSt.value.copy(cardUID = uidHex))
                    } else {
                        println("Ошибка: SW = ${"%04X".format(response.sw)}")
                    }

                    card.disconnect(false)
                }


            }
        }
    }

}