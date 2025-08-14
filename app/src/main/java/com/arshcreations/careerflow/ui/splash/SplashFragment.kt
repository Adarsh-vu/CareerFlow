package com.arshcreations.careerflow.ui.splash

import android.graphics.Color
import android.graphics.LinearGradient
import android.graphics.Shader
import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.arshcreations.careerflow.R
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SplashFragment : Fragment(R.layout.fragment_splash) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Find all the necessary views from the layout
        val careerTextView = view.findViewById<TextView>(R.id.career_text_view)
        val flowTextView = view.findViewById<TextView>(R.id.flow_text_view)
        val textContainer = view.findViewById<LinearLayout>(R.id.text_container)

        // Pass all views to the animation function
        startTypingAnimation(textContainer, careerTextView, flowTextView)
    }

    private fun startTypingAnimation(container: LinearLayout, careerTextView: TextView, flowTextView: TextView) {
        val careerText = "Career"
        val flowText = "Flow"

        flowTextView.post {
            // Set up the gradient for the "Flow" TextView to match the logo
            val paint = flowTextView.paint
            val width = paint.measureText(flowText)
            val shader = LinearGradient(
                0f, 0f, width, flowTextView.textSize,
                intArrayOf(
                    Color.parseColor("#43C6AC"),
                    Color.parseColor("#65C7F7")
                ),
                null,
                Shader.TileMode.CLAMP
            )
            flowTextView.paint.shader = shader

            // Clear the text before starting the animation
            careerTextView.text = ""
            flowTextView.text = ""

            // Launch the animation coroutine
            lifecycleScope.launch {
                // A tiny delay to ensure a smooth start
                delay(200)

                container.animate()
                    .alpha(1f)
                    .translationY(0f)
                    .setDuration(500)
                    .start()

                // Wait for the container to become visible before typing
                delay(500)

                // Type "Career" first in dark blue
                for (i in 1..careerText.length) {
                    careerTextView.text = careerText.substring(0, i)
                    delay(160) // Typing speed
                }

                // Then type "Flow" with the gradient
                for (i in 1..flowText.length) {
                    flowTextView.text = flowText.substring(0, i)
                    delay(160) // Typing speed
                }

                // A final pause after the animation is complete
                delay(1200)
                navigateToNextScreen()
            }
        }
    }

    private fun navigateToNextScreen() {
        if (!isAdded) return

        val navController = findNavController()
        val navOptions = NavOptions.Builder()
            .setPopUpTo(R.id.nav_graph, true)
            .build()

        val destination = if (FirebaseAuth.getInstance().currentUser != null) {
            R.id.main_graph
        } else {
            R.id.auth_graph
        }

        navController.navigate(destination, null, navOptions)
    }
}
