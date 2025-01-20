package com.madinaappstudio.viruscheck.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import com.google.firebase.firestore.getField
import com.madinaappstudio.viruscheck.R
import com.madinaappstudio.viruscheck.databinding.FragmentScanResultBinding
import com.madinaappstudio.viruscheck.models.FileReportResponse
import com.madinaappstudio.viruscheck.models.StatsModel
import com.madinaappstudio.viruscheck.models.UrlScanReportResponse
import com.madinaappstudio.viruscheck.utils.SharedPreference
import com.madinaappstudio.viruscheck.utils.USER_NODE
import com.madinaappstudio.viruscheck.utils.setLog
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter


class ScanResultFragment : Fragment() {

    private var _binding: FragmentScanResultBinding? = null
    private val binding get() = _binding!!
    private lateinit var preference: SharedPreference

    private val args: ScanResultFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentScanResultBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val scanReport = args.scanReport
        preference = SharedPreference(requireContext())

        binding.mtScanResultToolbar.setNavigationOnClickListener {
            findNavController().navigate(R.id.actionScanResultToScan, Bundle())
        }

        if (scanReport.fileReportResponse != null) {
            val attr = scanReport.fileReportResponse.fileData.fileAttributes
            val status = if (attr.lastAnalysisStats?.malicious != 0){
                2
            } else if (attr.lastAnalysisStats.suspicious != 0) {
                3
            } else {
                1
            }
            val model = getStatsModel(attr.typeExtension, status)
            saveStats(model)
            binding.mtScanResultToolbar.title = "File Scan Result"
            bindFileData(scanReport.fileReportResponse)
        } else {
            val stats = scanReport.urlScanReportResponse!!.urlData.urlAttributes!!.lastAnalysisStats!!
            val status = if (stats.malicious != 0) {
                2
            } else if (stats.suspicious != 0) {
                3
            } else {
                1
            }
            when(status){
                1 -> saveStats(StatsModel(totalScan = 1, url = 1, clean = 1))
                2 -> saveStats(StatsModel(totalScan = 1, url = 1, malicious = 1))
                3 -> saveStats(StatsModel(totalScan = 1, url = 1, suspicious = 1))
            }
            binding.mtScanResultToolbar.title = "URL Scan Result"
            bindUrlData(scanReport.urlScanReportResponse)
        }

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            findNavController().navigate(R.id.actionScanResultToScan, Bundle())
        }

    }


    private fun bindFileData(fileReportResponse: FileReportResponse) {
        val attribute = fileReportResponse.fileData.fileAttributes
        val stats = attribute.lastAnalysisStats!!

        binding.tvScanResultFirstSub.text = formatDate(attribute.firstSubmissionDate!!)
        binding.tvScanResultLastSub.text = formatDate(attribute.lastSubmissionDate!!)
        binding.tvScanResultLastAnalysis.text = formatDate(attribute.lastAnalysisDate!!)

        setUndetected(stats.undetected)
        setMalicious(stats.malicious)
        setSuspicious(stats.suspicious)
        setTimeout(stats.timeout)

    }

    private fun bindUrlData(urlScanReportResponse: UrlScanReportResponse) {
        val attribute = urlScanReportResponse.urlData.urlAttributes!!
        val stats = attribute.lastAnalysisStats!!

        binding.tvScanResultFirstSub.text = formatDate(attribute.firstSubmissionDate!!)
        binding.tvScanResultLastSub.text = formatDate(attribute.lastSubmissionDate!!)
        binding.tvScanResultLastAnalysis.text = formatDate(attribute.lastAnalysisDate!!)

        setUndetected(stats.undetected)
        setMalicious(stats.malicious)
        setSuspicious(stats.suspicious)
        setTimeout(stats.timeout)
    }

    private fun setUndetected(count: Int) {
        val viewUndetected = binding.viewScanResultUndetected
        viewUndetected.cvScanResultMain.setCardBackgroundColor(
                requireContext().resources.getColor(
                    R.color.bg_undetected, null
                )
            )
        viewUndetected.tvScanResultTitle.text = "Undetected"
        viewUndetected.ivScanResultIcon.setImageResource(R.drawable.ic_undetected)
        viewUndetected.tvScanResultCount.text = getString(R.string.antivirus_scan_result, count)
    }

    private fun setMalicious(count: Int) {
        val viewUndetected = binding.viewScanResultMalicious
        viewUndetected.cvScanResultMain.setCardBackgroundColor(
                requireContext().resources.getColor(
                    R.color.bg_malicious,
                    null
                )
            )
        viewUndetected.tvScanResultTitle.text = "Malicious"
        viewUndetected.ivScanResultIcon.setImageResource(R.drawable.ic_malicious)
        viewUndetected.tvScanResultCount.text = getString(R.string.antivirus_scan_result, count)
    }

    private fun setSuspicious(count: Int) {
        val viewUndetected = binding.viewScanResultSuspicious
        viewUndetected.cvScanResultMain.setCardBackgroundColor(
                requireContext().resources.getColor(
                    R.color.bg_suspicious, null
                )
            )
        viewUndetected.tvScanResultTitle.text = "Suspicious"
        viewUndetected.ivScanResultIcon.setImageResource(R.drawable.is_suspicious)
        viewUndetected.tvScanResultCount.text = getString(R.string.antivirus_scan_result, count)
    }

    private fun setTimeout(count: Int) {
        val viewUndetected = binding.viewScanResultTimeout
        viewUndetected.cvScanResultMain.setCardBackgroundColor(
                requireContext().resources.getColor(
                    R.color.bg_timeout,
                    null
                )
            )
        viewUndetected.tvScanResultTitle.text = "Timeout"
        viewUndetected.ivScanResultIcon.setImageResource(R.drawable.ic_timeout)
        viewUndetected.tvScanResultCount.text = getString(R.string.antivirus_scan_result, count)
    }

    private fun formatDate(second: Long): String {
        val formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy").withZone(ZoneId.systemDefault())
        return formatter.format(Instant.ofEpochSecond(second))
    }

    private fun saveStats(currentStats: StatsModel){
        Firebase.firestore.collection(USER_NODE).document(preference.getUserId()!!).get()
            .addOnSuccessListener {
                val oldStats = it.getField<StatsModel>("stats")
                setLog(oldStats)
                val newStats: StatsModel = if (oldStats != null){
                    mergeStatsModels(currentStats, oldStats)
                } else {
                    currentStats
                }
                Firebase.firestore.collection(USER_NODE).document(preference.getUserId()!!).update(
                    mapOf("stats" to newStats)
                )
            }
            .addOnFailureListener {
                setLog(it.message)
            }
    }

    private fun mergeStatsModels(model1: StatsModel, model2: StatsModel): StatsModel {
        return StatsModel(
            totalScan = model1.totalScan + model2.totalScan,
            clean = model1.clean + model2.clean,
            malicious = model1.malicious + model2.malicious,
            suspicious = model1.suspicious + model2.suspicious,
            file = model1.file + model2.file,
            url = model1.url + model2.url,
            apk = model1.apk + model2.apk,
            exe = model1.exe + model2.exe,
            document = model1.document + model2.document,
            archive = model1.archive + model2.archive,
            other = model1.other + model2.other
        )
    }


    private fun getStatsModel(fileType: String?, status: Int) : StatsModel {
        val statsModel = StatsModel()
        statsModel.totalScan = 1
        statsModel.file = 1
        when(fileType){
            "apk" -> {
                statsModel.apk = 1
            }
            "exe" -> {
                statsModel.exe = 1
            }
            "zip", "gz", "7z" -> {
                statsModel.archive = 1
            }
            "pdf", "docx", "txt" -> {
                statsModel.document = 1
            }
            else -> {
                statsModel.other = 1
            }
        }

        when(status){
            1 -> {
               statsModel.clean = 1
            }
            2 -> {
                statsModel.malicious = 1
            }
            3 -> {
                statsModel.suspicious = 1
            }
        }

        return statsModel

    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}