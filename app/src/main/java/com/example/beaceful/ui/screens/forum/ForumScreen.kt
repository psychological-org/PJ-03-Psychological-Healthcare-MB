package com.example.beaceful.ui.screens.forum

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.HorizontalDivider
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.example.beaceful.R
import com.example.beaceful.core.util.UserSession
import com.example.beaceful.domain.model.Community
import com.example.beaceful.domain.model.SearchItem
import com.example.beaceful.domain.model.User
import com.example.beaceful.ui.components.CustomSearchBar
import com.example.beaceful.ui.components.cards.PostCard
import com.example.beaceful.ui.navigation.CommunityRoute
import com.example.beaceful.ui.navigation.PostDetails
import com.example.beaceful.ui.screens.chat.ChatScreen
import com.example.beaceful.ui.viewmodel.ForumViewModel
import kotlinx.coroutines.launch

@Composable
fun ForumScreen(
    navController: NavController,
    initialTab: Int = 0,
    viewModel: ForumViewModel = hiltViewModel(),
) {
    val userId = UserSession.getCurrentUserId()
    LaunchedEffect(userId) {
        viewModel.fetchUserCommunityIds(userId)
    }

    val tabTitles = listOf(stringResource(R.string.co1_news), stringResource(R.string.co2_chat))
    var selectedTab by remember { mutableIntStateOf(initialTab) }
    // Tabs
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
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
                    text = { Text(title) }
                )
            }
        }
        HorizontalDivider(color = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f))
        when (selectedTab) {
            0 -> NewsScreen(navController = navController, viewModel = viewModel, userId = userId)
            1 -> ChatScreen(
                onUserClick = { userId, fullName ->
                    navController.navigate("chatDetail/$userId/$fullName")
                }
            )
        }
    }
}

@Composable
fun NewsScreen(navController: NavController, viewModel: ForumViewModel, userId: String) {
    val communityIds by viewModel.userCommunityIds.collectAsState()
    val posts by viewModel.allPosts.collectAsState()
    val likedPosts by viewModel.likedPosts.collectAsState()
    val comments by viewModel.comments.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()
    val allCommunities by viewModel.allCommunities.collectAsState()
    val nameSuggestions = remember(allCommunities) {
        allCommunities.map { SearchItem<Int>(id = it.id, name = it.name) }
    }
    val coroutineScope = rememberCoroutineScope()
    val postAuthors = remember { mutableStateMapOf<String, User?>() }
    var commentText by remember { mutableStateOf("") }

    // Làm mới dữ liệu khi quay lại NewsScreen
    LaunchedEffect(navController.currentBackStackEntry) {
        viewModel.fetchPosts()
    }

    LaunchedEffect(posts) {
        posts.forEach { post ->
            coroutineScope.launch {
                postAuthors[post.posterId] = viewModel.getUserById(post.posterId)
            }
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        CustomSearchBar(
            suggestions = nameSuggestions,
            onSearch = { selected ->
                navController.navigate(CommunityRoute.createRoute(selected.id, userId))
            },
            modifier = Modifier.padding(horizontal = 16.dp)
        )

        Spacer(Modifier.height(16.dp))
        LazyColumn(
            modifier = Modifier.fillMaxSize()
        ) {
            item {
                Text(
                    stringResource(R.string.co3_communities),
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
                Spacer(Modifier.height(16.dp))
                LazyRow(
                    modifier = Modifier.fillMaxWidth(),
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(communityIds) { communityId ->
                        val community = viewModel.getCommunityById(communityId)
                        if (community != null) {
                            CommunityItem(
                                community = community,
                                onClick = {
                                    navController.navigate(CommunityRoute.createRoute(communityId, userId))
                                }
                            )
                        }
                    }
                }
                Spacer(Modifier.height(16.dp))
            }

            item {
                Text(
                    stringResource(R.string.co4_recent_activity),
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
                Spacer(Modifier.height(16.dp))
            }

            items(posts) { post ->
                val user = postAuthors[post.posterId] ?: return@items
                var commentCount by remember { mutableStateOf(0) }

                LaunchedEffect(post.id) {
                    commentCount = viewModel.getCommentCountForPost(post.id)
                }

                PostCard(
                    post = post,
                    user = user,
                    commentCount = commentCount,
                    isLiked = likedPosts[post.id] ?: false,
                    onPostClick = { navController.navigate(PostDetails.createRoute(post.id)) },
                    onToggleLike = { viewModel.toggleLike(post.id, userId) },
                    onDeletePost = {
                        if (post.posterId == userId) {
                            viewModel.deletePost(post.id, userId)
                        } else {
                            viewModel.hidePost(post.id)
                        }
                    },
                    onEditPost = { content, visibility ->
                        viewModel.updatePost(post.id, userId, content, visibility)
                    },
                    community = if (post.communityId != null) viewModel.getCommunityById(post.communityId) else null,
                    comments = comments.filter { it.postId == post.id },
                    onLoadComments = { viewModel.loadCommentsForPost(post.id) },
                    userId = userId
                )
            }

            item {
                error?.let {
                    Spacer(Modifier.height(8.dp))
                    Text(
                        text = it,
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun CommunityItem(
    community: Community,
    onClick: () -> Unit
) {
    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(community.imageUrl)
                .crossfade(true)
                .build(),
            placeholder = painterResource(R.drawable.doctor_placeholder_avatar),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(80.dp)
                .clip(CircleShape)
                .border(
                    width = 2.dp,
                    color = MaterialTheme.colorScheme.primary,
                    shape = CircleShape
                )
                .clickable(onClick = onClick)
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = community.name,
            textAlign = TextAlign.Center,
            modifier = Modifier.width(80.dp),
            overflow = TextOverflow.Ellipsis,
            style = MaterialTheme.typography.bodyMedium
        )
    }
}
