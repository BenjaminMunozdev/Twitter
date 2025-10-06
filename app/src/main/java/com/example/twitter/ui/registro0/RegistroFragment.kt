package com.example.fragmento

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.twitter.Prefs
import com.example.twitter.R

class RegistroFragment : Fragment(R.layout.fragment_registro) {

    private lateinit var edtNombre: EditText
    private lateinit var edtCorreo: EditText
    private lateinit var edtPassword: EditText
    private lateinit var btnRegistro: Button

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val tvLoginLink = view.findViewById<TextView>(R.id.tvLoginLink)
        tvLoginLink.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        edtNombre = view.findViewById(R.id.etNombre)
        edtCorreo = view.findViewById(R.id.etCorreo)
        edtPassword = view.findViewById(R.id.etPassword)
        btnRegistro = view.findViewById(R.id.btnRegister)

        btnRegistro.setOnClickListener {
            validarYRegistrar()
        }
    }

    private fun validarYRegistrar() {
        val nombre = edtNombre.text.toString().trim()
        val correo = edtCorreo.text.toString().trim()
        val password = edtPassword.text.toString().trim()

        edtNombre.error = null
        edtCorreo.error = null
        edtPassword.error = null

        when {
            !esNombreValido(nombre) -> {
                edtNombre.error = "Solo letras y espacios (2 a 40 caracteres)."
                edtNombre.requestFocus()
            }
            !esCorreoValido(correo) -> {
                edtCorreo.error = "Correo válido (gmail, outlook o hotmail)."
                edtCorreo.requestFocus()
            }
            !esPasswordValida(password) -> {
                edtPassword.error = "Mínimo 6 caracteres y al menos un número."
                edtPassword.requestFocus()
            }
            else -> {
                Prefs.save(requireContext(), nombre, correo, password)
                Toast.makeText(requireContext(), "Registro exitoso 🎉", Toast.LENGTH_SHORT).show()
                parentFragmentManager.popBackStack()
            }
        }
    }

    private fun esNombreValido(nombre: String): Boolean {
        val regex = Regex("^[A-Za-zÁÉÍÓÚáéíóúÑñ ]{2,40}$")
        return regex.matches(nombre)
    }

    private fun esCorreoValido(correo: String): Boolean {
        val regex = Regex("^[A-Za-z0-9._%+-]+@(?:gmail|outlook|hotmail)\\.(?:com|cl|es)$", RegexOption.IGNORE_CASE)
        return regex.matches(correo)
    }

    private fun esPasswordValida(pass: String): Boolean {
        val regex = Regex("^(?=.*\\d).{6,}$")
        return regex.matches(pass)
    }
}
