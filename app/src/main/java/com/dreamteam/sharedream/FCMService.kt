package com.dreamteam.sharedream

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage


class FCMService : FirebaseMessagingService() {
    /** 푸시 알림으로 보낼 수 있는 메세지는 2가지
     * 1. Notification: 앱이 실행중(포그라운드)일 떄만 푸시 알림이 옴
     * 2. Data: 실행중이거나 백그라운드(앱이 실행중이지 않을때) 알림이 옴 -> TODO: 대부분 사용하는 방식 */

    override fun onNewToken(token: String) {
        //새로운 token이 생성될 때 마다 호출되는 callback함수
        Log.d("nyh", "onNewToken: $token")
        super.onNewToken(token)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)
//        remoteMessage.takeIf { it.data.isNotEmpty() }?.apply { this
//            //push를 전달받으면 동작해야 할 함수를 호출

        // Notification 메시지를 수신할 경우
        // remoteMessage.notification?.body!! 여기에 내용이 저장되있음
        // Log.d(TAG, "Notification Message Body: " + remoteMessage.notification?.body!!)

        val data = remoteMessage.data
        val notificationTitle = data["title"]
        val notificationBody = data["body"]

        if (notificationTitle != null && notificationBody != null) {
            sendNonotification(this, notificationTitle, notificationBody, data)
            Log.d("nyh", "onMessageReceived: ${remoteMessage.notification?.title}")
            Log.d("nyh", "onMessageReceived: ${remoteMessage.notification?.body}")
        } else {
            Log.d("nyh", "onMessageReceived else: $remoteMessage ")

        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun sendNonotification(
        context: Context?,
        title: String,
        body: String,
        data: Map<String, String>
    ) {
        if (context != null) {

            //channel 설성
            val channelId = "channelId"
            val channelName = "channelName"
            val channelDescription = "channelDescription"
            val soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)//
            val notificationManager =
                context?.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            //오레오 버전 이후에는 채널이 필요

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                Log.d("nyh", "sendNonotification: 오레오버전 이후 channel")
                val importance = NotificationManager.IMPORTANCE_HIGH // 중요도(HIGH: 상단바 표시 가능)
                val channel = NotificationChannel(channelId, channelName, importance).apply {
                    description = channelDescription
                }
                notificationManager.createNotificationChannel(channel)
            }
            Log.d("nyh", "sendNonotification: out of channel")

            // RequestCode. Id를 고유값으로 지정하여 알림이 개별 표시
            val uniId: Int = (System.currentTimeMillis() / 7).toInt()

            //일회용 PendingIntent : Intent 의 실행 권한을 외부의 어플리케이션에게 위임
            val intent = Intent(context.applicationContext, MainActivity::class.java)
            intent.putExtra("open_fragment", "alarm_fragment")

            //각 key, value 추가
            for (key in data.keys) {
                intent.putExtra(key, data[key])
            }
//        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP) // Activity Stack 을 경로만 남김 (A-B-C-D-B- => A-B)

            //23.05.22 Android 최신버전 대응 (FLAG_MUTABLE, FLAG_IMMUTABLE)
            //PendingIntent.FAG_MUTABLE은 PendingIntent의 내용을 변경할 수 있도록 허용. PendingIntnent.FLAG_IMMUTABLE은 PendingIntent의 내용을 변경할 수 없음.
            //val pendingIntent = PendingIntent.getActivity(this, uniId, intent, PendingIntent.FLAG_ONE_SHOT)
            // 최근에는 FLAG_UPDATE_CURRENT 을 사용하라고함
            // Context가 null이 아닌지 확인
            // PendingIntent 생성
            val pendingIntent = PendingIntent.getActivity(
                context,
                uniId,
                intent,
                PendingIntent.FLAG_IMMUTABLE
            )

            // 알림 채널 이름
            // val channelId = "my_channel"

            //알림에 대한 Ui 정보, 작업
            val notificationBuilder = NotificationCompat.Builder(context, channelId)
                .setPriority(NotificationCompat.PRIORITY_HIGH) // 중요도
                .setSmallIcon(R.mipmap.ic_launcher) // 아이콘 설정
                .setContentTitle(title) //title
                .setContentText(body) // body
                .setAutoCancel(true) // 알림 클릭 시 삭제여부
                .setSound(soundUri) // 알림 소리
                .setContentIntent(pendingIntent) // 알림 실행 시 Intent

            //getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            //오레오 버전 이후에는 채널이 필요
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val channel =
                    NotificationChannel(
                        channelId,
                        "Notice",
                        NotificationManager.IMPORTANCE_DEFAULT
                    )
                notificationManager.createNotificationChannel(channel)
            }
            //알림 생성
            notificationManager.notify(uniId, notificationBuilder.build())
            Log.d("nyh", "sendNonotification 알림생성 uniId = $uniId")
        } else {
            Log.e("nyh", "Context is null in sendNonotification")
        }
    }

    //Token 가져오기
    fun getFirebaseToken() {
        //비동기 방식
//        FirebaseMessaging.getInstance().token.addOnSuccessListener {
//            Log.d("nyh", "getFirebaseToken: token =$it")
//        }
        //동기방식
        FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
            if (!task.isSuccessful) {
                Log.d(TAG, "Fetching FCM registration token failed ${task.exception}")
                return@OnCompleteListener
            }
            var deviceToken = task.result
            Log.e(TAG, "token=${deviceToken}")
        })
    }

}

/**
"notificationBuilder" 알림 생성시 여러가지 옵션을 이용해 커스텀 가능.
setSmallIcon : 작은 아이콘 (필수)
setContentTitle : 제목 (필수)
setContentText : 내용 (필수)
setColor : 알림내 앱 이름 색
setWhen : 받은 시간 커스텀 ( 기본 시스템에서 제공합니다 )
setShowWhen : 알림 수신 시간 ( default 값은 true, false시 숨길 수 있습니다 )
setOnlyAlertOnce : 알림 1회 수신 ( 동일 아이디의 알림을 처음 받았을때만 알린다, 상태바에 알림이 잔존하면 무음 )
setContentTitle : 제목
setContentText : 내용
setFullScreenIntent : 긴급 알림 ( 자세한 설명은 아래에서 설명합니다 )
setTimeoutAfter : 알림 자동 사라지기 ( 지정한 시간 후 수신된 알림이 사라집니다 )
setContentIntent : 알림 클릭시 이벤트 ( 지정하지 않으면 클릭했을때 아무 반응이 없고 setAutoCancel 또한 작동하지 않는다 )
setLargeIcon : 큰 아이콘 ( mipmap 에 있는 아이콘이 아닌 drawable 폴더에 있는 아이콘을 사용해야 합니다. )
setAutoCancel : 알림 클릭시 삭제 여부 ( true = 클릭시 삭제 , false = 클릭시 미삭제 )
setPriority : 알림의 중요도를 설정 ( 중요도에 따라 head up 알림으로 설정할 수 있는데 자세한 내용은 밑에서 설명하겠습니다. )
setVisibility : 잠금 화면내 알림 노출 여부
Notification.VISIBILITY_PRIVATE : 알림의 기본 정보만 노출 (제목, 타이틀 등등)
Notification.VISIBILITY_PUBLIC : 알림의 모든 정보 노출
Notification.VISIBILITY_SECRET : 알림의 모든 정보 비노출
 */