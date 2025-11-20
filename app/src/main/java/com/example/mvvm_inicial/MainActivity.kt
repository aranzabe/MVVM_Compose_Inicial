package com.example.mvvm_inicial

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.layout.ModifierLocalBeyondBoundsLayout
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.mvvm_inicial.ui.theme.MVVM_InicialTheme

class MainActivity : ComponentActivity() {
    // Instancia del ViewModel
    private val usuarioViewModel: UsuarioViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MVVM_InicialTheme {
                Column (modifier = Modifier.fillMaxSize()) {
                    Spacer(modifier = Modifier.height(30.dp))
                    Column(){
                        FormularioNombreEdad(usuarioViewModel)
                        /*{ nombre, edad, recuerdame ->
                            Log.d("Formulario", "Nombre: $nombre, Edad: $edad")
                            if (!viewModel.agregarUsuario(nombre, edad, recuerdame)){
                                Toast.makeText(this@MainActivity, "Error de validación", Toast.LENGTH_SHORT).show()
                            }
                        }*/
                        ListadoSimple(usuarioViewModel)
                        //ListadoUsuarios(viewModel)
                    }
                }
            }
        }
    }
}



@Composable
fun ListadoSimple(viewModel: UsuarioViewModel = UsuarioViewModel()){
    val usuarios = viewModel.usuarios //Observa la lista desde el ViewModel
    val codigoError by viewModel.codigoError.collectAsState()

    // Verificar lista vacía cuando cambia
    LaunchedEffect(usuarios) {
        viewModel.verificarListaVacia()
    }
    /*
    LaunchedEffect asegura que:
        Solo se ejecute cuando usuarios cambie, no cada recomposición.
        Se ejecute en una coroutine, evitando problemas con operaciones que actualizan estado de forma asíncrona.
     */

    Column(){
            viewModel.verificarListaVacia()  //Verificamos si la lista está vacía y arriba estamos observando su valor en codigoError.
            Text(
                text = "Usuarios Registrados",
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            if (codigoError == 2){
                Text(
                    text = "Lista vacía",
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall
                )
            }
            else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .border(1.dp, MaterialTheme.colorScheme.primary)
                        .padding(8.dp)
                ) {
                    items(usuarios) { usuario ->
                        Text(
                            text = "Nombre: ${usuario.nombre}, Edad: ${usuario.edad}, Rec: ${usuario.rec}",
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.padding(vertical = 4.dp)
                        )
                    }
                }
            }

    }
}

@Composable
fun ListadoUsuarios(viewModel: UsuarioViewModel = UsuarioViewModel()){
    val usuarios = viewModel.usuarios //Observa la lista desde el ViewModel
    val codigoError by viewModel.codigoError.collectAsState()
    var mostrar by remember { mutableStateOf(true) }

    Column(){
        Button(
            onClick = {
                mostrar = !mostrar
            },
            modifier = Modifier.fillMaxWidth(),
        ) {
            if (mostrar) {
                Text("Ocultar")
            } else {
                Text("Mostrar")
            }
        }
        if (mostrar){
            viewModel.verificarListaVacia()  //Verificamos si la lista está vacía y arriba estamos observando su valor en codigoError.
            Text(
                text = "Usuarios Registrados",
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            if (codigoError == 2){
                Text(
                    text = "Lista vacía",
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall
                )
            }
            else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .border(1.dp, MaterialTheme.colorScheme.primary)
                        .padding(8.dp)
                ) {
                    items(usuarios) { usuario ->
                        Text(
                            text = "Nombre: ${usuario.nombre}, Edad: ${usuario.edad}, Rec: ${usuario.rec}",
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.padding(vertical = 4.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun MiCheckBoxTexto(texto: String, vainicial:Boolean, habilitado:Boolean):Boolean {
    var estado by remember { mutableStateOf(vainicial) }
    var estadoEnable by remember { mutableStateOf(habilitado) }
    Row() {
        Checkbox(
            checked = estado,
            enabled = estadoEnable,
            onCheckedChange = { estado = !estado })
//        Spacer(modifier = Modifier.width(8.dp).height(2.dp))
        Text(texto, modifier = Modifier.padding(vertical = 12.dp))
    }
    return estado
}

@Composable
//fun FormularioNombreEdad(viewModel: UsuarioViewModel = UsuarioViewModel(), onSubmit: (String, Int, Boolean) -> Unit) {
fun FormularioNombreEdad(viewModel: UsuarioViewModel = UsuarioViewModel()){
    var nombre by remember { mutableStateOf("") }
    var edad by remember { mutableStateOf("") }
    var nombreError by remember { mutableStateOf(false) }
    var edadError by remember { mutableStateOf(false) }
    var recuerdame by remember { mutableStateOf(false) }
    val focusRequester = remember { FocusRequester() }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Formulario",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        //Campo para el nombre
        OutlinedTextField(
            value = nombre,
            onValueChange = {
                nombre = it
                nombreError = it.isBlank()
            },
            label = { Text("Nombre") },
            isError = nombreError,
            modifier = Modifier.fillMaxWidth().focusRequester(focusRequester),
            singleLine = true
        )
        if (nombreError) {
            Text(
                text = "El nombre no puede estar vacío",
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall
            )
        }

        //Campo para la edad
        OutlinedTextField(
            value = edad,
            onValueChange = {
                edad = it
                edadError = it.toIntOrNull() == null || it.toInt() <= 0
            },
            label = { Text("Edad") },
            isError = edadError,
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
        )
        if (edadError) {
            Text(
                text = "Ingresa una edad válida (número mayor a 0)",
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall
            )
        }

        recuerdame = MiCheckBoxTexto("Recuerdame",false, true)
        //Botón para enviar
        Button(
            onClick = {
                if (nombre.isNotBlank() && edad.toIntOrNull() != null && edad.toInt() > 0) {
                    //onSubmit(nombre, edad.toInt(), recuerdame)
                    viewModel.agregarUsuario(nombre, edad.toInt(), recuerdame)
                    nombre = ""
                    edad = ""
                    nombreError = false
                    edadError = false
                    focusRequester.requestFocus() //Devuelve el foco a la caja de texto nombre.
                } else {
                    nombreError = nombre.isBlank()
                    edadError = edad.toIntOrNull() == null || edad.toInt() <= 0
                }
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = nombre.isNotBlank() && edad.toIntOrNull() != null && edad.toInt() > 0
        ) {
            Text("Enviar")
        }
    }
}


