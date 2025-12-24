package com.example.karmacart

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.example.karmacart.data.database.AppDatabaseSingleton
import com.example.karmacart.data.repository.PostRepository
import com.example.karmacart.databinding.ActivityAddPostBinding
import com.example.karmacart.viewmodel.PostViewModel
import com.example.karmacart.viewmodel.PostViewModelFactory
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch

class AddPostActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddPostBinding

    private val vm: PostViewModel by viewModels {
        val db = AppDatabaseSingleton.getDatabase(this)
        PostViewModelFactory(PostRepository(db.postDao()))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddPostBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupUi()
    }

    private fun setupUi() {

        binding.toggleType.check(binding.btnTypeRequest.id)
        updateToggleColors(true)

        binding.toggleType.addOnButtonCheckedListener { _, checkedId, isChecked ->
            if (!isChecked) return@addOnButtonCheckedListener

            updateToggleColors(checkedId == binding.btnTypeRequest.id)
        }

        binding.btnCreate.setOnClickListener {
            val title = binding.etTitle.text.toString().trim()
            val desc = binding.etDescription.text.toString().trim()
            val cat = binding.etCategory.text.toString().trim()
            val contact = binding.etContact.text.toString().trim()

            if (title.isEmpty() || desc.isEmpty() || cat.isEmpty() || contact.isEmpty()) {
                Snackbar.make(binding.root, "Please fill all fields", Snackbar.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val type = if (binding.btnTypeRequest.isChecked) "REQUEST" else "DONATION"

            lifecycleScope.launch {
                vm.addPost(type, title, desc, cat, contact)
                Snackbar.make(binding.root, "Post created", Snackbar.LENGTH_SHORT).show()
                finish()
            }
        }
    }

    private fun updateToggleColors(isRequest: Boolean) {

        val red = ContextCompat.getColor(this, R.color.redRequest)
        val purple = ContextCompat.getColor(this, R.color.purpleDark)
        val lightBg = ContextCompat.getColor(this, R.color.toggleSurface)
        val white = ContextCompat.getColor(this, R.color.white)

        if (isRequest) {
            binding.btnTypeRequest.setBackgroundColor(red)
            binding.btnTypeRequest.setTextColor(white)

            binding.btnTypeOffer.setBackgroundColor(lightBg)
            binding.btnTypeOffer.setTextColor(purple)

        } else {
            binding.btnTypeOffer.setBackgroundColor(purple)
            binding.btnTypeOffer.setTextColor(white)

            binding.btnTypeRequest.setBackgroundColor(lightBg)
            binding.btnTypeRequest.setTextColor(red)
        }
    }
}
