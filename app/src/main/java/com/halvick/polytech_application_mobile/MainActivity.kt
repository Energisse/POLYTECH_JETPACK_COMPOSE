package com.halvick.polytech_application_mobile

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.view.accessibility.AccessibilityEventCompat
import coil.compose.AsyncImage
import com.github.skydoves.colorpicker.compose.ColorEnvelope
import com.github.skydoves.colorpicker.compose.HsvColorPicker
import com.github.skydoves.colorpicker.compose.rememberColorPickerController
import com.halvick.polytech_application_mobile.ui.theme.POLYTECH_APPLICATION_MOBILETheme
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.reflect.full.memberProperties


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            POLYTECH_APPLICATION_MOBILETheme {
                FormScreen()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        Log.i("MyActivity", "resume")
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FormScreen() {
    var formAttributes by remember { mutableStateOf(FormAttributes()) }
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    var openResume by remember { mutableStateOf(false) }

    val imageCode = when (formAttributes.type.value) {
        Type.Consomable -> "404"
        Type.Durable -> "200"
        Type.Autre -> "500"
    }

    Scaffold(
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        }, modifier = Modifier.fillMaxSize()
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Row {
                AsyncImage(
                    model = "https://http.cat/$imageCode",
                    contentDescription = null
                )
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                RadioButton(selected = formAttributes.type.value == Type.Consomable, onClick = {
                    formAttributes.type.value = Type.Consomable
                })
                Text(text = "Consomable")
                RadioButton(selected = formAttributes.type.value == Type.Durable, onClick = {
                    formAttributes.type.value = Type.Durable
                })
                Text(text = "Durable")
                RadioButton(selected = formAttributes.type.value == Type.Autre, onClick = {
                    formAttributes.type.value = Type.Autre
                })
                Text(text = "Autre")
            }
            TextInput(
                value = formAttributes.name.value ,
                onValueChange = { formAttributes.name.value = it },
                error = formAttributes.name.error,
                label = "Nom",
            )
            TextInput(
                value = formAttributes.description.value,
                onValueChange = { formAttributes.description.value = it },
                error = formAttributes.description.error,
                label = "Description",
            )
            ColorPicker(
                value = formAttributes.couleur.value,
                error = formAttributes.couleur.error,
                onValueChange = {
                    formAttributes.couleur.value = it
                }
            )
            DatePickerDialogSample(
                value = formAttributes.dateDachat.value,
                error = formAttributes.dateDachat.error,
                onDateChange = {
                    formAttributes.dateDachat.value = it
                })
            Row(verticalAlignment = Alignment.CenterVertically) {
                Checkbox(checked = formAttributes.favoris.value
                    , onCheckedChange = {
                        formAttributes.favoris.value = it
                })
                Text(text = "Ajouter aux favoris")

            }
            Button(
                onClick = {
                    if(formAttributes.name.value.isEmpty()){
                        formAttributes.name.error = "Veuillez remplir ce champ"
                    } else {
                        formAttributes.name.error = ""
                    }

                    if(formAttributes.description.value.isEmpty()){
                        formAttributes.description.error = "Veuillez remplir ce champ"
                    } else {
                        formAttributes.description.error = ""
                    }

                    if(formAttributes.couleur.value.isEmpty()){
                        formAttributes.couleur.error = "Veuillez remplir ce champ"
                    } else {
                        formAttributes.couleur.error = ""
                    }

                    if(formAttributes.dateDachat.value == null){
                        formAttributes.dateDachat.error = "Veuillez remplir ce champ"
                    } else {
                        formAttributes.dateDachat.error = ""
                    }



                    for (prop in FormAttributes::class.memberProperties) {
                        if ((prop.get(formAttributes) as FieldAttributes<*>).error.isNotEmpty()) {
                            scope.launch {
                                snackbarHostState.showSnackbar("veuillez remplir tous les champs")
                            }
                            return@Button
                        }
                    }

                    openResume = true
                }
            ) {
                Text("Valider")
            }

            if(openResume){
                ModalResume(
                    formAttributes = formAttributes,
                    onClose = {
                        openResume = false
                    },
                )
            }
        }
    }
}

@Composable
fun ModalResume(formAttributes: FormAttributes, onClose: () -> Unit ) {
    val context = LocalContext.current

    AlertDialog(
        onDismissRequest = {
            onClose()
        },
        title = { Text(text = "Alert Dialog") },
        text = {
            Column {
                for (prop in FormAttributes::class.memberProperties) {
                    Text(text = "${prop.name}: ${(prop.get(formAttributes) as FieldAttributes<*>).value}")
                }
            }
        },
        dismissButton = {
            Button(
                onClick = {
                    onClose()
                }
            ) {
                Text(text = "Annuler")
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onClose()
                    Toast.makeText(
                        context,
                        "Objet ajouté",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            ) {
                Text(text = "Confirmer")
            }
        })
}

@Composable
fun ColorPicker( value:String, onValueChange: (String) -> Unit,error: String = "") {
    val controller = rememberColorPickerController()
    val openDialog = remember { mutableStateOf(false) }
    val source = remember {
        MutableInteractionSource()
    }

    if (
        openDialog.value
    ) {
        AlertDialog(
            onDismissRequest = {
            },
            title = { Text(text = "Choisissez une couleur") },
            text = {
                HsvColorPicker(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(450.dp)
                        .padding(10.dp),
                    controller = controller,
                    onColorChanged = { colorEnvelope: ColorEnvelope ->
                        onValueChange(colorEnvelope.hexCode)
                    }
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        openDialog.value = false
                        onValueChange(value)
                    }
                ) {
                    Text(text = "OK")
                }
            })
    }

    if ( source.collectIsPressedAsState().value)
        openDialog.value = true

    TextInput(
        readOnly = true,
        value = value,
        onValueChange = {},
        label = "couleur",
        interactionSource = source,
        error = error,
    )
}

@SuppressLint("RememberReturnType")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DatePickerDialogSample(value: Long?,onDateChange: (Long) -> Unit = {},error: String = "") {
    val openDialog = remember { mutableStateOf(false) }
    val datePickerState = rememberDatePickerState()
    val source = remember {
        MutableInteractionSource()
    }

    if (openDialog.value) {
        val confirmEnabled = remember {
            derivedStateOf { datePickerState.selectedDateMillis != null }
        }
        DatePickerDialog(
            onDismissRequest = {
                openDialog.value = false
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        openDialog.value = false
                        onDateChange(datePickerState.selectedDateMillis!!)
                    },
                    enabled = confirmEnabled.value
                ) {
                    Text("OK")
                }
            },
            dismissButton = {
                TextButton(onClick = { openDialog.value = false }) { Text("Cancel") }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }
    if ( source.collectIsPressedAsState().value)
        openDialog.value = true

    TextInput(
        readOnly = true,
        interactionSource =   source,
        value = value.let {
            if(it == null) ""
            else
            SimpleDateFormat(
                "dd/MM/yyyy",
                Locale.FRANCE
            ).format(Date(it))
        },
        onValueChange = {},
        label = "Date d'achat",
        error = error,
        trailingIcon ={
            Icon(Icons.Filled.DateRange, "color picker")
        }
    )
}

enum class Type {
    Consomable,
    Durable,
    Autre
}

data class FormAttributes(
    var name: FieldAttributes<String> = FieldAttributes(""),
    var description: FieldAttributes<String> = FieldAttributes(""),
    var couleur: FieldAttributes<String> = FieldAttributes(""),
    var favoris:  FieldAttributes<Boolean> = FieldAttributes(false),
    var type: FieldAttributes<Type> = FieldAttributes(Type.Consomable),
    var dateDachat: FieldAttributes<Long?> = FieldAttributes(null),
)

class FieldAttributes<T>(value: T) {
    var value by mutableStateOf(value)
    var error by mutableStateOf("")
}

@Composable
fun TextInput(
    onValueChange: (String) -> Unit,
    label: String,
    value: String,
    interactionSource: MutableInteractionSource = MutableInteractionSource(),
    readOnly: Boolean = false,
    error: String = "",
    trailingIcon: @Composable (() -> Unit)? = {
    if (error.isNotEmpty())
        Icon(Icons.Filled.Info, "error", tint = Color.Red)
}) {

    val color = if (error.isNotEmpty()) Color.Red else Color.DarkGray

    OutlinedTextField(
        readOnly = readOnly,
        interactionSource = interactionSource,
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        supportingText = {
                Text(error)
        },
        trailingIcon = {
            if (error.isNotEmpty())
                Icon(Icons.Filled.Info, "error", tint = Color.Red)
            else trailingIcon?.invoke()
        },
        colors = OutlinedTextFieldDefaults.colors(
            focusedTextColor = color,
            unfocusedTextColor = color,
            focusedBorderColor = color,
            unfocusedBorderColor =color,
            focusedSupportingTextColor = color,
            unfocusedSupportingTextColor = color,
             focusedLabelColor = color,
            unfocusedLabelColor = color
        )
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    POLYTECH_APPLICATION_MOBILETheme {
        FormScreen()
    }
}


