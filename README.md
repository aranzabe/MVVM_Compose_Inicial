# Formulario y ViewModel en Jetpack Compose

## 1️⃣ **ViewModel**

- El **ViewModel** es la clase que mantiene y gestiona el estado de la UI.
- Ventajas:
    - Persiste datos aunque la UI se destruya (rotación de pantalla, etc.).
    - Separa la lógica de negocio de la UI (MVVM).

**Ejemplo básico:**

```kotlin
class UsuarioViewModel : ViewModel() {
    private val _usuarios = mutableStateListOf<Usuario>()
    val usuarios: List<Usuario> get() = _usuarios

    private val _codigoError = MutableStateFlow<Int?>(null)
    val codigoError = _codigoError.asStateFlow()

    fun agregarUsuario(nombre: String, edad: Int, rec: Boolean) {
        if (nombre.isBlank() || edad <= 0) {
            _codigoError.value = 1
            return
        }
        _usuarios.add(Usuario(nombre, edad, rec))
        _codigoError.value = null
    }

    fun verificarListaVacia() {
        _codigoError.value = if (_usuarios.isEmpty()) 2 else null
    }
}
```

**Puntos clave:**

- `_usuarios` es **mutable internamente** pero solo se expone como lista **inmutable**.
- `_codigoError` es un `StateFlow` para poder **observar errores desde Compose**.
- Todas las operaciones de negocio (validación, agregar usuario) se hacen **en el ViewModel**, nunca en el Composable.

---

## 2️⃣ **Formulario en Compose**

- Los **Composables** solo muestran la UI y reaccionan al estado.
- No deben contener lógica de negocio complicada.

**Ejemplo:**

```kotlin
@Composable
fun FormularioNombreEdad(viewModel: UsuarioViewModel) {

    var nombre by remember { mutableStateOf("") }
    var edad by remember { mutableStateOf("") }
    var rec by remember { mutableStateOf(false) }

    Column {
        OutlinedTextField(
            value = nombre,
            onValueChange = { nombre = it },
            label = { Text("Nombre") }
        )

        OutlinedTextField(
            value = edad,
            onValueChange = { edad = it },
            label = { Text("Edad") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
        )

        Row {
            Checkbox(checked = rec, onCheckedChange = { rec = it })
            Text("Recuérdame")
        }

        Button(
            onClick = {
                val edadInt = edad.toIntOrNull() ?: -1
                viewModel.agregarUsuario(nombre, edadInt, rec)

                // Limpiar formulario
                nombre = ""
                edad = ""
                rec = false
            },
            enabled = nombre.isNotBlank() && edad.toIntOrNull()?.let { it > 0 } == true
        ) {
            Text("Enviar")
        }
    }
}
```

**Puntos clave:**

- `remember { mutableStateOf(...) }` mantiene el **estado local del formulario**.
- `onClick` del botón llama a `viewModel.agregarUsuario(...)`.
- **La UI no hace validaciones complicadas**, solo pasa los datos al ViewModel.
- Se puede limpiar el formulario fácilmente **reseteando las variables locales**.

---

## 3️⃣ **Observando el estado en Compose**

- Para mostrar la lista o errores:

```kotlin
val usuarios = viewModel.usuarios
val codigoError by viewModel.codigoError.collectAsState()
```

- `collectAsState()` convierte el `StateFlow` en un estado Compose **reactivo**.
- La UI se **recompondrá automáticamente** cuando cambie `codigoError` o `usuarios`.

---

## 4️⃣ **Resumen**

- **ViewModel** → Mantiene datos y lógica de negocio (agregar usuario, validaciones).
- **Formulario Composable** → Solo UI + pasar datos al ViewModel.
- **Estado**:
    - `mutableStateOf` → estado local del formulario
    - `StateFlow` / `mutableStateListOf` → estado compartido entre Composables
- **Observación**: `collectAsState()` permite que Compose se actualice automáticamente cuando cambian los datos.