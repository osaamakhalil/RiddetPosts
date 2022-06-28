package com.example.redditpost.presentation.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.redditpost.databinding.ItemPostBinding
import com.example.redditpost.databinding.NetworkStatusBinding
import com.example.redditpost.remote.model.DataX
import com.example.redditpost.utils.Constant.Companion.POST_LIST_VIEW
import com.example.redditpost.utils.Constant.Companion.VIEW_TYPE_LOADING
import com.example.redditpost.utils.NetworkUtil


class PostAdapter(
    private val networkUtil: NetworkUtil,
    private val onTryAgainClick: () -> Unit,
    private val onBookmarkClick: (DataX) -> Unit,
    private val isBookMark: Boolean
) : ListAdapter<DataX, RecyclerView.ViewHolder>(PostDiffCallBack()) {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == POST_LIST_VIEW) {
            val binding: ItemPostBinding =
                ItemPostBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            PostViewHolder(binding = binding, onBookmarkClick = onBookmarkClick)
        } else {
            val binding: NetworkStatusBinding =
                NetworkStatusBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            NetworkStatusViewHolder(
                binding = binding,
                networkUtil = networkUtil,
                onTryAgainClick = onTryAgainClick
            )
        }

    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is PostViewHolder) {
            populateItemRows(holder, position)
        } else if (holder is NetworkStatusViewHolder) {
            showLoadingView(holder)
        }
    }

    override fun submitList(list: List<DataX>?) {
        super.submitList(list?.let { ArrayList(it) })
    }

    override fun getItemViewType(position: Int): Int {
        return if (isLoading(position))
            VIEW_TYPE_LOADING
        else
            POST_LIST_VIEW
    }

    private fun isLoading(position: Int): Boolean {
        return position == itemCount - 1 && !networkUtil.lastPage && !isBookMark
    }

    private fun populateItemRows(viewHolder: PostViewHolder, position: Int) {
        val item = getItem(position)
        viewHolder.bind(item)
    }

}

/**
 * Callback for calculating the diff between two non-null items in a list.
 *
 * Used by ListAdapter to calculate the minumum number of changes between and old list and a new
 * list that's been passed to `submitList`.
 */
class PostDiffCallBack : DiffUtil.ItemCallback<DataX>() {
    override fun areItemsTheSame(oldItem: DataX, newItem: DataX): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: DataX, newItem: DataX): Boolean {
        return oldItem == newItem
    }

}