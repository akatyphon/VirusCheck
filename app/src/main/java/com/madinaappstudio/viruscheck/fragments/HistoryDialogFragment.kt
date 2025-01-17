package com.madinaappstudio.viruscheck.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.navigation
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.madinaappstudio.viruscheck.R
import com.madinaappstudio.viruscheck.adaptes.HistoryDialogAdapter
import com.madinaappstudio.viruscheck.database.HistoryDao
import com.madinaappstudio.viruscheck.database.HistoryDatabase
import com.madinaappstudio.viruscheck.databinding.FragmentHistoryDialogBinding
import com.madinaappstudio.viruscheck.models.HistoryViewModel
import com.madinaappstudio.viruscheck.utils.setLog

class HistoryDialogFragment : DialogFragment(), HistoryDialogAdapter.OnHistoryClickListener {

    private var _binding: FragmentHistoryDialogBinding? = null
    private val binding get() = _binding!!
    private lateinit var historyViewModel: HistoryViewModel
    private lateinit var adapter: HistoryDialogAdapter

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
            findNavController().popBackStack()
        }

        binding.tbHistoryNavIcon.setOnMenuItemClickListener {
            val itemId = it.itemId

            when(itemId){
                R.id.btnHistoryTDeleteAll -> {
                    historyDeleteConfirmation()
                    true
                }
                R.id.btnHistoryTMenuInfo -> {
                    showInfoDialog()
                    true
                }
                else -> false
            }
        }

        val historyDao: HistoryDao = HistoryDatabase.getInstance(requireContext()).historyDao()
        val historyViewModelFactory = HistoryViewModel.HistoryViewModelFactory(historyDao)
        historyViewModel = viewModels<HistoryViewModel> { historyViewModelFactory }.value

        adapter = HistoryDialogAdapter(mutableListOf(), historyViewModel, this@HistoryDialogFragment)
        binding.rvHistoryMain.addItemDecoration(
            DividerItemDecoration(requireContext(), LinearLayoutManager.VERTICAL)
        )

        binding.rvHistoryMain.layoutManager = LinearLayoutManager(requireContext())
        binding.rvHistoryMain.adapter = adapter

        historyViewModel.getAllHistory().observe(viewLifecycleOwner) { historyList ->
            if (historyList != null) {
                adapter.updateData(historyList)
            }
        }

    }

    private fun historyDeleteConfirmation() {
        val dialog = MaterialAlertDialogBuilder(requireContext()).apply {
            setMessage("Are you want to delete all history.")
            setNegativeButton("No"
            ) { dialog, _ ->
                dialog.dismiss()
            }
            setPositiveButton("yes"
            ) { dialog, _ ->
                historyViewModel.deleteAllHistory()
                adapter.updateData(emptyList())
                dialog.dismiss()
            }
        }
        dialog.show()

    }

    private fun showInfoDialog() {
        val dialog = MaterialAlertDialogBuilder(requireContext()).apply {
            setMessage("All the history records only saved in device.")
        }
        dialog.show()
    }

    companion object {
        fun newInstance(): HistoryDialogFragment {
            return HistoryDialogFragment()
        }
    }

    override fun onHistoryClicked(array: Array<String>) {
        val action = HistoryDialogFragmentDirections.actionHistoryDialogToScan(array)
        findNavController().navigate(action)
    }

}