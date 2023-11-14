package com.dreamteam.sharedream

import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.WindowInsets
import android.view.WindowInsetsController
import android.view.WindowManager
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatDelegate
import com.dreamteam.sharedream.databinding.ActivitySplashBinding
import com.google.firebase.auth.FirebaseAuth

class splash : AppCompatActivity() {
    private lateinit var topAnimation: Animation
    private lateinit var bottomAnimation: Animation

    private lateinit var _binding: ActivitySplashBinding
    private val binding get() = _binding

    private val firebaseAuth = FirebaseAuth.getInstance()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

            // 다크모드 제한
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

        _binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val handler = Handler(Looper.getMainLooper())

        setFullScreen()
        topAnimation = AnimationUtils.loadAnimation(this, R.anim.animation)
        bottomAnimation = AnimationUtils.loadAnimation(this, R.anim.animation2)


        if (firebaseAuth.currentUser != null) {
            val intent = Intent(this, LogInActivity::class.java)
            startActivity(intent)
            finish()
        } else {
            handler.postDelayed({
                val intent = Intent(this, LogInActivity::class.java)
                startActivity(intent)
                finish()
            }, 4500)
        }
    }

    fun setFullScreen() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            supportActionBar?.hide()

            window.setDecorFitsSystemWindows(false)
            val controller = window.insetsController
            if (controller != null) {
                controller.hide(WindowInsets.Type.statusBars() or WindowInsets.Type.navigationBars())

                controller.systemBarsBehavior =
                    WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
            }

        } else {
            supportActionBar?.hide()
            window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_IMMERSIVE)


        }
    }
    }


