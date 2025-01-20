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
    val names: List<String>? = null,
    val md5: String? = null,
    val magic: String? = null,
    val ssdeep: String? = null,
    @SerializedName("last_analysis_results") val lastAnalysisResult: Map<String, FileEngineResult>? = null,
    val sha1: String? = null,
    @SerializedName("unique_sources") val uniqueSources: Int? = null,
    @SerializedName("last_analysis_stats") val lastAnalysisStats: FileStats? = null,
    val tlsh: String? = null,
    val tags: List<String>? = null,
    val trids: List<Map<String, FileTridItem>>? = null,
    @SerializedName("last_modification_date") val lastModificationDate: Long? = null,
    @SerializedName("total_votes") val totalVotes: FileTotalVotes? = null,
    val sha256: String? = null,
    val size: Long? = null,
    @SerializedName("type_description") val typeDescription: String? = null,
    @SerializedName("times_submitted") val timesSubmitted: Int? = null,
    @SerializedName("last_submission_date") val lastSubmissionDate: Long? = null,
    val magiks: String? = null,
    @SerializedName("type_extension") val typeExtension: String? = null,
    @SerializedName("last_analysis_date") val lastAnalysisDate: Long? = null,
    @SerializedName("type_tag") val typeTag: String? = null,
    val reputation: Int? = null,
    @SerializedName("first_submission_date") val firstSubmissionDate: Long? = null,
    val filecondis: Map<String, String>? = null,
    @SerializedName("meaningful_name") val meaningfulName: String? = null
) : Parcelable {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as FileAttributes

        if (names != other.names) return false
        if (md5 != other.md5) return false
        if (magic != other.magic) return false
        if (ssdeep != other.ssdeep) return false
        if (lastAnalysisResult != other.lastAnalysisResult) return false
        if (sha1 != other.sha1) return false
        if (uniqueSources != other.uniqueSources) return false
        if (lastAnalysisStats != other.lastAnalysisStats) return false
        if (tlsh != other.tlsh) return false
        if (tags != other.tags) return false
        if (trids != other.trids) return false
        if (lastModificationDate != other.lastModificationDate) return false
        if (totalVotes != other.totalVotes) return false
        if (sha256 != other.sha256) return false
        if (size != other.size) return false
        if (typeDescription != other.typeDescription) return false
        if (timesSubmitted != other.timesSubmitted) return false
        if (lastSubmissionDate != other.lastSubmissionDate) return false
        if (magiks != other.magiks) return false
        if (typeExtension != other.typeExtension) return false
        if (lastAnalysisDate != other.lastAnalysisDate) return false
        if (typeTag != other.typeTag) return false
        if (reputation != other.reputation) return false
        if (firstSubmissionDate != other.firstSubmissionDate) return false
        if (filecondis != other.filecondis) return false
        if (meaningfulName != other.meaningfulName) return false

        return true
    }

    override fun hashCode(): Int {
        var result = (names?.hashCode() ?: "".hashCode())
        result = 31 * result + (md5?.hashCode() ?: "".hashCode())
        result = 31 * result + (magic?.hashCode() ?: "".hashCode())
        result = 31 * result + (ssdeep?.hashCode() ?: "".hashCode())
        result = 31 * result + (lastAnalysisResult?.hashCode() ?: 0)
        result = 31 * result + (sha1?.hashCode() ?: "".hashCode())
        result = 31 * result + (uniqueSources ?: 0)
        result = 31 * result + (lastAnalysisStats?.hashCode() ?: 0)
        result = 31 * result + (tlsh?.hashCode() ?: "".hashCode())
        result = 31 * result + (tags?.hashCode() ?: 0)
        result = 31 * result + (trids?.hashCode() ?: 0)
        result = 31 * result + (lastModificationDate?.hashCode() ?: 0)
        result = 31 * result + (totalVotes?.hashCode() ?: 0)
        result = 31 * result + (sha256?.hashCode() ?: "".hashCode())
        result = 31 * result + (size?.hashCode() ?: 0)
        result = 31 * result + (typeDescription?.hashCode() ?: "".hashCode())
        result = 31 * result + (timesSubmitted ?: 0)
        result = 31 * result + (lastSubmissionDate?.hashCode() ?: 0)
        result = 31 * result + (magiks?.hashCode() ?: "".hashCode())
        result = 31 * result + (typeExtension?.hashCode() ?: "".hashCode())
        result = 31 * result + (lastAnalysisDate?.hashCode() ?: 0)
        result = 31 * result + (typeTag?.hashCode() ?: "".hashCode())
        result = 31 * result + (reputation ?: 0)
        result = 31 * result + (firstSubmissionDate?.hashCode() ?: 0)
        result = 31 * result + (filecondis?.hashCode() ?: 0)
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
