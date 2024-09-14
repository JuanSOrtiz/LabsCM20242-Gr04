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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.labs20242_gr04.ui.theme.Labs20242Gr04Theme
import java.text.SimpleDateFormat
import java.util.*

class ContactDataActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Obtener los datos enviados desde la actividad anterior
        val firstName = intent.getStringExtra("firstName") ?: ""
        val lastName = intent.getStringExtra("lastName") ?: ""
        val birthDate = intent.getLongExtra("birthDate", 0L)
        val gender = intent.getStringExtra("gender") ?: ""
        val educationLevel = intent.getStringExtra("educationLevel") ?: ""

        // Configurar el contenido de la actividad
        setContent {
            Labs20242Gr04Theme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    ContactDataScreen(firstName, lastName, birthDate, gender, educationLevel)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ContactDataScreen(
    firstName: String,
    lastName: String,
    birthDate: Long,
    gender: String,
    educationLevel: String
) {
    var phoneNumber by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var country by remember { mutableStateOf("") }
    var city by remember { mutableStateOf("") }
    val context = LocalContext.current
    val scrollState = rememberScrollState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "Datos de Contacto") }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp)
                .verticalScroll(scrollState)
        ) {
            // Campos de entrada
            FormFields(
                phoneNumber, { phoneNumber = it },
                address, { address = it },
                email, { email = it },
                country, { country = it },
                city, { city = it }
            )

            Spacer(modifier = Modifier.weight(1f))

            // Botón Guardar
            Button(
                onClick = {
                    // Registrar los datos en el Logcat
                    logAllUserData(
                        firstName, lastName, birthDate, gender, educationLevel,
                        phoneNumber, address, email, country, city
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .padding(vertical = 16.dp)
            ) {
                Text("Guardar")
            }
        }
    }
}

// Función para registrar los datos en Logcat
fun logAllUserData(
    firstName: String, lastName: String, birthDate: Long, gender: String, educationLevel: String,
    phoneNumber: String, address: String, email: String, country: String, city: String
) {
    Log.d("ContactData", """
        Nombres: $firstName
        Apellidos: $lastName
        Fecha de nacimiento: ${formatDate(birthDate)}
        Sexo: $gender
        Grado de escolaridad: $educationLevel
        Teléfono: $phoneNumber
        Dirección: $address
        Email: $email
        País: $country
        Ciudad: $city
    """.trimIndent())
}

// Función para formatear la fecha de nacimiento
fun formatDate(milliseconds: Long): String {
    val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    return sdf.format(Date(milliseconds))
}

@Composable
fun FormFields(
    phoneNumber: String, onPhoneChange: (String) -> Unit,
    address: String, onAddressChange: (String) -> Unit,
    email: String, onEmailChange: (String) -> Unit,
    country: String, onCountryChange: (String) -> Unit,
    city: String, onCityChange: (String) -> Unit
) {
    // Campo Teléfono (obligatorio)
    OutlinedTextField(
        value = phoneNumber,
        onValueChange = onPhoneChange,
        label = { Text("Teléfono*") },
        keyboardOptions = KeyboardOptions.Default.copy(
            keyboardType = KeyboardType.Phone,
            imeAction = ImeAction.Next
        ),
        singleLine = true,
        modifier = Modifier.fillMaxWidth(),
        isError = phoneNumber.isEmpty()
    )
    Spacer(modifier = Modifier.height(16.dp))

    // Campo Dirección
    OutlinedTextField(
        value = address,
        onValueChange = onAddressChange,
        label = { Text("Dirección") },
        keyboardOptions = KeyboardOptions.Default.copy(
            imeAction = ImeAction.Next,
            autoCorrect = false
        ),
        singleLine = true,
        modifier = Modifier.fillMaxWidth()
    )
    Spacer(modifier = Modifier.height(16.dp))

    // Campo Email (obligatorio)
    OutlinedTextField(
        value = email,
        onValueChange = onEmailChange,
        label = { Text("Email*") },
        keyboardOptions = KeyboardOptions.Default.copy(
            keyboardType = KeyboardType.Email,
            imeAction = ImeAction.Next
        ),
        singleLine = true,
        modifier = Modifier.fillMaxWidth(),
        isError = email.isEmpty()
    )
    Spacer(modifier = Modifier.height(16.dp))

    // Campo País
    CountryDropdown(country, onCountryChange)
    Spacer(modifier = Modifier.height(16.dp))

    // Campo Ciudad
    CityDropdown(city, onCityChange)
    Spacer(modifier = Modifier.height(16.dp))
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CountryDropdown(country: String, onCountryChange: (String) -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    val countries = listOf("Argentina", "Brasil", "Chile", "Colombia", "Ecuador", "México", "Perú", "Venezuela")

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded }
    ) {
        OutlinedTextField(
            value = country,
            onValueChange = { },
            label = { Text("País*") },
            readOnly = true,
            modifier = Modifier
                .menuAnchor()
                .fillMaxWidth()
                .clickable { expanded = !expanded },
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
            },
            isError = country.isEmpty()
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            countries.forEach { item ->
                DropdownMenuItem(
                    onClick = {
                        onCountryChange(item)
                        expanded = false
                    },
                    text = { Text(item) }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CityDropdown(city: String, onCityChange: (String) -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    val cities = listOf("Bogotá", "Medellín", "Cali", "Barranquilla", "Cartagena", "Bucaramanga", "Pereira")

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded }
    ) {
        OutlinedTextField(
            value = city,
            onValueChange = { },
            label = { Text("Ciudad") },
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
            cities.forEach { item ->
                DropdownMenuItem(
                    onClick = {
                        onCityChange(item)
                        expanded = false
                    },
                    text = { Text(item) }
                )
            }
        }
    }
}
