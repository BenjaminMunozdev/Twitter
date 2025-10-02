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
import com.example.twitter.R
import com.example.twitter.Prefs


class LoginFragment : Fragment(R.layout.fragment_login) {

    private lateinit var edtCorreo: EditText
    private lateinit var edtPassword: EditText
    private lateinit var btnLogin: Button

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        edtCorreo = view.findViewById(R.id.etCorreoLogin)
        edtPassword = view.findViewById(R.id.etPasswordLogin)
        btnLogin = view.findViewById(R.id.btnLogin)

        // TextView de registro
        val tvRegister = view.findViewById<TextView>(R.id.tvRegister)
        tvRegister.setOnClickListener {
            // Navegar al fragment de registro
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainerView, RegistroFragment())
                .addToBackStack(null)
                .commit()
        }

        // Acción de login
        btnLogin.setOnClickListener {
            btnLogin.setOnClickListener {
                val correo = edtCorreo.text.toString().trim()
                val password = edtPassword.text.toString().trim()

                val savedCorreo = Prefs.getCorreo(requireContext())
                val savedPassword = Prefs.getPassword(requireContext())

                if (correo.isEmpty() || password.isEmpty()) {
                    Toast.makeText(requireContext(), "Por favor completa todos los campos", Toast.LENGTH_SHORT).show()
                } else if (correo == savedCorreo && password == savedPassword) {
                    Toast.makeText(requireContext(), "Login exitoso", Toast.LENGTH_SHORT).show()

                    // Ir al Dash
                    val intent = Intent(requireContext(), Dash::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)


                } else {
                    Toast.makeText(requireContext(), "Correo o contraseña incorrectos ", Toast.LENGTH_SHORT).show()
                }
            }

        }
    }
}
