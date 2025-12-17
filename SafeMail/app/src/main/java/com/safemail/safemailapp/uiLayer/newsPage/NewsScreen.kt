package com.safemail.safemailapp.uiLayer.newsPage

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.safemail.safemailapp.R
import com.safemail.safemailapp.dataModels.Article
import kotlinx.coroutines.launch

@Composable
fun NewsScreen(
    newsViewModel: NewsViewModel = viewModel(),
    onNavigateBack: () -> Unit,
    onNavigateToReadLater: () -> Unit
) {
    var currentPage by remember { mutableIntStateOf(0) }
    val itemsPerPage = 20
    val scope = rememberCoroutineScope()

    // Load top headlines when screen launches
    LaunchedEffect(Unit) {
        newsViewModel.getTopHeadlines()
    }

    // Observe API response
    val newsResponse by newsViewModel.newsResponse.collectAsState()

    // Observe Read Later articles from ViewModel
    val readLaterArticles: List<Article> by newsViewModel.readLaterArticles
        .collectAsState(initial = emptyList())

    val readLaterUrls = readLaterArticles.map { it.url }.toSet()
    val readLaterCount = readLaterArticles.size

    Column(modifier = Modifier.fillMaxSize()) {

        /* ---------------- TOP BAR ---------------- */
        Surface(
            modifier = Modifier.fillMaxWidth(),
            tonalElevation = 3.dp
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 4.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                    Text(
                        text = stringResource(R.string.news_page_heading),
                        style = MaterialTheme.typography.titleLarge,
                        modifier = Modifier.padding(start = 8.dp)
                    )
                }

                BadgedBox(
                    badge = {
                        if (readLaterCount > 0) {
                            Badge { Text(readLaterCount.toString()) }
                        }
                    }
                ) {
                    IconButton(onClick = onNavigateToReadLater) {
                        Icon(
                            imageVector = Icons.Filled.BookmarkBorder,
                            contentDescription = "Read Later"
                        )
                    }
                }
            }
        }

        /* ---------------- CONTENT ---------------- */
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            newsResponse?.articles?.let { articles ->
                val totalPages = (articles.size + itemsPerPage - 1) / itemsPerPage
                val startIndex = currentPage * itemsPerPage
                val endIndex = minOf(startIndex + itemsPerPage, articles.size)
                val paginatedArticles = articles.subList(startIndex, endIndex)

                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    itemsIndexed(paginatedArticles) { _, article ->
                        NewsItemCard(
                            article = article,
                            isReadLater = readLaterUrls.contains(article.url),
                            onReadLaterClick = {
                                scope.launch {
                                    newsViewModel.toggleReadLater(article)
                                }
                            }
                        )
                    }
                }

                if (totalPages > 1) {
                    PaginationControls(
                        currentPage = currentPage,
                        totalPages = totalPages,
                        onPreviousClick = { if (currentPage > 0) currentPage-- },
                        onNextClick = { if (currentPage < totalPages - 1) currentPage++ }
                    )
                }
            }
        }
    }
}
