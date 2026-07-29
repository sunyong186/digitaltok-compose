package com.yourcompany.digitaltok.data.network

import android.content.Context
import okhttp3.Cache
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File
import java.util.concurrent.TimeUnit

object RetrofitClient {

    private const val BASE_URL = "https://www.diring.site/api/v1/"
    private const val PREFS_NAME = "auth_prefs"
    private const val KEY_ACCESS_TOKEN = "accessToken"

    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    @Volatile
    private var appContext: Context? = null

    // OkHttp 캐시 인스턴스
    private var okHttpCache: Cache? = null

    // Application.onCreate()에서 1번 호출
    fun init(context: Context) {
        appContext = context.applicationContext
        // 추가: 캐시 초기화
        if (okHttpCache == null) {
            val cacheSize = (10 * 1024 * 1024).toLong() // 10 MB
            okHttpCache = Cache(File(context.cacheDir, "http-cache"), cacheSize)
        }
    }

    // 로그아웃 시 호출하여 OkHttp 캐시를 모두 삭제
    fun clearCache() {
        okHttpCache?.evictAll()
    }

    fun providePublicOkHttpClient(): OkHttpClient = publicOkHttpClient

    private fun getAccessTokenFromPrefs(): String? {
        val ctx = appContext ?: return null
        val prefs = ctx.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return prefs.getString(KEY_ACCESS_TOKEN, null)
    }

    val authenticatedOkHttpClient: OkHttpClient by lazy {
        val authInterceptor = Interceptor { chain ->
            val originalRequest = chain.request()
            val accessToken = getAccessTokenFromPrefs()

            val newRequest = originalRequest.newBuilder().apply {
                if (!accessToken.isNullOrBlank()) {
                    header("Authorization", "Bearer $accessToken")
                }
            }.build()

            chain.proceed(newRequest)
        }

        OkHttpClient.Builder()
            .addInterceptor(authInterceptor)
            .addInterceptor(loggingInterceptor)
            .connectTimeout(60, TimeUnit.SECONDS)
            .readTimeout(60, TimeUnit.SECONDS)
            .writeTimeout(60, TimeUnit.SECONDS)
            .cache(okHttpCache) // 추가: 캐시 사용
            .build()
    }

    // 인증이 필요 없는(공개) 요청을 위한 OkHttpClient (인증 인터셉터 없음)
    private val publicOkHttpClient: OkHttpClient by lazy {
        OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .connectTimeout(60, TimeUnit.SECONDS)
            .readTimeout(60, TimeUnit.SECONDS)
            .writeTimeout(60, TimeUnit.SECONDS)
            .cache(okHttpCache) // 추가: 캐시 사용
            .build()
    }

    // 인증이 필요한 요청을 위한 Retrofit 인스턴스
    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(authenticatedOkHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    // 인증이 필요 없는(공개) 요청을 위한 Retrofit 인스턴스
    private val publicRetrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(publicOkHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    // 기본 ApiService는 인증이 필요한 요청을 처리
    val apiService: ApiService by lazy {
        retrofit.create(ApiService::class.java)
    }

    // 공개 ApiService는 인증이 필요 없는 요청을 처리
    val publicApiService: ApiService by lazy {
        publicRetrofit.create(ApiService::class.java)
    }

    // 기존 create 함수는 유지 (인증된 서비스 생성)
    fun <T> create(service: Class<T>): T {
        return retrofit.create(service)
    }

    fun <T> createPublic(service: Class<T>): T {
        return publicRetrofit.create(service)
    }
}
