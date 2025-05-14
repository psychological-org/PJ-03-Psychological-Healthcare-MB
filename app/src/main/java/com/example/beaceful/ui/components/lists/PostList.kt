package com.example.beaceful.ui.components.lists

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import com.example.beaceful.domain.model.Post
import com.example.beaceful.ui.components.cards.PostCard
import com.example.beaceful.ui.navigation.PostDetails

@Composable
fun PostList(
    posts: List<Post>,
    navController: NavHostController
) {
    LazyColumn {
        items(posts) { post ->
            PostCard(
                post = post,
                isLiked = false,
                onPostClick = {
                    navController.navigate(PostDetails.createRoute(post.id))
                },
                onToggleLike = {},
            )
        }
    }
}
