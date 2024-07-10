package com.duelisto

import android.content.Intent
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.ImageView
import android.widget.PopupMenu
import android.widget.PopupWindow
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.duelisto.databinding.ActivityHomeBinding
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.squareup.picasso.Picasso

class HomeActivity : AppCompatActivity() {
    private lateinit var auth : FirebaseAuth
    private lateinit var binding: ActivityHomeBinding
    private lateinit var popupWindow: PopupWindow
    private lateinit var database : FirebaseFirestore
    private lateinit var googleSignInClient: GoogleSignInClient
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        auth = FirebaseAuth.getInstance()
        database = FirebaseFirestore.getInstance()

        binding.navprofileimg.setOnClickListener{
            showSlideMenu()
        }
        binding.optionsMenu.setOnClickListener{
            view-> showPopupMenu(view)
        }

        val userId = auth.currentUser!!.uid
        val userRef = database.collection("users").document(userId)
        userRef.get()
            .addOnSuccessListener { document ->
                if (document != null && document.exists()) {
                    val imgUrl = document.getString("imageUrl") ?: "https://www.kindpng.com/picc/m/24-248253_user-profile-default-image-png-clipart-png-download.png"
                    Picasso.get()
                        .load(imgUrl)
                        .into(binding.navprofileimg)
                }
            }

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(this, gso)
    }


    private fun showSlideMenu() {
        val slideMenuView = LayoutInflater.from(this).inflate(R.layout.activity_slidemenu, null)
        val userId = auth.currentUser!!.uid
        val userRef = database.collection("users").document(userId)
        userRef.get()
            .addOnSuccessListener { document ->
                if (document != null && document.exists()) {
                    val name = document.getString("name").orEmpty()
                    val imgUrl = document.getString("imageUrl") ?: "https://www.kindpng.com/picc/m/24-248253_user-profile-default-image-png-clipart-png-download.png"

                    slideMenuView.findViewById<TextView>(R.id.usernameslidemenu).text = name
                    Picasso.get()
                        .load(imgUrl)
                        .into(slideMenuView.findViewById<ImageView>(R.id.profileimageslidemenu))
                }
            }
        slideMenuView.findViewById<Button>(R.id.profilebutton).setOnClickListener{
            dismissSlideMenuWithAnimation{
                startActivity(Intent(this, ProfileActivity::class.java))
            }
        }

        slideMenuView.findViewById<Button>(R.id.logoutbutton).setOnClickListener{
            auth.signOut()
            googleSignInClient.signOut().addOnCompleteListener {
                dismissSlideMenuWithAnimation {
                    startActivity(Intent(this, LoginActivity::class.java))
                    finish()
                }
            }
        }

        popupWindow = PopupWindow(slideMenuView, 250.dpToPx(), ViewGroup.LayoutParams.MATCH_PARENT, true)
        popupWindow.isOutsideTouchable = true
        popupWindow.elevation = 10f

        popupWindow.animationStyle = R.style.PopupAnimation

        popupWindow.showAtLocation(slideMenuView, Gravity.START, 0, 0)
    }

    private fun Int.dpToPx(): Int{
        val scale = resources.displayMetrics.density
        return(this * scale + 0.5f).toInt()
    }

    private fun dismissSlideMenuWithAnimation(onDismissed: (() -> Unit)? = null) {
        val slideOutAnimation = AnimationUtils.loadAnimation(this, R.anim.slide_out_to_left)
        slideOutAnimation.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation?) {}

            override fun onAnimationEnd(animation: Animation?) {
                popupWindow.dismiss()
                onDismissed?.invoke()
            }

            override fun onAnimationRepeat(animation: Animation?) {}
        })
        popupWindow.contentView?.startAnimation(slideOutAnimation)
    }

    private fun showPopupMenu(view: View) {
        val popupMenu = PopupMenu(this, view)
        popupMenu.menuInflater.inflate(R.menu.popup_menu, popupMenu.menu)
        popupMenu.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.menu_settings -> {
                    // Handle Settings click
                    true
                }
                R.id.menu_about_us -> {
                    // Handle About Us click
                    true
                }
                R.id.menu_exit -> {
                    finishAffinity()
                    System.exit(0)
                    true
                }
                else -> false
            }
        }
        popupMenu.show()
    }
}
