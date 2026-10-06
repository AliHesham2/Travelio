package com.aly.travelio.ui.splash

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.core.splashscreen.SplashScreen
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.aly.travelio.R
import com.aly.travelio.base.BaseActivity
import com.aly.travelio.ui.dashboard.main.DashBoardActivity
import com.aly.travelio.ui.registration.RegistrationActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : BaseActivity() {
    var keepScreen = true
    private val viewModel: SplashViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        splashScreen.setKeepOnScreenCondition { keepScreen }
        super.onCreate(savedInstanceState)
        lifecycleScope.launch { delay(1500);keepScreen = false }
        splashScreen.setOnExitAnimationListener { splashProvider ->
            splashProvider.remove()
            if (viewModel.isUserAuthExist()) { navigateToDashBoard() } else { navigateToRegistration() }
        }
    }

    private fun init(){
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun navigateToRegistration(){
        val intent = Intent(this, RegistrationActivity::class.java)
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        startActivity(intent)
//        @Suppress("DEPRECATION")
//        overridePendingTransition(0, 0)
        finish()
    }

    private fun navigateToDashBoard(){
        val intent = Intent(this, DashBoardActivity::class.java)
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        startActivity(intent)
//        @Suppress("DEPRECATION")
//        overridePendingTransition(0, 0)
        finish()
    }
}