package com.example.beaceful.ui.screens.doctor

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.outlined.StarOutline
import androidx.compose.material.icons.rounded.FlightTakeoff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.example.beaceful.R
import com.example.beaceful.domain.model.DumpDataProvider
import com.example.beaceful.domain.model.SearchItem
import com.example.beaceful.domain.model.User
import com.example.beaceful.ui.components.CustomSearchBar
import com.example.beaceful.ui.navigation.SingleDoctorProfile
import com.example.beaceful.viewmodel.DoctorViewModel

@Composable
fun DoctorScreen(
    navController: NavHostController,
    viewModel: DoctorViewModel = hiltViewModel(),
    modifier: Modifier = Modifier
) {
    val allDoctors = remember {
        viewModel.getAllDoctors()
    }

    var query by remember { mutableStateOf("") }
    val nameSuggestions = remember(allDoctors) {
        allDoctors.map { SearchItem(it.id, it.fullName) }
    }

    val filteredDoctors = remember(query, allDoctors) {
        if (query.isBlank()) allDoctors
        else allDoctors.filter { it.fullName.contains(query, ignoreCase = true) }
    }

    Column(modifier.fillMaxSize()) {

        CustomSearchBar(
            suggestions = nameSuggestions,
            placeholder = "Tìm bác sĩ...",
            onSearch = { selected ->
                query = selected.name
            },
            modifier = Modifier.fillMaxWidth()
        )
        UserListScreen(
            modifier = Modifier.fillMaxSize(),
            users = filteredDoctors,
            navController = navController
        )
    }
}


@Composable
fun UserListScreen(
    modifier: Modifier = Modifier,
    users: List<User>,
    navController: NavHostController
) {

    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        contentPadding = PaddingValues(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        modifier = modifier,
    ) {
        items(users) { doctor ->
            UserCard(doctor,
                onProfileClick = {
                    navController.navigate(SingleDoctorProfile.createRoute(doctor.id))
                })
        }
    }
}


@Composable
fun UserCard(
    profile: User,
    onProfileClick: () -> Unit = {},
    modifier: Modifier = Modifier,
) {


    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.background.copy(alpha = 0f))
            .padding(top = 24.dp, bottom = 12.dp, start = 8.dp, end = 8.dp),
        contentAlignment = Alignment.TopCenter
    ) {

        Card(
            shape = RoundedCornerShape(40.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 36.dp),
            colors = CardDefaults.cardColors().copy(
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
            )
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .padding(top = 48.dp, bottom = 12.dp)
                    .fillMaxWidth()
            ) {
                Text(
                    text = profile.fullName,
                    style = MaterialTheme.typography.titleMedium
                )

                Spacer(Modifier.height(8.dp))

                /* 3 chỉ số */
                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.padding(horizontal = 8.dp)
                ) {
                    StatItem(
                        icon = Icons.Rounded.FlightTakeoff,
                        label = "10 năm",
                    )
                    StatItem(
                        icon = Icons.Outlined.StarOutline,
                        label = "3.4/5",
                    )
                    StatItem(
                        icon = Icons.Filled.Person,
                        label = "234",
                    )
                }

                Spacer(Modifier.height(16.dp))

                Button(
                    onClick = onProfileClick,
                    shape = RoundedCornerShape(40.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.secondary,
                        contentColor = MaterialTheme.colorScheme.onSecondary,
                    ),
                    modifier = Modifier
                        .width(120.dp)
                        .height(36.dp)
                ) {
                    Text(
                        stringResource(R.string.do2_access),
                        style = MaterialTheme.typography.titleSmall
                    )
                }
            }
        }

        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(profile.avatarUrl)
                .crossfade(true)
                .build(),
            placeholder = painterResource(R.drawable.doctor_placeholder_avatar),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(100.dp)
                .offset(y = (-20).dp)
                .clip(CircleShape)
                .border(
                    width = 2.dp,
                    color = MaterialTheme.colorScheme.primary,
                    shape = CircleShape
                )
        )
    }
}

@Composable
fun StatItem(
    icon: ImageVector,
    label: String,
) {
    Card(
        colors = CardDefaults.cardColors().copy(
            contentColor = MaterialTheme.colorScheme.onTertiary,
            containerColor = MaterialTheme.colorScheme.tertiary,
        ),
        modifier = Modifier.size(45.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxSize()
        ) {
            Icon(imageVector = icon, contentDescription = "")
            Text(text = label, style = MaterialTheme.typography.bodySmall)
        }
    }
}


//@Preview(widthDp = 360, heightDp = 640, showBackground = true)
//@Composable
//fun DoctorScreenPreview() {
//    DoctorScreen()
//}

