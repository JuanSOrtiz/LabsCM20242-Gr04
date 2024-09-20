package com.example.labs20242_gr04

import androidx.compose.ui.res.stringResource
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
    val masculino = stringResource(R.string.masculino)
    val primaria = stringResource(R.string.primaria)
    val personalError = stringResource(R.string.personal_error)


    var firstName by remember { mutableStateOf("") }
    var lastName by remember { mutableStateOf("") }
    var selectedDate by remember { mutableStateOf<Long?>(null) }
    var showDatePicker by remember { mutableStateOf(false) }
    var selectedGender by remember { mutableStateOf(masculino) }
    val genderOptions = listOf(
        stringResource(R.string.masculino),
        stringResource(R.string.femenino),
        stringResource(R.string.otro)
    )
    var educationLevel by remember { mutableStateOf(primaria) }
    var expanded by remember { mutableStateOf(false) }
    val educationLevels = listOf(
        stringResource(R.string.primaria),
        stringResource(R.string.secundaria),
        stringResource(R.string.universitaria),
        stringResource(R.string.posgrado)
    )
    val context = LocalContext.current
    val scrollState = rememberScrollState()

    var showErrorDialog by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = stringResource(R.string.personal_data_title)) }
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
                label = { Text(stringResource(R.string.first_name_label)) },
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
                label = { Text(stringResource(R.string.last_name_label)) },
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
            Text(text = stringResource(R.string.gender_label))
            genderOptions.forEach { gender ->
                Row(verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) {
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
                    text = stringResource(R.string.birthdate_selected, formatDate(selectedDate)),
                    style = MaterialTheme.typography.bodyLarge
                )
            } else {
                Text(text = stringResource(R.string.birthdate_not_selected), style = MaterialTheme.typography.bodyLarge)
            }
            Spacer(modifier = Modifier.height(8.dp))

            Button(onClick = { showDatePicker = true }) {
                Text(text = stringResource(R.string.select_birthdate_button))
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
                    label = { Text(stringResource(R.string.education_level_label)) },
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
                        logUserData(context, firstName, lastName, selectedDate, selectedGender, educationLevel)

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
                        errorMessage = personalError
                        showErrorDialog = true
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
            ) {
                Text(stringResource(R.string.next_button))
            }
        }
    }

    // Diálogo de error, debe estar fuera del Scaffold y de la Column
    if (showErrorDialog) {
        AlertDialog(
            onDismissRequest = { showErrorDialog = false },
            title = { Text(stringResource(R.string.error_title)) },
            text = { Text(errorMessage) },
            confirmButton = {
                Button(onClick = { showErrorDialog = false }) {
                    Text(stringResource(R.string.ok_button))
                }
            }
        )
    }
}

// Función para validar campos
fun validateFields(
    firstName: String,
    lastName: String,
    birthDate: Long?
): Boolean {
    return firstName.isNotEmpty() && lastName.isNotEmpty() && birthDate != null && birthDate != 0L
}

// Función para hacer logging de los datos personales
fun logUserData(
    context: android.content.Context,
    firstName: String,
    lastName: String,
    birthDate: Long?,
    gender: String,
    educationLevel: String
) {
    Log.d("PersonalData", """
        ${context.getString(R.string.first_name_label)}: $firstName
        ${context.getString(R.string.last_name_label)}: $lastName
        ${context.getString(R.string.birthdate_selected)}: ${formatDate(birthDate)}
        ${context.getString(R.string.gender_label)}: $gender
        ${context.getString(R.string.education_level_label)}: $educationLevel
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
                Text(stringResource(R.string.accept_button))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.cancel_button))
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
