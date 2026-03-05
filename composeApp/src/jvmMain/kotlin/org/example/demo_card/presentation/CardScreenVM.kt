package org.example.demo_card.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import org.example.demo_card.domain.converters.toCardDecimal
import org.example.demo_card.domain.converters.toWiegand26
import org.example.demo_card.domain.state.CardScreenSt
import org.example.demo_card.domain.state.DialogSt
import javax.smartcardio.Card
import javax.smartcardio.CardException
import javax.smartcardio.CommandAPDU
import javax.smartcardio.TerminalFactory


class CardScreenVM : ViewModel() {

    private val _dataSt = MutableStateFlow(CardScreenSt())
    val dataSt = _dataSt.asStateFlow()

    fun updData(value: CardScreenSt) { _dataSt.value = value }

    private val _messageSt = MutableStateFlow(DialogSt())
    val messageSt = _messageSt.asStateFlow()

    fun updMessage(value: DialogSt) { _messageSt.value = value }

    private var cardTerminals = TerminalFactory.getDefault().terminals()
    private var connection: Card? = null
    private var cardMonitorJob: Job? = null

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
                    updData(dataSt.value.copy(selTerminalID = -1))
                }
            }
        }
    }

    fun getUIDCard() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                if (connection != null) {
                    val channel = connection!!.basicChannel
                    //получаем с канала связи со считкой номер пропуска
                    val getUidApdu = CommandAPDU(
                        byteArrayOf(0xFF.toByte(), 0xCA.toByte(), 0x00, 0x00, 0x00)
                    )
                    val response = channel.transmit(getUidApdu)
                    if (response.sw == 0x9000) {
                        val uid = response.data
                        val uidHex = uid.joinToString("") { "%02X".format(it) }
                        updData(dataSt.value.copy(
                            cardUID_HEX = uidHex,
                            cardUID_DEC = uid.toCardDecimal(),
                            cardUID_W26 = uid.toWiegand26()))
                    }
                    connection = null
                } else openDialog("Не установлено соединение с картой", "Ошибка при чтении ID карты")
            } catch (e: Exception) {
                println("Ошибка в процессе получения ID карты: " + e.message)
            }
        }
    }

    fun updateConnection() {
        with(dataSt.value) {
            cardMonitorJob?.cancel()
            connection?.disconnect(false)
            if(listCardTerminal.isEmpty() || selTerminalID == -1) return
            val terminal = listCardTerminal[selTerminalID]
            cardMonitorJob = viewModelScope.launch(Dispatchers.IO) {
                while (isActive) {
                    try {
                        if (!terminal.isCardPresent) {
                            terminal.waitForCardPresent(0) // 0 = ждать бесконечно
                        }
                        connection = terminal.connect("*")
                        cardIsAttached(true)
                        terminal.waitForCardAbsent(0)
                        cardIsAttached(false)
                        connection?.disconnect(true)
                    } catch (e: Exception) {

                    }
                }
            }
        }
    }

    private fun openDialog(desc: String, title: String) {
        updMessage(_messageSt.value.copy(
            dialogIsOpen = true,
            description = desc,
            title = title))
    }

    private fun cardIsAttached(isAttached: Boolean) {
        updData(dataSt.value.copy(cardIsAttached = isAttached))
    }

}

