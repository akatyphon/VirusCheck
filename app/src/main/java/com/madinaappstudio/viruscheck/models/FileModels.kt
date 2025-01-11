package com.madinaappstudio.viruscheck.models

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class ScanResultType(
    val fileReportResponse: FileReportResponse? = null,
): Parcelable

@Parcelize
data class FileUploadResponse(
    val data: Data
) : Parcelable

@Parcelize
data class FileReportResponse(
    val data: Data,
) : Parcelable

@Parcelize
data class Data(
    val type: String? = null,
    val id: String? = null,
    val links: Links,
    val attributes: Attributes
) : Parcelable

@Parcelize
data class Links(
    val self: String? = null,
    val item: String? = null
) : Parcelable

@Parcelize
data class Attributes(
    val names: List<String>? = null,
    val md5: String? = null,
    val magic: String? = null,
    val ssdeep: String? = null,
    @SerializedName("last_analysis_results") val lastAnalysisResult: Map<String, EngineResult>? = null,
    val sha1: String? = null,
    @SerializedName("unique_sources") val uniqueSources: Int? = null,
    @SerializedName("last_analysis_stats") val lastAnalysisStats: Stats,
    val tlsh: String? = null,
    val tags: List<String>? = null,
    val trids: List<Map<String, TridItem>>? = null,
    @SerializedName("last_modification_date") val lastModificationDate: Long? = null,
    @SerializedName("total_votes") val totalVotes: TotalVotes? = null,
    val sha256: String? = null,
    val size: Long? = null,
    @SerializedName("type_description") val typeDescription: String? = null,
    @SerializedName("times_submitted") val timesSubmitted: Int? = null,
    @SerializedName("last_submission_date") val lastSubmissionDate: Long,
    val magiks: String? = null,
    @SerializedName("type_extension") val typeExtension: String? = null,
    @SerializedName("last_analysis_date") val lastAnalysisDate: Long,
    @SerializedName("type_tag") val typeTag: String? = null,
    val reputation: Int? = null,
    @SerializedName("first_submission_date") val firstSubmissionDate: Long,
    val filecondis: Map<String, String>? = null,
    @SerializedName("meaningful_name") val meaningfulName: String? = null

) : Parcelable

@Parcelize
data class EngineResult(
    val method: String? = null,
    @SerializedName("engine_name") val engineName: String? = null,
    @SerializedName("engine_version") val engineVersion: String? = null,
    @SerializedName("engine_update") val engineUpdate: String? = null,
    val category: String? = null,
    val result: String? = null
) : Parcelable

@Parcelize
data class Stats(
    val malicious: Int,
    val suspicious: Int,
    val undetected: Int,
    val harmless: Int,
    val timeout: Int,
    @SerializedName("confirmed-timeout") val confirmTimeout: Int,
    val failure: Int,
    @SerializedName("type-unsupported") val typeUnsupported: Int
) : Parcelable


@Parcelize
data class TotalVotes(
    val harmless: Int? = null,
    val malicious: Int? = null
) : Parcelable

@Parcelize
data class TridItem(
    @SerializedName("file_type") val fileType: String? = null,
    val probability: Double? = null
) : Parcelable