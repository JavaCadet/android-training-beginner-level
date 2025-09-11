package com.example.rickandmorty.ui.common

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.example.rickandmorty.R

@Composable
fun FailureScreen(
    message: String?,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = modifier.fillMaxSize()
    ) {
        Image(
            painter = painterResource(R.drawable.connection_error),
            contentDescription = null
        )
        Text(message ?: stringResource(R.string.loading_failed))
        Spacer(modifier = Modifier.height(dimensionResource(R.dimen.medium)))
        Button(onClick = onRetry) { Text("Retry") }
    }
}

@Preview(showSystemUi = true, showBackground = true)
@Composable
fun FailureScreenPreview() {
    FailureScreen(
        message = "No internet connection",
        onRetry = {}
    )
}