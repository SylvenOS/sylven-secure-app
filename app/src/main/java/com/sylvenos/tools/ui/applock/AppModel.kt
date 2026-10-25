package com.sylvenos.tools.ui.applock

// Data representation for installed applications
data class AppInfoItem(
    val packageName: String,
    val appName: String,
    val category: String,
)

// Sample installed applications list for display and testing
val SampleAppList = listOf(
    AppInfoItem("com.whatsapp", "WhatsApp", "Social & Messaging"),
    AppInfoItem("com.google.android.youtube", "YouTube", "Entertainment"),
    AppInfoItem("com.instagram.android", "Instagram", "Social Media"),
    AppInfoItem("com.google.android.apps.photos", "Photos", "Gallery"),
    AppInfoItem("com.facebook.katana", "Facebook", "Social Media"),
    AppInfoItem("com.snapchat.android", "Snapchat", "Messaging"),
    AppInfoItem("com.google.android.apps.messaging", "Messages", "Communication"),
    AppInfoItem("com.android.chrome", "Chrome", "Browser"),
    AppInfoItem("com.spotify.music", "Spotify", "Music"),
    AppInfoItem("com.telegram.messenger", "Telegram", "Messaging"),
)
