package com.reddit.rickandmortyapp.display.composables

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import coil3.compose.AsyncImage
import com.reddit.rickandmortyapp.R
import com.reddit.rickandmortyapp.display.RedditAssessmentViewModel
import com.reddit.rickandmortyapp.display.UiState.Completed.Error
import com.reddit.rickandmortyapp.display.UiState.Completed.Success
import com.reddit.rickandmortyapp.display.UiState.Loading
import com.reddit.rickandmortyapp.display.theme.RedditAssessmentTheme
import com.reddit.rickandmortyapp.display.theme.Space_16dp
import com.reddit.rickandmortyapp.display.theme.Space_2dp
import com.reddit.rickandmortyapp.display.theme.Space_4dp
import com.reddit.rickandmortyapp.display.theme.Space_8dp
import com.reddit.rickandmortyapp.domain.entities.RickAndMortyCharacterEntity


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RedditAssessmentHomeScreen(
    viewModel: RedditAssessmentViewModel = hiltViewModel<RedditAssessmentViewModel>()
) {
    val paginationError = stringResource(
        R.string.pagination_error
    )
    RedditAssessmentTheme {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            snackbarHost = {
                SnackbarHost(
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentSize(Alignment.BottomCenter),
                    hostState = viewModel.snackbarState
                ) { data ->
                    Snackbar(
                        modifier = Modifier.padding(horizontal = Space_16dp, vertical = Space_8dp),
                        snackbarData = data
                    )
                }
            }
        ) { innerPadding ->
            Box(
               modifier = Modifier
                   .fillMaxSize()
                   .padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                val uiState = viewModel.pagingData.collectAsStateWithLifecycle()
                when (val state = uiState.value) {
                    Loading -> {
                        CircularProgressIndicator(
                            color = MaterialTheme.colorScheme.primary
                        )
                    }

                    is Success -> {
                        val scrollState: LazyGridState = rememberSaveable(saver = LazyGridState.Saver) {
                            LazyGridState()
                        }
                        var pullToRefreshState by remember { mutableStateOf(false) }
                        val lazyPagingItems = state.paging.collectAsLazyPagingItems()
                        when (lazyPagingItems.loadState.append) {
                            is LoadState.NotLoading -> {
                                pullToRefreshState = false
                                viewModel.cacheDisplayList(
                                    lazyPagingItems.itemSnapshotList.items
                                )
                            }

                            is LoadState.Error -> {
                                pullToRefreshState = false
                                viewModel.showSnackBar(
                                    message = paginationError,
                                    duration = SnackbarDuration.Long
                                )
                            }
                            else -> {
                                pullToRefreshState = false
                            }
                        }

                        var loadingMore by remember { mutableStateOf(false) }
                        LaunchedEffect(scrollState to lazyPagingItems) {
                            snapshotFlow {
                                if (scrollState.lastScrolledForward) {
                                    val lastVisibleIndex =
                                        scrollState.layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0
                                    val totalItems = scrollState.layoutInfo.totalItemsCount
                                    Pair(lastVisibleIndex, totalItems)
                                } else {
                                    Pair(-1, 0)
                                }
                            }.collect { (lastVisibleIndex, totalItems) ->
                                if (lastVisibleIndex in 0..totalItems &&
                                    totalItems - lastVisibleIndex <= viewModel.KEY_PREFETCH_DISTANCE
                                ) {
                                    if (!loadingMore) {
                                        loadingMore = true
                                        lazyPagingItems.refresh()
                                    }
                                } else {
                                    loadingMore = false
                                }
                            }
                        }

                        PullToRefreshBox(
                            modifier = Modifier,
                            isRefreshing = pullToRefreshState,
                            onRefresh = {
                                pullToRefreshState = true
                                viewModel.pullToRefresh()
                            }
                        ) {
                            val displayList = viewModel.displayList.collectAsStateWithLifecycle()
                            LazyVerticalGrid(
                                modifier = Modifier
                                    .fillMaxSize(),
                                state = scrollState,
                                columns = if (LocalConfiguration.current.orientation ==
                                    Configuration.ORIENTATION_LANDSCAPE
                                ) GridCells.Fixed(3)
                                else GridCells.Fixed(2)
                            ) {
                                itemsIndexed(
                                    items = displayList.value
                                ) { index, entity ->
                                    RickAndMortyCard(entity)
                                }
                            }
                        }
                    }

                    is Error -> {
                        Column(
                            modifier = Modifier
                                .wrapContentSize()
                                .padding(Space_16dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = stringResource(R.string.system_error),
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.error
                            )
                            Button(
                                modifier = Modifier
                                    .padding(top = Space_16dp),
                                onClick = { viewModel.pullToRefresh() }
                            ) {
                                Text(
                                    modifier = Modifier.padding(
                                        horizontal = Space_16dp,
                                        vertical = Space_8dp
                                    ),
                                    text = stringResource(R.string.retry)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun RickAndMortyCard(
    item: RickAndMortyCharacterEntity
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(Space_4dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.onSurface
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = Space_4dp
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(Space_4dp)
        ) {
            AsyncImage(
                model = item.image,
                contentDescription = stringResource(
                    R.string.character_name,
                    item.name
                ),
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(16 / 9f, matchHeightConstraintsFirst = true)
                    .background(color = Color.White)
            )
            Text(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = Space_4dp, vertical = Space_2dp),
                text = item.name,
                style = MaterialTheme.typography.titleSmall,
                maxLines = 2,
                minLines = 2,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}