package com.madinaappstudio.viruscheck.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class ScanResultType(
    val fileReportResponse: FileReportResponse? = null,
    val urlScanReportResponse: UrlScanReportResponse? = null
): Parcelable {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ScanResultType

        if (fileReportResponse != other.fileReportResponse) return false
        if (urlScanReportResponse != other.urlScanReportResponse) return false

        return true
    }

    override fun hashCode(): Int {
        var result = fileReportResponse?.hashCode() ?: 0
        result = 31 * result + (urlScanReportResponse?.hashCode() ?: 0)
        return result
    }
}
