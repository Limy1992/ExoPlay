package com.lmy.libexoplay.exo

import android.content.Context
import android.net.Uri
import com.google.android.exoplayer2.*
import com.google.android.exoplayer2.source.ConcatenatingMediaSource
import com.google.android.exoplayer2.source.ExtractorMediaSource
import com.google.android.exoplayer2.source.MediaSource
import com.google.android.exoplayer2.source.dash.DashMediaSource
import com.google.android.exoplayer2.source.hls.HlsMediaSource
import com.google.android.exoplayer2.source.smoothstreaming.SsMediaSource
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.exoplayer2.util.Util

/**
 * CreateDate:2019/3/13
 * Author:lmy
 */
open class LMYExoPlay(mContext: Context) : LMYExoPlayListener(mContext) {
    companion object {
        const val LMY_EXO_PLAY = "LMYExoPlay"
    }

    private var dataSourceFactory: DefaultDataSourceFactory = DefaultDataSourceFactory(mContext.applicationContext
            , Util.getUserAgent(mContext.applicationContext, "LMYExoPlay"))

    override fun getExoPlay(): SimpleExoPlayer {
        return exoPlayer
    }

    /**
     * 设置播放地址
     * @param playUrl 播放地址
     */
    override fun setPlayUrl(playUrl: String) {
        setPlayUrl(playUrl, true)
    }

    /**
     * 设置播放地址
     * @param playUrl 播放地址
     * @param isStartPlay 是否开始播放 true 播放， false暂停
     */
    override fun setPlayUrl(playUrl: String, isStartPlay: Boolean) {
        val encodeUriString = if (!playUrl.contains("://")) {
            Uri.encode(playUrl)
        } else {
            playUrl
        }
        val mediaSourcePlayUrl = buildMediaSource(Uri.parse(encodeUriString))
        exoPlayer.playWhenReady = isStartPlay
        exoPlayer.prepare(mediaSourcePlayUrl, false, true)
    }

    override fun setPlayUrl(playUrl: List<String>) {
        val uris = arrayOfNulls<Uri>(playUrl.size)
        for (i in playUrl.indices) {
            val encodeUriString = if (!playUrl.contains("://")) {
                Uri.encode(playUrl[i])
            } else {
                playUrl[i]
            }
            uris[i] = Uri.parse(encodeUriString)
        }
        val mediaSources = arrayOfNulls<MediaSource>(uris.size)
        for (index in uris.indices) {
            mediaSources[index] = buildMediaSource(uris[index]!!)
        }
        val mediaSource = if (mediaSources.size == 1) mediaSources[0] else ConcatenatingMediaSource(*mediaSources)
        exoPlayer.prepare(mediaSource)
    }

    override fun seekTo(seekValue: Long) {
        exoPlayer.seekTo(seekValue)
    }

    override fun start() {
        exoPlayer.playWhenReady = true
    }

    override fun rePlay() {
        exoPlayer.seekTo(exoPlayer.currentWindowIndex, C.TIME_UNSET)
    }

    override fun pause() {
        exoPlayer.playWhenReady = false
    }

    override fun stop() {
        exoPlayer.stop()
    }

    override fun release() {
        if (isPlaying()) {
            stop()
        }
        exoPlayer.release()
    }

    override fun isPlaying(): Boolean {
        return exoPlayer.playWhenReady
    }

    override fun isPlayOver(): Boolean {
        return exoPlayer.playbackState == Player.STATE_ENDED
    }

    override fun getCurrentDuration(): Long {
//        val currentTimeline = exoPlayer.currentTimeline
//        val contentDuration = exoPlayer.contentDuration
//        LogUtils.i("exoPlayer.currentPosition："+exoPlayer.currentPosition)
        return exoPlayer.currentPosition
    }

    fun getContentBufferedPosition(): Long {
        return exoPlayer.contentBufferedPosition
    }

    override fun getDuration(): Long {
        return exoPlayer.duration
    }

    /**
     * 倍率播放
     * @param speed 播放速度 > 0F
     */
    override fun setSpeed(speed: Float) {
        setSpeed(speed, 0f)
    }

    /**
     * 倍率播放
     * @param speed 播放速度 > 0F
     * @param pitch 变调 > 0F
     */
    override fun setSpeed(speed: Float, pitch: Float) {
        val playbackParameters = PlaybackParameters(speed, pitch)
        exoPlayer.playbackParameters = playbackParameters
    }

    /**
     * 当前播放速度
     */
    fun getSpeed(): Float {
        return exoPlayer.playbackParameters.speed
    }

    fun getPitch(): Float {
        return exoPlayer.playbackParameters.pitch
    }

    override fun setVolume(volume: Float) {
        exoPlayer.volume = volume
    }

    protected fun buildMediaSource(uri: Uri): MediaSource {
        @C.ContentType val type = Util.inferContentType(uri)
        return when (type) {
            C.TYPE_DASH -> DashMediaSource.Factory(dataSourceFactory).createMediaSource(uri)
            C.TYPE_SS -> SsMediaSource.Factory(dataSourceFactory).createMediaSource(uri)
            C.TYPE_HLS -> HlsMediaSource.Factory(dataSourceFactory).createMediaSource(uri)
            C.TYPE_OTHER -> ExtractorMediaSource.Factory(dataSourceFactory).createMediaSource(uri)
            else -> throw IllegalStateException("Unsupported type: $type") as Throwable
        }
    }
}