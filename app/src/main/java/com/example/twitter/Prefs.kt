package com.example.twitter

import android.content.Context

object Prefs {
    private const val FILE = "app_prefs"
    private const val K_NOMBRE = "k_nombre"
    private const val K_CORREO = "k_correo"
    private const val K_PASSWORD = "k_password"

    fun save(context: Context, nombre: String, correo: String, password: String) {
        context.getSharedPreferences(FILE, Context.MODE_PRIVATE)
            .edit()
            .putString(K_NOMBRE, nombre)
            .putString(K_CORREO, correo)
            .putString(K_PASSWORD, password)
            .apply()
    }

    fun getNombre(context: Context) =
        context.getSharedPreferences(FILE, Context.MODE_PRIVATE).getString(K_NOMBRE, "") ?: ""

    fun getCorreo(context: Context) =
        context.getSharedPreferences(FILE, Context.MODE_PRIVATE).getString(K_CORREO, "") ?: ""

    fun getPassword(context: Context) =
        context.getSharedPreferences(FILE, Context.MODE_PRIVATE).getString(K_PASSWORD, "") ?: ""

    fun clear(context: Context) {
        context.getSharedPreferences(FILE, Context.MODE_PRIVATE).edit().clear().apply()
    }
}
