package com.dicoding.asclepius.view

import android.annotation.SuppressLint
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.dicoding.asclepius.databinding.ActivityResultBinding
import com.dicoding.asclepius.helper.ImageClassifierHelper
import org.tensorflow.lite.task.vision.classifier.Classifications
import java.text.SimpleDateFormat
import java.util.Date

class ResultActivity : AppCompatActivity() {
    private lateinit var binding: ActivityResultBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityResultBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // TODO: Menampilkan hasil gambar, prediksi, dan confidence score.
        val imageString = intent.getStringExtra(URI_IMAGE)
        if (imageString != null) {
            val imageUri = Uri.parse(imageString)
            displayingImage(imageUri)

            val imageClassifierHelper = ImageClassifierHelper(
                context = this,
                classifierListener = object : ImageClassifierHelper.ClassifierListener {
                    override fun onError(error: String) {
                        Log.d(TAG, "Error: $error")
                        showingToast("Error: $error")
                    }

                    override fun onResults(result: List<Classifications>?, interenceTime: Long) {
                        result?.let { showingResult(it) }
                    }
                }
            )
            try {
                imageClassifierHelper.classifyImage(imageUri)
            } catch (e: Exception) {
                val errorMessage = "Failed to classify image: ${e.message}"
                Log.e(TAG, errorMessage)
                showingToast(errorMessage)
            }

        } else {
            Log.e(TAG, "No Image Exist")
            finish()
        }
    }

    private fun showingToast(mess: String) {
        Toast.makeText(this, mess, Toast.LENGTH_SHORT).show()
    }

    @SuppressLint("SetTextI18n", "SimpleDateFormat")
    private fun showingResult(result: List<Classifications>) {
        val topResult = result[0]
        val label = topResult.categories[0].label
        val score = topResult.categories[0].score

        fun Float.formatToString(): String {
            return String.format("%.2f%%", this * 100)
        }

        val formatedDate = SimpleDateFormat("yyyy-MM-dd").format(Date())
        val formatedTime = SimpleDateFormat("HH:mm:ss").format(Date())
        val dateNow = "$formatedDate  $formatedTime"

        binding.resultText.text = "$label ${score.formatToString()}"
    }

    private fun displayingImage(uri: Uri) {
        Log.d(TAG, "Display Image: $uri")
        binding.resultImage.setImageURI(uri)
    }

    companion object {
        const val URI_IMAGE = "img_uri"
        const val TAG = "imagepPicker"
    }
}
