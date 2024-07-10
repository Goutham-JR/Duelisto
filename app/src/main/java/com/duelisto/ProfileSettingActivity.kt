package com.duelisto

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.duelisto.databinding.ActivityProfilesettingBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.squareup.picasso.Picasso

class ProfileSettingActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseFirestore
    private lateinit var binding: ActivityProfilesettingBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfilesettingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()
        database = FirebaseFirestore.getInstance()



        binding.editprofile.setOnClickListener{
            startActivity(Intent(this, UploadProfileImageActivity::class.java))
        }

        binding.profilesavebtn.setOnClickListener {
            saveInfo()
        }

        binding.profilebackbtn.setOnClickListener {
            startActivity(Intent(this, HomeActivity::class.java))
        }

        val userId = auth.currentUser?.uid
        if (userId != null) {
            val userRef = database.collection("users").document(userId)
            userRef.get()
                .addOnSuccessListener { document ->
                    if (document != null && document.exists()) {
                        val name = document.getString("name").orEmpty()
                        val email = document.getString("email").orEmpty()
                        val phoneNumber = document.getString("phonenumber").orEmpty()
                        val dob = document.getString("dob").orEmpty()
                        val gender = document.getString("gender").orEmpty()
                        val password = document.getString("password").orEmpty()
                        val imgUrl = document.getString("imageUrl") ?: "https://www.kindpng.com/picc/m/24-248253_user-profile-default-image-png-clipart-png-download.png"

                        binding.settingprofilename.text = name
                        binding.profileemail.text = Editable.Factory.getInstance().newEditable(email)
                        binding.profilepass.text = Editable.Factory.getInstance().newEditable(password)
                        binding.profilephone.text = Editable.Factory.getInstance().newEditable(phoneNumber)
                        binding.profiledob.text = Editable.Factory.getInstance().newEditable(dob)
                        Picasso.get()
                            .load(imgUrl)
                            .into(binding.settingprofileimg)

                        when (gender) {
                            "Male" -> binding.profilemale.isChecked = true
                            "Female" -> binding.profilefemale.isChecked = true
                            "Other" -> binding.profileother.isChecked = true
                        }

                        Toast.makeText(this, "User data loaded successfully", Toast.LENGTH_LONG).show()
                    } else {
                        Toast.makeText(this, "User data not found", Toast.LENGTH_LONG).show()
                    }
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Failed to load user data", Toast.LENGTH_LONG).show()
                }
        } else {
            Toast.makeText(this, "User is not available", Toast.LENGTH_LONG).show()
        }
    }

    //Update the modified values
    private fun saveInfo() {
        val auth = FirebaseAuth.getInstance()
        val database = FirebaseFirestore.getInstance()

        val userId = auth.currentUser?.uid

        if (userId != null) {
            val selectedGenderId = binding.profilegenderRadioGroup.checkedRadioButtonId
            val gender = when (selectedGenderId) {
                R.id.profilemale -> "Male"
                R.id.profilefemale -> "Female"
                R.id.profileother -> "Other"
                else -> null
            }

            if (gender == null) {
                Toast.makeText(this, "Please select a gender", Toast.LENGTH_SHORT).show()
                return
            }

            val updates = mutableMapOf<String, Any>()

            updates["password"] = binding.profilepass.text.toString()
            updates["phonenumber"] = binding.profilephone.text.toString()
            updates["dob"] = binding.profiledob.text.toString()
            updates["gender"] = gender

            database.collection("users").document(userId)
                .update(updates)
                .addOnSuccessListener {
                    Toast.makeText(this, "Profile updated successfully", Toast.LENGTH_SHORT).show()
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this, "Failed to update profile: ${e.message}", Toast.LENGTH_LONG).show()
                }
        } else {
            Toast.makeText(this, "User is not logged in", Toast.LENGTH_SHORT).show()
        }
    }

}
