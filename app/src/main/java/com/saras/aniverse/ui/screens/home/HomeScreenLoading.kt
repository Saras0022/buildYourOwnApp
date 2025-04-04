package com.saras.aniverse.ui.screens.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Card
import androidx.compose.material3.ElevatedCard
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.saras.aniverse.ui.components.shimmerEffect

// Loading Screen Template for Home Screen
@Composable
fun HomeScreenLoading() {
    Column {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .padding(vertical = 16.dp)
                .fillMaxWidth()
        ) {
            ElevatedCard(
                content = { Box(Modifier.fillMaxSize().shimmerEffect()) },
                modifier = Modifier
                    .height(500.dp)
                    .width((300.dp))
                    .padding(vertical = 16.dp)
            )
        }

        Spacer(Modifier.height(8.dp))

        Box(
            modifier = Modifier
                .width(128.dp)
                .height(16.dp)
                .padding(horizontal = 8.dp)
                .shimmerEffect()
        )
        Row(
            modifier = Modifier.padding(start = 8.dp, top = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            for (i in 0..3) {
                Card( content = { Box(Modifier.fillMaxSize().shimmerEffect()) },
                    modifier = Modifier
                        .height(300.dp)
                        .width(150.dp)
                )
            }
        }
    }
}