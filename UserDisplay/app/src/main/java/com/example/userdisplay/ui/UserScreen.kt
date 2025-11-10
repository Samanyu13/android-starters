package com.example.userdisplay.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.userdisplay.data.UserData

@Composable
fun UserScreen(
    modifier: Modifier = Modifier,
    viewModel: UserViewModel = viewModel(),
) {
    val state by viewModel.uiState.collectAsState()

    Scaffold(
        bottomBar = {
            Button(
                onClick = viewModel::fetchRandomUser,
                modifier = modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                enabled = state != UserUiState.Loading
            ) {
                Text(
                    text = when (state) {
                        UserUiState.Loading -> "Fetching"
                        else -> "Get random User"
                    }
                )
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            when (state) {
                is UserUiState.Loading -> LoadingContent()
                is UserUiState.Success -> UserCard((state as UserUiState.Success).user)
                is UserUiState.Error -> ErrorInfo((state as UserUiState.Error).message)
                is UserUiState.Initial -> InitData()
            }
        }
    }
}

@Composable
fun LoadingContent() {
    CircularProgressIndicator(modifier = Modifier.size(100.dp))
    Text(text = "Loading content..!", modifier = Modifier.padding(30.dp))
}

@Composable
fun ErrorInfo(message: String) {
    Text("Error fetching user: $message", modifier = Modifier.padding(30.dp))
}

@Composable
fun UserCard(user: UserData) {
    Card(
        modifier = Modifier
            .fillMaxWidth(0.8f)
            .padding(16.dp)
    ) {
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth()
        ) {
            AsyncImage(
                model = user.picture.large,
                contentDescription = "User Profile",
                modifier = Modifier
                    .size(100.dp)
                    .clip(CircleShape)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text("${user.name.first} ${user.name.last}")
            Spacer(modifier = Modifier.height(16.dp))
            Text(user.email)
        }
    }
}

@Composable
fun InitData() {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Icon(
            Icons.Filled.AccountCircle,
            "Person",
            modifier = Modifier.size(60.dp)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text("Click the button to get a new user")
    }
}