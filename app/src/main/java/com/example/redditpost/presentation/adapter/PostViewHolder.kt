package com.example.redditpost.presentation.adapter

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.redditpost.databinding.ItemPostBinding
import com.example.redditpost.remote.model.DataX

class PostViewHolder(
    private val binding: ItemPostBinding,
    private val onBookmarkClick: (DataX) -> Unit
) :
    RecyclerView.ViewHolder(binding.root) {
    fun bind(post: DataX) {
        binding.apply {
            authorText.text = post.author
            postTitle.text = post.title
            Glide.with(itemView.context)
                .load(post.thumbnail)
                .into(postImage)
            commentCount.text = post.commentCounts.toString()
            awardCount.text = post.totalAwardsReceived.toString()

            postSaveFav.setOnClickListener {
                onBookmarkClick(post)
            }
            hasVideo(post)
        }
    }

    private fun ItemPostBinding.hasVideo(post: DataX) {
        if (post.isVideo) {
            playButton.visibility = View.VISIBLE
        } else {
            playButton.visibility = View.GONE
        }
    }
}