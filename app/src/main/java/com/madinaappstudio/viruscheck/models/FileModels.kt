package com.madinaappstudio.viruscheck.models

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class FileUploadResponse(
    @SerializedName("data") val fileData: FileData
) : Parcelable

@Parcelize
data class FileReportResponse(
    @SerializedName("data") val fileData: FileData
) : Parcelable

@Parcelize
data class FileData(
    val type: String,
    val id: String,
    @SerializedName("links") val fileLinks: FileLinks,
    @SerializedName("attributes") val fileAttributes: FileAttributes
) : Parcelable

@Parcelize
data class FileLinks(
    val self: String? = null, val item: String? = null
) : Parcelable

@Parcelize
data class FileAttributes(
    @SerializedName("last_analysis_results") val lastAnalysisResult: Map<String, FileEngineResult>? = null,
    @SerializedName("last_analysis_stats") val lastAnalysisStats: FileStats? = null,
    @SerializedName("last_modification_date") val lastModificationDate: Long? = null,
    @SerializedName("total_votes") val totalVotes: FileTotalVotes? = null,
    val sha256: String? = null,
    val size: Long? = null,
    @SerializedName("times_submitted") val timesSubmitted: Int? = null,
    @SerializedName("last_submission_date") val lastSubmissionDate: Long? = null,
    @SerializedName("type_extension") val typeExtension: String? = null,
    @SerializedName("last_analysis_date") val lastAnalysisDate: Long? = null,
    val reputation: Int? = null,
    @SerializedName("first_submission_date") val firstSubmissionDate: Long? = null,
    @SerializedName("meaningful_name") val meaningfulName: String? = null
) : Parcelable {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true

        if (javaClass != other?.javaClass) return false

        other as FileAttributes

        if (lastAnalysisResult != other.lastAnalysisResult) return false
        if (lastAnalysisStats != other.lastAnalysisStats) return false
        if (lastModificationDate != other.lastModificationDate) return false
        if (totalVotes != other.totalVotes) return false
        if (sha256 != other.sha256) return false
        if (size != other.size) return false
        if (timesSubmitted != other.timesSubmitted) return false
        if (lastSubmissionDate != other.lastSubmissionDate) return false
        if (typeExtension != other.typeExtension) return false
        if (lastAnalysisDate != other.lastAnalysisDate) return false
        if (reputation != other.reputation) return false
        if (firstSubmissionDate != other.firstSubmissionDate) return false
        if (meaningfulName != other.meaningfulName) return false

        return true
    }

    override fun hashCode(): Int {
        var result = (lastAnalysisResult?.hashCode() ?: 0)
        result = 31 * result + (lastAnalysisStats?.hashCode() ?: 0)
        result = 31 * result + (lastModificationDate?.hashCode() ?: 0)
        result = 31 * result + (totalVotes?.hashCode() ?: 0)
        result = 31 * result + (sha256?.hashCode() ?: "".hashCode())
        result = 31 * result + (size?.hashCode() ?: 0)
        result = 31 * result + (timesSubmitted ?: 0)
        result = 31 * result + (lastSubmissionDate?.hashCode() ?: 0)
        result = 31 * result + (typeExtension?.hashCode() ?: "".hashCode())
        result = 31 * result + (lastAnalysisDate?.hashCode() ?: 0)
        result = 31 * result + (reputation ?: 0)
        result = 31 * result + (firstSubmissionDate?.hashCode() ?: 0)
        result = 31 * result + (meaningfulName?.hashCode() ?: "".hashCode())
        return result
    }
}


@Parcelize
data class FileEngineResult(
    val method: String? = null,
    @SerializedName("engine_name") val engineName: String? = null,
    @SerializedName("engine_version") val engineVersion: String? = null,
    @SerializedName("engine_update") val engineUpdate: String? = null,
    val category: String? = null,
    val result: String? = null
) : Parcelable

@Parcelize
data class FileStats(
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
data class FileTotalVotes(
    val harmless: Int, val malicious: Int
) : Parcelable

@Parcelize
data class FileTridItem(
    @SerializedName("file_type") val fileType: String? = null, val probability: Double? = null
) : Parcelable
