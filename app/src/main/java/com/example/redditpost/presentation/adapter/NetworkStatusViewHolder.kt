package com.example.redditpost.presentation.adapter

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.example.redditpost.databinding.NetworkStatusBinding
import com.example.redditpost.utils.NetworkUtil

class NetworkStatusViewHolder(
    private val binding: NetworkStatusBinding,
    val networkUtil: NetworkUtil,
    private val onTryAgainClick: () -> Unit
) :
    RecyclerView.ViewHolder(binding.root) {
    val pagingProgress = binding.pagingProgress
    val tvNoInternet = binding.noInternetConnection
    val btTryAgain = binding.tryAgain
    fun bind() {
        btTryAgain.setOnClickListener {
            onTryAgainClick()
        }
    }
}

fun showLoadingView(
    viewHolder: NetworkStatusViewHolder,
) {
    viewHolder.bind()
    viewHolder.apply {
        if (!networkUtil.hasInternetConnection()) {
            pagingProgress.visibility = View.GONE
            btTryAgain.visibility = View.VISIBLE
            tvNoInternet.visibility = View.VISIBLE
        } else {
            pagingProgress.visibility = View.VISIBLE
            btTryAgain.visibility = View.GONE
            tvNoInternet.visibility = View.GONE
        }
    }
}
