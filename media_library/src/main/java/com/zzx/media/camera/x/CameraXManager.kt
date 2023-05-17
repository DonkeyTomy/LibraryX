package com.zzx.media.camera.x

import android.content.Context
import android.graphics.Rect
import android.graphics.SurfaceTexture
import android.util.Size
import android.view.Surface
import androidx.camera.core.Camera
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.concurrent.futures.await
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import com.zzx.media.camera.CameraCore
import com.zzx.media.camera.ICameraManager
import com.zzx.media.recorder.IRecorder
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async

/**@author Tomy
 * Created by Tomy on 2023/5/17.
 */
class CameraXManager(private val mContext: Context, private val mLifecycleOwner: LifecycleOwner): ICameraManager<Surface, Camera> {

    private var mInitDeferred: Deferred<Unit>? = null

    private lateinit var mCameraProvider: ProcessCameraProvider

    init {
        mInitDeferred = mLifecycleOwner.lifecycleScope.async {
            mCameraProvider = ProcessCameraProvider.getInstance(mContext).await()
        }
    }

    override fun openFrontCamera() {
    }

    override fun openBackCamera() {
    }

    override fun openExternalCamera() {
    }

    override fun isCameraOpening(): Boolean {
    }

    override fun openSpecialCamera(cameraId: Int) {
    }

    override fun setPreviewSurfaceTexture(surfaceTexture: SurfaceTexture) {
    }

    override fun startPreview() {
    }

    override fun startPreview(surfaceTexture: SurfaceTexture) {
    }

    override fun startPreview(surface: Surface) {
    }

    override fun setPreviewDataCallback(previewDataCallback: ICameraManager.PreviewDataCallback?) {
    }

    override fun stopPreview() {
    }

    override fun startRecordPreview(surface: Surface?) {
    }

    override fun startRecord() {
    }

    override fun setIRecorder(recorder: IRecorder) {
    }

    override fun startAutoFocus(focusCallback: ICameraManager.AutoFocusCallback?) {
    }

    override fun setAutoFocusCallback(focusCallback: ICameraManager.AutoFocusCallback?) {
    }

    override fun cancelAutoFocus() {
    }

    override fun focusOnRect(focusRect: Rect, focusCallback: ICameraManager.AutoFocusCallback?) {
    }

    override fun focusOnPoint(
        x: Int,
        y: Int,
        screenWidth: Int,
        screenHeight: Int,
        horWidth: Int,
        verHeight: Int,
        focusCallback: ICameraManager.AutoFocusCallback?
    ) {
    }

    override fun getMaxNumFocusAreas(): Int {
    }

    override fun getFocusRect(): List<Rect> {
    }

    override fun getSupportFocusMode(): List<String> {
    }

    override fun setFocusMode(focusMode: String) {
    }

    override fun isManualFocusSupported(): Boolean {
    }

    override fun isVideoAutoFocusSupported(): Boolean {
    }

    override fun isPictureAutoFocusSupported(): Boolean {
    }

    override fun isBurstModeSupported(): Boolean {
    }

    override fun getCameraCore(): CameraCore<Camera> {
    }

    override fun stopRecord() {
    }

    override fun closeCamera() {
    }

    override fun releaseCamera() {
    }

    override fun getCameraCount(): Int {
    }

    override fun getSupportPreviewSizeList(): Array<Size> {
    }

    override fun getSupportPreviewFormatList(): Array<Int> {
    }

    override fun setPreviewParams(width: Int, height: Int, format: Int) {
    }

    override fun getSupportCaptureSizeList(): Array<Size> {
    }

    override fun getSupportCaptureFormatList(): Array<Int> {
    }

    override fun setCaptureParams(width: Int, height: Int, format: Int) {
    }

    override fun getSupportRecordSizeList(): Array<Size> {
    }

    override fun getSensorOrientation(): Int {
    }

    override fun takePicture(callback: ICameraManager.PictureCallback?) {
    }

    override fun takePicture() {
    }

    override fun takePictureBurst(count: Int, callback: ICameraManager.PictureCallback?) {
    }

    override fun takePictureBurst(count: Int) {
    }

    override fun startContinuousShot(count: Int, callback: ICameraManager.PictureCallback?) {
    }

    override fun cancelContinuousShot() {
    }

    override fun setContinuousShotSpeed(speed: Int) {
    }

    override fun setPictureCallback(callback: ICameraManager.PictureCallback?) {
    }

    override fun setRecordPreviewCallback(callback: ICameraManager.RecordPreviewReady?) {
    }

    override fun setPictureBurstMode(pictureCount: Int) {
    }

    override fun setPictureNormalMode() {
    }

    override fun setDisplayOrientation(rotation: Int) {
    }

    override fun setPictureRotation(rotation: Int) {
    }

    override fun enableShutter(enable: Boolean) {
    }

    override fun zoomUp(level: Int) {
    }

    override fun zoomDown(level: Int) {
    }

    override fun getZoomMax(): Int {
    }

    override fun setZoomLevel(level: Int) {
    }

    override fun setFlashOn() {
    }

    override fun setFlashOff() {
    }

    override fun setColorEffect(colorEffect: String) {
    }

    override fun getColorEffect(): String {
    }

    override fun restartPreview() {
    }

    override fun getCameraDevice(): Camera? {
    }

    override fun setStateCallback(stateCallback: ICameraManager.CameraStateCallback<Camera>) {
    }

    override fun setPreviewSurface(surface: Surface) {
    }
}