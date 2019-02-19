package med.umerfarooq.com.videoeditor.FFmpeg;

import android.content.Context;

import com.github.hiteshsondhi88.libffmpeg.FFmpeg;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import med.umerfarooq.com.videoeditor.VideoFeatures.VideoStickers;

@Module(
        injects = VideoStickers.class

)

@SuppressWarnings("unused")
public class DaggerDependencyModuleStickers
    {

    private final Context context;

    public DaggerDependencyModuleStickers(Context context) {
        this.context = context;
    }

    @Provides @Singleton
    FFmpeg provideFFmpeg() {
        return FFmpeg.getInstance(context.getApplicationContext());
    }

}
