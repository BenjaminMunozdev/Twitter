package com.example.twitter

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.PopupMenu
import androidx.core.content.ContextCompat
import androidx.core.view.WindowCompat
import com.google.android.material.floatingactionbutton.FloatingActionButton

class Dash : AppCompatActivity() {

    private val allPosts = mutableListOf(
        Post("Usuario", "Este es un post sobre desarrollo Android.", 24, 2, commentList = mutableListOf("¡Qué buen post!", "Gracias por compartir")),
        Post("Otro", "Otro post de ejemplo con Kotlin.", 10, 0),
        Post("Dev", "Me encanta programar en Android Studio.", 35, 1, commentList = mutableListOf("A mí también")),
        Post("Tester", "Probando la nueva funcionalidad de búsqueda en Kotlin.", 1, 0)
    )

    // Vistas de la UI
    private lateinit var postsContainer: LinearLayout
    private lateinit var createPostLayout: LinearLayout
    private lateinit var edtNewPost: EditText
    private lateinit var btnAddPost: Button
    private lateinit var fabAdd: FloatingActionButton
    private lateinit var bottomBar: LinearLayout
    private lateinit var searchLayout: LinearLayout
    private lateinit var edtSearch: EditText
    private lateinit var btnCloseSearch: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        setContentView(R.layout.activity_dash)

        // Inicialización de vistas
        postsContainer = findViewById(R.id.postsContainer)
        createPostLayout = findViewById(R.id.createPostLayout)
        edtNewPost = findViewById(R.id.edtNewPost)
        btnAddPost = findViewById(R.id.btnAddPost)
        fabAdd = findViewById(R.id.fabAdd)
        bottomBar = findViewById(R.id.bottomBar)
        searchLayout = findViewById(R.id.searchLayout)
        edtSearch = findViewById(R.id.edtSearch)
        btnCloseSearch = findViewById(R.id.btnCloseSearch)
        val btnMenu: ImageView = findViewById(R.id.btnMenu)
        val btnHome: ImageView = findViewById(R.id.btnHome)
        val btnSearch: ImageView = findViewById(R.id.btnSearch)

        // Listeners
        fabAdd.setOnClickListener { toggleCreatePostView(true) }
        btnAddPost.setOnClickListener {
            val content = edtNewPost.text.toString()
            if (content.isNotBlank()) {
                allPosts.add(0, Post("Tú", content, 0, 0, commentList = mutableListOf()))
                edtNewPost.text.clear()
                renderPosts() // Aquí está bien redibujar todo porque es una acción global
                toggleCreatePostView(false)
            } else {
                Toast.makeText(this, "Escribe algo para publicar", Toast.LENGTH_SHORT).show()
            }
        }
        findViewById<ScrollView>(R.id.scrollPosts).setOnClickListener {
            if (createPostLayout.visibility == View.VISIBLE) {
                toggleCreatePostView(false)
            }
        }
        btnSearch.setOnClickListener { toggleSearchView(true) }
        btnCloseSearch.setOnClickListener { toggleSearchView(false) }
        edtSearch.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                performSearch()
                true
            } else { false }
        }
        btnMenu.setOnClickListener { view -> showPopupMenu(view) }
        btnHome.setOnClickListener {
            if (searchLayout.visibility == View.VISIBLE) {
                toggleSearchView(false)
            } else {
                renderPosts()
                Toast.makeText(this, "Feed actualizado", Toast.LENGTH_SHORT).show()
            }
        }

        renderPosts()
    }

    private fun renderPosts(postsToRender: List<Post>? = null) {
        postsContainer.removeAllViews()
        val inflater = LayoutInflater.from(this)
        val posts = postsToRender ?: allPosts

        posts.forEach { post ->
            val postView = inflater.inflate(R.layout.item_post, postsContainer, false)

            // --- Vistas del post (usando los IDs de tu item_post.xml) ---
            val txtUsername = postView.findViewById<TextView>(R.id.txtUsername)
            val txtPostContent = postView.findViewById<TextView>(R.id.txtPostContent)
            val txtLikeCount = postView.findViewById<TextView>(R.id.txtLikeCount)
            val txtCommentCount = postView.findViewById<TextView>(R.id.txtCommentCount)
            val btnLike = postView.findViewById<ImageView>(R.id.btnLike)
            val btnComment = postView.findViewById<ImageView>(R.id.btnComment)
            val addCommentSection = postView.findViewById<LinearLayout>(R.id.addCommentSection)
            val commentsContainer = postView.findViewById<LinearLayout>(R.id.commentsContainer)
            val edtNewComment = postView.findViewById<EditText>(R.id.edtNewComment)
            val btnSendComment = postView.findViewById<Button>(R.id.btnSendComment)

            // --- Asignación de datos ---
            txtUsername.text = post.user
            txtPostContent.text = post.content
            txtLikeCount.text = post.likes.toString()
            txtCommentCount.text = post.comments.toString()
            updateLikeButtonColor(btnLike, post.liked)

            // --- Listener para el botón de Like (LÓGICA CORREGIDA) ---
            btnLike.setOnClickListener {
                post.liked = !post.liked
                post.likes += if (post.liked) 1 else -1
                // **LA CLAVE:** Actualizamos solo las vistas de este post, sin redibujar todo.
                txtLikeCount.text = post.likes.toString()
                updateLikeButtonColor(btnLike, post.liked)
            }

            // --- Listener para MOSTRAR/OCULTAR la sección de comentarios (LÓGICA CORREGIDA) ---
            val commentClickListener = View.OnClickListener {
                val isVisible = addCommentSection.visibility == View.VISIBLE
                val newVisibility = if (isVisible) View.GONE else View.VISIBLE
                addCommentSection.visibility = newVisibility
                commentsContainer.visibility = newVisibility

                if (newVisibility == View.VISIBLE) {
                    edtNewComment.requestFocus()
                    showKeyboard(edtNewComment)
                }
            }
            btnComment.setOnClickListener(commentClickListener)
            txtCommentCount.setOnClickListener(commentClickListener)

            // --- Listener para ENVIAR un nuevo comentario (LÓGICA CORREGIDA) ---
            btnSendComment.setOnClickListener {
                val commentText = edtNewComment.text.toString().trim()
                if (commentText.isNotEmpty()) {
                    post.commentList.add(0, commentText) // Añade a la lista de datos
                    post.comments = post.commentList.size

                    // **LA CLAVE:** Actualizamos las vistas de este post y añadimos el nuevo comentario visualmente.
                    txtCommentCount.text = post.comments.toString()
                    addCommentView(inflater, commentsContainer, "Tú", commentText, true) // Añade la nueva vista de comentario
                    edtNewComment.text.clear()
                    hideKeyboard(edtNewComment)
                }
            }

            // --- Renderizar los comentarios existentes ---
            commentsContainer.removeAllViews()
            post.commentList.forEach { commentText ->
                // Aquí podrías tener la lógica para saber quién escribió cada comentario
                addCommentView(inflater, commentsContainer, "Alguien", commentText, false)
            }

            postsContainer.addView(postView)
        }
    }

    // --- NUEVA FUNCIÓN DE AYUDA para añadir comentarios ---
    private fun addCommentView(inflater: LayoutInflater, container: LinearLayout, user: String, text: String, addToTop: Boolean) {
        val commentView = inflater.inflate(R.layout.item_comment, container, false)
        val txtCommentUsername = commentView.findViewById<TextView>(R.id.txtCommentUsername)
        val txtCommentContent = commentView.findViewById<TextView>(R.id.txtCommentContent)

        txtCommentUsername.text = user
        txtCommentContent.text = text

        if (addToTop) {
            container.addView(commentView, 0) // Añade al principio
        } else {
            container.addView(commentView) // Añade al final
        }
    }

    private fun updateLikeButtonColor(button: ImageView, isLiked: Boolean) {
        val colorRes = if (isLiked) android.R.color.holo_red_light else android.R.color.darker_gray
        button.setColorFilter(ContextCompat.getColor(this, colorRes))
    }

    // --- El resto de tus funciones (sin cambios) ---
    private fun toggleSearchView(show: Boolean) {
        if (show) {
            searchLayout.visibility = View.VISIBLE
            bottomBar.visibility = View.GONE
            fabAdd.hide()
            edtSearch.requestFocus()
            showKeyboard(edtSearch)
        } else {
            searchLayout.visibility = View.GONE
            bottomBar.visibility = View.VISIBLE
            fabAdd.show()
            edtSearch.text.clear()
            hideKeyboard(edtSearch)
            renderPosts()
        }
    }

    private fun performSearch() {
        val query = edtSearch.text.toString().trim()
        hideKeyboard(edtSearch)
        if (query.isEmpty()) {
            renderPosts()
        } else {
            val filteredPosts = allPosts.filter { post ->
                post.content.contains(query, ignoreCase = true) ||
                        post.user.contains(query, ignoreCase = true)
            }
            renderPosts(filteredPosts)
        }
    }

    private fun showKeyboard(view: View) {
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT)
    }

    private fun hideKeyboard(view: View) {
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }

    private fun toggleCreatePostView(show: Boolean) {
        if (show) {
            createPostLayout.visibility = View.VISIBLE
            fabAdd.hide()
            edtNewPost.requestFocus()
            showKeyboard(edtNewPost)
        } else {
            createPostLayout.visibility = View.GONE
            fabAdd.show()
            hideKeyboard(edtNewPost)
        }
    }

    private fun showPopupMenu(view: View) {
        val popup = PopupMenu(this, view)
        popup.menuInflater.inflate(R.menu.main_menu, popup.menu)
        popup.setOnMenuItemClickListener { item: MenuItem ->
            when (item.itemId) {
                R.id.menu_profile -> {
                    val intent = Intent(this, Perfil::class.java)
                    startActivity(intent)
                    true
                }
                R.id.menu_logout -> {
                    val intent = Intent(this, Login::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                    finish()
                    true
                }
                else -> false
            }
        }
        popup.show()
    }
}

// Clase de datos sin cambios
data class Post(
    val user: String,
    val content: String,
    var likes: Int,
    var comments: Int,
    var liked: Boolean = false,
    val commentList: MutableList<String> = mutableListOf()
)
