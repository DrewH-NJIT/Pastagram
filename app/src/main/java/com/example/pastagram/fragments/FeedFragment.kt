package com.example.pastagram.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.pastagram.EndlessRecyclerViewScrollListener
import com.example.pastagram.FeedAdapter
import com.example.pastagram.Post
import com.example.pastagram.R
import com.parse.FindCallback
import com.parse.ParseException
import com.parse.ParseQuery

open class FeedFragment : Fragment() {

    lateinit var rvFeed: RecyclerView
    lateinit var adapter: FeedAdapter
    lateinit var swipeContainer: SwipeRefreshLayout

    var feedPosts: MutableList<Post> = mutableListOf()

    var countLoaded = 0
    val numPostsToShow = 20

    lateinit var endListener: EndlessRecyclerViewScrollListener

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_feed, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        rvFeed = view.findViewById(R.id.rvFeed) as RecyclerView
        adapter = FeedAdapter(requireContext(), feedPosts)
        rvFeed.adapter = adapter
        rvFeed.layoutManager = LinearLayoutManager(requireContext())

        swipeContainer = view.findViewById(R.id.swipeContainer) as SwipeRefreshLayout

        swipeContainer.setOnRefreshListener {
            queryPosts()
        }

        swipeContainer.setColorSchemeResources(
            android.R.color.holo_blue_bright,
            android.R.color.holo_green_light,
            android.R.color.holo_orange_light,
            android.R.color.holo_red_light,
        )

        endListener = object :
            EndlessRecyclerViewScrollListener(rvFeed.layoutManager as LinearLayoutManager) {
            override fun onLoadMore(page: Int, totalItemsCount: Int, view: RecyclerView?) {
                // Triggered only when new data needs to be appended to the list
                // Add whatever code is needed to append new items to the bottom of the list
                loadMorePosts(countLoaded)
            }
        }

        rvFeed.addOnScrollListener(endListener);


        queryPosts()
    }

    open fun loadMorePosts(startPos: Int) {
        val query: ParseQuery<Post> = ParseQuery.getQuery(Post::class.java)

        query.include(Post.KEY_USER)
        query.addDescendingOrder("createdAt")

        query.limit = numPostsToShow
        query.skip = startPos

        countLoaded += numPostsToShow

        query.findInBackground(object :
            FindCallback<Post> { // don't convert this to lambda. i like to know what's being called.
            override fun done(posts: MutableList<Post>?, e: ParseException?) {
                if (e != null) {
                    Log.e(TAG, "Error fetching posts ")
                } else {
                    if (posts != null) {
                        for (post in posts) {
                            Log.i(
                                TAG, "Post: " + post.getDescription() + " , username: " +
                                        post.getUser()?.username
                            )
                        }
                        feedPosts.addAll(posts)
                        adapter.notifyDataSetChanged()
                        Toast.makeText(requireContext(), "Loaded $numPostsToShow more posts", Toast.LENGTH_SHORT).show()
                        Log.i(TAG, "$numPostsToShow posts loaded.")

                    }
                }
                swipeContainer.isRefreshing = false
            }
        })
    }


    open fun queryPosts() {
        val query: ParseQuery<Post> = ParseQuery.getQuery(Post::class.java)

        query.include(Post.KEY_USER)
        query.addDescendingOrder("createdAt")

        query.limit = numPostsToShow

        countLoaded = 0
        countLoaded += numPostsToShow
        feedPosts.clear()

        query.findInBackground(object :
            FindCallback<Post> { // don't convert this to lambda. i like to know what's being called.
            override fun done(posts: MutableList<Post>?, e: ParseException?) {
                if (e != null) {
                    Log.e(TAG, "Error fetching posts ")
                } else {
                    if (posts != null) {
                        for (post in posts) {
                            Log.i(
                                TAG, "Post: " + post.getDescription() + " , username: " +
                                        post.getUser()?.username
                            )
                        }
                        feedPosts.addAll(posts)
                        adapter.notifyDataSetChanged()
                    }
                }
                swipeContainer.isRefreshing = false
            }
        })
    }

    companion object {
        const val TAG = "FeedFragment"
    }


}