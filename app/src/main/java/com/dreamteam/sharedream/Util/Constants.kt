package com.dreamteam.sharedream.Util

import com.dreamteam.sharedream.model.PostRcv
import com.dreamteam.sharedream.model.UserData

class Constants {
    companion object Constants {
        var currentUserUid: String? = null
        var currentUserInfo: UserData? = null
        var currentDocumentId : PostRcv? = null
        const val FCM_URL = "https://fcm.googleapis.com/"
        const val FCM_KEY = "AAAAhrBPG8Y:APA91bGlbWTRIYRew6SUJ5Bapcxb8tEoQMmd9aNOPaMljY2lHbyS7xGqRJpTeXkEKCC-g1ZLbyXkQasY7ZigQdzfMLHDP7KQ6YKKhv4xxWTeCcOg1QvPidGxtqbn7qjuhB00RVKjJfA9"
    }
}