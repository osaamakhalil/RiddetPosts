package com.example.redditpost.presentation.view

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AbsListView
import android.widget.SearchView
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.redditpost.MyApplication
import com.example.redditpost.presentation.adapter.PostAdapter
import com.example.redditpost.databinding.FragmentSearchBinding
import com.example.redditpost.di.ViewModelProviderFactory
import com.example.redditpost.remote.model.DataX
import com.example.redditpost.presentation.viewmodel.SearchViewModel
import com.example.redditpost.presentation.viewstate.SearchViewEvent
import com.example.redditpost.presentation.viewstate.SearchViewState
import com.example.redditpost.utils.NetworkUtil
import com.google.android.material.snackbar.Snackbar
import io.reactivex.Observable
import io.reactivex.ObservableOnSubscribe
import io.reactivex.disposables.CompositeDisposable
import java.util.*
import java.util.concurrent.TimeUnit
import javax.inject.Inject

private const val TAG = "SearchFragment"

class SearchFragment : Fragment() {
    private var searchQuery = ""
    private var isScrolling = false
    private var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding!!
    private lateinit var postAdapter: PostAdapter

    @Inject
    lateinit var networkUtil: NetworkUtil

    @Inject
    lateinit var viewModelFactory: ViewModelProviderFactory
    private val searchViewModel: SearchViewModel by viewModels { viewModelFactory }

    private val compositeDisposable = CompositeDisposable()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSearchBinding.inflate(inflater, container, false)
        // Inflate the layout for this fragment
        return binding.root
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        (context.applicationContext as MyApplication).provideAppComponent().inject(this)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        handleSearchResponse()
        searchForPost()
        setupRecyclerView()
        tryAgainButton()
        navigateToBack()
        handleViewEvent()
    }

    private fun handleSearchResponse() {
        searchViewModel.searchViewState.observe(viewLifecycleOwner) { searchList ->
            when (searchList) {
                is SearchViewState.Error -> {
                    serverErrorView(true)
                    tryAgainView(true)
                    progressbarView(false)
                    noInternetView(false)
                    searchList.error.let { message ->
                        if (message != null) {
                            Log.e(TAG, message)
                        }
                    }
                }
                SearchViewState.Loading -> {
                    progressbarView(true)
                    tryAgainView(false)
                    noInternetView(false)
                    serverErrorView(false)
                }
                SearchViewState.NoInternet -> {
                    tryAgainView(true)
                    noInternetView(true)
                    progressbarView(false)
                    serverErrorView(false)
                }
                is SearchViewState.Success -> {
                    tryAgainView(false)
                    progressbarView(false)
                    serverErrorView(false)
                    noInternetView(false)
                    postAdapter.submitList(searchList.posts)
                }
            }

        }
    }

    private fun handleViewEvent() {
        searchViewModel.searchViewEvent.observe(viewLifecycleOwner) { event ->
            event.peekContent().let { viewEvent ->
                when (viewEvent) {
                    SearchViewEvent.Error -> {
                        Snackbar.make(binding.root, "something went wrong !!", Snackbar.LENGTH_LONG)
                            .show()
                    }
                    is SearchViewEvent.ShowAddPostSnackBar -> {
                        Snackbar.make(binding.root, "Successfully Save post", Snackbar.LENGTH_LONG)
                            .apply {
                                setAction("Undo") {
                                    searchViewModel.deletePostFromFavorite(viewEvent.post)
                                }
                                show()
                            }
                    }
                    is SearchViewEvent.ShowDeletePostSnackBar -> {
                        Snackbar.make(
                            binding.root,
                            "Successfully delete post",
                            Snackbar.LENGTH_LONG
                        ).apply {
                            setAction("Undo") {
                                searchViewModel.deletePostFromFavorite(viewEvent.post)
                            }
                            show()
                        }
                    }

                }

            }
        }
    }


        //handle input from user hit request after 1000 MILLISECONDS
        private fun searchForPost() {
            compositeDisposable.addAll(Observable.create(ObservableOnSubscribe<String> { subscriber ->
                binding.postSearchView.setOnQueryTextListener((object :
                    SearchView.OnQueryTextListener {
                    override fun onQueryTextSubmit(query: String?): Boolean {
                        query?.let {
                            subscriber.onNext(query)
                        }
                        return false
                    }

                    override fun onQueryTextChange(newText: String?): Boolean {
                        newText?.let {
                            subscriber.onNext(newText)
                        }

                        return false
                    }
                }))
            }).map { text -> text.lowercase(Locale.getDefault()).trim() }
                .debounce(1000, TimeUnit.MILLISECONDS)
                .distinctUntilChanged()
                .filter { text -> text.isNotBlank() }
                .subscribe { text ->
                    searchViewModel.clearSearch()
                    searchViewModel.getPostSearch(text)
                    searchQuery = text
                })
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
                    searchViewModel.getPostSearch(searchQuery)
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

        private fun setupRecyclerView() {
            postAdapter = PostAdapter(
                networkUtil = networkUtil,
                onTryAgainClick = { searchViewModel.getPostSearch(searchQuery) },
                onBookmarkClick = { addPostToFavorite(it) },
                isBookMark = false
            )
            binding.apply {
                searchPostRecycler.adapter = postAdapter
                searchPostRecycler.addOnScrollListener(this@SearchFragment.scrollListener)
            }
        }

        private fun addPostToFavorite(post: DataX) {
            searchViewModel.addPostToFavorite(post)
        }

        private fun tryAgainButton() {
            binding.searchBtTryAgain.setOnClickListener {
                searchViewModel.getPostSearch(searchQuery)
            }
        }

        private fun navigateToBack() {
            binding.searchBackArrow.setOnClickListener {
                findNavController().popBackStack()
            }
        }

        override fun onDestroy() {
            compositeDisposable.clear()
            super.onDestroy()
        }

        /*
     * handle views visibility
     *  */
        private fun progressbarView(showViews: Boolean) {
            binding.searchPostProgressBar.isVisible = showViews
        }

        private fun noInternetView(showViews: Boolean) {
            binding.searchTvNoInternet.isVisible = showViews
        }

        private fun serverErrorView(showViews: Boolean) {
            binding.searchIvServerError.isVisible = showViews
        }

        private fun tryAgainView(showViews: Boolean) {
            binding.searchBtTryAgain.isVisible = showViews
        }


    }