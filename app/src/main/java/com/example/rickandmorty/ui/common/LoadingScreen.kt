package com.example.rickandmorty.ui.common

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.tooling.preview.Preview
import com.example.rickandmorty.R

@Composable
fun LoadingScreen(
    modifier: Modifier = Modifier
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = modifier.fillMaxSize()
    ) {
        CircularProgressIndicator(
            modifier = Modifier.size(dimensionResource(R.dimen.small_image_size))
        )
        Spacer(modifier = Modifier.height(dimensionResource(R.dimen.medium)))
        Text(
            text = "Loading...",
            style = MaterialTheme.typography.bodyLarge
        )
    }
}

@Preview(showSystemUi = true, showBackground = true)
@Composable
fun LoadingScreenPreview() {
    LoadingScreen()
}
