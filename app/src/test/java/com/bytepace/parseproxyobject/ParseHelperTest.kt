package com.bytepace.parseproxyobject

import android.os.Bundle
import com.parse.Parse
import com.parse.ParseGeoPoint
import com.parse.ParseUser
import org.json.JSONObject
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.`when`
import org.mockito.Mockito.mock
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment


/**
 * Created by Viktor Matskevich on 16.04.2018.
 * Company: Bytepace
 * EMAIL: viktor.matskevich@bytepace.com
 */
@RunWith(RobolectricTestRunner::class)
class ParseHelperTest {

    companion object {
        private const val SERVER_URL = "http://example.com/parse"
        private const val APP_ID = "MyAppId"
        private const val CLIENT_KEY = "MyClientKey"

        private const val PARSE_SERVER_URL = "com.parse.SERVER_URL"
        private const val PARSE_APPLICATION_ID = "com.parse.APPLICATION_ID"
        private const val PARSE_CLIENT_KEY = "com.parse.CLIENT_KEY"

        private const val VALUE_USERNAME1 = "Username1"
        private const val VALUE_USERNAME2 = "Username2"
        private const val VALUE_EMAIL = "qwe@qwe.com"
        private const val VALUE_LATITUDE = 0.121
        private const val VALUE_LONGITUDE = 0.4454

        private const val KEY_USER = "user"
        private const val KEY_GEO_POINT = "geoPoint"
        private const val KEY_USERNAME = "username"
        private const val KEY_EMAIL = "email"
        private const val KEY_LATITUDE = "lat"
        private const val KEY_LONGITUDE = "lng"
    }

    @Before
    fun setUp() {
        val metaData = setupMockMetaData()
        `when`(metaData.getString(PARSE_SERVER_URL)).thenReturn(SERVER_URL)
        `when`(metaData.getString(PARSE_APPLICATION_ID)).thenReturn(APP_ID)
        `when`(metaData.getString(PARSE_CLIENT_KEY)).thenReturn(CLIENT_KEY)
        Parse.initialize(RuntimeEnvironment.application)
    }

    @Test
    @Throws(Exception::class)
    fun createParseProxyObject() {
        val (parseUser, innerParseUser) = getOriginalParseUser()
        val serializableParseUser = ParseHelper.from(parseUser)
        checkSerializableParseUser(serializableParseUser.data, parseUser, innerParseUser)
        checkRestoredParseObject(serializableParseUser, parseUser)
    }

    private fun checkRestoredParseObject(serializableUser: Proxy, parseUser: ParseUser) {
        val restoreParseUser = serializableUser.restore<ParseUser>()
        assertEquals(restoreParseUser.username, parseUser.username)
        assertEquals(restoreParseUser.email, parseUser.email)
        assertEquals((restoreParseUser[KEY_USER] as ParseUser).username,
                (parseUser[KEY_USER] as ParseUser).username)

        val restoreGeoPoint = restoreParseUser[KEY_GEO_POINT] as ParseGeoPoint
        val parseOriginalGeoPoint = parseUser[KEY_GEO_POINT] as ParseGeoPoint
        assertEquals(restoreGeoPoint.latitude, parseOriginalGeoPoint.latitude, 0.001)
        assertEquals(restoreGeoPoint.longitude, parseOriginalGeoPoint.longitude, 0.001)
    }

    private fun getOriginalParseUser(): Pair<ParseUser, ParseUser> {
        val originalParseUser = ParseUser()
        originalParseUser.username = VALUE_USERNAME1
        originalParseUser.email = VALUE_EMAIL

        val innerParseUser = ParseUser()
        innerParseUser.username = VALUE_USERNAME2
        originalParseUser.put(KEY_USER, innerParseUser)

        originalParseUser.put(KEY_GEO_POINT, ParseGeoPoint(VALUE_LATITUDE, VALUE_LONGITUDE))
        return Pair(originalParseUser, innerParseUser)
    }

    private fun checkSerializableParseUser(data: JSONObject, parseUser: ParseUser,
                                           innerParseUser: ParseUser) {
        assertEquals(data[KEY_USERNAME], parseUser.username)
        assertEquals(data[KEY_EMAIL], parseUser.email)

        assertEquals((data[KEY_USER] as ParseProxyObject).data[KEY_USERNAME], innerParseUser.username)

        assertEquals((data[KEY_GEO_POINT] as ParseProxyGeoPoint).data[KEY_LATITUDE], VALUE_LATITUDE)
        assertEquals((data[KEY_GEO_POINT] as ParseProxyGeoPoint).data[KEY_LONGITUDE], VALUE_LONGITUDE)
    }

    @Throws(Exception::class)
    private fun setupMockMetaData(): Bundle {
        val metaData = mock(Bundle::class.java)
        RuntimeEnvironment.application.applicationInfo.metaData = metaData
        return metaData
    }
}
