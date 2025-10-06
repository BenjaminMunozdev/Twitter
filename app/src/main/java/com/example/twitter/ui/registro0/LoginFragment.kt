package com.example.fragmento

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.twitter.Dash
import com.example.twitter.Prefs
import com.example.twitter.R

class LoginFragment : Fragment(R.layout.fragment_login) {

    private lateinit var edtCorreo: EditText
    private lateinit var edtPassword: EditText
    private lateinit var btnLogin: Button

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        edtCorreo = view.findViewById(R.id.etCorreoLogin)
        edtPassword = view.findViewById(R.id.etPasswordLogin)
        btnLogin = view.findViewById(R.id.btnLogin)

        val tvRegister = view.findViewById<TextView>(R.id.tvRegister)
        tvRegister.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainerView, RegistroFragment())
                .addToBackStack(null)
                .commit()
        }

        btnLogin.setOnClickListener {
            validarYLogin()
        }
    }

    private fun validarYLogin() {
        val correo = edtCorreo.text.toString().trim()
        val password = edtPassword.text.toString().trim()

        edtCorreo.error = null
        edtPassword.error = null

        val savedCorreo = Prefs.getCorreo(requireContext())
        val savedPassword = Prefs.getPassword(requireContext())

        when {
            !esCorreoValido(correo) -> {
                edtCorreo.error = "Correo inválido (gmail/outlook/hotmail)."
                edtCorreo.requestFocus()
            }
            !esPasswordValida(password) -> {
                edtPassword.error = "Contraseña no válida (mínimo 6 caracteres y un número)."
                edtPassword.requestFocus()
            }
            correo == savedCorreo && password == savedPassword -> {
                Toast.makeText(requireContext(), "Login exitoso ", Toast.LENGTH_SHORT).show()
                val intent = Intent(requireContext(), Dash::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
            }
            else -> {
                Toast.makeText(requireContext(), "Correo o contraseña incorrectos", Toast.LENGTH_SHORT).show()
            }
        }
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
