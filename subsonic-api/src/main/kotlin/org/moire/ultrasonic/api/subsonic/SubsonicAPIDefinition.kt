package org.moire.ultrasonic.api.subsonic

import org.moire.ultrasonic.api.subsonic.models.SubsonicResponse
import retrofit2.Call
import retrofit2.http.GET

/**
 * // TODO
 */
interface SubsonicAPIDefinition {
    @GET("ping.view")
    fun ping(): Call<SubsonicResponse>

    @GET("getLicense.view")
    fun getLicense(): Call<SubsonicResponse>
}