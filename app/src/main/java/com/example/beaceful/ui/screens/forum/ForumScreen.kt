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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.example.beaceful.R
import com.example.beaceful.domain.model.Community
import com.example.beaceful.domain.model.SearchItem
import com.example.beaceful.ui.components.CustomSearchBar
import com.example.beaceful.ui.components.cards.PostCard
import com.example.beaceful.ui.navigation.CommunityRoute
import com.example.beaceful.ui.navigation.PostDetails
import com.example.beaceful.ui.screen.ChatScreen
import com.example.beaceful.ui.viewmodel.ForumViewModel

@Composable
fun ForumScreen(
    navController: NavController,
    viewModel: ForumViewModel = hiltViewModel(),
    userId: Int
) {
    val tabTitles =
        listOf(stringResource(R.string.co1_news), stringResource(R.string.co2_chat))
    var selectedTab by remember { mutableIntStateOf(0) }
    // Tabs
    Column {
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
            0 -> NewsScreen(navController = navController, viewModel = viewModel)
            1 -> ChatScreen(
                onUserClick = { userId, fullName ->
                    navController.navigate("chatDetail/$userId/$fullName")
                },
                onLogout = {
                    // TODO: Thêm logic đăng xuất (ví dụ: quay về màn hình login)
                }
            )
        }
    }
}

@Composable
fun NewsScreen(navController: NavController, viewModel: ForumViewModel) {
    val communityIds = viewModel.getUserCommunityIds(4)
    val communityPosts = viewModel.getUserCommunityPosts(4)
    val allCommunities = remember {
        viewModel.getAllCommunities()
    }
    val nameSuggestions = remember(allCommunities) {
        allCommunities.map { SearchItem(it.id, it.name) }
    }
    Column(modifier = Modifier.fillMaxSize()) {
        CustomSearchBar(
            suggestions = nameSuggestions,
            onSearch = { selected -> navController.navigate(CommunityRoute.createRoute(selected.id))},
            modifier = Modifier.padding(horizontal =  16.dp),
        )

        Spacer(Modifier.height(16.dp))
        LazyColumn(
            modifier = Modifier
                .fillMaxSize(),
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
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                ) {
                    items(communityIds) { communityId ->
                        val community: Community? = viewModel.getCommunityById(communityId)
                        if (community != null)
                            CommunityItem(
                                community = community,
                                onClick = {
                                    navController.navigate(
                                        CommunityRoute.createRoute(
                                            communityId
                                        )
                                    )
                                }
                            )
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

            items(communityPosts) { post ->
                val user = viewModel.repository.getUserById(post.posterId) ?: return@items
                val commentCount = viewModel.repository.getCommentCountForPost(post.id)
                val isLiked = viewModel.repository.isPostLiked(post.id)

                PostCard(
                    post = post,
                    user = user,
                    commentCount = commentCount,
                    isLiked = isLiked,
                    onPostClick = { navController.navigate(PostDetails.createRoute(post.id)) },
                    onToggleLike = { viewModel.repository.toggleLike(post.id) },
                    community = if (post.communityId != null) viewModel.getCommunityById(post.communityId) else null,
                    onDeletePost = { viewModel.hidePost(post.id) }
                )

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
        Spacer(Modifier.height(8.dp))
        Text(text = community.name)
    }
}
