package jp.co.yumemi.android.code_check.network

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit

object GitHubApi {
    private const val BASE_URL = "https://api.github.com/"

    @OptIn(ExperimentalSerializationApi::class)
    private val retrofit = Retrofit.Builder().apply {
        val parser = Json {
            ignoreUnknownKeys = true // 定義していないキーがあるため、無視する
        }

        val contentType = "application/json".toMediaType()

        val logger = HttpLoggingInterceptor()
            .apply { level = HttpLoggingInterceptor.Level.BODY }

        val client = OkHttpClient.Builder()
            .addInterceptor(logger)
            .build()

        client(client)
        addConverterFactory(parser.asConverterFactory(contentType))
        baseUrl(BASE_URL)
    }.build()

    val gitHubApiService: GitHubApiService by lazy {
        retrofit.create(GitHubApiService::class.java)
    }
}
