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
    val type: String? = null,
    val id: String? = null,
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
    val url: String,
    @SerializedName("first_submission_date") val firstSubmissionDate: Long,
    @SerializedName("last_submission_date") val lastSubmissionDate: Long,
    @SerializedName("last_analysis_results") val lastAnalysisResult: Map<String, UrlEngineResult>? = null,
    @SerializedName("last_analysis_stats") val lastAnalysisStats: UrlStats,
    @SerializedName("last_modification_date") val lastModificationDate: Long? = null,
    val title: String? = null,
    @SerializedName("times_submitted") val timesSubmitted: Int? = null,
    @SerializedName("last_analysis_date") val lastAnalysisDate: Long,
    val reputation: Int? = null,
    val tld: String? = null

) : Parcelable

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
