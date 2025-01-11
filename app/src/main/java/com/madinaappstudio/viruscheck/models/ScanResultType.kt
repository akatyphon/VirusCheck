package com.madinaappstudio.viruscheck.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class ScanResultType(
    val fileReportResponse: FileReportResponse? = null,
    val urlScanReportResponse: UrlScanReportResponse? = null
): Parcelable
