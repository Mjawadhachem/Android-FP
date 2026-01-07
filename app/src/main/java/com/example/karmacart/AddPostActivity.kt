package com.example.karmacart

import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import androidx.activity.result.contract.ActivityResultContracts
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

    private var selectedLatitude = 0.0
    private var selectedLongitude = 0.0
    private var isRequestSelected = true

    private val vm: PostViewModel by viewModels {
        val db = AppDatabaseSingleton.getDatabase(this)
        PostViewModelFactory(PostRepository(db.postDao()))
    }

    private val mapLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                result.data?.let { data ->
                    selectedLatitude = data.getDoubleExtra("latitude", 0.0)
                    selectedLongitude = data.getDoubleExtra("longitude", 0.0)

                    Snackbar.make(binding.root, "Location selected", Snackbar.LENGTH_SHORT).show()
                }
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddPostBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupUi()
    }

    private fun setupUi() {

        setupCategoryDropdown() // ✅ now works

        binding.toggleType.addOnButtonCheckedListener { _, checkedId, isChecked ->
            if (!isChecked) return@addOnButtonCheckedListener
            isRequestSelected = checkedId == binding.btnTypeRequest.id
            updateToggleColors(isRequestSelected)
        }

        binding.btnSelectLocation.setOnClickListener {
            val intent = Intent(this, MapActivity::class.java)
                .putExtra(MapActivity.EXTRA_SELECT_MODE, true)
            mapLauncher.launch(intent)
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

            if (selectedLatitude == 0.0 && selectedLongitude == 0.0) {
                Snackbar.make(binding.root, "Please select a location", Snackbar.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val type = if (isRequestSelected) "REQUEST" else "DONATION"

            lifecycleScope.launch {
                vm.addPost(
                    type = type,
                    title = title,
                    desc = desc,
                    cat = cat,
                    contact = contact,
                    latitude = selectedLatitude,
                    longitude = selectedLongitude
                )

                Snackbar.make(binding.root, "Post created", Snackbar.LENGTH_SHORT).show()
                finish()
            }
        }
    }

    // ✅ DROPDOWN SETUP (NOW VALID)
    private fun setupCategoryDropdown() {
        val categories = listOf(
            "Blood",
            "Food",
            "Clothes",
            "Medicine"
        )

        val adapter = ArrayAdapter(
            this,
            android.R.layout.simple_list_item_1,
            categories
        )

        binding.etCategory.setAdapter(adapter)
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
