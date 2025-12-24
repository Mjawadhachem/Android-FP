package com.example.karmacart.viewmodel

class AddPostActivity {
}package com.example.karmacart

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.room.Room
import com.example.karmacart.data.database.AppDatabase
import com.example.karmacart.data.repository.PostRepository
import com.example.karmacart.databinding.ActivityAddPostBinding
import com.example.karmacart.viewmodel.PostViewModel
import com.example.karmacart.viewmodel.PostViewModelFactory
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch

class AddPostActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddPostBinding

    private val db by lazy {
        Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java,
            "karmacart.db"
        ).build()
    }

    private val vm: PostViewModel by viewModels {
        PostViewModelFactory(PostRepository(db.postDao()))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddPostBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupUi()
    }

    private fun setupUi() {

        // Default type = request
        binding.toggleType.check(binding.btnTypeRequest.id)

        binding.btnCreate.setOnClickListener {
            val title = binding.etTitle.text.toString().trim()
            val desc = binding.etDescription.text.toString().trim()
            val cat = binding.etCategory.text.toString().trim()
            val contact = binding.etContact.text.toString().trim()

            val type = if (binding.btnTypeRequest.isChecked) "REQUEST" else "DONATION"

            if (title.isEmpty() || desc.isEmpty() || cat.isEmpty() || contact.isEmpty()) {
                Snackbar.make(binding.root, "Please fill all fields", Snackbar.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            lifecycleScope.launch {
                vm.addPost(type, title, desc, cat, contact)
                Snackbar.make(binding.root, "Post created", Snackbar.LENGTH_SHORT).show()
                finish()
            }
        }
    }
}
