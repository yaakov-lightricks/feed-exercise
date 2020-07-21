package com.lightricks.feedexercise.data

import com.squareup.moshi.FromJson
import com.squareup.moshi.Json
import com.squareup.moshi.JsonQualifier
import com.squareup.moshi.ToJson


/**
 * This data class is our internal representation of a feed item.
 * In a real-life application this is template meta-data for a user's project.
 * The rest of the properties are left out for brevity.
 */
data class FeedItem(
    val id: String,
    @UrlPrefix @Json(name = "templateThumbnailURI")
    val thumbnailUrl: String,
    val isPremium: Boolean
)

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