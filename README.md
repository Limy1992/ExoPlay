# ExoPlay
类似MediaPlay Api简易封装
package com.lmy.libexoplay.exo

import com.google.android.exoplayer2.SimpleExoPlayer

/**
 * 播放接口
 * CreateDate:2019/3/15
 * Author:lmy
 */
interface ILMYExoPlay {

    fun getExoPlay(): SimpleExoPlayer
    
    /**设置播放源*/
    fun setPlayUrl(playUrl: String)

    /**
     * 设置播放源
     * @param playUrl 播放源
     * @param isStartPlay if true play else false
     * */
    fun setPlayUrl(playUrl: String, isStartPlay: Boolean)

    /**
     * 设置播放源
     * @param playUrl 播放合集
     * */
    fun setPlayUrl(playUrl: List<String>)

    /**开始播放*/
    fun start()

    /**重新播放*/
    fun rePlay()

    /**暂停*/
    fun pause()

    /**停止 恢复STATE_IDLE状态*/
    fun stop()

    /**释放播放器，下次使用需要重新创建实例*/
    fun release()

    /**是否正在播放*/
    fun isPlaying(): Boolean

    /**当前的音乐是否播放完毕*/
    fun isPlayOver():Boolean

    /**变速*/
    fun setSpeed(speed: Float)

    /**
     * 倍率播放变调
     * @param speed 播放速度 > 0F
     * @param pitch 变调 > 0F
     */
    fun setSpeed(speed: Float, pitch: Float)

    fun seekTo(seekValue: Long)

    /**当前播放进度*/
    fun getCurrentDuration(): Long

    /**当前总的播放进度*/
    fun getDuration(): Long

    fun setVolume(volume: Float)

    /**音乐播放状态*/
    fun setOnAudioPlayListener(onAudioPlayListener: OnAudioPlayListener)

    /**循环当前播放*/
    fun setLoopPlayCurrent(loopPlay: Boolean)

    interface OnAudioPlayListener {
        /**prepare设置完url后，音乐准备完毕调用*/
        fun onPrepared(){}

        /**开始播放*/
        fun onStartPlay() {}

        /**暂停播放*/
        fun onPausePlay() {}

        /**播放结束*/
        fun onCompletion() {}
    }
}
