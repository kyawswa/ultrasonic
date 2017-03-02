package org.moire.ultrasonic.api.subsonic

import okhttp3.mockwebserver.MockResponse
import okio.Okio
import org.amshove.kluent.`should be`
import org.amshove.kluent.`should not be`
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.moire.ultrasonic.api.subsonic.models.SubsonicResponse
import org.moire.ultrasonic.api.subsonic.rules.MockWebServerRule
import retrofit2.Response
import java.nio.charset.Charset

/**
 * Integration test for [SubsonicAPI] class.
 */
class SubsonicAPITest {
    companion object {
        val USERNAME = "some-user"
        val PASSWORD = "some-password"
        val CLIENT_VERSION = SubsonicAPIVersions.V1_13_0
        val CLIENT_ID = "test-client"
    }

    @JvmField
    @Rule
    val mockWebServerRule = MockWebServerRule()

    private lateinit var api: SubsonicAPI

    @Before
    fun setUp() {
        api = SubsonicAPI(mockWebServerRule.mockWebServer.url("/").toString(), USERNAME, PASSWORD,
                CLIENT_VERSION, CLIENT_ID)
    }

    @Test
    fun `Should parse ping ok response`() {
        enqueueResponse("ping_ok.json")

        val response = api.getApi().ping().execute()

        assertResponseSuccessful(response)
        with(response.body()) {
            status `should be` SubsonicResponse.Status.OK
            version `should be` SubsonicAPIVersions.V1_13_0
        }
    }

    @Test
    fun `Should parse error response`() {
        enqueueResponse("generic_error_response.json")

        val response = api.getApi().ping().execute()

        assertResponseSuccessful(response)
        with(response.body()) {
            status `should be` SubsonicResponse.Status.ERROR
            version `should be` SubsonicAPIVersions.V1_13_0
            error `should be` SubsonicError.GENERIC
        }
    }

    @Test
    fun `Should parse get license response`() {
        enqueueResponse("license_ok.json")

        val response = api.getApi().getLicense().execute()

        assertResponseSuccessful(response)
        with(response.body()) {
            status `should be` SubsonicResponse.Status.OK
            version `should be` SubsonicAPIVersions.V1_13_0
        }
    }

    private fun enqueueResponse(resourceName: String) {
        mockWebServerRule.mockWebServer.enqueue(MockResponse()
                .setBody(loadJsonResponse(resourceName)))
    }

    private fun loadJsonResponse(name: String): String {
        val source = Okio.buffer(Okio.source(javaClass.classLoader.getResourceAsStream(name)))
        return source.readString(Charset.forName("UTF-8"))
    }

    private fun assertResponseSuccessful(response: Response<SubsonicResponse>) {
        response.isSuccessful `should be` true
        response.body() `should not be` null
    }
}