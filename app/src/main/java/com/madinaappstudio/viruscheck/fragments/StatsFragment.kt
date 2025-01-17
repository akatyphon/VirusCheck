package com.madinaappstudio.viruscheck.fragments

import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import com.google.firebase.firestore.getField
import com.madinaappstudio.viruscheck.R
import com.madinaappstudio.viruscheck.databinding.FragmentStatsBinding
import com.madinaappstudio.viruscheck.models.StatsModel
import com.madinaappstudio.viruscheck.utils.ProgressLoading
import com.madinaappstudio.viruscheck.utils.SharedPreference
import com.madinaappstudio.viruscheck.utils.USER_NODE
import com.madinaappstudio.viruscheck.utils.showToast
import java.text.Format
import java.util.Locale

class StatsFragment : Fragment() {

    private var _binding: FragmentStatsBinding? = null
    private val binding get() = _binding!!
    private lateinit var progressLoading: ProgressLoading

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentStatsBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        progressLoading = ProgressLoading(requireContext(), 0.9f)
        progressLoading.show()
        loadStatsData()
    }

    private fun loadStatsData(){
        progressLoading.show()
        val userId = SharedPreference(requireContext()).getUserId()!!
        Firebase.firestore.collection(USER_NODE).document(userId).get()
            .addOnSuccessListener {
                val stats = it.getField<StatsModel>("stats")
                if (stats != null) {
                    bindViewData(stats)
                } else {
                    progressLoading.hide()
                    showToast(requireContext(), "No Record!")
                }
            }
            .addOnFailureListener {
                progressLoading.hide()
                showToast(requireContext(), "Failed to load records.")
            }
    }

    private fun bindViewData(statsModel: StatsModel){
        setTotalScan(statsModel.totalScan)
        setClean(statsModel.clean)
        setMalicious(statsModel.malicious)
        setSuspicious(statsModel.suspicious)

        binding.tvStatsFile.text = "${statsModel.file}"
        binding.tvStatsUrl.text = "${statsModel.url}"
        binding.tvStatsApk.text = "${statsModel.apk}"
        binding.tvStatsArchive.text = "${statsModel.archive}"
        binding.tvStatsDocument.text = "${statsModel.document}"
        binding.tvStatsOther.text = "${statsModel.other}"

        progressLoading.hide()
    }

    private fun setTotalScan(count: Int) {
        val viewUndetected = binding.viewStatsTotalScan
        viewUndetected.tvStatsScanCount.setTextColor(
            Color.parseColor("#1B9AF5")
        )
        viewUndetected.tvStatsScanTitle.text = "Total Scan"
        viewUndetected.tvStatsScanCount.text = "$count"
    }

    private fun setClean(count: Int) {
        val viewUndetected = binding.viewStatsClean
        viewUndetected.tvStatsScanCount.setTextColor(
            Color.parseColor("#22C55E")
        )
        viewUndetected.tvStatsScanTitle.text = "Clean"
        viewUndetected.tvStatsScanCount.text = "$count"
    }

    private fun setMalicious(count: Int) {
        val viewUndetected = binding.viewStatsMalicious
        viewUndetected.tvStatsScanCount.setTextColor(
            Color.parseColor("#EF4444")
        )
        viewUndetected.tvStatsScanTitle.text = "Malicious"
        viewUndetected.tvStatsScanCount.text = "$count"
    }

    private fun setSuspicious(count: Int) {
        val viewUndetected = binding.viewStatsSuspicious
        viewUndetected.tvStatsScanCount.setTextColor(
            Color.parseColor("#EAB308")
        )
        viewUndetected.tvStatsScanTitle.text = "Suspicious"
        viewUndetected.tvStatsScanCount.text = "$count"
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}