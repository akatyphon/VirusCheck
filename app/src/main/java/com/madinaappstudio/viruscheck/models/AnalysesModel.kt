package com.madinaappstudio.viruscheck.models

data class AnalysesModel(
    private val data: Data
) {
    fun getData() : Data = data
}

data class Data (
    val attributes: Attributes
)

data class Attributes (
    val status: String
)