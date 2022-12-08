package com.zzx.camera.glide

import android.content.Context
import com.bumptech.glide.GlideBuilder
import com.bumptech.glide.annotation.GlideModule
import com.bumptech.glide.load.DecodeFormat
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.module.AppGlideModule
import com.bumptech.glide.request.RequestOptions

/**@author Tomy
 * Created by Tomy on 2018/7/5.
 */
@GlideModule
class GlideModuleConfig: AppGlideModule() {

    override fun applyOptions(context: Context, builder: GlideBuilder) {
        super.applyOptions(context, builder)
        builder.setDefaultRequestOptions(RequestOptions()
                .optionalCircleCrop()
                .format(DecodeFormat.PREFER_RGB_565)
                .diskCacheStrategy(DiskCacheStrategy.NONE))


        /*builder.setDiskCache(InternalCacheDiskCacheFactory(context, "glide-images", 10 * 1024 * 1024))
        builder.setDiskCacheExecutor(GlideExecutor.newDiskCacheExecutor())

        builder.setSourceExecutor(GlideExecutor.newSourceExecutor())
        builder.setAnimationExecutor(GlideExecutor.newAnimationExecutor())

        val calculator = MemorySizeCalculator.Builder(context)
                .setMemoryCacheScreens(1f)
                .build()


        val maxMemory = Runtime.getRuntime().maxMemory() / 8
        builder.setMemoryCache(LruResourceCache(calculator.memoryCacheSize.toLong()))*/

    }

}