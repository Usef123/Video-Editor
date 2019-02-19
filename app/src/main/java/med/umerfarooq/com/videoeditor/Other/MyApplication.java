package med.umerfarooq.com.videoeditor.Other;

/**
 * Created by Umerfarooq on 4/12/2018.
 */

import android.app.Application;
import com.google.android.gms.ads.MobileAds;

import med.umerfarooq.com.videoeditor.R;

/**
 * Created by ravi on 25/12/17.
 */

public class MyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        // initialize the AdMob app
        MobileAds.initialize(this, getString(R.string.admob_app_id));
    }
}