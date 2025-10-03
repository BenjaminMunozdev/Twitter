package com.example.twitter

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
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
import com.google.android.material.floatingactionbutton.FloatingActionButton

class Dash : AppCompatActivity() {

    private val allPosts = mutableListOf(
        Post("Usuario", "Este es un post sobre desarrollo Android.", 24, 13),
        Post("Otro", "Otro post de ejemplo con Kotlin.", 10, 5),
        Post("Dev", "Me encanta programar en Android Studio.", 35, 8),
        Post("Tester", "Probando la nueva funcionalidad de búsqueda en Kotlin.", 1, 1)
    )

    // Vistas de la UI (sin cambios)
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
        setContentView(R.layout.activity_dash)

        // --- INICIALIZACIÓN DE VISTAS ---
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

        // --- CONFIGURACIÓN DE LISTENERS ---
        // (Sin cambios en esta sección)
        fabAdd.setOnClickListener { toggleCreatePostView(true) }
        btnAddPost.setOnClickListener {
            val content = edtNewPost.text.toString()
            if (content.isNotBlank()) {
                allPosts.add(0, Post("Tú", content, 0, 0))
                edtNewPost.text.clear()
                renderPosts()
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
            } else {
                false
            }
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

        // --- RENDERIZADO INICIAL ---
        renderPosts()
    }

    /**
     * Dibuja los posts en la UI.
     * @param postsToRender La lista de posts a mostrar. Si es nula, muestra todos los posts.
     */
    private fun renderPosts(postsToRender: List<Post>? = null) {
        postsContainer.removeAllViews()
        val posts = postsToRender ?: allPosts

        posts.forEach { post ->
            val postView = layoutInflater.inflate(R.layout.item_post, postsContainer, false)

            // --- Inicialización de vistas del post (igual que antes) ---
            val txtPostContent = postView.findViewById<TextView>(R.id.txtPostContent)
            val txtLikeCount = postView.findViewById<TextView>(R.id.txtLikeCount)
            val txtCommentCount = postView.findViewById<TextView>(R.id.txtCommentCount)
            val btnLike = postView.findViewById<ImageView>(R.id.btnLike)
            val btnComment = postView.findViewById<ImageView>(R.id.btnComment)
            // ===== NUEVA VISTA: El contenedor para la sección de comentarios =====
            val commentsSectionContainer = postView.findViewById<LinearLayout>(R.id.commentsSectionContainer)

            // --- Asignación de datos (igual que antes) ---
            txtPostContent.text = post.content
            txtLikeCount.text = post.likes.toString()
            txtCommentCount.text = post.comments.toString()
            updateLikeButtonColor(btnLike, post.liked)

            // --- Listeners de botones (igual que antes) ---
            btnLike.setOnClickListener {
                post.liked = !post.liked
                post.likes += if (post.liked) 1 else -1
                txtLikeCount.text = post.likes.toString()
                updateLikeButtonColor(btnLike, post.liked)
            }

            val commentClickListener = View.OnClickListener {
                showCommentDialog(post)
            }
            btnComment.setOnClickListener(commentClickListener)
            txtCommentCount.setOnClickListener(commentClickListener)

            // ===== NUEVA LÓGICA PARA RENDERIZAR COMENTARIOS =====
            // Si la lista de comentarios del post no está vacía...
            if (post.commentList.isNotEmpty()) {
                // Hacemos visible el contenedor de comentarios.
                commentsSectionContainer.visibility = View.VISIBLE
                commentsSectionContainer.removeAllViews() // Limpiamos por si acaso

                // Iteramos sobre cada texto de comentario en la lista del post.
                post.commentList.forEach { commentText ->
                    // Inflamos nuestro nuevo layout item_comment.xml.
                    val commentView = layoutInflater.inflate(R.layout.item_comment, commentsSectionContainer, false) as TextView
                    // Asignamos el texto del comentario.
                    commentView.text = commentText
                    // Añadimos la vista del comentario al contenedor.
                    commentsSectionContainer.addView(commentView)
                }
            } else {
                // Si no hay comentarios, nos aseguramos de que el contenedor esté oculto.
                commentsSectionContainer.visibility = View.GONE
            }

            // Añadimos la vista completa del post (con sus comentarios ya dentro) al contenedor principal.
            postsContainer.addView(postView)
        }
    }

    /**
     *  ===== NUEVA FUNCIÓN PARA MOSTRAR DIÁLOGO DE COMENTARIOS =====
     *  Muestra un cuadro de diálogo para que el usuario añada un comentario a un post específico.
     *  @param post El post que se está comentando.
     */
    private fun showCommentDialog(post: Post) {
        // Infla el layout del diálogo que creamos
        val dialogView = layoutInflater.inflate(R.layout.dialog_add_comment, null)
        val edtCommentInput = dialogView.findViewById<EditText>(R.id.edtCommentInput)
        val btnCancel = dialogView.findViewById<Button>(R.id.btnCancelComment)
        val btnPublish = dialogView.findViewById<Button>(R.id.btnPublishComment)

        // Crea el AlertDialog
        val dialog = AlertDialog.Builder(this)
            .setView(dialogView)
            .create()

        // Acción del botón de Cancelar
        btnCancel.setOnClickListener {
            dialog.dismiss() // Cierra el diálogo
        }

        // Acción del botón de Publicar
        btnPublish.setOnClickListener {
            val commentText = edtCommentInput.text.toString()
            if (commentText.isNotBlank()) {
                // Añade el comentario a la lista dentro del objeto Post
                post.commentList.add(commentText)
                // Actualiza el contador
                post.comments = post.commentList.size

                // Cierra el diálogo y vuelve a renderizar los posts para actualizar el contador en la UI
                dialog.dismiss()
                renderPosts()
                Toast.makeText(this, "Comentario publicado", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "El comentario no puede estar vacío", Toast.LENGTH_SHORT).show()
            }
        }

        dialog.show() // Muestra el diálogo
    }


    // --- El resto de tus funciones (toggleSearchView, performSearch, showPopupMenu, etc.) se mantienen sin cambios ---
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
                    Prefs.clear(this)
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

    private fun updateLikeButtonColor(button: ImageView, isLiked: Boolean) {
        if (isLiked) {
            button.setColorFilter(ContextCompat.getColor(this, R.color.black_like))
        } else {
            button.setColorFilter(ContextCompat.getColor(this, android.R.color.darker_gray))
        }
    }
}

// La clase de datos Post se mantiene igual
data class Post(
    val user: String,
    val content: String,
    var likes: Int,
    var comments: Int,
    var liked: Boolean = false,
    val commentList: MutableList<String> = mutableListOf() // Lista para guardar los comentarios
)

