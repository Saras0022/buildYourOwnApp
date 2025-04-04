package com.saras.aniverse.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ErrorOutline
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.unit.dp

// Composable for showing Error Image in case of error in loading image
@Composable
fun ImageErrorComp() {
    Box(modifier = Modifier.fillMaxSize().background(Color(0, 0, 0, 0x50)), contentAlignment = Alignment.Center) {
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxSize()
        ) {
            Icon(Icons.Outlined.ErrorOutline, "Error Loading Image", tint = Color.Red)
            Spacer(Modifier.height(4.dp))
            Text("Error loading image!", color = Color.Red, fontStyle = FontStyle.Italic)
        }
    }
}