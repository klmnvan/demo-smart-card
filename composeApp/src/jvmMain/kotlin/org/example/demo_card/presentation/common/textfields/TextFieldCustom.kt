package org.example.demo_card.presentation.common.textfields

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.Black
import androidx.compose.ui.unit.dp

@Composable
fun TextFieldCustom(value: String, placeholder: String, input: (String) -> Unit) {
    OutlinedTextField(
        value = value,
        onValueChange = {input(it) },
        modifier = Modifier
            .shadow(
                elevation = 4.dp, shape = RoundedCornerShape(30), spotColor = Color(
                    Black.value
                )
            ),
        placeholder = { Text(text = placeholder)},
        singleLine = true,
        shape = RoundedCornerShape(15.dp),
    )
}