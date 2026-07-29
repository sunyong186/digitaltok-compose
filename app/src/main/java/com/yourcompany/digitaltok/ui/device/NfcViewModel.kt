package com.yourcompany.digitaltok.ui.device

import android.nfc.Tag
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

/**
 * MainActivity와 Fragment간에 감지된 NFC 태그 정보를 공유하기 위한 ViewModel
 */
class NfcViewModel : ViewModel() {

    private val _tag = MutableLiveData<Tag?>()
    val tag: LiveData<Tag?> = _tag

    /**
     * 새로운 NFC 태그가 감지되었을 때 호출됩니다.
     */
    fun onTagDiscovered(tag: Tag) {
        _tag.value = tag
    }

    /**
     * 태그 정보 처리가 완료된 후 호출하여, LiveData를 초기화하고 반복적인 처리를 방지합니다.
     */
    fun tagHandled() {
        _tag.value = null
    }
}