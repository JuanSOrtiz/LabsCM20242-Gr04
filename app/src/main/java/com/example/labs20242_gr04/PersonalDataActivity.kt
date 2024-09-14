package com.example.labs20242_gr04

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.unit.dp
import com.example.labs20242_gr04.ui.theme.Labs20242Gr04Theme
import java.text.SimpleDateFormat
import java.util.*

class PersonalDataActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Labs20242Gr04Theme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    PersonalDataScreen()
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PersonalDataScreen() {
    var firstName by remember { mutableStateOf("") }
    var lastName by remember { mutableStateOf("") }
    var selectedDate by remember { mutableStateOf<Long?>(null) }
    var showDatePicker by remember { mutableStateOf(false) }
    var selectedGender by remember { mutableStateOf("Masculino") }
    val genderOptions = listOf("Masculino", "Femenino", "Otro")
    var educationLevel by remember { mutableStateOf("Primaria") }
    var expanded by remember { mutableStateOf(false) }
    val educationLevels = listOf("Primaria", "Secundaria", "Universitaria", "Posgrado")
    val context = LocalContext.current
    val scrollState = rememberScrollState()

    var showErrorDialog by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "Datos Personales") }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
                .verticalScroll(scrollState)
        ) {
// Campo Nombres
            OutlinedTextField(
                value = firstName,
                onValueChange = { firstName = it },
                label = { Text("Nombres*") },
                keyboardOptions = KeyboardOptions.Default.copy(
                    capitalization = KeyboardCapitalization.Words,
                    imeAction = ImeAction.Next,
                    autoCorrect = false
                ),
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                isError = firstName.isEmpty()
            )
            Spacer(modifier = Modifier.height(16.dp))

            // Campo Apellidos
            OutlinedTextField(
                value = lastName,
                onValueChange = { lastName = it },
                label = { Text("Apellidos*") },
                keyboardOptions = KeyboardOptions.Default.copy(
                    capitalization = KeyboardCapitalization.Words,
                    imeAction = ImeAction.Next,
                    autoCorrect = false
                ),
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                isError = lastName.isEmpty()
            )
            Spacer(modifier = Modifier.height(16.dp))

            // RadioButton Sexo
            Text(text = "Sexo")
            genderOptions.forEach { gender ->
                Row {
                    RadioButton(
                        selected = selectedGender == gender,
                        onClick = { selectedGender = gender }
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = gender)
                }
            }
            Spacer(modifier = Modifier.height(16.dp))

            // DatePicker Fecha de Nacimiento
            if (selectedDate != null) {
                Text(
                    text = "Fecha de nacimiento: ${formatDate(selectedDate)}",
                    style = MaterialTheme.typography.bodyLarge
                )
            } else {
                Text(text = "No hay fecha seleccionada", style = MaterialTheme.typography.bodyLarge)
            }
            Spacer(modifier = Modifier.height(8.dp))

            Button(onClick = { showDatePicker = true }) {
                Text(text = "Seleccionar fecha de nacimiento*")
            }

            if (showDatePicker) {
                DatePickerModal(
                    onDateSelected = { dateMillis ->
                        selectedDate = dateMillis
                        showDatePicker = false
                    },
                    onDismiss = {
                        showDatePicker = false
                    }
                )
            }
            Spacer(modifier = Modifier.height(16.dp))

            // Grado de escolaridad (Spinner)
            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = { expanded = !expanded }
            ) {
                OutlinedTextField(
                    value = educationLevel,
                    onValueChange = { },
                    label = { Text("Grado de Escolaridad*") },
                    readOnly = true,
                    modifier = Modifier
                        .menuAnchor()
                        .fillMaxWidth()
                        .clickable { expanded = !expanded },
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                    }
                )
                ExposedDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    educationLevels.forEach { level ->
                        DropdownMenuItem(
                            onClick = {
                                educationLevel = level
                                expanded = false
                            },
                            text = { Text(level) }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Botón Siguiente con validación y logging
            Button(
                onClick = {
                    if (validateFields(firstName, lastName, selectedDate)) {
                        logUserData(firstName, lastName, selectedDate, selectedGender, educationLevel)

                        // Pasar los datos a ContactDataActivity
                        val intent = Intent(context, ContactDataActivity::class.java).apply {
                            putExtra("firstName", firstName)
                            putExtra("lastName", lastName)
                            putExtra("birthDate", selectedDate)
                            putExtra("gender", selectedGender)
                            putExtra("educationLevel", educationLevel)
                        }
                        context.startActivity(intent)
                    } else {
                        errorMessage = "Por favor, complete todos los campos obligatorios."
                        showErrorDialog = true
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
            ) {
                Text("Siguiente")
            }
        }
    }

    // Diálogo de error, debe estar fuera del Scaffold y de la Column
    if (showErrorDialog) {
        AlertDialog(
            onDismissRequest = { showErrorDialog = false },
            title = { Text("Error") },
            text = { Text(errorMessage) },
            confirmButton = {
                Button(onClick = { showErrorDialog = false }) {
                    Text("OK")
                }
            }
        )
    }
}

// Función para validar campos
fun validatePersonalDataFields(firstName: String, lastName: String, birthDate: Long?): Boolean {
    return firstName.isNotBlank() && lastName.isNotBlank() && birthDate != null
}

// Función para hacer logging de los datos personales
fun logPersonalData(firstName: String, lastName: String, birthDate: Long?, gender: String, educationLevel: String) {
    Log.d("PersonalData", """
        Nombres: $firstName
        Apellidos: $lastName
        Fecha de nacimiento: ${formatDate(birthDate)}
        Sexo: $gender
        Grado de escolaridad: $educationLevel
    """.trimIndent())
}




@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DatePickerModal(
    onDateSelected: (Long?) -> Unit,
    onDismiss: () -> Unit
) {
    val datePickerState = rememberDatePickerState()

    DatePickerDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = {
                onDateSelected(datePickerState.selectedDateMillis)
                onDismiss()
            }) {
                Text("Aceptar")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    ) {
        DatePicker(state = datePickerState)
    }
}

fun formatDate(timestamp: Long?): String {
    return if (timestamp != null) {
        val sdf = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
        sdf.format(Date(timestamp))
    } else {
        "N/A"
    }
}

fun validateFields(
    firstName: String,
    lastName: String,
    birthDate: Long?
): Boolean {
    return firstName.isNotEmpty() && lastName.isNotEmpty() && birthDate != 0L
}

fun logUserData(
    firstName: String,
    lastName: String,
    birthDate: Long?,
    gender: String,
    educationLevel: String
) {
    Log.d("PersonalData", """
        Nombres: $firstName
        Apellidos: $lastName
        Fecha de nacimiento: ${formatDate(birthDate)}
        Sexo: $gender
        Grado de escolaridad: $educationLevel
    """.trimIndent())
}

@Composable
fun SimpleOutlinedTextFieldSample(txt: String) {
    var text by remember { mutableStateOf("") }

    OutlinedTextField(
        value = text,
        onValueChange = { newText -> text = newText },
        label = { Text(txt) },
        keyboardOptions = KeyboardOptions(
            capitalization = KeyboardCapitalization.Sentences,
            imeAction = ImeAction.Done
        ),
        modifier = Modifier.fillMaxWidth() // Asegúrate de que el TextField use el ancho completo
    )
}