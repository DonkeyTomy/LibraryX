package com.zzx.media.custom.view.glsurface

import android.graphics.SurfaceTexture

/**@author Tomy
 * Created by Tomy on 2024/3/4.
 */
interface IDrawer {

    fun setTextureID(textureId: Int)

    fun setViewPort(width: Int, height: Int)

    fun draw()

    fun release()

    fun setSurfaceTextureCallback(cb: (st: SurfaceTexture) -> Unit)

}