package org.example.demo_card.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import org.example.demo_card.presentation.common.dialogs.DialogError
import org.example.demo_card.presentation.common.spacers.SpacerHeight

@OptIn(ExperimentalMaterialApi::class)
@Preview(showBackground = true)
@Composable
fun CardScreen() {

    MaterialTheme {
        val vm = viewModel { CardScreenVM() }
        val state = vm.dataSt.collectAsState()
        val messageState = vm.messageSt.collectAsState()

        if(messageState.value.dialogIsOpen) {
            DialogError(messageState.value.title, messageState.value.description) {
                vm.updMessage(messageState.value.copy(dialogIsOpen = false))
            }
        }

        with(state.value) {
            LaunchedEffect(selTerminalID) {
                vm.updateConnection()
            }

            Column(
                Modifier
                    .fillMaxSize()
                    .padding(20.dp),
            ) {
                Text("Настольные считыватели: ")
                SpacerHeight(20.dp)
                CustomDropDownMenu(state.value.listCardTerminal,state.value.selTerminalID) {
                    vm.updData(vm.dataSt.value.copy(selTerminalID = it))
                }
                SpacerHeight(20.dp)
                Row(modifier = Modifier.fillMaxWidth()) {
                    TextButton(onClick = { vm.getUIDCard() },
                        colors = ButtonDefaults.textButtonColors(
                            backgroundColor = Color(0xFFFF9800),
                            contentColor = Color.White)
                    ) {
                        Text("Получить UID")
                    }
                }
                SpacerHeight(20.dp)
                Text("Код карты: ${state.value.cardUID}")
            }
        }
    }

}

