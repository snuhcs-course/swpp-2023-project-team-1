package com.project.spire.ui.create.image

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class ImageCreateViewModel: ViewModel() {
    private val _originImageUri = MutableLiveData<Uri>()
    val originImageUri: LiveData<Uri>
        get() = _originImageUri

    fun setOriginImageUri(uri: Uri) {
        _originImageUri.value = uri
    }


}