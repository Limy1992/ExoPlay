package com.lmy.libexoplay.exo

/**
 * 音乐进度参数值
 * CreateDate:2019/5/23
 * Author:lmy
 */
data class MergeAppendPlayPar(
        /**单曲音乐时长*/
        var musicDuration: Long = 0,
        /**每个音乐的进度条颜色*/
        var progressColor: Int = 0,
        /**播放地址*/
        var musicUrl:String = ""
)