package com.example.focusbuddy.ui.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.focusbuddy.R
import com.example.focusbuddy.databinding.ActivityProfileBinding
import com.example.focusbuddy.ui.HomeActivity
import com.example.focusbuddy.ui.login.LoginActivity
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.firestore

class ProfileActivity : AppCompatActivity() {
    private lateinit var binding: ActivityProfileBinding
    private val db = Firebase.firestore
    private lateinit var tvName: TextView
    private lateinit var tvUsia: TextView
    private lateinit var tipePengguna: TextView
    private lateinit var tvName2: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        tvName2 = binding.tvName1
        tvName = binding.tvName
        tvUsia = binding.tvUsia
        tipePengguna = binding.tipePengguna

        val uid = FirebaseAuth.getInstance().currentUser!!.uid

        val ref = db.collection("quickSetup").document(uid)
        ref.get().addOnSuccessListener {
            if (it != null) {
                val name = it.getString("nama")
                val usia = it.get("usia").toString()
                val tipe = it.getString("hasilAkhir")
                tvName.text = name
                tvUsia.text = usia
                tipePengguna.text = tipe
                tvName2.text = name
            }
        }

        binding.logoutButton.setOnClickListener{
            logout()
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
    private fun logout() {
        Firebase.auth.signOut()
        startActivity(Intent(this@ProfileActivity, LoginActivity::class.java))
        finish()
        finishAffinity()

    }
}