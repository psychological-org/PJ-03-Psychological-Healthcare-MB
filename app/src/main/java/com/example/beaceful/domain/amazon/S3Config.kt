package com.example.beaceful.domain.amazon

import android.content.Context
import com.amazonaws.auth.BasicAWSCredentials
import com.amazonaws.regions.Region
import com.amazonaws.regions.Regions
import com.amazonaws.services.s3.AmazonS3Client
import com.amazonaws.services.s3.model.CannedAccessControlList
import com.amazonaws.services.s3.model.PutObjectRequest
import com.example.beaceful.BuildConfig
import java.io.File
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

object S3Manager {
    private lateinit var s3Client: AmazonS3Client
    private const val BUCKET_NAME = "store-s3-psychology"

    fun initialize(context: Context) {
        val credentials = BasicAWSCredentials(
            BuildConfig.AWS_ACCESS_KEY,
            BuildConfig.AWS_SECRET_KEY
        )
        s3Client = AmazonS3Client(credentials, Region.getRegion(Regions.AP_SOUTHEAST_1))
    }

    suspend fun uploadFile(file: File, key: String): String = withContext(Dispatchers.IO) {
        val request = PutObjectRequest(BUCKET_NAME, key, file)
        s3Client.putObject(request)
        s3Client.getUrl(BUCKET_NAME, key).toString()
    }
}