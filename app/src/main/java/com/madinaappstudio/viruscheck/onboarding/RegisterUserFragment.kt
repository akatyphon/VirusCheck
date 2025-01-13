package com.madinaappstudio.viruscheck.onboarding

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import com.madinaappstudio.viruscheck.HomeActivity
import com.madinaappstudio.viruscheck.databinding.FragmentRegisterUserBinding
import com.madinaappstudio.viruscheck.utils.ProgressLoading
import com.madinaappstudio.viruscheck.utils.SharedPreference
import com.madinaappstudio.viruscheck.utils.USER_NODE
import com.madinaappstudio.viruscheck.utils.getHardwareId
import com.madinaappstudio.viruscheck.utils.showToast

class RegisterUserFragment : Fragment() {

    private var _binding: FragmentRegisterUserBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRegisterUserBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val userId = getHardwareId()
        val progressLoading = ProgressLoading(requireContext())

        binding.btnRegisterUser.setOnClickListener {
            val name = binding.etRegisterUserName.text.toString().trim()
            if (name.isNotEmpty()) {
                progressLoading.show()
                Firebase.firestore.collection(USER_NODE).document(userId)
                    .set(mapOf("userId" to userId, "name" to name))
                    .addOnSuccessListener {
                        SharedPreference(requireContext())
                            .saveUserSession(true, userId)
                        showToast(requireContext(), "Success")
                        startActivity(Intent(requireContext(), HomeActivity::class.java))
                        requireActivity().finishAffinity()
                    }
                    .addOnFailureListener {
                        progressLoading.hide()
                        showToast(requireContext(), "Failed to register user")
                    }
            } else {
                showToast(requireContext(), "Please enter name")
            }
        }

    }


    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}