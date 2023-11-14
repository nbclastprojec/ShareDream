package com.dreamteam.sharedream.viewmodel

import android.net.Uri
import android.view.View
import com.google.android.gms.tasks.Task
import com.google.firebase.storage.UploadTask

interface ImgRepository {

    // Storage 이미지 업로드
    suspend fun uploadImg(path: String, uri: Uri): UploadTask

    // Storage 이미지 다운로드
    suspend fun downloadImg(path: String, detailPath: String): Task<Uri>

    // Storage 이미지 삭제
    suspend fun deleteImg(path: String, detailPath: String): Task<Void>

}