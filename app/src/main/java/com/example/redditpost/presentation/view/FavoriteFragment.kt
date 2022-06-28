package com.example.redditpost.presentation.view

import android.R
import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.example.redditpost.MyApplication
import com.example.redditpost.presentation.adapter.PostAdapter
import com.example.redditpost.databinding.FragmentFavoriteBinding
import com.example.redditpost.di.ViewModelProviderFactory
import com.example.redditpost.presentation.viewmodel.FavoriteViewModel
import com.example.redditpost.presentation.viewstate.FavoriteViewEvent
import com.example.redditpost.presentation.viewstate.FavoriteViewState
import com.example.redditpost.remote.model.DataX
import com.example.redditpost.utils.NetworkUtil
import com.google.android.material.snackbar.Snackbar
import javax.inject.Inject

private const val TAG = "FavoriteFragment"

class FavoriteFragment : Fragment() {
    private var _binding: FragmentFavoriteBinding? = null
    private val binding get() = _binding!!
    private lateinit var postAdapter: PostAdapter

    @Inject
    lateinit var networkUtil: NetworkUtil

    @Inject
    lateinit var viewModelFactory: ViewModelProviderFactory
    private val favoriteViewModel: FavoriteViewModel by viewModels { viewModelFactory }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding =
            FragmentFavoriteBinding.inflate(inflater, container, false)
        // Inflate the layout for this fragment
        return binding.root
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        (context.applicationContext as MyApplication).provideAppComponent().inject(this)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        favoriteViewModel.favoriteViewState.observe(viewLifecycleOwner) { response ->
            when (response) {
                is FavoriteViewState.Error -> {
                    serverErrorView(true)
                    progressbarView(false)
                }
                FavoriteViewState.Loading -> {
                    serverErrorView(false)
                    progressbarView(true)
                }
                is FavoriteViewState.Success -> {
                    serverErrorView(false)
                    progressbarView(false)
                    postAdapter.submitList(response.posts)
                }
            }
        }
        setupRecyclerView()
        navigateToBack()
        clearAllPosts()
        handleViewEvent()
    }

    private fun handleViewEvent() {
        favoriteViewModel.favoriteViewEvent.observe(viewLifecycleOwner) { event ->
            event.peekContent().let { favoriteViewEvent ->
                when (favoriteViewEvent) {
                    FavoriteViewEvent.Error -> {
                        Snackbar.make(binding.root, "something went wrong !!", Snackbar.LENGTH_LONG).show()
                    }
                    is FavoriteViewEvent.ShowDeleteSnackBar -> {
                        Snackbar.make(
                            binding.root,
                            "Successfully deleted post",
                            Snackbar.LENGTH_LONG
                        ).apply {
                            setAction("Undo") {
                                favoriteViewModel.addPostToFavorite(favoriteViewEvent.post)
                            }
                            show()
                        }
                    }
                    FavoriteViewEvent.ShowDeleteAllDialog -> {
                        AlertDialog.Builder(context)
                            .setTitle("Delete All")
                            .setMessage("Are you sure you want to delete all posts?")
                            .setPositiveButton(
                                "Yes"
                            ) { _, _ -> favoriteViewModel.clearAllPosts() }
                            .setNegativeButton("Cancel", null)
                            .setIcon(R.drawable.ic_dialog_alert)
                            .show()
                    }
                }
            }
        }
    }

    /*
   * handle swipe to delete post
   * */
    private val itemTouchHelperCallback = object : ItemTouchHelper.SimpleCallback(
        ItemTouchHelper.UP or ItemTouchHelper.DOWN,
        ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
    ) {
        override fun onMove(
            recyclerView: RecyclerView,
            viewHolder: RecyclerView.ViewHolder,
            target: RecyclerView.ViewHolder,
        ): Boolean {
            return true
        }

        override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
            val position = viewHolder.adapterPosition
            val post = postAdapter.currentList[position]
            deletePostFromFavorite(post)
        }
    }


    private fun setupRecyclerView() {
        postAdapter = PostAdapter(
            networkUtil = networkUtil,
            onTryAgainClick = {},
            onBookmarkClick = {
                deletePostFromFavorite(it)
            },
            isBookMark = true
        )
        binding.apply {
            favoriteRecycler.adapter = postAdapter
            // handle swipe to delete
            ItemTouchHelper(itemTouchHelperCallback).apply {
                attachToRecyclerView(favoriteRecycler)
            }
        }
    }

    private fun deletePostFromFavorite(post: DataX) {
        favoriteViewModel.deletePostFromFavorite(post)
    }

    private fun clearAllPosts() {
        binding.clearAll.setOnClickListener {
            favoriteViewModel.showDeleteAllDialog()
        }
    }

    private fun navigateToBack() {
        binding.favoriteBackArrow.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    /*
  * handle views visibility
  *  */
    private fun progressbarView(showViews: Boolean) {
        binding.favoriteProgressBar.isVisible = showViews
    }

    private fun serverErrorView(showViews: Boolean) {
        binding.ivServerError.isVisible = showViews
    }

}