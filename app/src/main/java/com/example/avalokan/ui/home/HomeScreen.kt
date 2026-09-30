package com.example.avalokan.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.avalokan.R
import com.example.avalokan.ui.theme.AvalokanTheme

@Composable
fun AvalokanHome(
    modifier: Modifier = Modifier,
){
    Scaffold(
        modifier = modifier,
        containerColor = MaterialTheme.colorScheme.background,
    ){ innerPadding ->
        HomeScreen(
            modifier = Modifier.fillMaxSize(),
            contentPadding = innerPadding
        )
    }
}

@Composable
private fun HomeScreen(
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(0.dp),
) {
    Column(
        modifier = modifier.padding(contentPadding)
    ) {
        TopIcons()
    }
}

@Composable
private fun TopIcons(
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column {
            Text(
                text = stringResource(R.string.namaste),
                fontSize = 12.sp,
                letterSpacing = 1.sp,
                color = Color.Gray
            )

            Text(
                text = stringResource(R.string.explore),
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF008577)
            )
        }

        IconButton(
            onClick = { /* TODO */ },
            modifier = Modifier
                .size(40.dp)
                .background(
                    color = Color(0xFFF3F7F6),
                    shape = CircleShape
                )
        ) {
            Icon(
                imageVector = Icons.Default.Notifications,
                contentDescription = stringResource(R.string.notifications),
                tint = Color(0xFF008577),
                modifier = Modifier.size(18.dp)
            )
        }
    }
}

@Preview
@Composable
private fun HomeScreenPreview(){
    AvalokanTheme() {
        AvalokanHome()
    }
}