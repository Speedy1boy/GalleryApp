package com.practicum.galleryapp

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.practicum.galleryapp.model.Picture

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { GalleryApp() }
    }
}

@SuppressLint("MutableCollectionMutableState")
@OptIn(ExperimentalMaterial3Api::class, ExperimentalGlideComposeApi::class)
@Composable
fun GalleryApp() {
    var gallery by remember { mutableStateOf(generateSamplePictures().toMutableList()) }
    var searchText by remember { mutableStateOf(TextFieldValue("")) }
    var isGrid by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            Column(
                Modifier
                    .fillMaxWidth()
                    .padding(WindowInsets.statusBars.asPaddingValues())
                    .padding(8.dp)
            ) {
                TextField(
                    value = searchText,
                    onValueChange = { searchText = it },
                    label = { Text("Поиск по автору") },
                    modifier = Modifier.fillMaxWidth()
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    IconButton(onClick = { isGrid = !isGrid }) {
                        Icon(
                            if (isGrid) {
                                Icons.AutoMirrored.Filled.List
                            } else Icons.Default.GridView,
                            contentDescription = null
                        )
                    }
                    IconButton(onClick = { gallery = mutableListOf()}) {
                        Icon(Icons.Default.Clear, contentDescription = "Очистить всё")
                    }
                }
            }
        },
        floatingActionButton = {
            FloatingActionButton(onClick = {
                val newPicture = Picture(
                    id = (0..1000).random(),
                    author = "Новый автор ${(0..100).random()}",
                    url = "https://avatarzo.ru/wp-content/uploads/podsolnuh.jpg"
                )
                val exists = gallery.any { it.url == newPicture.url || it.id == newPicture.id }
                if (!exists) {
                    gallery = gallery.toMutableList().apply { add(0, newPicture) }
                }

            }) {
                Icon(Icons.Default.Add, contentDescription = "Добавить")
            }
        }
    ) { padding ->
        val filtered = gallery.filter {
            it.author.contains(searchText.text, ignoreCase = true)
        }

        if (isGrid) {
            LazyVerticalGrid(
                columns = GridCells.Adaptive(150.dp),
                contentPadding = padding
            ) {
                items(filtered.size) { i ->
                    PictureItem(filtered[i]) { picture ->
                        gallery = gallery.toMutableList().apply { remove(picture) }
                    }
                }
            }
        } else {
            LazyColumn(contentPadding = padding) {
                items(filtered.size) { i ->
                    PictureItem(filtered[i]) { picture ->
                        gallery = gallery.toMutableList().apply { remove(picture) }
                    }
                }
            }
        }
    }
}

@ExperimentalGlideComposeApi
@Composable
fun PictureItem(picture: Picture, onDelete: (Picture) -> Unit) {
    Card(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth()
            .clickable { onDelete(picture) },
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            GlideImage(
                model = picture.url,
                contentDescription = picture.author,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
            )
            Text(picture.author, Modifier.padding(8.dp))
        }
    }
}

fun generateSamplePictures(): List<Picture> = List(5) {
    Picture(
        id = it,
        author = "Автор $it",
        url = "https://avatarzo.ru/wp-content/uploads/sinij-mak.jpg"
    )
}
