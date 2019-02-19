package med.umerfarooq.com.videoeditor.FFmpeg;

import android.content.Context;

import com.github.hiteshsondhi88.libffmpeg.FFmpeg;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import med.umerfarooq.com.videoeditor.VideoFeatures.VideoSound;

@Module(
        injects = VideoSound.class

)

@SuppressWarnings("unused")
public class DaggerDependencyModuleSound
    {

    private final Context context;

    public DaggerDependencyModuleSound(Context context) {
        this.context = context;
    }

    @Provides @Singleton
    FFmpeg provideFFmpeg() {
        return FFmpeg.getInstance(context.getApplicationContext());
    }

}
