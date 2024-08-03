package com.bics.expense.receptionistmodule.splash

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import com.bics.expense.receptionistmodule.R
import com.bics.expense.receptionistmodule.databinding.ActivityMainBinding
import com.bics.expense.receptionistmodule.databinding.ActivitySplashBinding
import com.bics.expense.receptionistmodule.login.MainActivity


class SplashActivity : AppCompatActivity() {

    lateinit var binding: ActivitySplashBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_splash)

        val splashTimeOut: Long = 1000
        val homeIntent = Intent(this@SplashActivity, MainActivity::class.java)

        android.os.Handler().postDelayed({
            startActivity(homeIntent)
            finish()
        }, splashTimeOut)
    }

}
