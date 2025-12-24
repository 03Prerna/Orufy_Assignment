package com.example.assignment_orufytechnologies

import android.content.Intent
import android.os.Bundle
import android.webkit.WebView
import android.webkit.WebViewClient
import android.net.Uri
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.unit.dp
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import coil.compose.AsyncImage
import com.example.assignment_orufytechnologies.ui.theme.Assignment_OrufyTechnologiesTheme
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Assignment_OrufyTechnologiesTheme {
                App()
            }
        }
    }
}

data class HistoryItem(
    val url: String,
    val time: Long
)

private val dateFormat: DateTimeFormatter = DateTimeFormatter.ofPattern("hh: mm a, dd MMM yy")
    .withZone(ZoneId.systemDefault())

@Composable
fun App() {
    val nav = rememberNavController()
    val historyList = remember { mutableStateListOf<HistoryItem>() }
    var urlText by rememberSaveable { mutableStateOf("https://webtonative.com") }
    NavHost(navController = nav, startDestination = "home") {
        composable("home") {
            HomeScreen(
                url = urlText,
                onUrlChange = { urlText = it },
                onOpen = {
                    val item = HistoryItem(url = urlText, time = System.currentTimeMillis())
                    historyList.add(0, item)
                    val encoded = Uri.encode(urlText)
                    nav.navigate("webview/$encoded")
                },
                onOpenHistory = { nav.navigate("history") }
            )
        }
        composable("history") {
            HistoryScreen(
                items = historyList,
                onClear = { historyList.clear() },
                onOpenItem = { url ->
                    urlText = url
                    val encoded = Uri.encode(url)
                    nav.navigate("webview/$encoded")
                },
                onBack = { nav.popBackStack() }
            )
        }
        composable(
            route = "webview/{url}",
            arguments = listOf(navArgument("url") { type = NavType.StringType })
        ) { backStackEntry ->
            val urlArg = backStackEntry.arguments?.getString("url") ?: ""
            val decoded = Uri.decode(urlArg)
            urlText = decoded
            WebViewScreen(
                url = urlText,
                onBack = { nav.popBackStack() },
                onClose = {
                    urlText = ""
                    nav.popBackStack(route = "home", inclusive = false)
                },
                onUrlChange = { urlText = it }
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewApp() {
    Assignment_OrufyTechnologiesTheme {
        App()
    }
}

@OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    url: String,
    onUrlChange: (String) -> Unit,
    onOpen: () -> Unit,
    onOpenHistory: () -> Unit
) {
    Scaffold(
        containerColor = Color.White,
        topBar = {
            TopAppBar(
                title = { Text("WebToNative") },
                actions = {
                    IconButton(onClick = onOpenHistory) { Icon(Icons.Default.Menu, contentDescription = "History") }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF2196F3),
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White,
                    actionIconContentColor = Color.White
                )
            )
        }
    ) { inner ->
        Column(
            modifier = Modifier
                .padding(inner)
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                AsyncImage(
                    model = "https://images.unsplash.com/photo-1501785888041-af3ef285b470?w=600",
                    contentDescription = null,
                    modifier = Modifier
                        .height(140.dp)
                        .fillMaxWidth(0.9f)
                )
                AsyncImage(
                    model = "https://images.unsplash.com/photo-1470770841072-f978cf4d019e?w=600",
                    contentDescription = null,
                    modifier = Modifier
                        .height(140.dp)
                        .fillMaxWidth(0.9f)
                )
            }
            Spacer(Modifier.height(16.dp))
            TextField(
                value = url,
                onValueChange = onUrlChange,
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("https://webtonative.com") }
            )
            Spacer(Modifier.height(24.dp))
            Button(onClick = onOpen) {
                Text("Open")
            }
        }
    }
}

@OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)
@Composable
fun HistoryScreen(
    items: List<HistoryItem>,
    onClear: () -> Unit,
    onOpenItem: (String) -> Unit,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    Scaffold(
        containerColor = Color.White,
        topBar = {
            TopAppBar(
                title = { Text("History") },
                navigationIcon = {
                    IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, contentDescription = "Back") }
                },
                actions = {
                    IconButton(onClick = onClear) { Icon(Icons.Default.Delete, contentDescription = "Clear") }
                    IconButton(onClick = {
                        val text = items.joinToString("\n\n") {
                            "${dateFormat.format(Instant.ofEpochMilli(it.time))}\n${it.url}"
                        }
                        val intent = Intent(Intent.ACTION_SEND)
                        intent.type = "text/plain"
                        intent.putExtra(Intent.EXTRA_TEXT, text)
                        context.startActivity(Intent.createChooser(intent, "Share"))
                    }) { Icon(Icons.Default.Share, contentDescription = "Share") }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF2196F3),
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White,
                    actionIconContentColor = Color.White
                )
            )
        }
    ) { inner ->
        LazyColumn(
            modifier = Modifier
                .padding(inner)
                .fillMaxSize()
                .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(items) { item ->
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(dateFormat.format(Instant.ofEpochMilli(item.time)))
                        Spacer(Modifier.height(6.dp))
                        Text(item.url)
                    }
                }
            }
        }
    }
}

@OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)
@Composable
fun WebViewScreen(
    url: String,
    onBack: () -> Unit,
    onClose: () -> Unit,
    onUrlChange: (String) -> Unit
) {
    Scaffold(
        containerColor = Color.White,
        topBar = {
            TopAppBar(
                navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, null) } },
                title = {
                    TextField(
                        value = url,
                        onValueChange = onUrlChange,
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text("https://webtonative.com") }
                    )
                },
                actions = { IconButton(onClick = onClose) { Icon(Icons.Default.Close, null) } },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF2196F3),
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White,
                    actionIconContentColor = Color.White
                )
            )
        }
    ) { inner ->
        Box(modifier = Modifier.padding(inner).fillMaxSize()) {
            AndroidView(
                modifier = Modifier.fillMaxSize(),
                factory = { context ->
                    WebView(context).apply {
                        settings.javaScriptEnabled = true
                        webViewClient = WebViewClient()
                        setBackgroundColor(android.graphics.Color.WHITE)
                        loadUrl(url.takeIf { it.isNotBlank() } ?: "about:blank")
                    }
                },
                update = { view ->
                    if (url.isNotBlank()) view.loadUrl(url)
                }
            )
        }
    }
}
