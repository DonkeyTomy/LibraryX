package com.zzx.camera.presenter

/**@author Tomy
 * Created by Tomy on 2018/10/6.
 */
interface IViewController {

    fun setRotation(rotation: Int)

    /**
     * 初始化UI
     */
    fun init()

    /**
     * 根据当前录像状态切换录像状态.
     * @see [startRecord]/[stopRecord]
     */
    fun toggleRecord()

    fun performCameraClick()

    /**
     * 开始录像.
     * @param imp Boolean
     */
    fun startRecord(imp: Boolean = false)

    /**
     * 停止录像.
     * @param enableCheckPreOrDelay Boolean
     */
    fun stopRecord(enableCheckPreOrDelay: Boolean = true)

    /***
     * 用户执行录像按键的入口.内部会判断是执行[startRecord]还是[toggleRecord]
     * @param imp Boolean
     */
    fun performRecord(imp: Boolean = false)

    /**
     * 拍照.
     */
    fun takePicture(needResult: Boolean = false, oneShot: Boolean = false)

    /**
     * 释放 View/Controller
     */
    fun release()

    fun releaseCamera()

    /**
     * @param show Boolean 是否显示重要文件图标.
     */
    fun showImpIcon(show: Boolean)

    /**
     * 控制录像UI状态
     */
    fun showRecordingStatus(show: Boolean)

    fun configurationChanged()

    fun hideAllView()

    fun showAllView()

    fun switchCamera()

    fun getCameraState(): Int

    fun isCapturing(): Boolean

}