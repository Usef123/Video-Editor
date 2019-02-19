package med.umerfarooq.com.videoeditor.FFmpeg;

import android.content.Context;

import com.github.hiteshsondhi88.libffmpeg.FFmpeg;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import med.umerfarooq.com.videoeditor.VideoFeatures.VideoThemes;

@Module(
        injects = VideoThemes.class

)

@SuppressWarnings("unused")
public class DaggerDependencyModuleThmes
    {

    private final Context context;

    public DaggerDependencyModuleThmes(Context context) {
        this.context = context;
    }

    @Provides @Singleton
    FFmpeg provideFFmpeg() {
        return FFmpeg.getInstance(context.getApplicationContext());
    }

}
