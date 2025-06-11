package com.example.beaceful.ui.screens.profile

import android.util.Log
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import coil3.compose.AsyncImage
import com.example.beaceful.R
import com.example.beaceful.core.util.UserSession
import com.example.beaceful.domain.model.User
import com.example.beaceful.ui.components.CustomInputField
import com.example.beaceful.ui.components.cards.PostCard
import com.example.beaceful.ui.navigation.CustomerDetails
import com.example.beaceful.ui.navigation.EditRoute
import com.example.beaceful.ui.navigation.PostDetails
import com.example.beaceful.ui.screens.doctor.DoctorAboutSection
import com.example.beaceful.ui.viewmodel.ProfileViewModel
import com.example.beaceful.viewmodel.DoctorViewModel
import kotlinx.coroutines.launch

@Composable
fun ProfileScreen(
    navController: NavHostController,
    modifier: Modifier = Modifier,
    viewModel: ProfileViewModel = hiltViewModel(),
) {
    val userId = UserSession.getCurrentUserId()
    val tabTitles = listOf(stringResource(R.string.do5_activity), stringResource(R.string.do6_about_me))
    var selectedTab by remember { mutableIntStateOf(0) }
    val user by viewModel.user.collectAsState()
    val posts by viewModel.posts.collectAsState()
    val comments by viewModel.comments.collectAsState()
    val error by viewModel.error.collectAsState()
    val coroutineScope = rememberCoroutineScope()
    val postAuthors = remember { mutableStateMapOf<String, User?>() }
    var commentText by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        viewModel.fetchUserProfile()
    }

    LaunchedEffect(posts) {
        posts.forEach { post ->
            coroutineScope.launch {
                postAuthors[post.posterId] = viewModel.getUserById(post.posterId)
            }
        }
    }

    Log.d("PROFILE","Doctor=$user")

    if (user == null) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = androidx.compose.foundation.layout.Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (error != null) {
                Text(
                    text = error ?: "Lỗi không xác định",
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyMedium
                )
            } else {
                CircularProgressIndicator()
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Đang tải hồ sơ...",
                    color = MaterialTheme.colorScheme.primary,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    } else {

        LazyColumn(
            modifier = modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
        ) {
            item {
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
                        model = user!!.avatarUrl,
                        contentDescription = null,
                        modifier = Modifier
                            .align(Alignment.BottomStart)
                            .offset(x = 24.dp, y = 50.dp)
                            .border(4.dp, MaterialTheme.colorScheme.primary, CircleShape)
                            .size(120.dp)
                            .clip(CircleShape),
                        contentScale = ContentScale.Crop
                    )

//                    Button(
//                        onClick = { navController.navigate(CustomerDetails.createRoute(4, false)) },
//                        shape = RoundedCornerShape(20.dp),
//                        colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
//                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary),
//                        modifier = Modifier
//                            .align(Alignment.BottomEnd)
//                            .offset(x = (-24).dp, y = 50.dp)
//                    ) {
//                        Text(
//                            "Xem lịch hẹn",
//                            color = MaterialTheme.colorScheme.primary
//                        )
//                    }
                }
            }

            item {
                Spacer(Modifier.height(60.dp))

                // Tên
                Text(
                    text = user!!.fullName,
                    style = MaterialTheme.typography.titleLarge.copy(color = MaterialTheme.colorScheme.primary),
                    modifier = Modifier.padding(horizontal = 24.dp)
                )

                Text(
                    text = user!!.headline ?: "",
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = MaterialTheme.colorScheme.primary.copy(
                            alpha = 0.7f
                        )
                    ),
                    modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp)
                )

                Spacer(Modifier.height(8.dp))
            }
            item {
                Row (Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(space = 16.dp, alignment = Alignment.CenterHorizontally)) {
                    Button(
                        onClick = {navController.navigate(EditRoute.route)},
                        shape = RoundedCornerShape(20.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary),
                    ) {
//                        Icon(
//                            Icons.Default.Edit,
//                            contentDescription = null,
//                            tint = MaterialTheme.colorScheme.primary
//                        )
//                        Spacer(Modifier.width(8.dp))
                        Text(
                            stringResource(R.string.do15_edit_profile),
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                    Button(
                        onClick = { navController.navigate(CustomerDetails.createRoute(userId, false)) },
                        shape = RoundedCornerShape(20.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary),
                    ) {
                        Text(
                            "Xem lịch hẹn",
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }
            item {
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
            }

            when (selectedTab) {
                0 -> item {
                    CustomInputField(
                        placeholder = R.string.co7_your_thought,
                        inputText = "",
                        onTextChange = { },
                        onSent = {

                        },
                        modifier = Modifier.padding(24.dp, 16.dp)
                    )
                }
            }

            when (selectedTab) {
                0 -> items(posts) { post ->
                    val user = postAuthors[post.posterId] ?: return@items
                    var commentCount by remember { mutableStateOf(0) }
                    var isLiked by remember { mutableStateOf(false) }

                    LaunchedEffect(post.id) {
                        commentCount = viewModel.getCommentCount(post.id)
                        isLiked = viewModel.isPostLiked(post.id, userId)
                    }

                    PostCard(
                        post = post,
                        user = user,
                        commentCount = commentCount,
                        isLiked = isLiked,
                        onPostClick = { navController.navigate(PostDetails.createRoute(post.id)) },
                        onToggleLike = { viewModel.toggleLike(post.id, userId) },
                        onDeletePost = {},
                        userId = userId,
                        comments = comments.filter { it.postId == post.id },
                        commentText = commentText,
                        onCommentTextChange = { commentText = it },
                        onLoadComments = { viewModel.loadCommentsForPost(post.id) },
                        onSubmitComment = {
                            viewModel.createComment(post.id, userId, commentText)
                            commentText = ""
                        },
                        onEditPost = { content, visibility ->
//                            viewModel.updatePost(post.id, userId, content, visibility)
                        }
                    )
                }
                1 -> item {
                    AboutSection(biography = user!!.biography)
                }
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