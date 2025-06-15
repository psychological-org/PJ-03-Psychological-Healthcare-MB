package com.example.beaceful.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemColors
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.dp
import com.example.beaceful.R
import com.example.beaceful.ui.navigation.BeacefulRoutes
import java.util.Locale


@Composable
fun BottomNavRow(
    allScreens: List<BeacefulRoutes>,
    onTabSelected: (BeacefulRoutes) -> Unit,
    currentScreen: BeacefulRoutes
) {
    Column (Modifier.background(MaterialTheme.colorScheme.background)) {
        HorizontalDivider(
            modifier = Modifier.fillMaxWidth(),
            color = MaterialTheme.colorScheme.secondary,
            thickness = 1.dp
        )

        NavigationBar(
            modifier = Modifier
                .fillMaxWidth().padding(horizontal = 16.dp),
            containerColor = colorResource(R.color.purple_700).copy(alpha = 0f),
        ) {
            allScreens.forEach { screen ->
                NavigationBarItem(
                    icon = {
                        Icon(
                            imageVector = screen.icon,
                            contentDescription = null
                        )
                    },
                    selected = currentScreen == screen,
                    onClick = { onTabSelected(screen) },
                    label = {
                        Text(
                            screen.route.replaceFirstChar {
                                if (it.isLowerCase()) it.titlecase(Locale.getDefault())
                                else it.toString()
                            }
                        )
                    },
                    colors = NavigationBarItemColors(
                        selectedIconColor = colorResource(R.color.purple_700),
                        selectedTextColor = colorResource(R.color.purple_500),
                        unselectedIconColor = colorResource(R.color.purple_500),
                        unselectedTextColor = colorResource(R.color.purple_500),
                        selectedIndicatorColor = colorResource(R.color.purple_200),
                        disabledIconColor = Color.Gray,
                        disabledTextColor = Color.Gray,
                    ),
                )
            }
        }
    }
}
