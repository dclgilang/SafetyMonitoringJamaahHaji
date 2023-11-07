package id.co.dif.smj.presentation.activity

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.messaging.FirebaseMessaging
import id.co.dif.smj.R
import id.co.dif.smj.base.BaseActivity
import id.co.dif.smj.base.BaseViewModel
import id.co.dif.smj.databinding.ActivitySplashScrennBinding
import id.co.dif.smj.databinding.SplashScreenUkaBinding
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import timber.log.Timber

class SplashScreenActivity : BaseActivity<BaseViewModel, SplashScreenUkaBinding>(), KoinComponent {

    override val layoutResId = R.layout.splash_screen_uka

    private val SPLASH_DELAY: Long = 2000


    override fun onViewBindingCreated(savedInstanceState: Bundle?) {
        FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
            if (!task.isSuccessful) {
                Timber.tag(javaClass.name)
                    .w(task.exception, "Fetching FCM registration token failed")
                showAlert(
                    message = "Failed to initialize app!",
                    value = "There is something wrong while initializing. Check network or reinstall the app!",
                    buttonPrimaryText = "Quit",
                    onButtonPrimaryClicked = { finishAffinity() },
                    cancelable = false,
                    iconId = R.drawable.baseline_highlight_off_24,
                    iconTint = R.color.red
                )
                return@OnCompleteListener
            }
            val token = task.result
            val lastToken = preferences.firebaseToken.value
            Handler(Looper.getMainLooper()).postDelayed({
                if (session?.token_access != null && token == lastToken) {
                    Log.d(TAG, "LoginDebug: token valid")
                    val rememberMe = preferences.rememberMe.value
                    if (rememberMe == true) {
                        startMainScreen()
                    } else {
                        startOnBoardingScreen(token)
                    }
                } else {
                    Log.d(TAG, "LoginDebug: token invalid")
                    startOnBoardingScreen(token)
                }
                Log.d(TAG, "LoginDebug: ${preferences.session.value}")
            }, 3000)
        })

//        binding.looperGroup1.startAnimation(
//            AnimationUtils.loadAnimation(this, R.anim.splash_screen_looper1_rotate)
//        )
//        binding.looperGroup2.startAnimation(
//            AnimationUtils.loadAnimation(this, R.anim.splash_screen_looper2_rotate)
//        )

        val slideUpAnimation = AnimationUtils.loadAnimation(this, R.anim.slide_up)
        val fadeInAnimation = AnimationUtils.loadAnimation(this, R.anim.fade_in)

        slideUpAnimation.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation?) {}
            override fun onAnimationEnd(animation: Animation?) {
                binding.tv.visibility = View.VISIBLE
                binding.tv.startAnimation(fadeInAnimation)
            }
            override fun onAnimationRepeat(animation: Animation?) {}
        })

        binding.iconUka.visibility = View.VISIBLE
        binding.iconUka.startAnimation(slideUpAnimation)

//        binding.iconUka.startAnimation(
//            AnimationUtils.loadAnimation(this, R.anim.splash_screen_title_fade_in)
//        )
//        binding.tv.startAnimation(
//            AnimationUtils.loadAnimation(this, R.anim.splash_screen_title_fade_in)
//        )

//        stopLocationServiceScheduler()

    }

    private fun startOnBoardingScreen(token: String) {
        preferences.wipe()
        preferences.firebaseToken.value = token
        startActivity(Intent(this, OnBoardingActivity::class.java))
        finish()
    }

    private fun startMainScreen() {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }
}