package com.madinaappstudio.viruscheck.api

import com.madinaappstudio.viruscheck.models.AnalysesModel
import com.madinaappstudio.viruscheck.models.FileReportResponse
import com.madinaappstudio.viruscheck.models.FileUploadResponse
import com.madinaappstudio.viruscheck.models.UrlScanReportResponse
import com.madinaappstudio.viruscheck.models.UrlScanResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Headers
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

    @POST("urls")
    fun scanUrl(
        @Body body: RequestBody,
        @Header("x-apikey") apiKey: String
    ): Call<UrlScanResponse>

    @GET("urls/{id}")
    fun getUrlReport(
        @Path("id") id: String,
        @Header("x-apikey") apiKey: String
    ): Call<UrlScanReportResponse>

    @GET("analyses/{id}")
    fun getAnalyses(
        @Path("id") id: String,
        @Header("x-apikey") apiKey: String
    ): Call<AnalysesModel>

}