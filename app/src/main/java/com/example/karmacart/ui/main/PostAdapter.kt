package com.example.karmacart.ui.main

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.karmacart.R
import com.example.karmacart.data.entity.Post
import com.example.karmacart.databinding.ItemPostBinding

class PostAdapter(
    private var posts: List<Post>,
    private val onLongPress: (Post) -> Unit,
    private val onDoneClick: (Post) -> Unit,
    private val onContactClick: (Post) -> Unit
) : RecyclerView.Adapter<PostAdapter.PostViewHolder>() {

    inner class PostViewHolder(
        private val binding: ItemPostBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(post: Post) {

            if (post.type == "REQUEST") {
                binding.iconType.setImageResource(R.drawable.ic_request)
                binding.tvType.text = "Request"
            } else {
                binding.iconType.setImageResource(R.drawable.ic_offer)
                binding.tvType.text = "Offer"
            }

            binding.tvTitle.text = post.title
            binding.tvDescription.text = post.description
            binding.tvMeta.text = "${post.category} • ${post.contact}"

            // Done button state
            if (post.isCompleted) {
                binding.btnDone.isEnabled = false
                binding.btnDone.text = "Completed"
            } else {
                binding.btnDone.isEnabled = true
                binding.btnDone.text = "Done"
            }

            binding.btnDone.setOnClickListener { onDoneClick(post) }
            binding.btnContact.setOnClickListener { onContactClick(post) }

            itemView.setOnLongClickListener {
                onLongPress(post)
                true
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        val binding = ItemPostBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return PostViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        holder.bind(posts[position])
    }

    override fun getItemCount(): Int = posts.size

    fun submit(newPosts: List<Post>) {
        posts = newPosts
        notifyDataSetChanged()
    }
}
