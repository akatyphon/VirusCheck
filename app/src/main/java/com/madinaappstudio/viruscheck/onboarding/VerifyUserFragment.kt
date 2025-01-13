package com.madinaappstudio.viruscheck.onboarding

import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import com.madinaappstudio.viruscheck.R
import com.madinaappstudio.viruscheck.databinding.FragmentVerifyUserBinding
import com.madinaappstudio.viruscheck.utils.SharedPreference
import com.madinaappstudio.viruscheck.utils.USER_NODE
import com.madinaappstudio.viruscheck.utils.generateUUID

class VerifyUserFragment : Fragment() {

    private var _binding: FragmentVerifyUserBinding? = null
    private val binding get() = _binding!!
    private var listener: VerificationListener? = null
    private lateinit var preference: SharedPreference

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is VerificationListener) {
            listener = context
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentVerifyUserBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        preference = SharedPreference(requireContext())

        verifyUser()

    }

    private fun verifyUser(){
        val userId = generateUUID()
        val handler = Handler(Looper.getMainLooper())

        val collection = Firebase.firestore.collection(USER_NODE)
        collection.document(userId).get()
            .addOnSuccessListener {
                if (it.exists()) {
                    changeView(0)
                    preference.saveUserSession(true, userId)
                    handler.postDelayed({
                        listener?.onVerifySuccess()
                    }, 2000)
                } else {
                    changeView(1)
                    handler.postDelayed({
                        listener?.onVerifyFail()
                    }, 2000)
                }
            }
            .addOnFailureListener {
                changeView(2)
            }
    }

    private fun changeView(flag: Int) {
        when (flag) {
            0 -> {
                binding.pbVerifyUserC.visibility = View.GONE
                binding.ivVerifyUserImg.visibility = View.VISIBLE
                binding.ivVerifyUserImg.setImageResource(R.drawable.ic_success)
                binding.tvVerifyUserStatus.text = "Verification Complete"
                binding.tvVerifyUserMsg.text = "Existing user found!"
            }

            1 -> {
                binding.pbVerifyUserC.visibility = View.GONE
                binding.ivVerifyUserImg.visibility = View.VISIBLE
                binding.ivVerifyUserImg.setImageResource(R.drawable.ic_no_account)
                binding.tvVerifyUserStatus.text = "Verification Complete"
                binding.tvVerifyUserMsg.text = "User not found!"
            }

            2 -> {
                binding.pbVerifyUserC.visibility = View.GONE
                binding.ivVerifyUserImg.visibility = View.VISIBLE
                binding.ivVerifyUserImg.setImageResource(R.drawable.ic_error)
                binding.tvVerifyUserStatus.text = "Verification Failed"
                binding.tvVerifyUserMsg.text = "Internal error try again later!"
            }
        }
    }

    interface VerificationListener {
        fun onVerifySuccess()
        fun onVerifyFail()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }
}