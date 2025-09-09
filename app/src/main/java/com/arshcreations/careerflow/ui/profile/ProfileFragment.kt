package com.arshcreations.careerflow.ui.profile

import android.net.Uri
import android.os.Bundle
import android.text.InputType
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.view.children
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.arshcreations.careerflow.R
import com.arshcreations.careerflow.data.models.User
import com.arshcreations.careerflow.data.models.UserPreferences
import com.arshcreations.careerflow.databinding.FragmentProfileBinding
import com.bumptech.glide.Glide
import com.google.android.material.chip.Chip
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.storage.FirebaseStorage
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore
    private lateinit var storage: FirebaseStorage

    private val pickImageLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let {
            uploadProfilePicture(it)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()
        storage = FirebaseStorage.getInstance()

        setupClickListeners()
        loadUserProfile()
    }

    private fun setupClickListeners() {
        binding.fabEditPicture.setOnClickListener { pickImageLauncher.launch("image/*") }
        binding.etDob.setOnClickListener { showDatePicker() }
        binding.btnSaveProfile.setOnClickListener { saveUserProfileData() }
        binding.btnLogout.setOnClickListener { logoutUser() }

        binding.statCardScore.setOnClickListener {
            showEditDialog("Update Score", binding.tvStatScore.text.toString(), true) { newValue ->
                binding.tvStatScore.text = newValue
            }
        }
        binding.statCardRank.setOnClickListener {
            showEditDialog("Update Rank", binding.tvStatRank.text.toString(), false) { newValue ->
                binding.tvStatRank.text = newValue
            }
        }

        val exams = resources.getStringArray(R.array.entrance_exams)
        binding.tvExamSelect.setOnClickListener {
            showMultiSelectDialog("Select Exams", exams, binding.chipGroupExam)
        }

        val locations = resources.getStringArray(R.array.indian_states)
        binding.tvLocationSelect.setOnClickListener {
            showMultiSelectDialog("Select Locations", locations, binding.chipGroupLocation)
        }

        val streams = resources.getStringArray(R.array.streams_array)
        binding.tvStreamSelect.setOnClickListener {
            showMultiSelectDialog("Select Streams", streams, binding.chipGroupStream)
        }
    }

    private fun loadUserProfile() {
        val currentUser = auth.currentUser
        if (currentUser == null) {
            // User not logged in, handle appropriately
            logoutUser()
            return
        }

        // Step 1: Immediately populate the header with guaranteed info from the auth object.
        populateHeader(currentUser)

        // Step 2: Fetch the rest of the profile (preferences) from Firestore.
        firestore.collection("users").document(currentUser.uid).get()
            .addOnSuccessListener { document ->
                if (document != null && document.exists()) {
                    val user = document.toObject(User::class.java)
                    // We only need to populate the preferences now, the header is already done.
                    user?.preferences?.let { populatePreferences(it) }
                }
            }
            .addOnFailureListener { e ->
                Log.e("ProfileFragment", "Error loading preferences", e)
                Toast.makeText(requireContext(), "Failed to load preferences.", Toast.LENGTH_SHORT).show()
            }
    }

    private fun populateHeader(firebaseUser: FirebaseUser) {
        binding.tvProfileName.text = firebaseUser.displayName ?: "No Name"
        binding.tvProfileEmail.text = firebaseUser.email ?: "No Email"

        val photoUrl = firebaseUser.photoUrl
        if (photoUrl != null) {
            Glide.with(this).load(photoUrl).circleCrop().into(binding.ivProfilePicture)
        } else {
            binding.ivProfilePicture.setImageResource(R.drawable.ic_usr_nav)
        }
    }

    private fun populatePreferences(prefs: UserPreferences) {
        binding.tvStatScore.text = prefs.marks?.toString() ?: "N/A"
        binding.tvStatRank.text = prefs.rank?.toString() ?: "N/A"
        binding.etDob.setText(prefs.dob.ifEmpty { null })

        updateChipGroup(binding.chipGroupExam, prefs.attemptedExams)
        updateChipGroup(binding.chipGroupLocation, prefs.locations)
        updateChipGroup(binding.chipGroupStream, prefs.streams)

        binding.etIncome.setText(prefs.annualIncome?.toString() ?: "")
    }

    private fun saveUserProfileData() {
        val userId = auth.currentUser?.uid ?: return

        val marksText = binding.tvStatScore.text.toString()
        val rankText = binding.tvStatRank.text.toString()

        val preferences = UserPreferences(
            dob = binding.etDob.text.toString(),
            attemptedExams = binding.chipGroupExam.children.map { (it as Chip).text.toString() }.toList(),
            marks = if (marksText == "N/A") null else marksText.toDoubleOrNull(),
            rank = if (rankText == "N/A") null else rankText.toIntOrNull(),
            locations = binding.chipGroupLocation.children.map { (it as Chip).text.toString() }.toList(),
            streams = binding.chipGroupStream.children.map { (it as Chip).text.toString() }.toList(),
            annualIncome = binding.etIncome.text.toString().toLongOrNull()
        )

        val dataToSave = mapOf("preferences" to preferences)
        firestore.collection("users").document(userId).set(dataToSave, SetOptions.merge())
            .addOnSuccessListener { Toast.makeText(requireContext(), "Profile updated successfully!", Toast.LENGTH_SHORT).show() }
            .addOnFailureListener { e -> Toast.makeText(requireContext(), "Error: ${e.message}", Toast.LENGTH_LONG).show() }
    }

    private fun uploadProfilePicture(uri: Uri) {
        val userId = auth.currentUser?.uid ?: return
        val storageRef = storage.reference.child("profile_pictures/$userId")

        Toast.makeText(requireContext(), "Uploading image...", Toast.LENGTH_SHORT).show()
        storageRef.putFile(uri)
            .addOnSuccessListener {
                storageRef.downloadUrl.addOnSuccessListener { downloadUrl ->
                    // Also update the photoUrl in the main Firebase Auth user profile
                    val profileUpdates = com.google.firebase.auth.userProfileChangeRequest {
                        photoUri = downloadUrl
                    }
                    auth.currentUser?.updateProfile(profileUpdates)

                    // And update the URL in Firestore
                    val updates = mapOf("photoUrl" to downloadUrl.toString())
                    firestore.collection("users").document(userId).update(updates)
                        .addOnSuccessListener {
                            Toast.makeText(requireContext(), "Profile picture updated.", Toast.LENGTH_SHORT).show()
                            Glide.with(this).load(downloadUrl).circleCrop().into(binding.ivProfilePicture)
                        }
                }
            }
            .addOnFailureListener { e ->
                Toast.makeText(requireContext(), "Upload failed: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun showMultiSelectDialog(title: String, options: Array<String>, chipGroup: com.google.android.material.chip.ChipGroup) {
        val currentSelections = chipGroup.children.map { (it as Chip).text.toString() }.toList()
        val checkedItems = options.map { currentSelections.contains(it) }.toBooleanArray()

        AlertDialog.Builder(requireContext())
            .setTitle(title)
            .setMultiChoiceItems(options, checkedItems) { _, which, isChecked ->
                checkedItems[which] = isChecked
            }
            .setPositiveButton("OK") { _, _ ->
                val selectedItems = options.filterIndexed { index, _ -> checkedItems[index] }.toList()
                updateChipGroup(chipGroup, selectedItems)
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun updateChipGroup(chipGroup: com.google.android.material.chip.ChipGroup, items: List<String>) {
        chipGroup.removeAllViews()
        for (item in items) {
            val chip = Chip(requireContext()).apply {
                text = item
                isCloseIconVisible = true
                setOnCloseIconClickListener { chipGroup.removeView(this) }
            }
            chipGroup.addView(chip)
        }
    }

    private fun showDatePicker() {
        val datePicker = MaterialDatePicker.Builder.datePicker().setTitleText("Select Date").build()
        datePicker.addOnPositiveButtonClickListener { selection ->
            val calendar = java.util.Calendar.getInstance(TimeZone.getTimeZone("UTC"))
            calendar.timeInMillis = selection
            val format = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            binding.etDob.setText(format.format(calendar.time))
        }
        datePicker.show(childFragmentManager, "DATE_PICKER")
    }

    private fun showEditDialog(title: String, currentValue: String, isNumericDecimal: Boolean, onSave: (String) -> Unit) {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle(title)
        val container = FrameLayout(requireContext())
        val editText = EditText(requireContext()).apply {
            layoutParams = FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT).apply {
                setMargins(48, 16, 48, 0)
            }
            if (currentValue != "N/A") setText(currentValue)
            inputType = if (isNumericDecimal) InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_DECIMAL else InputType.TYPE_CLASS_NUMBER
        }
        container.addView(editText)
        builder.setView(container)
        builder.setPositiveButton("Save") { _, _ -> if (editText.text.isNotEmpty()) onSave(editText.text.toString()) }
        builder.setNegativeButton("Cancel", null)
        builder.show()
    }

    private fun logoutUser() {
        auth.signOut()
        findNavController().navigate(R.id.action_profileFragment_to_auth_graph)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}