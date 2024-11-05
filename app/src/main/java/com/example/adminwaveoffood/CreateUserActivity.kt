package com.example.adminwaveoffood  // Ensure this matches your actual package name

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.adminwaveoffood.databinding.ActivityAddItemBinding
import com.example.adminwaveoffood.databinding.ActivityCreateUserBinding


class CreateUserActivity : AppCompatActivity() {
    // Lazy initialization of binding using the correct type
    private val binding by lazy {
        ActivityCreateUserBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        // Set a click listener on the back button to finish the activity
        binding.backButton.setOnClickListener {
            finish()
        }
    }
}
