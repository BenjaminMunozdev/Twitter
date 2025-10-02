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

        // TextView para volver al login
        val tvLoginLink = view.findViewById<TextView>(R.id.tvLoginLink)
        tvLoginLink.setOnClickListener {
            parentFragmentManager.popBackStack() // Vuelve al Login
        }

        edtNombre = view.findViewById(R.id.etNombre)
        edtCorreo = view.findViewById(R.id.etCorreo)
        edtPassword = view.findViewById(R.id.etPassword)
        btnRegistro = view.findViewById(R.id.btnRegister)

        btnRegistro.setOnClickListener {
            val nombre = edtNombre.text.toString().trim()
            val correo = edtCorreo.text.toString().trim()
            val password = edtPassword.text.toString().trim()

            if (nombre.isEmpty() || correo.isEmpty() || password.isEmpty()) {
                Toast.makeText(requireContext(), "Por favor completa todos los campos", Toast.LENGTH_SHORT).show()
            } else {
                // Guardar en SharedPreferences
                Prefs.save(requireContext(), nombre, correo, password)

                Toast.makeText(requireContext(), "Registro exitoso 🎉", Toast.LENGTH_SHORT).show()

                // Volver al Login automáticamente
                parentFragmentManager.popBackStack()
            }
        }
    }
}

