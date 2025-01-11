package com.madinaappstudio.viruscheck.models

import com.google.gson.annotations.SerializedName


data class FileUploadResponse(
    val data: Data
)

data class FileReportResponse(
    val data: Data
)

data class Data (
    val type: String,
    val id: String,
    val links: Links,
    val attributes: Attributes
)

data class Links(
    val self: String,
    val item: String
)

data class Attributes (
    val names: List<String>,
    val md5: String,
    val magic: String,
    val ssdeep: String,
    @SerializedName("last_analysis_results") val lastAnalysisResult: Map<String, EngineResult>,
    val sha1: String,
    @SerializedName("unique_sources") val uniqueSources: Int,
    @SerializedName("last_analysis_stats") val lastAnalysisStats: Stats,
    val tlsh: String,
    val tags: List<String>,
    val trids: List<Map<String, TridItem>>,
    @SerializedName("last_modification_date") val lastModificationDate: Long,
    @SerializedName("total_votes") val totalVotes: TotalVotes,
    val sha256: String,
    val size: Long,
    @SerializedName("type_description") val typeDescription: String,
    @SerializedName("times_submitted") val timesSubmitted: Int,
    @SerializedName("last_submission_date") val lastSubmissionDate: Long,
    val magiks: String,
    @SerializedName("type_extension") val typeExtension: String,
    @SerializedName("last_analysis_date") val lastAnalysisDate: Long,
    @SerializedName("type_tag") val typeTag: String,
    val reputation: Int,
    @SerializedName("first_submission_date") val firstSubmissionDate: Long,
    val filecondis: Map<String, String>,
    @SerializedName("meaningful_name") val meaningfulName: String

)

data class EngineResult(
    val method: String,
    @SerializedName("engine_name") val engineName: String,
    @SerializedName("engine_version") val engineVersion: String?,
    @SerializedName("engine_update") val engineUpdate: String,
    val category: String,
    val result: String?
)

data class Stats(
    val malicious: Int,
    val suspicious: Int,
    val undetected: Int,
    val harmless: Int,
    val timeout: Int,
    @SerializedName("confirmed-timeout") val confirmTimeout: Int,
    val failure: Int,
    @SerializedName("type-unsupported") val typeUnsupported: Int
)


data class TotalVotes(
    val harmless: Int,
    val malicious: Int
)

data class TridItem(
    @SerializedName("file_type") val fileType: String,
    val probability: Double
)