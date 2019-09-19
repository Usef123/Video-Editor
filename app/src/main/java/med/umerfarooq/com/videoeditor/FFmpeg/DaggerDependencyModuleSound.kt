package med.umerfarooq.com.videoeditor.FFmpeg

import android.content.Context

import com.github.hiteshsondhi88.libffmpeg.FFmpeg

import javax.inject.Singleton

import dagger.Module
import dagger.Provides
import med.umerfarooq.com.videoeditor.VideoFeatures.VideoSound

@Module(injects = [VideoSound::class])
class DaggerDependencyModuleSound(private val context: Context) {

    @Provides
    @Singleton
    internal fun provideFFmpeg(): FFmpeg {
        return FFmpeg.getInstance(context.applicationContext)
    }

}
