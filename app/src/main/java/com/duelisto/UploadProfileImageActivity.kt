package com.duelisto

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.widget.Toast
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.duelisto.databinding.ActivityUploadpicsBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.squareup.picasso.Picasso

class UploadProfileImageActivity : AppCompatActivity(){

    private lateinit var uri : Uri
    private lateinit var storageref : FirebaseStorage
    private  lateinit var database : FirebaseFirestore
    private lateinit var binding : ActivityUploadpicsBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUploadpicsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        storageref = FirebaseStorage.getInstance()
        database = FirebaseFirestore.getInstance()

        val galleryImage = registerForActivityResult(
            ActivityResultContracts.GetContent(),
            ActivityResultCallback {
                it?.let { selectedUri ->
                    binding.profileimgadv.setImageURI(selectedUri)
                    uri = selectedUri
                }
            }
        )

        val userId = FirebaseAuth.getInstance().currentUser!!.uid
            val userRef = database.collection("users").document(userId)
            userRef.get()
                .addOnSuccessListener { document ->
                    if (document != null && document.exists()) {
                        val name = document.getString("name").orEmpty()
                        val imgUrl = document.getString("imageUrl") ?: "https://www.kindpng.com/picc/m/24-248253_user-profile-default-image-png-clipart-png-download.png"
                        binding.editprofilenameadv.text = name
                        Picasso.get()
                            .load(imgUrl)
                            .into(binding.profileimgadv)
                    }
                }

        binding.profileimgeditbtn.setOnClickListener{
            galleryImage.launch("image/*")
        }

        binding.profilecancelbtnadv.setOnClickListener {
            startActivity(Intent(this, ProfileActivity::class.java))
        }

        binding.profilesavebtnadv.setOnClickListener {
            storageref.getReference("profileimages").child(System.currentTimeMillis().toString())
                .putFile(uri)
                .addOnSuccessListener {
                        task ->
                    task.metadata!!.reference!!.downloadUrl
                        .addOnSuccessListener{
                            val userId = FirebaseAuth.getInstance().currentUser!!.uid
                            val url = it.toString()
                            val userRef = database.collection("users").document(userId)
                            userRef.update("imageUrl", url)
                                .addOnSuccessListener {
                                    Toast.makeText(this, "Profile Updated successfully", Toast.LENGTH_SHORT).show()
                                }
                                .addOnFailureListener{ e->
                                    Toast.makeText(this, "Failed to update ${e.message}", Toast.LENGTH_SHORT).show()}
                            Toast.makeText(this,url,Toast.LENGTH_LONG).show()
                        }
                        .addOnFailureListener{ e->
                            Toast.makeText(this, e.toString(), Toast.LENGTH_LONG).show()
                        }
                }
        }
    }
}