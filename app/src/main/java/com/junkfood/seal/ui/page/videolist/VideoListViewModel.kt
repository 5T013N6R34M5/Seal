package com.junkfood.seal.ui.page.videolist

import androidx.lifecycle.ViewModel
import com.junkfood.seal.database.DownloadedVideoInfo
import com.junkfood.seal.util.DatabaseUtil
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update

private const val TAG = "VideoListViewModel"

class VideoListViewModel : ViewModel() {

    private val _mediaInfoFlow = DatabaseUtil.getMediaInfo()

    private val mediaInfoFlow: Flow<List<DownloadedVideoInfo>> =
        _mediaInfoFlow.map { it.reversed().sortedBy { info -> info.filterByType() } }


    val filterSetFlow = _mediaInfoFlow.map { infoList ->
        mutableSetOf<String>().apply {
            infoList.forEach {
                this.add(it.extractor)
            }
        }
    }

    private val mutableStateFlow = MutableStateFlow(VideoListViewState())
    val stateFlow = mutableStateFlow.asStateFlow()

    val videoListFlow = mediaInfoFlow
    fun clickVideoFilter() {
        if (mutableStateFlow.value.videoFilter) mutableStateFlow.update { it.copy(videoFilter = false) }
        else mutableStateFlow.update { it.copy(videoFilter = true, audioFilter = false) }
    }

    fun clickAudioFilter() {
        if (mutableStateFlow.value.audioFilter) mutableStateFlow.update { it.copy(audioFilter = false) }
        else mutableStateFlow.update { it.copy(audioFilter = true, videoFilter = false) }
    }

    fun clickExtractorFilter(index: Int) {
        if (mutableStateFlow.value.activeFilterIndex == index) mutableStateFlow.update {
            it.copy(
                activeFilterIndex = -1
            )
        }
        else mutableStateFlow.update { it.copy(activeFilterIndex = index) }
    }

    data class VideoListViewState(
        val activeFilterIndex: Int = -1,
        val videoFilter: Boolean = false,
        val audioFilter: Boolean = false
    )

}