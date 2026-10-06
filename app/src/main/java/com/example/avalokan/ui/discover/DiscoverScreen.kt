package com.example.avalokan.ui.discover

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.avalokan.R
import com.example.avalokan.ui.theme.AccentMarigold
import com.example.avalokan.ui.theme.AvalokanTheme
import com.example.avalokan.ui.theme.BadgeShape
import com.example.avalokan.ui.theme.PrimaryLight
import com.example.avalokan.ui.theme.PrimaryTeal
import com.example.avalokan.ui.theme.Spacing
import com.example.avalokan.ui.theme.StandardCardShape

@Composable
fun DiscoverScreen() {
    var query by rememberSaveable { mutableStateOf("") }
    var selectedChip by rememberSaveable { mutableIntStateOf(0) }
    val filters = listOf("All", "Historical", "Cultural", "Nature")

    Column(
        modifier = Modifier.fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .statusBarsPadding()
            .verticalScroll(rememberScrollState())
            .padding(bottom = Spacing.large),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Spacer(Modifier.height(Spacing.small))
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = Spacing.sidePadding),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = stringResource(R.string.discover),
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.primary
            )
            IconButton(
                onClick = { /* TODO: filters */ },
                modifier = Modifier
                    .size(40.dp)
                    .background(
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        shape = CircleShape
                    )
            ) {
                Icon(
                    imageVector = Icons.Default.Tune,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
        SearchField(
            query = query, onQuery = { query = it },
            modifier = Modifier.padding(horizontal = Spacing.sidePadding)
        )
        Suggestions(
            filters = filters, selected = selectedChip, onSelect = { selectedChip = it }
        )
    }
}

@Composable
private fun SearchField(
    query: String,
    onQuery: (String) -> Unit,
    modifier: Modifier = Modifier
){
    OutlinedTextField(
        value = query,
        onValueChange = onQuery,
        modifier = modifier.fillMaxWidth()
            .height(48.dp),
        placeholder = {
            Text(
                text = stringResource(R.string.search),
                style = MaterialTheme.typography.bodyMedium
            )
        },
        leadingIcon = {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription =  null,
                modifier = Modifier.size(18.dp)
            )
        },
        singleLine = true,
        shape = StandardCardShape,
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = Color.Transparent, unfocusedBorderColor = Color.Transparent,
            focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
            unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    )
}

@Composable
private fun Suggestions(
    filters: List<String>,
    selected: Int,
    onSelect: (Int) -> Unit,
    modifier: Modifier = Modifier
){
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ){
        LazyRow(
            contentPadding = PaddingValues(horizontal = Spacing.sidePadding),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ){
            itemsIndexed(filters) { index, label ->
                val isSelected = index == selected
                FilterChip(
                    selected = isSelected,
                    onClick = { onSelect(index) },
                    label = { Text(text = label, style = MaterialTheme.typography.bodyMedium, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium) },
                    shape = BadgeShape,
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = PrimaryTeal, selectedLabelColor = Color.White,
                        containerColor = MaterialTheme.colorScheme.surfaceVariant, labelColor = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                )
            }
        }
        Column(
            modifier = Modifier.padding(horizontal = Spacing.sidePadding),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            SearchPlaceCard(
                title = "Kathmandu Durbar Square", meta = "Kathmandu • Historical Site", rating = "4.8",
                desc = "The heart of old Kathmandu city, once the residence of the Nepalese Royal Family and home to the living goddess, Kumari."
            )
            SearchPlaceCard(
                title = "Lumbini Garden", meta = "Lumbini • Spiritual Site", rating = "4.9",
                desc = "The sacred birthplace of Lord Buddha, a UNESCO World Heritage site offering profound peace and historical depth."
            )
        }
    }
}

@Composable
private fun SearchPlaceCard(
    title: String,
    meta: String,
    rating: String,
    desc: String
){
    Card(
        modifier = Modifier.fillMaxWidth(), shape = StandardCardShape,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column {
            Box(
                modifier = Modifier.fillMaxWidth().height(180.dp).background(PrimaryLight),
                contentAlignment = Alignment.TopEnd) {
                Surface(modifier = Modifier.padding(12.dp), shape = CircleShape, color = Color.White.copy(alpha = 0.9f)) {
                    Icon(Icons.Default.BookmarkBorder, null, tint = PrimaryTeal, modifier = Modifier.padding(6.dp).size(16.dp))
                }
            }
            Column(
                modifier = Modifier.padding(12.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = title, style = MaterialTheme.typography.titleMedium, maxLines = 1, overflow = TextOverflow.Ellipsis, modifier = Modifier.weight(1f))
                    Spacer(Modifier.width(8.dp))
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(2.dp)) {
                        Icon(Icons.Default.Star, null, tint = AccentMarigold, modifier = Modifier.size(14.dp))
                        Text(text = rating, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold, color = AccentMarigold)
                    }
                }
                Text(text = meta, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant, maxLines = 1, overflow = TextOverflow.Ellipsis)
                Text(text = desc, style = MaterialTheme.typography.bodyMedium, maxLines = 3, overflow = TextOverflow.Ellipsis)
            }
        }
    }
}

@Preview
@Composable
private fun DiscoverPreview(){
    AvalokanTheme(){
        DiscoverScreen()
    }
}