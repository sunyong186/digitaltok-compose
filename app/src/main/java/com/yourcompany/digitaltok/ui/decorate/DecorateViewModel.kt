package com.yourcompany.digitaltok.ui.decorate

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yourcompany.digitaltok.data.model.ImagePreview
import com.yourcompany.digitaltok.data.model.ImageUploadResult
import com.yourcompany.digitaltok.data.model.PriorityTemplate
import com.yourcompany.digitaltok.data.model.PriorityTemplateDetail
import com.yourcompany.digitaltok.data.model.RecentImagesResponse
import com.yourcompany.digitaltok.data.model.SubwayGenerateRequest
import com.yourcompany.digitaltok.data.model.SubwayTemplateDetail
import com.yourcompany.digitaltok.data.model.SubwayTemplateResponse
import com.yourcompany.digitaltok.data.repository.ImageRepository
import com.yourcompany.digitaltok.data.repository.TemplateRepository
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import java.lang.NumberFormatException

class DecorateViewModel : ViewModel() {

    private val imageRepository = ImageRepository()
    private val templateRepository = TemplateRepository()

    // 이미지 업로드 UI 상태
    sealed class UploadUiState {
        object Idle : UploadUiState()
        object Loading : UploadUiState()
        data class Success(val result: ImageUploadResult) : UploadUiState()
        data class Error(val message: String) : UploadUiState()
    }

    private val _uploadState = MutableLiveData<UploadUiState>(UploadUiState.Idle)
    val uploadState: LiveData<UploadUiState> = _uploadState

    // 즐겨찾기 UI 상태
    sealed class FavoriteUiState {
        object Idle : FavoriteUiState()
        object Loading : FavoriteUiState()
        data class Success(val imageId: String, val isFavorite: Boolean) : FavoriteUiState()
        data class Error(val message: String) : FavoriteUiState()
    }

    private val _favoriteState = MutableLiveData<FavoriteUiState>(FavoriteUiState.Idle)
    val favoriteState: LiveData<FavoriteUiState> = _favoriteState

    // 이미지 미리보기 UI 상태
    sealed class PreviewUiState {
        object Idle : PreviewUiState()
        object Loading : PreviewUiState()
        data class Success(val preview: ImagePreview) : PreviewUiState()
        data class Error(val message: String) : PreviewUiState()
    }

    private val _previewState = MutableLiveData<PreviewUiState>(PreviewUiState.Idle)
    val previewState: LiveData<PreviewUiState> = _previewState

    // 최근 이미지 목록 UI 상태
    sealed class RecentImagesUiState {
        object Idle : RecentImagesUiState()
        object Loading : RecentImagesUiState()
        data class Success(val response: RecentImagesResponse) : RecentImagesUiState()
        data class Error(val message: String) : RecentImagesUiState()
    }

    private val _recentImagesState = MutableLiveData<RecentImagesUiState>(RecentImagesUiState.Idle)
    val recentImagesState: LiveData<RecentImagesUiState> = _recentImagesState

    // 교통약자 템플릿 목록 UI 상태
    sealed class PriorityTemplatesUiState {
        object Loading : PriorityTemplatesUiState()
        data class Success(val templates: List<PriorityTemplate>) : PriorityTemplatesUiState()
        data class Error(val message: String) : PriorityTemplatesUiState()
    }

    private val _priorityTemplatesState = MutableLiveData<PriorityTemplatesUiState>()
    val priorityTemplatesState: LiveData<PriorityTemplatesUiState> = _priorityTemplatesState

    // 교통약자 템플릿 상세 정보 UI 상태
    sealed class PriorityTemplateDetailUiState {
        object Loading : PriorityTemplateDetailUiState()
        data class Success(val templateDetail: PriorityTemplateDetail) : PriorityTemplateDetailUiState()
        data class Error(val message: String) : PriorityTemplateDetailUiState()
    }

    private val _priorityTemplateDetailState = MutableLiveData<PriorityTemplateDetailUiState>()
    val priorityTemplateDetailState: LiveData<PriorityTemplateDetailUiState> = _priorityTemplateDetailState

    // 지하철역 목록 UI 상태
    sealed class SubwayTemplatesUiState {
        object Loading : SubwayTemplatesUiState()
        data class Success(val response: SubwayTemplateResponse) : SubwayTemplatesUiState()
        data class Error(val message: String) : SubwayTemplatesUiState()
    }

    private val _subwayTemplatesState = MutableLiveData<SubwayTemplatesUiState>()
    val subwayTemplatesState: LiveData<SubwayTemplatesUiState> = _subwayTemplatesState

    // 단일 지하철역 템플릿 상세 정보 UI 상태
    sealed class SubwayTemplateDetailUiState {
        object Loading : SubwayTemplateDetailUiState()
        data class Success(val templateDetail: SubwayTemplateDetail) : SubwayTemplateDetailUiState()
        data class Error(val message: String) : SubwayTemplateDetailUiState()
    }

    private val _subwayTemplateDetailState = MutableLiveData<SubwayTemplateDetailUiState>()
    val subwayTemplateDetailState: LiveData<SubwayTemplateDetailUiState> = _subwayTemplateDetailState

    // 지하철역 검색 결과 UI 상태
    sealed class SubwaySearchUiState {
        object Loading : SubwaySearchUiState()
        data class Success(val response: SubwayTemplateResponse) : SubwaySearchUiState()
        data class Error(val message: String) : SubwaySearchUiState()
    }

    private val _subwaySearchState = MutableLiveData<SubwaySearchUiState>()
    val subwaySearchState: LiveData<SubwaySearchUiState> = _subwaySearchState

    init {
        fetchRecentImages()
        fetchPriorityTemplates()
        fetchSubwayTemplates()
    }

    fun fetchPriorityTemplates() {
        viewModelScope.launch {
            _priorityTemplatesState.value = PriorityTemplatesUiState.Loading
            val result = templateRepository.getPriorityTemplates()
            result.onSuccess {
                _priorityTemplatesState.value = PriorityTemplatesUiState.Success(it)
            }.onFailure {
                _priorityTemplatesState.value = PriorityTemplatesUiState.Error(it.message ?: "템플릿 목록을 불러오는데 실패했습니다.")
            }
        }
    }

    fun fetchPriorityTemplateDetail(templateId: Int) {
        viewModelScope.launch {
            _priorityTemplateDetailState.value = PriorityTemplateDetailUiState.Loading
            val result = templateRepository.getPriorityTemplateDetail(templateId)
            result.onSuccess {
                _priorityTemplateDetailState.value = PriorityTemplateDetailUiState.Success(it)
            }.onFailure {
                _priorityTemplateDetailState.value = PriorityTemplateDetailUiState.Error(it.message ?: "템플릿 상세 정보를 불러오는데 실패했습니다.")
            }
        }
    }

    fun fetchSubwayTemplates() {
        viewModelScope.launch {
            _subwayTemplatesState.value = SubwayTemplatesUiState.Loading
            val result = templateRepository.getSubwayTemplates()
            result.onSuccess {
                _subwayTemplatesState.value = SubwayTemplatesUiState.Success(it)
            }.onFailure {
                _subwayTemplatesState.value = SubwayTemplatesUiState.Error(it.message ?: "지하철역 목록을 불러오는데 실패했습니다.")
            }
        }
    }

    fun fetchSubwayTemplateDetail(templateId: Int) {
        viewModelScope.launch {
            _subwayTemplateDetailState.value = SubwayTemplateDetailUiState.Loading
            val result = templateRepository.getSubwayTemplateDetail(templateId)
            result.onSuccess {
                _subwayTemplateDetailState.value = SubwayTemplateDetailUiState.Success(it)
            }.onFailure {
                _subwayTemplateDetailState.value = SubwayTemplateDetailUiState.Error(it.message ?: "지하철 템플릿 상세 정보를 불러오는데 실패했습니다.")
            }
        }
    }

    // 지하철역 검색
    fun searchSubwayTemplates(keyword: String) {
        viewModelScope.launch {
            _subwaySearchState.value = SubwaySearchUiState.Loading
            val result = templateRepository.searchSubwayTemplates(keyword)
            result.onSuccess {
                _subwaySearchState.value = SubwaySearchUiState.Success(it)
            }.onFailure {
                _subwaySearchState.value = SubwaySearchUiState.Error(it.message ?: "지하철역 검색에 실패했습니다.")
            }
        }
    }

    fun fetchRecentImages() {
        viewModelScope.launch {
            _recentImagesState.value = RecentImagesUiState.Loading
            val result = imageRepository.getRecentImages()
            result.onSuccess {
                _recentImagesState.value = RecentImagesUiState.Success(it)
            }.onFailure {
                _recentImagesState.value = RecentImagesUiState.Error(it.message ?: "최근 이미지 목록을 불러오는데 실패했습니다.")
            }
        }
    }

    fun uploadImage(imageFile: File) {
        viewModelScope.launch {
            _uploadState.value = UploadUiState.Loading

            val requestFile = imageFile.asRequestBody("image/*".toMediaTypeOrNull())
            val body = MultipartBody.Part.createFormData("file", imageFile.name, requestFile)

            val imageName = imageFile.nameWithoutExtension
            val result = imageRepository.uploadImage(imageName, body)

            result.onSuccess {
                _uploadState.value = UploadUiState.Success(it)
                fetchRecentImages()
            }.onFailure {
                _uploadState.value = UploadUiState.Error(it.message ?: "업로드 중 오류가 발생했습니다.")
            }
        }
    }

    fun toggleFavoriteStatus(imageId: String, isFavorite: Boolean) {
        viewModelScope.launch {
            _favoriteState.value = FavoriteUiState.Loading
            val result = imageRepository.updateFavoriteStatus(imageId, isFavorite)
            result.onSuccess {
                _favoriteState.value = FavoriteUiState.Success(imageId, isFavorite)
                fetchRecentImages()
            }.onFailure {
                _favoriteState.value = FavoriteUiState.Error(it.message ?: "즐겨찾기 상태 변경에 실패했습니다.")
            }
        }
    }

    fun getImagePreview(imageId: String) {
        viewModelScope.launch {
            _previewState.value = PreviewUiState.Loading
            try {
                val imageIdAsInt = imageId.toInt()
                val result = imageRepository.getImagePreview(imageIdAsInt)
                result.onSuccess {
                    _previewState.value = PreviewUiState.Success(it)
                }.onFailure {
                    _previewState.value = PreviewUiState.Error(it.message ?: "미리보기 정보를 불러오는데 실패했습니다.")
                }
            } catch (e: NumberFormatException) {
                _previewState.value = PreviewUiState.Error("잘못된 이미지 ID 형식입니다: $imageId")
            }
        }
    }
}
