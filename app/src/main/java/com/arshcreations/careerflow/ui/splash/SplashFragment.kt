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
import androidx.navigation.fragment.findNavController
import com.arshcreations.careerflow.R
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SplashFragment : Fragment(R.layout.fragment_splash) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val careerTextView = view.findViewById<TextView>(R.id.career_text_view)
        val flowTextView = view.findViewById<TextView>(R.id.flow_text_view)
        val textContainer = view.findViewById<LinearLayout>(R.id.text_container)

        startTypingAnimation(textContainer, careerTextView, flowTextView)
    }

    private fun startTypingAnimation(container: LinearLayout, careerTextView: TextView, flowTextView: TextView) {
        val careerText = "Career"
        val flowText = "Flow"

        flowTextView.post {
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

            careerTextView.text = ""
            flowTextView.text = ""

            lifecycleScope.launch {
                delay(200)

                container.animate()
                    .alpha(1f)
                    .translationY(0f)
                    .setDuration(500)
                    .start()

                delay(500)

                for (i in 1..careerText.length) {
                    careerTextView.text = careerText.substring(0, i)
                    delay(160)
                }

                for (i in 1..flowText.length) {
                    flowTextView.text = flowText.substring(0, i)
                    delay(160)
                }

                delay(1200)
                navigateToNextScreen()
            }
        }
    }

    private fun navigateToNextScreen() {
        if (!isAdded) return

        lifecycleScope.launch {
            val destinationAction = if (FirebaseAuth.getInstance().currentUser != null) {
                // User is logged in, go to the main app
                R.id.action_splashFragment_to_main_graph
            } else {
                // User is not logged in, go to the login/signup flow
                R.id.action_splashFragment_to_auth_graph
            }
            findNavController().navigate(destinationAction)
        }
    }
}