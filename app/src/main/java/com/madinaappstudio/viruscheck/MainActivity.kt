package com.madinaappstudio.viruscheck

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.fragment.app.Fragment
import com.madinaappstudio.viruscheck.databinding.ActivityMainBinding
import com.madinaappstudio.viruscheck.models.SplashViewModel
import com.madinaappstudio.viruscheck.onboarding.RegisterUserFragment
import com.madinaappstudio.viruscheck.onboarding.VerifyUserFragment
import com.madinaappstudio.viruscheck.utils.SharedPreference

class MainActivity : AppCompatActivity(), VerifyUserFragment.VerificationListener {

    private lateinit var binding: ActivityMainBinding
    private lateinit var sharedPreference: SharedPreference
    private val splashViewModel: SplashViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        installSplashScreen().apply {
            setKeepOnScreenCondition {
                splashViewModel.isLoading.value!!
            }
        }
        setContentView(binding.root)
        sharedPreference = SharedPreference(this@MainActivity)
        splashViewModel.isLoading.observe(this) { isLoading ->
            if (!isLoading) {
                if (sharedPreference.isUserLoggedIn()) {
                    startActivity(Intent(this@MainActivity, HomeActivity::class.java))
                    finishAffinity()
                } else {
                    loadFragment(VerifyUserFragment())
                }
            }
        }

    }

    private fun loadFragment(fragment: Fragment) {
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.flMainFrame, fragment)
        transaction.commit()
    }

    override fun onVerifySuccess() {
        startActivity(Intent(this@MainActivity, HomeActivity::class.java))
        finishAffinity()
    }

    override fun onVerifyFail() {
        loadFragment(RegisterUserFragment())
    }
}