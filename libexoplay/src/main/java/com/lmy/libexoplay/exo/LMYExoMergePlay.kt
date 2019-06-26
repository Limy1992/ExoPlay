package com.lmy.libexoplay.exo

import android.content.Context
import android.net.Uri
import com.google.android.exoplayer2.source.ConcatenatingMediaSource
import com.google.android.exoplayer2.source.MediaSource
import java.util.*

/**
 * 合并播放
 * CreateDate:2019/6/11
 * Author:lmy
 */
class LMYExoMergePlay(mContext: Context) : LMYExoPlay(mContext) {
    private var thumbSeatValue: Long = 0
    //记录合并播放的参数
    private var concatList: MutableList<Concatenating>? = null
    //记录每个最大的播放时间进行叠加，用于seekTo比较， 当前seekTo到哪个音乐
    private var musicDuration = 0L
    var rePlayCurrentPosition = 0L

    fun setMergeAppendPlayUrl(playUrls: List<MergeAppendPlayPar>) {
        if (concatList == null) {
            concatList = ArrayList()
        } else {
            concatList?.clear()
        }
        musicDuration = 0L
        for (index in playUrls.indices) {
            val p = playUrls[index]
            musicDuration += p.musicDuration
            concatList?.add(Concatenating(index, musicDuration))
        }

        val uris = arrayOfNulls<Uri>(playUrls.size)
        for (i in playUrls.indices) {
            val musicUrl = playUrls[i].musicUrl
            val encodeUriString = if (!musicUrl.contains("://")) {
                Uri.encode(musicUrl)
            } else {
                musicUrl
            }
            uris[i] = Uri.parse(encodeUriString)
        }

        val mediaSources = arrayOfNulls<MediaSource>(uris!!.size)
        for (index in uris.indices) {
            mediaSources[index] = buildMediaSource(uris[index]!!)
        }
        val mediaSource = if (mediaSources.size == 1) mediaSources[0] else ConcatenatingMediaSource(*mediaSources)
        exoPlayer.prepare(mediaSource)
    }

    fun seekToMergeAppend(seekValue: Long, thumbSeatValue: Long) {
        this.thumbSeatValue = thumbSeatValue
//        LogUtils.i("当前seekTo的时间：$seekValue")
        if (concatList != null) {
            //seekTo的时候，记录第几个播放音乐
            var reCurrentWindowIndex = 0
            //指定音乐的播放位置
            var reCurrentPlayDuration = 0L
            for (concatenating in concatList!!) {
                val recodeCurrentDuration = concatenating.recodeCurrentDuration
                if (seekValue > recodeCurrentDuration) {
                    reCurrentWindowIndex = concatenating.currentWindowIndex + 1
                    if (reCurrentWindowIndex == concatList!!.size) {
                        reCurrentWindowIndex -= 1
                    }
                    reCurrentPlayDuration = seekValue - recodeCurrentDuration
                    rePlayCurrentPosition = seekValue - exoPlayer.currentPosition
                }
            }
            exoPlayer.seekTo(reCurrentWindowIndex, if (reCurrentPlayDuration > 0) reCurrentPlayDuration else seekValue)
        }
    }

    //当前合并播放的位置 用于判断是否叠加播放进度  从位置2开始叠加
    var recodeCurrentWindowIndex = 1
    //当前获取播放进度的值 用于叠加播放进度
    var expPlayerCurrentPosition = 0L

    /**
     * 播放时间
     */
    fun getCurrentMergeAppendDuration(): Long {
        val currentWindowIndex = exoPlayer.currentWindowIndex
        return if (currentWindowIndex > 0) {
            if (recodeCurrentWindowIndex < currentWindowIndex) {
                rePlayCurrentPosition += expPlayerCurrentPosition
            }
            recodeCurrentWindowIndex = currentWindowIndex
            expPlayerCurrentPosition = exoPlayer.currentPosition
            exoPlayer.currentPosition + rePlayCurrentPosition
        } else {
            rePlayCurrentPosition = exoPlayer.currentPosition
            rePlayCurrentPosition
        }
    }

    fun getDurationMergeAppend(): Long {
        return musicDuration
    }
}