package com.dreamteam.sharedream.Util

import android.media.tv.TvContract.Channels.CONTENT_TYPE
import com.dreamteam.sharedream.Util.Constants.Constants.FCM_KEY
import com.dreamteam.sharedream.model.NotificationBody
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface FCMInterface {
    @Headers("Authorization: key=$FCM_KEY", "Content-Yype:$CONTENT_TYPE")
    @POST("fcm/send")
    suspend fun sendNotification(
        @Body notification: NotificationBody
    ) : Response<ResponseBody>
}
