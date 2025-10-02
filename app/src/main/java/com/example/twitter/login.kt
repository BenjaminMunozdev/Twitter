package com.example.twitter

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.fragmento.LoginFragment
import com.example.fragmento.RegistroFragment

class Login : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.login)

        // Mostrar FragmentLogin por defecto
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainerView, LoginFragment())
                .commit()
        }
    }

    fun mostrarRegistro() {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainerView, RegistroFragment())
            .addToBackStack(null)
            .commit()
    }

    fun mostrarLogin() {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainerView, LoginFragment())
            .addToBackStack(null)
            .commit()
    }
}
