package com.zzx.media.custom.view.glsurface

import android.content.Context
import android.graphics.SurfaceTexture
import com.zzx.media.custom.view.opengl.egl.Drawable2D
import com.zzx.media.custom.view.opengl.egl.FullFrameRect
import com.zzx.media.custom.view.opengl.egl.Texture2DProgram

/**@author Tomy
 * Created by Tomy on 2024/3/4.
 */
class VideoDrawer(val context: Context, val prefab: Drawable2D.Prefab = Drawable2D.Prefab.FULL_RECTANGLE): IDrawer {

    private var mTextureId: Int = -1

    private var mSurfaceTexture: SurfaceTexture? = null

    private var mStCallback: ((SurfaceTexture) -> Unit)? = null

    private var mFullFrameRect: FullFrameRect? = null
    private val mMatrix = FloatArray(16)


    override fun setTextureID(textureId: Int) {
        mTextureId = textureId
        mSurfaceTexture = SurfaceTexture(mTextureId)
        mStCallback?.invoke(mSurfaceTexture!!)
        createGLProgram()
    }

    override fun setViewPort(width: Int, height: Int) {
    }

    override fun draw() {
        if (mTextureId != -1) {
            mSurfaceTexture!!.updateTexImage()
            mSurfaceTexture!!.getTransformMatrix(mMatrix)
            mFullFrameRect?.drawFrame(mTextureId, mMatrix)
        }
    }

    override fun release() {
    }

    private fun createGLProgram() {
        if (mFullFrameRect == null) {
            mFullFrameRect = FullFrameRect(
                Texture2DProgram(Texture2DProgram.ProgramType.TEXTURE_EXT, context),
                prefab = prefab
            )
        }
    }

    override fun setSurfaceTextureCallback(cb: (st: SurfaceTexture) -> Unit) {
        mStCallback = cb
    }
}