package com.madinaappstudio.viruscheck.api

import com.madinaappstudio.viruscheck.models.FileReportResponse
import com.madinaappstudio.viruscheck.models.FileUploadResponse
import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path

interface RetrofitInterface {

    @Multipart
    @POST("files")
    fun uploadFile(
        @Part file: MultipartBody.Part,
        @Header("x-apikey") apiKey: String
    ): Call<FileUploadResponse>

    @GET("files/{id}")
    fun getFileReport(
        @Path("id") sha256: String,
        @Header("x-apikey") apiKey: String
    ): Call<FileReportResponse>

}