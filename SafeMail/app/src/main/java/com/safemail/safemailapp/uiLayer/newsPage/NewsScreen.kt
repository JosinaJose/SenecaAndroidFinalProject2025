package com.safemail.safemailapp.uiLayer.newsPage

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.launch
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavHostController
import com.safemail.safemailapp.R

@Composable
fun NewsScreen(
    newsViewModel: NewsViewModel,
    navController: NavHostController, // Changed to NavHostController
    onNavigateToReadLater: () -> Unit
) {
    var currentPage by rememberSaveable { mutableIntStateOf(1) }
    var searchQuery by rememberSaveable { mutableStateOf("") }
    val selectedCategory by newsViewModel.currentCategory.collectAsState()

    val newsResponse by newsViewModel.newsResponse.collectAsState()
    val readLaterArticles by newsViewModel.readLaterArticles.collectAsState(initial = emptyList())
    val readLaterUrls = readLaterArticles.map { it.url }.toSet()

    val scope = rememberCoroutineScope()
    val listState = rememberLazyListState()

    LaunchedEffect(currentPage, selectedCategory) {
        if (searchQuery.isEmpty()) {
            newsViewModel.getTopHeadlines(category = selectedCategory, page = currentPage)
        } else {
            newsViewModel.searchNews(searchQuery, page = currentPage)
        }
        if (newsResponse?.articles?.isNotEmpty() == true) {
            listState.scrollToItem(0)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()

    ) {

        NewsTopBar(
            readLaterCount = readLaterArticles.size,
            onBack = {
                // Navigate to home explicitly instead of using callback
                navController.navigate("home") {
                    popUpTo("home") { inclusive = false }
                }
            },
            onReadLater = onNavigateToReadLater
        )

        // Content area
        Box(
            modifier = Modifier
                .fillMaxSize()
        ) {
            newsResponse?.articles?.let { articles ->
                if (articles.isEmpty()) {
                    Text(
                        text = "No news found",
                        modifier = Modifier.align(Alignment.Center),
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                } else {
                    LazyColumn(
                        state = listState,
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        contentPadding = PaddingValues(
                            start = 6.dp,
                            end = 8.dp,
                            bottom = 8.dp
                        )
                    ) {
                        // Search section with proper padding
                        item {
                            SearchBarSection(
                                query = searchQuery,
                                onQueryChange = { searchQuery = it },
                                onSearch = {
                                    currentPage = 1
                                    newsViewModel.searchNews(searchQuery, page = 1)
                                }
                            )
                        }

                        // Category filter
                        item {
                            CategoryFilterRow(
                                selectedCategory = selectedCategory,
                                onCategorySelected = { category ->
                                    searchQuery = ""
                                    currentPage = 1
                                    newsViewModel.getTopHeadlines(category = category, page = 1)
                                }
                            )
                        }

                        // News articles with padding
                        itemsIndexed(articles) { _, article ->
                            Box(modifier = Modifier.padding(horizontal = 16.dp)) {
                                NewsItemCard(
                                    article = article,
                                    isReadLater = readLaterUrls.contains(article.url),
                                    onReadLaterClick = {
                                        scope.launch { newsViewModel.toggleReadLater(article) }
                                    }
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
fun SearchBarSection(query: String, onQueryChange: (String) -> Unit, onSearch: () -> Unit) {
    OutlinedTextField(
        value = query,
        onValueChange = onQueryChange,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .padding(top = 16.dp, bottom = 8.dp),
        placeholder = {
            Text(
                "Search keywords...",
                style = MaterialTheme.typography.bodyMedium
            )
        },
        leadingIcon = {
            Icon(
                Icons.Default.Search,
                contentDescription = "Search",
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        },
        shape = RoundedCornerShape(16.dp),
        singleLine = true,
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
        keyboardActions = KeyboardActions(onSearch = { onSearch() }),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = MaterialTheme.colorScheme.primary,
            unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
        )
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoryFilterRow(selectedCategory: String?, onCategorySelected: (String?) -> Unit) {
    val categories = listOf("Business", "Technology", "Sports", "Health", "Science", "Entertainment")
    LazyRow(
        modifier = Modifier.fillMaxWidth(),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(categories) { category ->
            val isSelected = selectedCategory == category.lowercase()
            FilterChip(
                selected = isSelected,
                onClick = { onCategorySelected(if (isSelected) null else category.lowercase()) },
                label = {
                    Text(
                        text = category,
                        style = MaterialTheme.typography.labelLarge
                    )
                },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                    selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        }
    }
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewsTopBar(
    readLaterCount: Int,
    onBack: () -> Unit,
    onReadLater: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 4.dp),
        tonalElevation = 2.dp,
        shadowElevation = 4.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 6.dp),
            verticalAlignment = Alignment.Top,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {

            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onBack) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, null)
                }
                Text(
                    text = stringResource(R.string.news_page_heading),
                    style = MaterialTheme.typography.titleLarge
                )
            }

            BadgedBox(
                badge = {
                    if (readLaterCount > 0) {
                        Badge { Text(readLaterCount.toString()) }
                    }
                }
            ) {
                IconButton(onClick = onReadLater) {
                    Icon(Icons.Default.BookmarkBorder, null)
                }
            }
        }
    }
}