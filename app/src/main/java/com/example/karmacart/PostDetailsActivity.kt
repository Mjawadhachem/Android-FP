package com.example.karmacart

import android.net.Uri
import android.os.Bundle
import android.util.Patterns
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.karmacart.data.database.AppDatabaseSingleton
import com.example.karmacart.data.repository.PostRepository
import com.example.karmacart.databinding.ActivityPostDetailsBinding
import com.example.karmacart.viewmodel.PostViewModel
import com.example.karmacart.viewmodel.PostViewModelFactory
import kotlinx.coroutines.launch
import android.content.Intent

class PostDetailsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPostDetailsBinding

    private val vm: PostViewModel by viewModels {
        val db = AppDatabaseSingleton.getDatabase(this)
        PostViewModelFactory(PostRepository(db.postDao()))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPostDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val postId = intent.getIntExtra(MapActivity.EXTRA_POST_ID, -1)
        if (postId == -1) {
            finish()
            return
        }

        vm.posts.observe(this) { posts ->
            val post = posts.firstOrNull { it.id == postId } ?: return@observe

            binding.tvTitle.text = post.title
            binding.tvType.text = post.type
            binding.tvCategory.text = post.category
            binding.tvDescription.text = post.description
            binding.tvContact.text = post.contact

            binding.btnContact.setOnClickListener {
                openContact(post.contact)
            }
        }

        binding.btnBack.setOnClickListener {
            finish()
        }
    }

    private fun openContact(contact: String) {
        val trimmed = contact.trim()

        if (Patterns.EMAIL_ADDRESS.matcher(trimmed).matches()) {
            startActivity(
                Intent(Intent.ACTION_SENDTO).apply {
                    data = Uri.parse("mailto:$trimmed")
                }
            )
        } else {
            startActivity(
                Intent(Intent.ACTION_DIAL).apply {
                    data = Uri.parse("tel:${trimmed.replace(" ", "")}")
                }
            )
        }
    }
}
