package com.lightricks.feedexercise.network

import com.squareup.moshi.FromJson
import com.squareup.moshi.Json
import com.squareup.moshi.JsonQualifier
import com.squareup.moshi.ToJson

data class FeedResponse(val templatesMetadata: List<FeedItemResponse>)

data class FeedItemResponse(val id: String,
                            @UrlPrefix @Json(name = "templateThumbnailURI")
                            val thumbnailUrl: String,
                            val isPremium: Boolean)


private const val thumbnailUrlPrefix =
    "https://assets.swishvideoapp.com/Android/demo/catalog/thumbnails/"

@Retention(AnnotationRetention.RUNTIME)
@JsonQualifier
annotation class UrlPrefix
internal class FeedItemAdapter {

    @ToJson
    fun toJson(@UrlPrefix url: String) = url

    @FromJson
    @UrlPrefix
    fun fromJson(url: String) = "$thumbnailUrlPrefix$url"
}