package com.madinaappstudio.viruscheck.fragments

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import com.madinaappstudio.viruscheck.BuildConfig
import com.madinaappstudio.viruscheck.MainActivity
import com.madinaappstudio.viruscheck.databinding.FragmentSettingsBinding
import com.madinaappstudio.viruscheck.utils.ProgressLoading
import com.madinaappstudio.viruscheck.utils.SharedPreference
import com.madinaappstudio.viruscheck.utils.USER_NODE
import com.madinaappstudio.viruscheck.utils.generateUUID
import com.madinaappstudio.viruscheck.utils.showToast

class SettingsFragment : Fragment() {

    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!
    private lateinit var progressLoading: ProgressLoading
    private lateinit var preference: SharedPreference

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSettingsBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        progressLoading = ProgressLoading(requireContext())
        preference = SharedPreference(requireContext())

        binding.tvSettingsUserName.text = preference.getUserName()
        binding.tvSettingsUserId.text = preference.getUserId()
        binding.tvSettingsVersion.text = BuildConfig.VERSION_NAME

        binding.btnSettingsNotification.setOnClickListener {
            val intent = Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS)
                .putExtra(Settings.EXTRA_APP_PACKAGE, requireActivity().packageName)
            startActivity(intent)
        }

        binding.btnSettingsSecurity.setOnClickListener {
            pendingDialog()
        }

        binding.btnSettingsPrivacy.setOnClickListener {
            pendingDialog()
        }

        binding.btnSettingSC.setOnClickListener {
            val intent = Intent(
                Intent.ACTION_VIEW,
                Uri.parse("https://github.com/akatyphon/VirusCheck")
            )
            startActivity(intent)
        }

        binding.btnSettingsContributors.setOnClickListener {
            showContributorsDialog()
        }

        binding.btnSettingsDelete.setOnClickListener {
            showConfirmationDialog()
        }

    }

    private fun showContributorsDialog() {
        val sb = StringBuilder()
        sb.append("- UI/UX Designer ")
        sb.append("Ritik Rai IG @whosthisritik")
        sb.append("\n")
        sb.append("- App Built By ")
        sb.append("Me Github @akatyphon")

        val dialog = MaterialAlertDialogBuilder(requireContext()).apply {
            setTitle("This project build with ❤\uFE0F by two friends")
            setMessage(sb.toString())
        }
        dialog.show()
    }

    private fun showConfirmationDialog() {
        val alertDialog = MaterialAlertDialogBuilder(requireContext()).apply {
            setTitle("Account delete!")
            setMessage("Are you sure want delete your account?")
        }
        alertDialog.setPositiveButton("Yes") { dialog, _ ->
            deleteAccount()
            dialog.dismiss()
        }
        alertDialog.setNegativeButton("Cancel") { dialog, _ ->
            dialog.cancel()
        }
        alertDialog.show()

    }

    private fun deleteAccount() {
        progressLoading.show()
        Firebase.firestore.collection(USER_NODE).document(generateUUID()).delete()
            .addOnSuccessListener {
                showToast(requireContext(), "Account delete successfully")
                preference.clearUserSession()
                startActivity(Intent(requireContext(), MainActivity::class.java))
                requireActivity().finish()
            }
            .addOnFailureListener {
                progressLoading.hide()
                showToast(requireContext(), "Failed to delete account, try later!")
            }
    }

    private fun pendingDialog() {
        MaterialAlertDialogBuilder(requireContext()).apply {
            setMessage("This feature currently not implemented!")
            setCancelable(true)
            show()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}