package com.dreamteam.sharedream.viewmodel

import android.net.Uri
import com.google.android.gms.tasks.Task
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.UploadTask
import com.google.firebase.storage.ktx.storage

class ImgRepositoryImpl :ImgRepository{

    private val storage = Firebase.storage

    override suspend fun uploadImg(path: String,uri: Uri):UploadTask {
        return storage.reference.child(path).putFile(uri)
    }

    override suspend fun downloadImg(path: String,detailPath: String):Task<Uri> {
        return storage.reference.child(path).child(detailPath).downloadUrl
    }

    override suspend fun deleteImg(path: String, detailPath: String): Task<Void> {
        return storage.reference.child(path).child(detailPath).delete()
    }

}