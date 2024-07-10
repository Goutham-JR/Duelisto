package com.duelisto

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.RadioGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.duelisto.databinding.ActivitySignupBinding
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore


class RegisterActivity : AppCompatActivity(){
    private lateinit var binding : ActivitySignupBinding
    private lateinit var auth : FirebaseAuth
    private lateinit var database: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignupBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()
        database = FirebaseFirestore.getInstance()

        findViewById<Button>(R.id.regbtn).setOnClickListener{
            val email =binding.regemail.text.toString()
            val password =binding.regpass.text.toString()
            val confirmpassword =binding.regconpass.text.toString()
            val name =binding.regname.text.toString()
            val phonenumber =binding.regphone.text.toString()
            val dob =binding.regdob.text.toString()
            val gender = when(findViewById<RadioGroup>(R.id.gender_radio_group).checkedRadioButtonId){
                R.id.regmale -> "Male"
                R.id.regfemale -> "Female"
                R.id.regother -> "Other"
                else -> null
            }

            if(password == confirmpassword)
            {
                auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this){
                    authTask ->
                    if (authTask.isSuccessful) {
                        Toast.makeText(this,"Account Created", Toast.LENGTH_SHORT).show()
                        val userId = auth.currentUser?.uid

                        val user = User(name,email, password,phonenumber, dob, gender,"")

                        userId?.let {
                            database.collection("users").document(it).set(user)
                                .addOnSuccessListener {
                                    Toast.makeText(this,"Data stored successfully", Toast.LENGTH_SHORT).show()
                                }
                                .addOnFailureListener{
                                    e-> Toast.makeText(this,e.message.toString(), Toast.LENGTH_SHORT).show()
                                }
                        }
                    }else {
                        // Account creation failed
                        Toast.makeText(this, "Account creation failed: ${authTask.exception?.message}", Toast.LENGTH_SHORT).show()
                    }

                }
            }
            else{
                Toast.makeText(this,"Password didn't match", Toast.LENGTH_SHORT).show()
            }
        }

        binding.clickhere.setOnClickListener{
            startActivity(Intent(this,LoginActivity::class.java))
        }
    }

}