package com.arshcreations.careerflow.ui.splash

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.arshcreations.careerflow.R
import com.google.firebase.auth.FirebaseAuth

class SplashFragment : Fragment(R.layout.fragment_splash) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.postDelayed({ navigateToNextScreen() }, 800) // small delay for splash
    }

    private fun navigateToNextScreen() {
        if (!isAdded) return

        val navController = findNavController()
        val navOptions = NavOptions.Builder()
            .setPopUpTo(R.id.nav_graph, true) // clear splash from back stack
            .build()

        val dest = if (FirebaseAuth.getInstance().currentUser != null) {
            R.id.main_graph
        } else {
            R.id.auth_graph
        }

        navController.navigate(dest, null, navOptions)
    }
}
