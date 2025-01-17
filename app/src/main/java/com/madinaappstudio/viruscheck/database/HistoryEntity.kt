package com.madinaappstudio.viruscheck.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "HistoryTable")
data class HistoryEntity (

    @PrimaryKey(autoGenerate = true)
    private val id: Long = 0,
    private val type: String,
    private val name: String,
    private val fileSha256: String? = null,
    private val urlBase64: String? = null,
    private val fileSize: String? = null,
    private val date: Long

) {
    fun getId(): Long = id
    fun getType(): String = type
    fun getName(): String = name
    fun getFileSha256(): String? = fileSha256
    fun getUrlBase64(): String? = urlBase64
    fun getFileSize(): String? = fileSize
    fun getDate(): Long = date
}