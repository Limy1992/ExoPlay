package com.lmy.libexoplay.exo

import android.content.Context
import com.google.android.exoplayer2.*
import com.google.android.exoplayer2.util.Log

/**
 * 播放状态监听
 * CreateDate:2019/3/17
 * Author:lmy
 */
abstract class LMYExoPlayListener(mContext: Context) : ILMYExoPlay {
    private var isPrepared = false
    private var isLoopPlay = false
    var exoPlayer: SimpleExoPlayer = ExoPlayerFactory.newSimpleInstance(mContext.applicationContext)

    init {
        exoPlayer.addListener(object : Player.EventListener {
            override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {
                if (playbackState == Player.STATE_ENDED) {
                    Log.d(LMYExoPlay.LMY_EXO_PLAY, "音乐播放结束")
                    mOnAudioPlayListener?.onCompletion()
                    if (isLoopPlay) {
                        exoPlayer.seekTo(exoPlayer.currentWindowIndex, C.TIME_UNSET)
                        start()
                    }
                } else if (playbackState == Player.STATE_READY) {
                    if (playWhenReady) {
                        Log.d(LMYExoPlay.LMY_EXO_PLAY, "音乐开始播放")
                        mOnAudioPlayListener?.onStartPlay()
                    } else {
                        Log.d(LMYExoPlay.LMY_EXO_PLAY, "音乐暂停")
                        mOnAudioPlayListener?.onPausePlay()
                    }
                    if (!isPrepared) {
                        isPrepared = true
                        Log.d(LMYExoPlay.LMY_EXO_PLAY, "音乐准备完成")
                        mOnAudioPlayListener?.onPrepared()

                    }
                } else if (playbackState == Player.STATE_BUFFERING) {
                    Log.d(LMYExoPlay.LMY_EXO_PLAY, "网络数据缓存中")
                } else if (playbackState == Player.STATE_IDLE && playWhenReady) {
                }

                Log.d(LMYExoPlay.LMY_EXO_PLAY, "playbackState=$playbackState,playWhenReady=$playWhenReady")
            }

            override fun onPlayerError(error: ExoPlaybackException?) {
                Log.d(LMYExoPlay.LMY_EXO_PLAY, "音乐播放失败了： $error")
            }

            override fun onPositionDiscontinuity(reason: Int) {
            }
        })

        val audioAttributes = com.google.android.exoplayer2.audio.AudioAttributes.Builder()
                .setContentType(C.CONTENT_TYPE_UNKNOWN)
                .build()

        exoPlayer.audioAttributes = audioAttributes
    }

    var mOnAudioPlayListener: ILMYExoPlay.OnAudioPlayListener? = null

    override fun setOnAudioPlayListener(onAudioPlayListener: ILMYExoPlay.OnAudioPlayListener) {
        this.mOnAudioPlayListener = onAudioPlayListener
    }

    override fun setLoopPlayCurrent(loopPlay: Boolean) {
        this.isLoopPlay = loopPlay
    }
}