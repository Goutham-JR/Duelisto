package com.duelisto

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.duelisto.databinding.ActivityLoginBinding
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FirebaseFirestore


class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var database : FirebaseFirestore
    private lateinit var googlesignin : GoogleSignInClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()
        database = FirebaseFirestore.getInstance()

        //Google Authentication
        binding.googleimg.setOnClickListener{
            signInWithGoogle()
        }
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        googlesignin = GoogleSignIn.getClient(this, gso)


        val checkUser = auth.currentUser?.uid
        if(checkUser != null)
        {
            Toast.makeText(this,"Login successfully", Toast.LENGTH_SHORT).show()
            startActivity(Intent(this, HomeActivity::class.java))
            finish()
        }

        binding.loginbtn.setOnClickListener {
            val email = binding.loginemail.text.toString()
            val password = binding.loginpassword.text.toString()

            if (email.isEmpty() || password.isEmpty())
            {
                Toast.makeText(this,"Fields are empty", Toast.LENGTH_SHORT).show()
            }else{
                auth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener { authTask ->
                        if (authTask.isSuccessful) {
                            Toast.makeText(this,"Login successfully", Toast.LENGTH_SHORT).show()
                            startActivity(Intent(this, HomeActivity::class.java))
                            finish()
                        } else {
                            Toast.makeText(this,"Login failed: ${authTask.exception?.message}", Toast.LENGTH_SHORT).show()
                        }
                    }
            }
        }

        binding.signupbtn.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }
    }

    private fun signInWithGoogle()
    {
        val signInIntent = googlesignin.signInIntent
        launcer.launch(signInIntent)
    }

    private val launcer = registerForActivityResult(ActivityResultContracts.StartActivityForResult())
    {
        result ->
        if (result.resultCode == Activity.RESULT_OK)
        {
            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            handleResults(task)
        }
    }

    private fun handleResults(task: Task<GoogleSignInAccount>) {
        try {
            val account: GoogleSignInAccount? = task.getResult(Exception::class.java)
            if (account != null) {
                updateUI(account)
            }
        } catch (e: Exception) {
            Toast.makeText(this, e.message, Toast.LENGTH_SHORT).show()
        }
    }

    private  fun updateUI(account : GoogleSignInAccount)
    {
        val credential = GoogleAuthProvider.getCredential(account.idToken, null)
        auth.signInWithCredential(credential).addOnCompleteListener{ task->
            if (task.isSuccessful) {
                val userId = auth.currentUser?.uid
                if (userId != null) {
                    val userRef = database.collection("users").document(userId)
                    userRef.get()
                        .addOnSuccessListener { document ->
                            if (document != null && document.exists()) {
                                Toast.makeText(this,"Login successfully", Toast.LENGTH_SHORT).show()
                                startActivity(Intent(this, HomeActivity::class.java))
                                finish()
                            }
                            else{
                                val user = User(account.displayName,account.email, "","", "", "", account.photoUrl.toString())
                                userId.let {
                                    database.collection("users").document(it).set(user)
                                        .addOnSuccessListener {
                                            Toast.makeText(this,"Data stored successfully", Toast.LENGTH_SHORT).show()
                                        }
                                        .addOnFailureListener{
                                                e-> Toast.makeText(this,e.message.toString(), Toast.LENGTH_SHORT).show()
                                        }
                                }
                                Toast.makeText(this,"Login successfully", Toast.LENGTH_SHORT).show()
                                startActivity(Intent(this, HomeActivity::class.java))
                                finish()
                            }
                        }
                }

            } else {
                Toast.makeText(this,"Login failed: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

}
