package isel.leic.tds.gui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.Button
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEvent
import androidx.compose.ui.input.key.key
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogState

/**
 * Window to edit the name of the game to start.
 * Has the buttons "Start" and "Cancel" and receives as parameters the functions [onCancel] and [onStart].
 * Is a stateful composable to store the name during editing.
 *
 */
@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun DialogInput(onStart: (String)->Unit, onCancel: ()->Unit) = Dialog(
    onCloseRequest = onCancel,
    title = "Game name",
    state = DialogState( height = Dp.Unspecified, width = 200.dp)
) {
    val (name, setName) = remember { mutableStateOf(
        "${('a'..'z').random()}".repeat((1..4).random())

    )}
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
       OutlinedTextField(name, onValueChange = { setName(it) }, label = { Text("name") }, singleLine = true )
       Row(
           Modifier.fillMaxWidth(),
           horizontalArrangement = Arrangement.SpaceEvenly
       ) {
           Button(onClick = {
                if (name.isNotBlank()) onStart(name)
           } ) { Text("Start") }
           Button(onClick = onCancel) { Text("Cancel")}
       }
   }
}