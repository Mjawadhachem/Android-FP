package com.example.karmacart

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.karmacart.data.database.AppDatabaseSingleton
import com.example.karmacart.data.entity.Post
import com.example.karmacart.data.repository.PostRepository
import com.example.karmacart.databinding.ActivityMainBinding
import com.example.karmacart.ui.main.PostAdapter
import com.example.karmacart.viewmodel.PostViewModel
import com.example.karmacart.viewmodel.PostViewModelFactory
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var adapter: PostAdapter

    private var allPosts: List<Post> = emptyList()

    private val vm: PostViewModel by viewModels {
        val db = AppDatabaseSingleton.getDatabase(this)
        PostViewModelFactory(PostRepository(db.postDao()))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupRecycler()
        observePosts()
        setupSearch()
        setupCategories()
        setupFab()
        setupDashboard()
    }

    private fun setupRecycler() {
        adapter = PostAdapter(emptyList()) { post ->
            lifecycleScope.launch { vm.deletePost(post) }
        }
        binding.rvPosts.layoutManager = LinearLayoutManager(this)
        binding.rvPosts.adapter = adapter
    }

    private fun observePosts() {
        vm.posts.observe(this) { posts ->
            allPosts = posts
            adapter.submit(posts)
            binding.emptyState.isVisible = posts.isEmpty()
        }
    }

    private fun setupSearch() {
        binding.etSearch.addTextChangedListener { text ->
            val q = text.toString().lowercase()

            val filtered = allPosts.filter {
                it.title.lowercase().contains(q) ||
                        it.description.lowercase().contains(q) ||
                        it.category.lowercase().contains(q)
            }

            adapter.submit(filtered)
            binding.emptyState.isVisible = filtered.isEmpty()
        }
    }

    private fun setupCategories() {
        binding.chipBlood.setOnClickListener { filterCategory("blood") }
        binding.chipFood.setOnClickListener { filterCategory("food") }
        binding.chipClothes.setOnClickListener { filterCategory("clothes") }
        binding.chipMedicine.setOnClickListener { filterCategory("medicine") }
    }

    private fun filterCategory(cat: String) {
        val filtered = allPosts.filter { it.category.lowercase().contains(cat) }
        adapter.submit(filtered)
        binding.emptyState.isVisible = filtered.isEmpty()
    }

    private fun setupFab() {
        binding.fabAdd.setOnClickListener {
            binding.fabAdd.animate().rotationBy(360f).setDuration(300).start()
            startActivity(Intent(this, AddPostActivity::class.java))
        }
    }

    private fun setupDashboard() {

        // ✔ SHOW ONLY REQUEST POSTS (SOS)
        binding.btnRequestHelp.setOnClickListener {
            val filtered = allPosts.filter { it.type.equals("REQUEST", ignoreCase = true) }
            adapter.submit(filtered)
            binding.emptyState.isVisible = filtered.isEmpty()
        }

        // ✔ SHOW ONLY OFFER POSTS (DONATION)
        binding.btnOfferHelp.setOnClickListener {
            val filtered = allPosts.filter { it.type.equals("DONATION", ignoreCase = true) }
            adapter.submit(filtered)
            binding.emptyState.isVisible = filtered.isEmpty()
        }

        // Future: filter by contact (owner)
        binding.btnMyPosts.setOnClickListener {
            // Use user phone number when you add auth system
        }

        // ✔ SHOW ALL POSTS
        binding.btnAllPosts.setOnClickListener {
            adapter.submit(allPosts)
            binding.emptyState.isVisible = allPosts.isEmpty()
        }
    }
}
