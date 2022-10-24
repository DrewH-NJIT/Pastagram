package com.example.pastagram.fragments

import android.util.Log
import android.widget.Toast
import com.example.pastagram.Post
import com.parse.FindCallback
import com.parse.ParseException
import com.parse.ParseQuery
import com.parse.ParseUser

//query.whereEqualTo(Post.KEY_USER, ParseUser.getCurrentUser()) // This is the only unique line.

class ProfileFragment:FeedFragment() {

    override fun loadMorePosts(startPos: Int) {
        val query: ParseQuery<Post> = ParseQuery.getQuery(Post::class.java)

        query.include(Post.KEY_USER)
        query.whereEqualTo(Post.KEY_USER, ParseUser.getCurrentUser()) // This is the only unique line.
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


    override fun queryPosts() {
        val query: ParseQuery<Post> = ParseQuery.getQuery(Post::class.java)

        query.include(Post.KEY_USER)
        query.whereEqualTo(Post.KEY_USER, ParseUser.getCurrentUser()) // This is the only unique line.
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
}