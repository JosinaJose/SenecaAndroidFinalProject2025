package com.safemail.safemailapp.uiLayer.newsPage

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.rememberAsyncImagePainter
import com.safemail.safemailapp.components.NormalTextComponent
import com.safemail.safemailapp.dataModels.Article
import com.safemail.safemailapp.R

@Composable
fun NewsScreen(newsViewModel: NewsViewModel = viewModel()) {

    // Call API once when the screen is first displayed
    LaunchedEffect(Unit) {
        newsViewModel.getTopHeadlines()
    }

    val newsResponse by newsViewModel.newsResponse.collectAsState()
    val errorMessage by newsViewModel.errorMessage.collectAsState()

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
      NormalTextComponent(stringResource(R.string.news_page_heading))
        
        if (errorMessage != null) {
            Text(
                text = errorMessage ?: "",
                color = MaterialTheme.colorScheme.error
            )
        }

        newsResponse?.articles?.let { articles ->
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(articles) { article ->
                    NewsItem(article)
                }
            }
        }
    }
}

@Composable
fun NewsItem(article: Article) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(text = article.title ?: "", style = MaterialTheme.typography.titleMedium)
        Spacer(modifier = Modifier.height(4.dp))
        Text(text = article.description ?: "", style = MaterialTheme.typography.bodyMedium)
        Spacer(modifier = Modifier.height(4.dp))
        article.urlToImage?.let { url ->
            Image(
                painter = rememberAsyncImagePainter(url),
                contentDescription = article.title,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
    }
}
