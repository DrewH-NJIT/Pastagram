package com.example.pastagram

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import java.text.SimpleDateFormat


const val MOVIE_EXTRA = "MOVIE_EXTRA"

class FeedAdapter(
    private val context: Context,
    private val posts: List<Post>,
) :
    RecyclerView.Adapter<FeedAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        val view = LayoutInflater.from(context).inflate(R.layout.item_feed, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val post = posts[position]
        holder.bind(post)
    }

    private fun getTimeInMs(post: Post): String {

        //val format: String = "EEE MMM dd HH:mm:ss ZZZZZ yyyy"
        val format: String = "EEE MMM dd HH:mm:ss zzz yyyy"
        val s = SimpleDateFormat(format)
        val ii: Long = s.parse(post.createdAt.toString()).time
        val ii2: Long = System.currentTimeMillis()
        val utct: Long = ((ii2 - ii) / 1000)

        //if under 60, print s. if under 60*24, print h.
        var timeSuffix: String = ""
        val timeMinimized: Long

        if (utct < 60) {
            timeSuffix = "s"
            timeMinimized = utct
        } else if (utct < 60 * 60) {
            timeSuffix = "m"
            timeMinimized = utct / 60
        } else if (utct < 60 * 60 * 24) {
            timeSuffix = "h"
            timeMinimized = utct / 60 / 60
        } else {
            timeSuffix = "d"
            timeMinimized = utct / 60 / 60 / 24
        }

        return "$timeMinimized$timeSuffix"
    }

    override fun getItemCount() = posts.size

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val ivPoster: ImageView
        private val tvUser: TextView
        private val tvDesc: TextView
        private val tvDate: TextView

        init {
            ivPoster = itemView.findViewById<ImageView>(R.id.ivPreview)
            tvUser = itemView.findViewById<TextView>(R.id.tvName)
            tvDesc = itemView.findViewById<TextView>(R.id.tvDescription)
            tvDate = itemView.findViewById(R.id.tvCA)
        }

        fun bind(post: Post) {
            val timeStringMin: String = getTimeInMs(post)

            tvDesc.text = post.getDescription()
            tvUser.text = post.getUser()?.username
            tvDate.text = "$timeStringMin ago"

            Glide.with(itemView.context).load(post.getImage()?.url).into(ivPoster)

        }
    }
}
