package com.example.focusbuddy

import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import com.example.focusbuddy.data.pref.UserPreference
import com.example.focusbuddy.databinding.ActivityMainBinding
import com.example.focusbuddy.ui.login.LoginActivity
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import kotlinx.coroutines.launch


class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var userPreference: UserPreference
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)/*
        userPreference = UserPreference(this)*/

        auth = FirebaseAuth.getInstance()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val btnEnableService: Button = findViewById(R.id.btnEnableService)
        btnEnableService.setOnClickListener {
            // Mengarahkan pengguna ke pengaturan aksesibilitas
            val intent = Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)
            startActivity(intent)
        }

        binding.logoutButton.setOnClickListener {
            logout()
        }
    }

    private fun logout() {
        Firebase.auth.signOut()
        startActivity(Intent(this@MainActivity, LoginActivity::class.java))
    }
}

