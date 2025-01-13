package com.madinaappstudio.viruscheck

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.madinaappstudio.viruscheck.databinding.ActivityMainBinding
import com.madinaappstudio.viruscheck.onboarding.RegisterUserFragment
import com.madinaappstudio.viruscheck.onboarding.VerifyUserFragment
import com.madinaappstudio.viruscheck.utils.ProgressLoading
import com.madinaappstudio.viruscheck.utils.SharedPreference

class MainActivity : AppCompatActivity(), VerifyUserFragment.VerificationListener {

    private lateinit var binding: ActivityMainBinding
    private lateinit var sharedPreference: SharedPreference
    private lateinit var progressLoading: ProgressLoading

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        progressLoading = ProgressLoading(this@MainActivity)
        sharedPreference = SharedPreference(this@MainActivity)

        progressLoading.show()

        val isUserLogged = sharedPreference.isUserLoggedIn()

        if (isUserLogged) {
            Handler(Looper.getMainLooper())
                .postDelayed({
                    startActivity(Intent(this@MainActivity, HomeActivity::class.java))
                    finishAffinity()
                }, 1000)
        } else {
            progressLoading.hide()
            loadFragment(VerifyUserFragment())
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