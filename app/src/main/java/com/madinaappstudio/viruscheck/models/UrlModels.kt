package com.madinaappstudio.viruscheck.models

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class UrlScanResponse(
    @SerializedName("data") val urlData: UrlData
) : Parcelable

@Parcelize
data class UrlScanReportResponse(
    @SerializedName("data") val urlData: UrlData
) : Parcelable

@Parcelize
data class UrlData(
    val type: String,
    val id: String,
    @SerializedName("links") val urlLinks: UrlLinks,
    @SerializedName("attributes") val urlAttributes: UrlAttributes? = null
) : Parcelable

@Parcelize
data class UrlLinks(
    val self: String? = null, val item: String? = null
) : Parcelable

@Parcelize
data class UrlAttributes(
    @SerializedName("total_votes") val urlTotalVotes: UrlTotalVotes? = null,
    @SerializedName("redirection_chain") val redirectionChain: List<String>? = null,
    @SerializedName("targeted_brand") val urlTargetedBrand: UrlTargetedBrand? = null,
    val url: String? = null,
    @SerializedName("first_submission_date") val firstSubmissionDate: Long? = null,
    @SerializedName("last_submission_date") val lastSubmissionDate: Long? = null,
    @SerializedName("last_analysis_results") val lastAnalysisResult: Map<String, UrlEngineResult>? = null,
    @SerializedName("last_analysis_stats") val lastAnalysisStats: UrlStats? = null,
    @SerializedName("last_modification_date") val lastModificationDate: Long? = null,
    val title: String? = null,
    @SerializedName("times_submitted") val timesSubmitted: Int? = null,
    @SerializedName("last_analysis_date") val lastAnalysisDate: Long? = null,
    val reputation: Int? = null,
    val tld: String? = null
) : Parcelable {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as UrlAttributes

        if (urlTotalVotes != other.urlTotalVotes) return false
        if (redirectionChain != other.redirectionChain) return false
        if (urlTargetedBrand != other.urlTargetedBrand) return false
        if (url != other.url) return false
        if (firstSubmissionDate != other.firstSubmissionDate) return false
        if (lastSubmissionDate != other.lastSubmissionDate) return false
        if (lastAnalysisResult != other.lastAnalysisResult) return false
        if (lastAnalysisStats != other.lastAnalysisStats) return false
        if (lastModificationDate != other.lastModificationDate) return false
        if (title != other.title) return false
        if (timesSubmitted != other.timesSubmitted) return false
        if (lastAnalysisDate != other.lastAnalysisDate) return false
        if (reputation != other.reputation) return false
        if (tld != other.tld) return false

        return true
    }

    override fun hashCode(): Int {
        var result = urlTotalVotes?.hashCode() ?: 0
        result = 31 * result + (redirectionChain?.hashCode() ?: 0)
        result = 31 * result + (urlTargetedBrand?.hashCode() ?: 0)
        result = 31 * result + (url?.hashCode() ?: "".hashCode())
        result = 31 * result + (firstSubmissionDate?.hashCode() ?: 0)
        result = 31 * result + (lastSubmissionDate?.hashCode() ?: 0)
        result = 31 * result + (lastAnalysisResult?.hashCode() ?: 0)
        result = 31 * result + (lastAnalysisStats?.hashCode() ?: 0)
        result = 31 * result + (lastModificationDate?.hashCode() ?: 0)
        result = 31 * result + (title?.hashCode() ?: "".hashCode())
        result = 31 * result + (timesSubmitted ?: 0)
        result = 31 * result + (lastAnalysisDate?.hashCode() ?: 0)
        result = 31 * result + (reputation ?: 0)
        result = 31 * result + (tld?.hashCode() ?: "".hashCode())
        return result
    }
}
@Parcelize
data class UrlTargetedBrand(
    val safeToOpen: String? = null
): Parcelable


@Parcelize
data class UrlEngineResult(
    val method: String? = null,
    @SerializedName("engine_name") val engineName: String? = null,
    val category: String? = null,
    val result: String? = null
) : Parcelable

@Parcelize
data class UrlStats(
    val malicious: Int,
    val suspicious: Int,
    val undetected: Int,
    val harmless: Int,
    val timeout: Int,
) : Parcelable


@Parcelize
data class UrlTotalVotes(
    val harmless: Int? = null, val malicious: Int? = null
) : Parcelable
