package challenge.tomas.pt.imagegalleryapp.views;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;

import java.util.ArrayList;

import challenge.tomas.pt.imagegalleryapp.ImageGalleryApplication;
import challenge.tomas.pt.imagegalleryapp.R;
import challenge.tomas.pt.imagegalleryapp.data.FlickerSearchData;
import challenge.tomas.pt.imagegalleryapp.network.MobileNetworkManager;
import challenge.tomas.pt.imagegalleryapp.network.WifiNetworkManager;

/**
 * Main activity.
 * <p>
 * Modified by Tomas on 03/09/2018.
 */
public class MainActivity extends FragmentActivity {

    public ArrayList<FlickerSearchData> flickerSearchData = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WifiNetworkManager.getInstance(getImageGalleryApplication()).init();

        setContentView(R.layout.activity_main);

        MainFragment fragmentMain = new MainFragment();
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.fragment, fragmentMain);
        fragmentTransaction.commit();
    }

    public ImageGalleryApplication getImageGalleryApplication(){
        return (ImageGalleryApplication) getApplication();
    }

    public ArrayList<FlickerSearchData> getFlickerSearchData() {
        return flickerSearchData;
    }

    public void setFlickerSearchData(ArrayList<FlickerSearchData> flickerSearchData) {
        this.flickerSearchData = flickerSearchData;
    }

    public void clearSearchData() {
        flickerSearchData.clear();
    }

    @Override
    public void onBackPressed() {
        if (getFragmentManager().getBackStackEntryCount() > 0) {
            getFragmentManager().popBackStack();
        } else {
            super.onBackPressed();
        }
    }
}
