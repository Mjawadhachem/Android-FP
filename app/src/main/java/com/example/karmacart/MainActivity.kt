package com.example.karmacart

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Patterns
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import com.example.karmacart.data.database.AppDatabaseSingleton
import com.example.karmacart.data.entity.Post
import com.example.karmacart.data.repository.PostRepository
import com.example.karmacart.databinding.ActivityMainBinding
import com.example.karmacart.ui.main.PostAdapter
import com.example.karmacart.viewmodel.PostViewModel
import com.example.karmacart.viewmodel.PostViewModelFactory
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit
import org.osmdroid.config.Configuration

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

        val config = Configuration.getInstance()
        config.load(applicationContext, getSharedPreferences("osmdroid", MODE_PRIVATE))
        config.userAgentValue = packageName
        config.osmdroidBasePath = filesDir
        config.osmdroidTileCache = filesDir

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupRecycler()
        observePosts()
        setupSearch()
        setupCategories()
        setupDashboard()
        setupFab()
    }

    private fun setupRecycler() {
        adapter = PostAdapter(
            emptyList(),

            // Long press delete (existing behavior)
            onLongPress = { post ->
                lifecycleScope.launch {
                    vm.deletePost(post)
                }
            },

            // Done button
            onDoneClick = { post ->
                if (!post.isCompleted) {
                    vm.markPostCompleted(post.id)
                    scheduleUrgentNotification(post.title, post.category)
                }
            },

            // Contact button
            onContactClick = { post ->
                openContact(post.contact)
            },

            // ✅ DELETE BUTTON (THIS WAS MISSING)
            onDeleteClick = { post ->
                lifecycleScope.launch {
                    vm.deletePost(post)
                }
            }
        )

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

    private fun setupDashboard() {

        binding.btnRequestHelp.setOnClickListener {
            val filtered = allPosts.filter { it.type.equals("REQUEST", true) }
            adapter.submit(filtered)
            binding.emptyState.isVisible = filtered.isEmpty()
        }

        binding.btnOfferHelp.setOnClickListener {
            val filtered = allPosts.filter { it.type.equals("DONATION", true) }
            adapter.submit(filtered)
            binding.emptyState.isVisible = filtered.isEmpty()
        }

        binding.btnAllPosts.setOnClickListener {
            adapter.submit(allPosts)
            binding.emptyState.isVisible = allPosts.isEmpty()
        }

        binding.btnMyPosts.setOnClickListener { }

        // ✅ VIEW MODE
        binding.btnMap.setOnClickListener {
            startActivity(
                Intent(this, MapActivity::class.java).apply {
                    putExtra(MapActivity.EXTRA_SELECT_MODE, false)
                }
            )
        }
    }

    private fun setupFab() {
        binding.fabAdd.setOnClickListener {
            startActivity(Intent(this, AddPostActivity::class.java))
        }
    }

    private fun openContact(contact: String) {
        val trimmed = contact.trim()
        if (Patterns.EMAIL_ADDRESS.matcher(trimmed).matches()) {
            startActivity(Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:$trimmed")))
            return
        }
        startActivity(Intent(Intent.ACTION_DIAL, Uri.parse("tel:${trimmed.replace(" ", "")}")))
    }

    private fun scheduleUrgentNotification(title: String, category: String) {
        val data = workDataOf("title" to title, "category" to category)
        val request = OneTimeWorkRequestBuilder<UrgentPostWorker>()
            .setInputData(data)
            .setInitialDelay(10, TimeUnit.SECONDS)
            .build()
        WorkManager.getInstance(this).enqueue(request)
    }
}
