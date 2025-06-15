package com.example.beaceful.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import com.example.beaceful.domain.model.SearchItem

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun <T> CustomSearchBar(
    suggestions: List<SearchItem<T>>?,
    modifier: Modifier = Modifier,
    placeholder: String = "Tìm kiếm...",
    onSearch: (SearchItem<T>) -> Unit = {},
) {
    var query by remember { mutableStateOf("") }
    var active by remember { mutableStateOf(false) }

    val filtered = remember(query, suggestions) {
        if (query.isBlank()) suggestions?.take(10) else
            suggestions?.filter { it.name.contains(query, ignoreCase = true) }?.take(10)
    }

    SearchBar(
        query = query,
        onQueryChange = { query = it },
        onSearch = {
            val selected = suggestions?.find { it.name.equals(query, ignoreCase = true) }
            if (selected != null) onSearch(selected)
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
            .heightIn(max = 300.dp),
    ) {
        if (filtered.isNullOrEmpty()) {
            Text(
                "Không tìm thấy kết quả",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(16.dp)
            )
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 200.dp) // Giới hạn chiều cao danh sách gợi ý
            ) {
                items(filtered) { item ->
                    ListItem(
                        headlineContent = { Text(item.name) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                query = item.name
                                onSearch(item)
                                active = false
                            }
                            .padding(8.dp),
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
