package com.example.recyclerviewdoubleapi

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.recyclerviewdoubleapi.data.data.MatchCardData
import com.example.recyclerviewdoubleapi.data.data.MatchId
import com.example.recyclerviewdoubleapi.databinding.ActivityMainBinding
import com.example.recyclerviewdoubleapi.ui.CricketInfoAdapter
import com.example.recyclerviewdoubleapi.ui.CricketViewModel
import com.example.recyclerviewdoubleapi.ui.MatchListUiState
import com.example.recyclerviewdoubleapi.utils.Constants.USE_COMPOSE
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var viewModel: CricketViewModel
    private lateinit var adapter: CricketInfoAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Log.i("Samanyu", "onCreate")
        viewModel =
            ViewModelProvider(this, CricketViewModel.Factory())[CricketViewModel::class.java]

        if (USE_COMPOSE) {
            setContent {
                MaterialTheme {
                    MatchListScreen(viewModel)
                }
            }
        } else {
            binding = ActivityMainBinding.inflate(layoutInflater)
            setContentView(binding.root)
            setupAdapter()

            lifecycleScope.launch {
                repeatOnLifecycle(Lifecycle.State.STARTED) {
                    viewModel.uiState.collect { state ->

                        Log.i("Samanyu", "state - $state")
                        // set up visibility
                        binding.progressBar.visibility = View.GONE
                        binding.errorText.visibility = View.GONE
                        binding.recyclerView.visibility = View.GONE

                        when (state) {
                            MatchListUiState.Loading -> {
                                binding.progressBar.visibility = View.VISIBLE
                            }

                            is MatchListUiState.Success -> {
                                adapter.submitList(state.matchIds)
                                binding.recyclerView.visibility = View.VISIBLE

                            }

                            is MatchListUiState.Error -> {
                                binding.errorText.text = state.message
                                binding.errorText.visibility = View.VISIBLE

                                binding.errorText.setOnClickListener {
                                    viewModel.loadMatchIds()
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private fun setupAdapter() {
        adapter = CricketInfoAdapter(
            detailFetcher = { matchId ->
                viewModel.fetchMatchInfo(matchId)
            }
        )
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.adapter = adapter
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MatchListScreen(viewModel: CricketViewModel) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(topBar = { TopAppBar(title = { Text("Live Cricket Scores") }) }) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when (uiState) {
                is MatchListUiState.Loading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }

                is MatchListUiState.Success -> {
                    val matchIds = (uiState as MatchListUiState.Success).matchIds
                    MatchListContent(matchIds) { id ->
                        viewModel.fetchMatchInfo(id)
                    }
                }

                is MatchListUiState.Error -> {
                    val message = (uiState as MatchListUiState.Error).message
                    Column(
                        modifier = Modifier
                            .align(Alignment.Center)
                            .padding(16.dp)
                            .clickable { viewModel.loadMatchIds() },
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Error loading data:",
                            color = Color.Red,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = message,
                            color = Color.Red,
                            modifier = Modifier.padding(top = 8.dp)
                        )
                        Text(
                            text = "Tap to Retry",
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.padding(top = 16.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun MatchListContent(
    matchIds: List<MatchId>,
    matchFetcher: suspend (String) -> Result<MatchCardData>
) {
    LazyColumn(
        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.fillMaxSize()
    ) {
        items(matchIds, key = { it.id }) {
            MatchCard(it, matchFetcher)
        }
    }
}

@Composable
private fun MatchCard(matchId: MatchId, matchFetcher: suspend (String) -> Result<MatchCardData>) {
    var matchData by remember { mutableStateOf<MatchCardData?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf<String?>(null) }

    // Kind of mimics the coroutine job in the ViewHolder's bind()
    // Fetch to restart if matchId changes (unlikely here)
    LaunchedEffect(matchId) {
        isLoading = true
        error = null
        matchData = null

        val result = matchFetcher(matchId.id)
        result.fold(
            onSuccess = { data ->
                matchData = data
                error = null
                isLoading = false
            },
            onFailure = {
                matchData = null
                error = "Data failed to load :/"
                isLoading = false
            }
        )
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(enabled = error != null) {
                // Simple retry mechanism for error state
                if (error != null) {
                    // Re-launching the effect will trigger re-fetch
                    matchData = null
                    error = null
                    isLoading = true
                }
            }
    ) {
        // Content layout (similar to item_match_card.xml)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                if (isLoading) {
                    Text(
                        text = "Fetching details for ${matchId.id}...",
                        style = MaterialTheme.typography.bodyMedium
                    )
                } else if (error != null) {
                    Text(
                        text = "Failed to load details for ${matchId.id}",
                        color = Color.Red,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = "Tap to Retry",
                        color = MaterialTheme.colorScheme.primary,
                        fontSize = 12.sp
                    )
                } else if (matchData != null) {
                    Text(
                        text = matchData!!.homeTeam,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = matchData!!.awayTeam,
                        fontSize = 16.sp,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
            }

            // Score / Indicator on the right
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    strokeWidth = 2.dp
                )
            } else if (matchData != null) {
                Text(
                    text = matchData!!.score,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}