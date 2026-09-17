package com.jetbrains.taskflow

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import java.util.UUID


data class HomeItem(
    val id: String = UUID.randomUUID().toString(),
    val title: String,
    val subtitle: String,
    val icon: ImageVector,
    val tintColor: Color
)

// MARK: - Sample Data

val sampleHomeItems = listOf(
    HomeItem(title = "Design Sprint", subtitle = "Wraps up Friday", icon = Icons.Default.Edit, tintColor = Color(0xFFEC4899)),
    HomeItem(title = "Team Standup", subtitle = "Daily at 9:30 AM", icon = Icons.Default.Person, tintColor = Color(0xFF3B82F6)),
    HomeItem(title = "Release Notes", subtitle = "v2.4 shipped today", icon = Icons.Default.Notifications, tintColor = Color(0xFFF97316)),
    HomeItem(title = "Analytics", subtitle = "Weekly report ready", icon = Icons.Default.Info, tintColor = Color(0xFF22C55E)),
    HomeItem(title = "Feedback", subtitle = "3 new responses", icon = Icons.Default.Email, tintColor = Color(0xFFA855F7))
)

private val categories = listOf("All", "Design", "Engineering", "Marketing", "Support")

@Composable
fun App() {
    HomeScreen()
}

@Composable
fun HomeScreen(
    items: List<HomeItem> = sampleHomeItems,
    userName: String = "Alex"
) {
    var searchText by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("All") }

    val filteredItems = remember(searchText, items) {
        if (searchText.isBlank()) items
        else items.filter { it.title.contains(searchText, ignoreCase = true) }
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(top = 12.dp, bottom = 32.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            HomeHeader(userName = userName, onProfileTap = { /* Handle profile tap */ })

            HomeSearchBar(
                text = searchText,
                onTextChange = { searchText = it }
            )

            CategoryChips(
                categories = categories,
                selected = selectedCategory,
                onSelect = { selectedCategory = it }
            )

            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "Today",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )

                filteredItems.forEach { item ->
                    HomeItemRow(
                        item = item,
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )
                }
            }
        }
    }
}

// MARK: - Header

@Composable
private fun HomeHeader(userName: String, onProfileTap: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(
                text = "Good morning",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = userName,
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold
            )
        }
        IconButton(onClick = onProfileTap) {
            Icon(
                imageVector = Icons.Default.AccountCircle,
                contentDescription = "Profile",
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(36.dp)
            )
        }
    }
}

// MARK: - Search Bar

@Composable
private fun HomeSearchBar(text: String, onTextChange: (String) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .padding(horizontal = 12.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Icon(
            imageVector = Icons.Default.Search,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )
        BasicTextField(
            value = text,
            onValueChange = onTextChange,
            modifier = Modifier.weight(1f),
            singleLine = true,
            textStyle = LocalTextStyle.current.copy(color = MaterialTheme.colorScheme.onSurface),
            decorationBox = { innerTextField ->
                Box {
                    if (text.isEmpty()) {
                        Text("Search", color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    innerTextField()
                }
            }
        )
        if (text.isNotEmpty()) {
            IconButton(onClick = { onTextChange("") }, modifier = Modifier.size(20.dp)) {
                Icon(
                    imageVector = Icons.Default.Cancel,
                    contentDescription = "Clear",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

// MARK: - Category Chips

@Composable
private fun CategoryChips(
    categories: List<String>,
    selected: String,
    onSelect: (String) -> Unit
) {
    LazyRow(
        contentPadding = PaddingValues(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        items(categories) { category ->
            CategoryChip(
                title = category,
                isSelected = category == selected,
                onClick = { onSelect(category) }
            )
        }
    }
}

@Composable
private fun CategoryChip(title: String, isSelected: Boolean, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .clip(CircleShape)
            .background(if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant)
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium,
            color = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface
        )
    }
}

// MARK: - Home Item Row

@Composable
private fun HomeItemRow(item: HomeItem, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .padding(14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(item.tintColor.copy(alpha = 0.15f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = item.icon,
                contentDescription = null,
                tint = item.tintColor,
                modifier = Modifier.size(20.dp)
            )
        }

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = item.title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = item.subtitle,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        Icon(
            imageVector = Icons.Default.ChevronRight,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.size(16.dp)
        )
    }
}