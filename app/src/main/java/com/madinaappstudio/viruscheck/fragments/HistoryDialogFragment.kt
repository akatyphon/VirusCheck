package com.madinaappstudio.viruscheck.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.madinaappstudio.viruscheck.R
import com.madinaappstudio.viruscheck.adaptes.HistoryDialogAdapter
import com.madinaappstudio.viruscheck.databinding.FragmentHistoryDialogBinding

class HistoryDialogFragment : DialogFragment() {

    private var _binding: FragmentHistoryDialogBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.Theme_VirusCheck_DialogFragment)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHistoryDialogBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.tbHistoryNavIcon.setNavigationOnClickListener {
            val dialog = dialog
            if (dialog != null) {
                if (dialog.isShowing) {
                    dialog.dismiss()
                }
            }
        }

        val list = listOf(
            "file name 1",
            "file name 2",
            "example.com 3",
            "example.com 4",
            "file name 5",
            "example.com 6",
            "file name 7",
            "file name 8",
            "url address 9",
            "file name 10",
            "file name 11"
        )

        val adapter = HistoryDialogAdapter(list)
        binding.rvHistoryMain.layoutManager = LinearLayoutManager(requireContext())
        binding.rvHistoryMain.adapter = adapter
    }

    companion object {
        fun newInstance(): HistoryDialogFragment {
            return HistoryDialogFragment()
        }
    }

}