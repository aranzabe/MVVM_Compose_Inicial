package com.example.mvvm_inicial

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class UsuarioViewModel : ViewModel() {
    // Lista observable por Compose
    private val _usuarios = mutableStateListOf<Usuario>()
    val usuarios: List<Usuario> get() = _usuarios

    // Código de error
    private val _codigoError = MutableStateFlow<Int?>(null)
    val codigoError = _codigoError.asStateFlow()



    fun agregarUsuario(nombre: String, edad: Int, recuerdame:Boolean) {
        // Validación básica
        if (nombre.isBlank() || edad <= 0 || edad > 120) {
            _codigoError.value = 1   // Error de validación
            return
        }

        // Agregar usuario
        _usuarios.add(Usuario(nombre, edad, recuerdame))
        _codigoError.value = null   // Limpia el error
    }

    fun verificarListaVacia() {
        if (_usuarios.isEmpty()) {
            _codigoError.value = 2 //Código para lista vacía
        } else {
            _codigoError.value = null //Sin error.
        }
    }
}