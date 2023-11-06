package com.dreamteam.sharedream.Util

import com.dreamteam.sharedream.model.NotificationBody
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface FCMInterface {
    @POST("fcm/send")
    suspend fun sendNotification(
        @Body notification: NotificationBody
    ) : Response<ResponseBody>
}
//}
//    @POST("fcm/send")
//    suspend fun sendNotification(
//        @Body notification: RemoteMessage
//    ) : Response<ResponseBody>
//}