package com.example.beaceful.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import com.example.beaceful.domain.model.Collection
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MusicPlayerViewModel @Inject constructor(
    private val context: Application
) : AndroidViewModel(context) {

    private val _currentCollection = MutableStateFlow<Collection?>(null)
    val currentCollection: StateFlow<Collection?> = _currentCollection

    private val _isPlaying = MutableStateFlow(false)
    val isPlaying: StateFlow<Boolean> = _isPlaying

    private val _currentPosition = MutableStateFlow(0L)
    val currentPosition: StateFlow<Long> = _currentPosition

    private val _duration = MutableStateFlow(0L)
    val duration: StateFlow<Long> = _duration

    private var positionJob: Job? = null

    fun playCollection(collection: Collection) {
        _currentCollection.value = collection
        val mediaItem = MediaItem.fromUri(collection.resourceUrl)
        player.repeatMode = Player.REPEAT_MODE_ONE
        player.setMediaItem(mediaItem)
        player.prepare()
        player.play()
        _isPlaying.value = true
        trackPlaybackPosition()
    }

    fun togglePlayback() {
        player.repeatMode = Player.REPEAT_MODE_ONE
        if (player.isPlaying) {
            player.pause()
            _isPlaying.value = false
        } else {
            player.play()
            _isPlaying.value = true
            trackPlaybackPosition()
        }
    }

    private fun trackPlaybackPosition() {
        positionJob?.cancel()
        positionJob = viewModelScope.launch {
            while (true) {
                _currentPosition.value = player.currentPosition
                _duration.value = player.duration
                delay(500L)
            }
        }
    }

    fun stopAndClear() {
        player.stop()
        _isPlaying.value = false
        _currentCollection.value = null
        positionJob?.cancel()
    }

    fun seekTo(position: Long) {
        player.seekTo(position)
    }

    override fun onCleared() {
        super.onCleared()
        player.release()
        positionJob?.cancel()
    }


    val player: ExoPlayer = ExoPlayer.Builder(context).build()


    fun stop() {
        player.stop()
        _isPlaying.value = false
    }
}
