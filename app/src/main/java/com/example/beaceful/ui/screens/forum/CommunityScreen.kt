package com.example.beaceful.ui.screens.forum

import android.util.Log
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.example.beaceful.R
import com.example.beaceful.domain.model.DumpDataProvider
import com.example.beaceful.ui.components.CustomInputField
import com.example.beaceful.ui.components.cards.PostCard
import com.example.beaceful.ui.components.cards.UserList
import com.example.beaceful.ui.navigation.PostDetails
import com.example.beaceful.ui.navigation.SingleDoctorProfile
import com.example.beaceful.ui.viewmodel.ForumViewModel

@Composable
fun CommunityScreen(
    navController: NavHostController,
    communityId: Int,
    viewModel: ForumViewModel = hiltViewModel()
) {
    val community = viewModel.getCommunityById(communityId)
    val communityAdmin = viewModel.getAdminByCommunity(community)

    LaunchedEffect(communityId) {
        viewModel.initCommunityPosts(communityId)
    }
    val post = viewModel.postText.value
    val localPosts = viewModel.localPosts

    val tabTitles =
        listOf(stringResource(R.string.co5_activity), stringResource(R.string.co6_member))
    var selectedTab by remember { mutableIntStateOf(0) }

    val communityIds = viewModel.getUserCommunityIds(4)
    val isJoined: Boolean = communityId in communityIds
    Log.d("log", "communityId: $communityId, communityIds: $communityIds, communityId in communityIds: ${communityId in communityIds} $isJoined")

    if (community != null) {
        LazyColumn {
            item {
                Column(
                    modifier = Modifier.padding(24.dp, 6.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp),
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
                            .size(120.dp)
                            .clip(CircleShape)
                            .border(
                                width = 1.dp,
                                color = MaterialTheme.colorScheme.primary,
                                shape = CircleShape
                            )
                    )
                    Text(community.name, style = MaterialTheme.typography.titleLarge)
                    Text(community.content, textAlign = TextAlign.Center)
                }
            }

            item {
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
                    if (isJoined) {
                    CustomInputField(
                        placeholder = R.string.co7_your_thought,
                        inputText = post,
                        onTextChange = { viewModel.onPostTextChange(it) },
                        onSent = {
                            viewModel.submitPost(communityId)
                        },
                        modifier = Modifier.padding(24.dp, 16.dp)
                    )} else {
                        Row (Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {

                            Button(onClick = {viewModel.joinCommunity()}) {
                                Text("Yêu cầu tham gia")
                            }
                        }
                    }
                }
            }
            when (selectedTab) {
                0 -> items(localPosts) { post ->
                    val user = viewModel.repository.getUserById(post.posterId) ?: return@items
                    val commentCount = viewModel.repository.getCommentCountForPost(post.id)
                    val isLiked = viewModel.repository.isPostLiked(post.id)
                    PostCard(
                        post = post,
                        isLiked = isLiked,
                        onPostClick = {
                            navController.navigate(PostDetails.createRoute(post.id))
                        },
                        onToggleLike = {},
                        user = user,
                        commentCount = commentCount,
                        onDeletePost = {}
                    )
                }

                1 -> item {
                    if (communityAdmin != null) {
                        Text(
                            stringResource(R.string.co8_admin),
                            modifier = Modifier.padding(horizontal = 16.dp)
                        )
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp)
                                .clickable(onClick = {
                                    navController.navigate(
                                        SingleDoctorProfile.createRoute(communityAdmin.id)
                                    )
                                })
                        ) {
                            AsyncImage(
                                model = ImageRequest.Builder(LocalContext.current)
                                    .data(communityAdmin.avatarUrl)
                                    .crossfade(true)
                                    .build(),
                                placeholder = painterResource(R.drawable.doctor_placeholder_avatar),
                                contentDescription = null,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .size(80.dp)
                                    .clip(CircleShape)
                                    .border(
                                        width = 1.dp,
                                        color = MaterialTheme.colorScheme.primary,
                                        shape = CircleShape
                                    )

                            )
                            Spacer(Modifier.width(8.dp))
                            Text(communityAdmin.fullName)
                        }
                    }
                    Spacer(Modifier.height(16.dp))
                    Text(
                        stringResource(R.string.co6_member),
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )
                    UserList(
                        users = remember { DumpDataProvider.listUser },
                        navController = navController
                    )
                }
            }

        }
    }
}

