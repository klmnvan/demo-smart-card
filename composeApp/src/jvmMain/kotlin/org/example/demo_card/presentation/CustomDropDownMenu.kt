package org.example.demo_card.presentation

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ExposedDropdownMenuBox
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import javax.smartcardio.CardTerminal

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun CustomDropDownMenu(list: List<CardTerminal>, selOptionId: Int, onSelect: (Int) -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded },
    ) {
        OutlinedTextField(
            modifier = Modifier.Companion.fillMaxWidth(),
            value = if (selOptionId != -1) list[selOptionId].name else "Не выбрано",
            onValueChange = { },
            placeholder = { Text(text = "Считыватели") },
            readOnly = true,
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            list.forEach { terminal ->
                DropdownMenuItem(
                    onClick = {
                        expanded = false
                        onSelect(list.indexOf(terminal))
                    },
                ) {
                    Text(text = terminal.name)
                }
            }
        }
    }
}