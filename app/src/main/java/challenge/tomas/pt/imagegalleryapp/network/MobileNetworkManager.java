package challenge.tomas.pt.imagegalleryapp.network;

import android.content.Context;
import android.net.ConnectivityManager;
import android.provider.Settings;
import android.telephony.TelephonyManager;

import com.google.common.base.Strings;

import challenge.tomas.pt.imagegalleryapp.ImageGalleryApplication;


/**
 * Manager to get device network related information.
 * <p>
 * Modified by Tomás Rodrigues on 20-01-2018.
 */
public class MobileNetworkManager {

    private final static String MOBILE_DATA_ON_COMMAND = "svc data enable\n";
    private final static String MOBILE_DATA_OFF_COMMAND = "svc data disable\n";

    private static final int UNKNOWN = -1;

    private static MobileNetworkManager instance;

    private final TelephonyManager telephonyManager;

    private final ConnectivityManager connectivityManager;

    private final ImageGalleryApplication application;

    private MobileNetworkManager(ImageGalleryApplication application) {
        this.application = application;
        telephonyManager = (TelephonyManager) application.getSystemService(Context.TELEPHONY_SERVICE);
        connectivityManager = (ConnectivityManager) application.getSystemService(Context.CONNECTIVITY_SERVICE);
    }

    /**
     * Gets the manager instance.
     *
     * @param application the application object.
     * @return the manager instance.
     */
    public synchronized static MobileNetworkManager getInstance(ImageGalleryApplication application) {
        if (instance == null) {
            instance = new MobileNetworkManager(application);
        }
        return instance;
    }

    /**
     * Gets if the mobile network is available.
     *
     * @return if the mobile network is available.
     */
    public boolean isMobileAvailable() {
        return isSimReady() && Strings.nullToEmpty(getNetworkOperator()).length() > 0;
    }

    /**
     * Gets if the mobile data is available.
     *
     * @return if the mobile data is available.
     */
    public boolean isMobileDataAvailable() {
        boolean mobileYN = false;

        if (isSimReady()) {
            if(android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN_MR1) {
                mobileYN = Settings.Global.getInt(application.getContentResolver(), "mobile_data", 1) == 1;
            }
            else{
                mobileYN = Settings.Secure.getInt(application.getContentResolver(), "mobile_data", 1) == 1;
            }
        }

        return mobileYN;
    }

    public int getSimState() {

        int simState = telephonyManager.getSimState();
        switch (simState) {
            case TelephonyManager.SIM_STATE_PIN_REQUIRED:
                return 1;
            case TelephonyManager.SIM_STATE_PUK_REQUIRED:
                return 2;
            case TelephonyManager.SIM_STATE_READY:
                return 0;
            case TelephonyManager.SIM_STATE_NETWORK_LOCKED:
            case TelephonyManager.SIM_STATE_ABSENT:
            case TelephonyManager.SIM_STATE_UNKNOWN:
            default:
                return 3;
        }
    }

    public String getCountryCode() {
        return telephonyManager.getNetworkCountryIso();
    }

    private String getNetworkOperatorName() {
        return telephonyManager.getNetworkOperatorName();
    }

    private String getNetworkOperator() {
        return telephonyManager.getNetworkOperator();
    }

    private boolean isSimReady() {
        return telephonyManager.getSimState() == TelephonyManager.SIM_STATE_READY;
    }

    public static boolean isSimOnDevice(Context context)
    {
        TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);  //gets the current TelephonyManager
        return !(tm.getSimState() == TelephonyManager.SIM_STATE_ABSENT);
    }
}
