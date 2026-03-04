package org.example.demo_card.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import org.example.demo_card.domain.state.CardScreenSt
import javax.smartcardio.CardException
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
                    val cause = e.cause?.message ?: ""
                    if ("SCARD_E_NO_READERS_AVAILABLE" in cause) {
                        restartSmartCardService()
                    } else {
                        delay(2000)
                    }
                }
            }
        }
    }

    private fun restartSmartCardService() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                // Перезапускаем службу через командную строку
                Runtime.getRuntime().exec("net stop SCardSvr").waitFor()
                Runtime.getRuntime().exec("net start SCardSvr").waitFor()
                delay(1000) // ждём пока служба запустится
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

}