package com.yourcompany.digitaltok.ui.upload

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yourcompany.digitaltok.data.repository.ImageRepository
import kotlinx.coroutines.launch

class TemplatePreviewViewModel : ViewModel() {

    private val imageRepository = ImageRepository()

    // 바이너리 데이터 준비 결과 (NFC 전송용)
    private val _binaryData = MutableLiveData<Result<ByteArray>>()
    val binaryData: LiveData<Result<ByteArray>> = _binaryData

    /**
     * 템플릿 상세 정보에서 받은 dataUrl로 실제 바이너리 데이터를 다운로드한다.
     */
    fun downloadTemplateBinary(dataUrl: String) {
        viewModelScope.launch {
            // URL에 타임스탬프를 추가하여 캐시 문제 방지
            val urlWithCacheBust = "${dataUrl}?t=${System.currentTimeMillis()}"
            val downloadResult = imageRepository.downloadImageBinary(urlWithCacheBust)

            downloadResult.onSuccess { responseBody ->
                val bytes = responseBody.bytes()
                _binaryData.postValue(Result.success(bytes))
            }.onFailure { exception ->
                _binaryData.postValue(Result.failure(exception))
            }
        }
    }
}