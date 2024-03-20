package com.zzx.media.custom.view.opengl.egl

import android.opengl.GLES20
import android.opengl.Matrix
import android.os.SystemClock
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer

/**
 * Created by zzr on 2018/5/22.
 */
class WaterSignature {
    private val mVertexArray: FloatBuffer
    private val mTexCoordArray: FloatBuffer
    private val mCoordsPerVertex: Int
    private val mCoordsPerTexture: Int
    private val mVertexCount: Int
    private val mVertexStride: Int
    private val mTexCoordStride: Int
    var mProjectionMatrix = FloatArray(16) // 投影矩阵
    var mViewMatrix = FloatArray(16) // 摄像机位置朝向9参数矩阵
    var mModelMatrix = FloatArray(16) // 模型变换矩阵
    var mMVPMatrix = FloatArray(16) // 获取具体物体的总变换矩阵
    private val SIZE_OF_FLOAT = 4
    private val finalMatrix: FloatArray
        private get() {
            val time = SystemClock.uptimeMillis() % 4000L
            val angle = 0.090f * time.toInt()
            //        Matrix.setRotateM(mModelMatrix, 0, 90f, 0, 0, -1f);//矩陣旋轉 但是是在原有的圖像区域旋转  会变形(結合)
            Matrix.multiplyMM(mMVPMatrix, 0, mViewMatrix, 0, mModelMatrix, 0)
            Matrix.multiplyMM(mMVPMatrix, 0, mProjectionMatrix, 0, mMVPMatrix, 0)
            return mMVPMatrix
        }

    private fun createFloatBuffer(coords: FloatArray): FloatBuffer {
        val bb = ByteBuffer.allocateDirect(coords.size * SIZE_OF_FLOAT)
        bb.order(ByteOrder.nativeOrder())
        val fb = bb.asFloatBuffer()
        fb.put(coords)
        fb.position(0)
        return fb
    }

    private lateinit var mProgram: Texture2DProgram

    init {
        mVertexArray = createFloatBuffer(FULL_RECTANGLE_COORDS)
        mTexCoordArray = createFloatBuffer(FULL_RECTANGLE_TEX_COORDS)
        mCoordsPerVertex = 2
        mCoordsPerTexture = 2
        mVertexCount = FULL_RECTANGLE_COORDS.size / mCoordsPerVertex // 4
        mTexCoordStride = 2 * SIZE_OF_FLOAT
        mVertexStride = 2 * SIZE_OF_FLOAT
        Matrix.setIdentityM(mProjectionMatrix, 0)
        Matrix.setIdentityM(mViewMatrix, 0)
        Matrix.setIdentityM(mModelMatrix, 0)
        Matrix.setIdentityM(mMVPMatrix, 0)
    }

    fun setShaderProgram(mProgram: Texture2DProgram) {
        this.mProgram = mProgram
    }

    fun drawFrame(mTextureId: Int) {
        GLES20.glUseProgram(mProgram.mProgramHandle)
        // 设置纹理
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0)
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mTextureId)
        GLES20.glUniform1i(mProgram.sTextureLoc, 0)
        GLUtil.checkError("GL_TEXTURE_2D sTexture")
        // 设置 model / view / projection 矩阵
        GLES20.glUniformMatrix4fv(mProgram.muMVPMatrixLoc, 1, false, finalMatrix, 0)
        GLUtil.checkError("glUniformMatrix4fv uMVPMatrixLoc")
        // 使用简单的VAO 设置顶点坐标数据
        GLES20.glEnableVertexAttribArray(mProgram.maPositionLoc)
        GLES20.glVertexAttribPointer(
            mProgram.maPositionLoc, mCoordsPerVertex,
            GLES20.GL_FLOAT, false, mVertexStride, mVertexArray
        )
        GLUtil.checkError("VAO aPositionLoc")
        // 使用简单的VAO 设置纹理坐标数据
        GLES20.glEnableVertexAttribArray(mProgram.maTextureCoordLoc)
        GLES20.glVertexAttribPointer(
            mProgram.maTextureCoordLoc, mCoordsPerTexture,
            GLES20.GL_FLOAT, false, mTexCoordStride, mTexCoordArray
        )
        GLUtil.checkError("VAO aTextureCoordLoc")
        // GL_TRIANGLE_STRIP三角形带，这就为啥只需要指出4个坐标点，就能画出两个三角形了。
//        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, mVertexCount);
        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_FAN, 0, mVertexCount)
        // Done -- 解绑~
        GLES20.glDisableVertexAttribArray(mProgram.maPositionLoc)
        GLES20.glDisableVertexAttribArray(mProgram.maTextureCoordLoc)
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0)
        GLES20.glUseProgram(0)
    }

    companion object {
        /**
         * 一个“完整”的正方形，从两维延伸到-1到1。
         * 当 模型/视图/投影矩阵是都为单位矩阵的时候，这将完全覆盖视口。
         * 纹理坐标相对于矩形是y反的。
         * (This seems to work out right with external textures from SurfaceTexture.)
         */
        //    private static final float FULL_RECTANGLE_COORDS[] = {
        //            -1.0f, -1.0f,   // 0 bottom left
        //            1.0f, -1.0f,   // 1 bottom right
        //            -1.0f,  1.0f,   // 2 top left
        //            1.0f,  1.0f,   // 3 top right
        //    };
        //    private static final float FULL_RECTANGLE_TEX_COORDS[] = {
        //            0.0f, 1.0f,     //0 bottom left     //0.0f, 0.0f, // 0 bottom left
        //            1.0f, 1.0f,     //1 bottom right    //1.0f, 0.0f, // 1 bottom right
        //            0.0f, 0.0f,     //2 top left        //0.0f, 1.0f, // 2 top left
        //            1.0f, 0.0f,     //3 top right       //1.0f, 1.0f, // 3 top right
        //    };
        private val FULL_RECTANGLE_COORDS = floatArrayOf(
            0f, 0f,  // 0 bottom left
            -0.5f, 0.5f,  // 1 bottom right
            0.5f, 0.5f,  // 2 top left
            0.5f, -0.5f,  // 3 top right
            -0.5f, -0.5f,  // 1 bottom right
            -0.5f, 0.5f
        )
        private val FULL_RECTANGLE_TEX_COORDS = floatArrayOf(
            0.5f, 0.5f,
            0f, 0f,
            1f, 0f,
            1f, 1f,
            0f, 1f,
            0f, 0f
        )
    }
}
