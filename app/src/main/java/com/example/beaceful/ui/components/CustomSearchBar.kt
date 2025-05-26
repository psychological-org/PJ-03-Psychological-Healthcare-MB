package com.example.beaceful.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomSearchBar(
    suggestions: List<String>?,
    modifier: Modifier = Modifier,
    placeholder: String = "Tìm kiếm...",
    onSearch: (String) -> Unit = {}
) {
    var query by remember { mutableStateOf("") }
    var active by remember { mutableStateOf(false) }

    val filtered = remember(query, suggestions) {
        if (query.isBlank()) suggestions else
            suggestions?.filter { it.contains(query, ignoreCase = true) }
    }

    SearchBar(
        query = query,
        onQueryChange = { query = it },
        onSearch = {
            onSearch(query)
            active = false
        },
        active = active,
        onActiveChange = { active = it },
        placeholder = { Text(placeholder) },
        leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
        trailingIcon = {
            if (active) {
                Icon(
                    Icons.Default.Close, contentDescription = null,
                    modifier = Modifier.clickable {
                        query = ""
                    })
            } else {
                query = ""; active = false
            }
        },
        colors = SearchBarDefaults.colors(
            containerColor = MaterialTheme.colorScheme.secondary,
            inputFieldColors = TextFieldDefaults.colors(
                focusedTextColor = MaterialTheme.colorScheme.primary,
                unfocusedTextColor = MaterialTheme.colorScheme.primary,
                focusedPlaceholderColor = MaterialTheme.colorScheme.primary,
                focusedTrailingIconColor = MaterialTheme.colorScheme.primary,
                focusedLeadingIconColor = MaterialTheme.colorScheme.primary,
                focusedIndicatorColor = MaterialTheme.colorScheme.onPrimary,
                unfocusedPlaceholderColor = MaterialTheme.colorScheme.primary,
                unfocusedTrailingIconColor = MaterialTheme.colorScheme.primary,
                unfocusedLeadingIconColor = MaterialTheme.colorScheme.primary,
                unfocusedIndicatorColor = MaterialTheme.colorScheme.onPrimary

            )
        ),
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {
        if (filtered != null) {
            if (filtered.isEmpty()) {
                Text(
                    "Không tìm thấy kết quả",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(16.dp)
                )
            } else {
                filtered.forEach { item ->
                    ListItem(
                        headlineContent = { Text(item) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                query = item
                                onSearch(item)
                                active = false
                            },
                        colors = ListItemDefaults.colors(
                            containerColor = MaterialTheme.colorScheme.tertiary,
                            headlineColor = MaterialTheme.colorScheme.onTertiary
                        )
                    )
                }
            }
        }
    }
}
