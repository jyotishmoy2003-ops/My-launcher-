package com.example.mylauncher

import android.app.WallpaperManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.ResolveInfo
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.core.graphics.drawable.toBitmap
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

data class AppInfo(val label: String, val packageName: String, val icon: Drawable)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { LauncherScreen() }
    }
}

@Composable
fun LauncherScreen() {
    val context = LocalContext.current
    var apps by remember { mutableStateOf<List<AppInfo>>(emptyList()) }
    var query by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        apps = withContext(Dispatchers.IO) { loadApps(context) }
    }

    val filtered = remember(apps, query) {
        if (query.isBlank()) apps else apps.filter { it.label.contains(query, ignoreCase = true) }
    }

    val darkTheme = isSystemInDarkTheme()
    val colorScheme = if (darkTheme) dynamicDarkColorScheme(LocalContext.current) else dynamicLightColorScheme(LocalContext.current)
    MaterialTheme(colorScheme = colorScheme) {
        Box(Modifier.fillMaxSize()) {
            WallpaperBackground()
            Column(Modifier.fillMaxSize().padding(12.dp)) {
            OutlinedTextField(
                value = query,
                onValueChange = { query = it },
                placeholder = { Text("Search apps") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(8.dp))
            LazyVerticalGrid(columns = GridCells.Fixed(4), modifier = Modifier.fillMaxSize()) {
                items(filtered, key = { it.packageName }) { app ->
                    AppTile(app, context)
                }
            }
        }
    }
}
}

@Composable
fun AppTile(app: AppInfo, context: Context) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .padding(8.dp)
            .clickable {
                context.startActivity(context.packageManager.getLaunchIntentForPackage(app.packageName))
            }
    ) {
        Image(bitmap = app.icon.toBitmap().asImageBitmap(), contentDescription = app.label, modifier = Modifier.size(48.dp))
        Text(app.label, maxLines = 1, overflow = TextOverflow.Ellipsis, textAlign = TextAlign.Center, style = MaterialTheme.typography.labelSmall)
    }
}

@Composable
fun WallpaperBackground() {
    val context = LocalContext.current
    val wp = remember { WallpaperManager.getInstance(context).drawable }
    if (wp != null) {
        Image(bitmap = wp.toBitmap().asImageBitmap(), contentDescription = null, modifier = Modifier.fillMaxSize())
    }
}

fun loadApps(context: Context): List<AppInfo> {
    val pm = context.packageManager
    val intent = Intent(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_LAUNCHER)
    val infos: List<ResolveInfo> = pm.queryIntentActivities(intent, PackageManager.MATCH_ALL)
    return infos.map { ri ->
        AppInfo(
            label = ri.loadLabel(pm).toString(),
            packageName = ri.activityInfo.packageName,
            icon = ri.loadIcon(pm)
        )
    }.sortedBy { it.label.lowercase() }
}
