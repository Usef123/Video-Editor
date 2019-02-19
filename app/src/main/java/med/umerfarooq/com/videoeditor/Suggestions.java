package med.umerfarooq.com.videoeditor;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;
import med.umerfarooq.com.videoeditor.Database.TinyDB;
import med.umerfarooq.com.videoeditor.Other.others;
import med.umerfarooq.com.videoeditor.VideoFeatures.Listhome.CustomAdapter;
import med.umerfarooq.com.videoeditor.VideoFeatures.Listhome.DataModel;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class Suggestions
        extends AppCompatActivity
    {
        private static CustomAdapter adapter;
        String url;
        String title;
        String description;
        Button button;
        TextView textone, texttwo;
        ArrayList<DataModel> dataModels;
        ListView listView;
        private AdView mAdView;
        private Button btnFullscreenAd, btnShowRewardedVideoAd;
        private CircleImageView mCircleImageView;
        private TinyDB tinydb;

        @Override
        protected void attachBaseContext(Context newBase)
        {
            super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
        }

        @Override
        protected void onCreate(Bundle savedInstanceState)
        {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_suggestions);
            CalligraphyConfig.initDefault(new CalligraphyConfig.Builder().setDefaultFontPath("fonts/Roboto-Medium.ttf").setFontAttrId(R.attr.fontPath).build());





            View adContainer = findViewById(R.id.adView);
            AdView mAdView = new AdView(Suggestions.this);
            mAdView.setAdSize(AdSize.SMART_BANNER);
            ((FrameLayout) adContainer).addView(mAdView);
            mAdView.setAdUnitId(others.BANNER);

            AdRequest adRequest = new AdRequest.Builder().addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                    // Check the LogCat to get your test device ID
                    .addTestDevice("C04B1BFFB0774708339BC273F8A43708").build();

            mAdView.setAdListener(new AdListener()
                {
                    @Override
                    public void onAdLoaded()
                    {
                    }

                    @Override
                    public void onAdClosed()
                    {

                    }

                    @Override
                    public void onAdFailedToLoad(int errorCode)
                    {

                    }

                    @Override
                    public void onAdLeftApplication()
                    {
//
                    }

                    @Override
                    public void onAdOpened()
                    {
                        super.onAdOpened();
                    }
                });

            mAdView.loadAd(adRequest);


            tinydb = new TinyDB(Suggestions.this);
            final ArrayList<String> url = tinydb.getListString("icon_url");
            ArrayList<String> title = tinydb.getListString("title");
            ArrayList<String> description = tinydb.getListString("description");
            final ArrayList<String> target_url = tinydb.getListString("target_url");
            listView = (ListView) findViewById(R.id.list);

            dataModels = new ArrayList<>();
            for (int i = 0; i < title.size(); i++) {
                {

                    dataModels.add(new DataModel(url.get(i),title.get(i),description.get(i)));

                }

            }


            adapter = new CustomAdapter(dataModels,getApplicationContext());

            listView.setAdapter(adapter);
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener()
                {
                    @Override
                    public void onItemClick(AdapterView<?> parent,View view,int position,long id)
                    {

                        Intent i = new Intent(Intent.ACTION_VIEW);
                        i.setData(Uri.parse(target_url.get(position)));
                        startActivity(i);


                    }

                });

            RelativeLayout app = findViewById(R.id.app);
            app.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        startActivity(new Intent(Suggestions.this,LoadingScreen.class));
                    }
                });
        }


    }
