package com.example.beaceful.ui.screens.profile

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Event
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import coil3.compose.AsyncImage
import com.example.beaceful.R
import com.example.beaceful.domain.model.DumpDataProvider
import com.example.beaceful.domain.model.Post
import com.example.beaceful.domain.model.PostVisibility
import com.example.beaceful.ui.components.CustomInputField
import com.example.beaceful.ui.components.lists.PostList
import com.example.beaceful.ui.navigation.EditRoute
import com.example.beaceful.ui.screens.doctor.DoctorAboutSection
import java.time.LocalDateTime

@Composable
fun ProfileScreen(
    navController: NavHostController,
    userId: Int,
    modifier: Modifier = Modifier
) {
    val tabTitles =
        listOf(stringResource(R.string.do5_activity), stringResource(R.string.do6_about_me))
    var selectedTab by remember { mutableIntStateOf(0) }

    val user = remember {
        DumpDataProvider.listUser.find { it.id == userId }
    }
    val posts = remember {
        DumpDataProvider.posts.filter { it.posterId == userId }
    }
    var post by remember { mutableStateOf("") }

    val localPosts =
        remember { mutableStateListOf<Post>().apply { addAll(posts) } }

    if (user == null) {
        Text("Ng dung không tồn tại")
    } else {

        Column(
            modifier = modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
        ) {
            Box {
                Image(
                    painter = painterResource(id = R.drawable.profile_background),
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                )
                AsyncImage(
                    model = user.avatarUrl,
                    contentDescription = null,
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .offset(x = 24.dp, y = 50.dp)
                        .border(4.dp, MaterialTheme.colorScheme.primary, CircleShape)
                        .size(120.dp)
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop
                )

                // button
                Button(
                    onClick = {navController.navigate(EditRoute.route)},
                    shape = RoundedCornerShape(20.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary),
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .offset(x = (-24).dp, y = 50.dp)
                ) {
                    Icon(
                        Icons.Default.Edit,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        stringResource(R.string.do15_edit_profile),
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }

            Spacer(Modifier.height(60.dp))

            // Tên
            Text(
                text = user.fullName,
                style = MaterialTheme.typography.titleLarge.copy(color = MaterialTheme.colorScheme.primary),
                modifier = Modifier.padding(horizontal = 24.dp)
            )

            Text(
                text = user.headline ?: "",
                style = MaterialTheme.typography.bodySmall.copy(
                    color = MaterialTheme.colorScheme.primary.copy(
                        alpha = 0.7f
                    )
                ),
                modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp)
            )

            Spacer(Modifier.height(16.dp))

            // Tabs
            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = Color.Transparent,
                contentColor = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(horizontal = 8.dp)
            ) {
                tabTitles.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        text = { Text(title) },
                    )
                }
            }

            HorizontalDivider(color = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f))

            when (selectedTab) {
                0 ->                     CustomInputField(
                    placeholder = R.string.co7_your_thought,
                    inputText = post,
                    onTextChange = { post = it },
                    onSent = {
                        localPosts.add(
                            Post(
                                id = localPosts.size + 1,
                                content = post,
                                posterId = 1,
                                communityId = null,
                                visibility = PostVisibility.PUBLIC,
                                imageUrl = "",
                                reactCount = 0,
                                createdAt = LocalDateTime.now()
                            ),
                        )
                        post = ""
                    },
                    modifier = Modifier.padding(24.dp, 16.dp)
                )
            }

            when (selectedTab) {
                0 -> PostList(posts = posts, navController = navController)
                1 -> AboutSection(biography = user.biography)
            }
        }
    }
}


@Composable
fun AboutSection(biography: String?) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = biography ?: "Không có thông tin.",
            color = MaterialTheme.colorScheme.primary,
            style = MaterialTheme.typography.bodyMedium
        )
    }
}