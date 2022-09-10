package jp.co.yumemi.android.code_check.network

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import okhttp3.Interceptor
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit

object GitHubApi {
    private const val BASE_URL = "https://api.github.com/"

    @OptIn(ExperimentalSerializationApi::class)
    private fun getRetrofit(token: String?) = Retrofit.Builder().apply {
        val parser = Json {
            ignoreUnknownKeys = true // 定義していないキーがあるため、無視する
        }

        val contentType = "application/json".toMediaType()

        val logger = HttpLoggingInterceptor()
            .apply { level = HttpLoggingInterceptor.Level.BODY }

        val headerAuthorizationInterceptor = getAuthorizationInterceptor(token)

        val client = OkHttpClient.Builder()
            .addInterceptor(logger)
            .addInterceptor(headerAuthorizationInterceptor)
            .build()

        client(client)
        addConverterFactory(parser.asConverterFactory(contentType))
        baseUrl(BASE_URL)
    }.build()

    /**
     * GitHubの認証情報を持った[Interceptor]。
     * [token]がNullまたは空であればなにもしない。
     */
    private fun getAuthorizationInterceptor(token: String?): Interceptor = Interceptor { chain ->
        val request = chain.request()
        if (token.isNullOrEmpty()) {
            return@Interceptor chain.proceed(request)
        }
        val headers =
            request.headers.newBuilder().add("Authorization", "Bearer $token")
                .build()
        val newRequest = request.newBuilder().headers(headers).build()
        return@Interceptor chain.proceed(newRequest)
    }

    fun getGitHubApiService(token: String?): GitHubApiService {
        return getRetrofit(token).create(GitHubApiService::class.java)
    }
}
