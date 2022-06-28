package com.example.redditpost.presentation.view

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AbsListView
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.redditpost.MyApplication
import com.example.redditpost.presentation.adapter.PostAdapter
import com.example.redditpost.databinding.FragmentHomeBinding
import com.example.redditpost.di.ViewModelProviderFactory
import com.example.redditpost.presentation.viewmodel.HomeViewModel
import com.example.redditpost.presentation.viewstate.HomeViewEvent
import com.example.redditpost.presentation.viewstate.HomeViewState
import com.example.redditpost.remote.model.DataX
import com.example.redditpost.utils.NetworkUtil
import com.google.android.material.snackbar.Snackbar
import javax.inject.Inject

private const val TAG = "HomeFragment"

class HomeFragment : Fragment() {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    @Inject
    lateinit var networkUtil: NetworkUtil
    private lateinit var postAdapter: PostAdapter
    private var isScrolling = false

    @Inject
    lateinit var viewModelFactory: ViewModelProviderFactory
    private val homeViewModel: HomeViewModel by viewModels { viewModelFactory }


    override fun onAttach(context: Context) {
        super.onAttach(context)
        (context.applicationContext as MyApplication).provideAppComponent().inject(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        // Inflate the layout for this fragment
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        handleListPostResponse()
        setupRecyclerView()
        tryAgainButton()
        swipeRefresh()
        handleViewEvent()
    }
    private fun handleViewEvent(){
        homeViewModel.homeViewEvent.observe(viewLifecycleOwner){event->
            event.peekContent().let { viewEvent->
                when(viewEvent){
                    HomeViewEvent.Error -> {
                        Snackbar.make(binding.root, "something went wrong !!", Snackbar.LENGTH_LONG).show()
                    }
                    is HomeViewEvent.ShowAddSnackBar -> {
                        Snackbar.make(binding.root, "Successfully Save post", Snackbar.LENGTH_LONG).apply {
                            setAction("Undo") {
                                homeViewModel.deletePostFromFavorite(viewEvent.post)
                            }
                            show()
                        }
                    }
                    is HomeViewEvent.ShowDeleteSnackBar -> {
                        Snackbar.make(binding.root, "Successfully delete post", Snackbar.LENGTH_LONG).apply {
                            setAction("Undo") {
                                homeViewModel.deletePostFromFavorite(viewEvent.post)
                            }
                            show()
                        }
                    }
                }
            }
        }
    }

    private fun handleListPostResponse() {
        homeViewModel.homeViewState.observe(viewLifecycleOwner) { response ->
            response?.let { postResponse ->
                when (postResponse) {
                    is HomeViewState.Error -> {
                        serverErrorView(true)
                        tryAgainView(true)
                        progressbarView(false)
                        noInternetView(false)
                        postResponse.message?.let { message ->
                            Log.e(TAG, message)
                        }
                    }
                    HomeViewState.Loading -> {
                        progressbarView(true)
                        tryAgainView(false)
                        noInternetView(false)
                        serverErrorView(false)
                    }
                    HomeViewState.NoInternet -> {
                        tryAgainView(true)
                        noInternetView(true)
                        progressbarView(false)
                        serverErrorView(false)
                    }
                    is HomeViewState.Success -> {
                        tryAgainView(false)
                        progressbarView(false)
                        serverErrorView(false)
                        noInternetView(false)
                        postAdapter.submitList(postResponse.posts)
                    }
                }
            }
        }
    }

    private fun setupRecyclerView() {
        postAdapter = PostAdapter(
            networkUtil = networkUtil,
            onTryAgainClick = { homeViewModel.getPost() },
            onBookmarkClick = {
                addPostToFavorite(it)
            },
            isBookMark = false
        )
        binding.apply {
            postRecycler.adapter = postAdapter
            postRecycler.addOnScrollListener(this@HomeFragment.scrollListener)
        }
    }


    private fun addPostToFavorite(post: DataX) {
        homeViewModel.addPostToFavorite(post)
    }

    /*
     * handle pagination
     *  */
    private val scrollListener = object : RecyclerView.OnScrollListener() {
        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            super.onScrolled(recyclerView, dx, dy)

            val layoutManager = recyclerView.layoutManager as LinearLayoutManager
            val firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()
            val visibleItemCount = layoutManager.childCount
            val totalItemCount = layoutManager.itemCount

            val isAtLastItem = firstVisibleItemPosition + visibleItemCount >= totalItemCount
            val isNotAtBeginning = firstVisibleItemPosition >= 0
            val shouldPaginate = isAtLastItem && isNotAtBeginning && isScrolling

            if (shouldPaginate) {
                homeViewModel.getPost()
                isScrolling = false
            }
        }

        override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
            super.onScrollStateChanged(recyclerView, newState)
            if (newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {
                isScrolling = true
            }
        }
    }

    private fun tryAgainButton() {
        binding.btTryAgain.setOnClickListener {
            homeViewModel.getPost()
        }
    }

    private fun swipeRefresh() {
        val swipeRefresh = binding.swipeRefresh
        swipeRefresh.setOnRefreshListener {
            homeViewModel.swipeToRefresh()
            swipeRefresh.isRefreshing = false
        }
    }


    /*
     * handle views visibility
     *  */
    private fun progressbarView(showViews: Boolean) {
        binding.postProgressBar.isVisible = showViews
    }

    private fun noInternetView(showViews: Boolean) {
        binding.tvNoInternet.isVisible = showViews
    }

    private fun serverErrorView(showViews: Boolean) {
        binding.ivServerError.isVisible = showViews
    }

    private fun tryAgainView(showViews: Boolean) {
        binding.btTryAgain.isVisible = showViews
    }


}