package com.duelisto

import android.os.Bundle
import android.text.Editable
import androidx.appcompat.app.AppCompatActivity
import com.duelisto.databinding.ActivitySlidemenuBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.squareup.picasso.Picasso

class SlideMenuActivity : AppCompatActivity() {

    private lateinit var binding : ActivitySlidemenuBinding
    private lateinit var database : FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivitySlidemenuBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)


    }
}