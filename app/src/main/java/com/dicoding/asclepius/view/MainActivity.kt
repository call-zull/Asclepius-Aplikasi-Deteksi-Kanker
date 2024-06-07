package com.dicoding.asclepius.view

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.dicoding.asclepius.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    private var currentImageUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.elevation = 0f
        supportActionBar?.title = "Cancer Detect"

        binding.galleryButton.setOnClickListener { openGallery() }
        binding.analyzeButton.setOnClickListener {
            currentImageUri?.let {
                movingToResult()
            } ?: showToast("Pilih Foto Terlebih Dahulu")
        }
    }

    private fun openGallery() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "image/*"
        val chooser = Intent.createChooser(intent, "Pilih Foto")
        launcherGallery.launch(chooser)
    }

    private val launcherGallery = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            val selectedImage = result.data?.data
            selectedImage?.let { uri ->
                currentImageUri = uri
                showingImage()
            } ?: showToast("Gagal Mendapatkan foto")
        }
    }

    private fun showingImage() {
        currentImageUri?.let { uri ->
            Log.d(TAG, "Displaying image: $uri")
            binding.previewImageView.setImageURI(uri)
        } ?: Log.d(TAG, "No image to display")
    }

    private fun movingToResult() {
        val intent = Intent(this, ResultActivity::class.java)
        currentImageUri?.let { uri ->
            intent.putExtra(ResultActivity.URI_IMAGE, uri.toString())
            startActivity(intent)
        } ?: showToast("Tidak ada foto yang dipilih")
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    companion object {
        const val TAG = "ImagePicker"
    }
}
