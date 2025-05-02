package com.example.focusbuddy.ui.register

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.focusbuddy.MainActivity
import com.example.focusbuddy.R
import com.example.focusbuddy.databinding.ActivityRegisterBinding
import com.example.focusbuddy.ui.HomeActivity
import com.example.focusbuddy.ui.intro.SetupActivity
import com.example.focusbuddy.ui.login.LoginActivity
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth

class RegisterActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRegisterBinding
    private lateinit var auth: FirebaseAuth


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)
        enableEdgeToEdge()
        binding.signUpTextView.setOnClickListener{
            startActivity(Intent(this@RegisterActivity, LoginActivity::class.java))
        }
        auth = FirebaseAuth.getInstance()

        binding.loginButton.setOnClickListener {
            auth.createUserWithEmailAndPassword(binding.edLoginEmail.text.toString(), binding.edLoginPassword.text.toString()).addOnCompleteListener {
                if (it.isSuccessful){
                    startActivity(Intent(this@RegisterActivity, SetupActivity::class.java))
                }
                else{
                    Toast.makeText(this, it.exception.toString(), Toast.LENGTH_SHORT).show()
                }
            }
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    override fun onStart() {
        super.onStart()
        val currentUser = auth.currentUser
        if (currentUser != null){
            startActivity(Intent(this@RegisterActivity, HomeActivity::class.java))
        }
    }
}