package com.sylvenos.tools.ui.applock

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sylvenos.tools.R
import com.sylvenos.tools.ui.theme.SylvenSecureTheme

// Hide App Screen showing real installed apps directly matching snapshot UI
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HideApp(
    onBackClick: () -> Unit,
) {
    val context = LocalContext.current
    var searchQuery by remember { mutableStateOf("") }
    val realApps = remember { mutableStateListOf<InstalledAppItem>() }

    // Fetch real installed apps on startup
    LaunchedEffect(Unit) {
        val apps = getInstalledApps(context)
        realApps.clear()
        realApps.addAll(apps)
    }

    val hiddenStateMap = remember {
        mutableStateMapOf<String, Boolean>()
    }

    val filteredApps = remember(searchQuery, realApps.size) {
        if (searchQuery.isBlank()) {
            realApps
        } else {
            realApps.filter {
                it.appName.contains(searchQuery, ignoreCase = true) ||
                        it.packageName.contains(searchQuery, ignoreCase = true)
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Hide App",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface,
                ),
            )
        },
        containerColor = MaterialTheme.colorScheme.background,
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 20.dp, vertical = 12.dp),
        ) {
            // Search Input Bar
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { query -> searchQuery = query },
                placeholder = { Text("Search apps") },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search",
                    )
                },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp),
                shape = RoundedCornerShape(24.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFF8B5CF6),
                    unfocusedBorderColor = Color.Transparent,
                    focusedContainerColor = MaterialTheme.colorScheme.surface,
                    unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                ),
            )

            // Direct List of Real Apps without forced outer card
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
            ) {
                itemsIndexed(
                    items = filteredApps,
                    key = { _, app -> app.packageName },
                ) { index, app ->
                    val isHidden = hiddenStateMap[app.packageName] ?: false

                    HideAppRow(
                        appInfo = app,
                        isHidden = isHidden,
                    ) { checked ->
                        hiddenStateMap[app.packageName] = checked
                    }

                    if (index < filteredApps.lastIndex) {
                        HorizontalDivider(
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f),
                        )
                    }
                }
            }
        }
    }
}

// Individual Real App Hide Row
@Composable
fun HideAppRow(
    appInfo: InstalledAppItem,
    isHidden: Boolean,
    onToggleChange: (Boolean) -> Unit,
) {
    val bitmap = remember(appInfo.packageName) { appInfo.icon?.toImageBitmap() }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.weight(1f),
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(
                        if (isHidden) Color(0xFF8B5CF6).copy(alpha = 0.15f)
                        else MaterialTheme.colorScheme.surfaceVariant,
                    ),
                contentAlignment = Alignment.Center,
            ) {
                if (bitmap != null) {
                    Image(
                        bitmap = bitmap,
                        contentDescription = appInfo.appName,
                        modifier = Modifier.size(32.dp),
                    )
                } else {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_hide_app),
                        contentDescription = "Hide App Icon",
                        tint = if (isHidden) Color(0xFF8B5CF6) else MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(22.dp),
                    )
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            Text(
                text = appInfo.appName,
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface,
            )
        }

        Switch(
            checked = isHidden,
            onCheckedChange = onToggleChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.White,
                checkedTrackColor = Color(0xFF8B5CF6),
                uncheckedThumbColor = Color.White,
                uncheckedTrackColor = MaterialTheme.colorScheme.surfaceVariant,
            ),
        )
    }
}

@Preview(showBackground = true)
@Composable
fun HideAppPreview() {
    SylvenSecureTheme {
        HideApp {}
    }
}
