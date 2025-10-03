package com.arshcreations.careerflow

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.arshcreations.careerflow.databinding.ActivityMainBinding
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController
    private var lastClickTime = 0L
    private val clickDelay = 300L
    private var isNavigating = false

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupNavigation()
        setupBackPressHandler()
    }

    private fun setupNavigation() {
        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController

        setupBottomNavigationWithDebounce()
        setupNavigationListener()
    }

    private fun setupBottomNavigationWithDebounce() {
        binding.bottomNavView.setOnItemSelectedListener { item ->
            val currentTime = System.currentTimeMillis()

            // Prevent rapid clicks
            if (currentTime - lastClickTime < clickDelay) {
                Log.d(TAG, "Navigation click ignored (debounce)")
                return@setOnItemSelectedListener false
            }

            if (isNavigating) {
                Log.d(TAG, "Navigation in progress, ignoring click")
                return@setOnItemSelectedListener false
            }

            lastClickTime = currentTime

            try {
                if (navController.currentDestination?.id == item.itemId) {
                    Log.d(TAG, "Already on destination: ${item.itemId}")
                    return@setOnItemSelectedListener true
                }
                isNavigating = true
                navController.navigate(item.itemId)
                Log.d(TAG, "Navigated to: ${item.itemId}")

                // Reset navigation flag after delay
                lifecycleScope.launch {
                    delay(500)
                    isNavigating = false
                }

                true
            } catch (e: IllegalArgumentException) {
                isNavigating = false
                Log.e(TAG, "Navigation failed: Invalid destination ${item.itemId}", e)
                false
            } catch (e: Exception) {
                isNavigating = false
                Log.e(TAG, "Navigation failed: ${e.message}", e)
                false
            }
        }
    }

    private fun setupNavigationListener() {
        navController.addOnDestinationChangedListener { _, destination, _ ->
            isNavigating = false

            val shouldShowBottomNav = when (destination.id) {
                R.id.homeFragment,
                R.id.collegesFragment,
                R.id.aptitudeFragment,
                R.id.careerGuideFragment,
                R.id.profileFragment -> true
                else -> false
            }

            binding.bottomNavView.visibility = if (shouldShowBottomNav) {
                View.VISIBLE
            } else {
                View.GONE
            }

            Log.d(TAG, "Destination changed to: ${destination.label}")
        }
    }

    private fun setupBackPressHandler() {
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (isNavigating) {
                    Log.d(TAG, "Back press ignored (navigation in progress)")
                    return
                }

                if (!navController.popBackStack()) {
                    finish()
                }
            }
        })
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp() || super.onSupportNavigateUp()
    }

    override fun onPause() {
        super.onPause()
        isNavigating = false
    }

    companion object {
        private const val TAG = "MainActivity"
    }
}
