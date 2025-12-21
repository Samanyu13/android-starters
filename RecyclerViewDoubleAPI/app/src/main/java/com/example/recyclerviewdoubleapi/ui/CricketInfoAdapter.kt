package com.example.recyclerviewdoubleapi.ui

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.recyclerviewdoubleapi.data.data.MatchCardData
import com.example.recyclerviewdoubleapi.data.data.MatchId
import com.example.recyclerviewdoubleapi.databinding.CardItemBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

class CricketInfoAdapter(private val detailFetcher: suspend (String) -> Result<MatchCardData>) :
    ListAdapter<MatchId, CricketInfoAdapter.MatchViewHolder>(MatchDiffCallback()) {
    override fun onCreateViewHolder(
        parent: ViewGroup, viewType: Int
    ): MatchViewHolder {
        val binding = CardItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MatchViewHolder(binding)
    }

    override fun onBindViewHolder(
        holder: MatchViewHolder, position: Int
    ) {
        holder.bind(getItem(position), detailFetcher)
    }

    class MatchViewHolder(
        private val binding: CardItemBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        private var fetchJob: Job? = null
        fun bind(item: MatchId, detailFetcher: suspend (String) -> Result<MatchCardData>) {
            fetchJob?.cancel()

            // temporary loading stuff here?

            fetchJob = CoroutineScope(Dispatchers.Main).launch {
                val result = detailFetcher(item.id)

                if (isActive) {
                    result.fold(
                        onSuccess = { match ->
                            binding.homeTeamText.text = match.homeTeam
                            binding.awayTeamText.text = match.awayTeam
                            binding.scoreText.text = match.score
                        },
                        onFailure = {
                            binding.scoreText.text = "Data fetching failed..!"
                            Log.e("ViewHolder", "Detail fetch failed: ${it.message}")
                        },
                    )
                } else {
                    Log.d("ViewHolder", "Fetch for ${item.id} cancelled due to recycling.")
                }
            }
        }
    }

    class MatchDiffCallback : DiffUtil.ItemCallback<MatchId>() {
        override fun areItemsTheSame(oldItem: MatchId, newItem: MatchId): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: MatchId, newItem: MatchId): Boolean {
            return oldItem == newItem
        }
    }
}