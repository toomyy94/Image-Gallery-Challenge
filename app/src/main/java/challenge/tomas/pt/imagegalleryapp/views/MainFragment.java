package challenge.tomas.pt.imagegalleryapp.views;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.common.base.Strings;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import challenge.tomas.pt.imagegalleryapp.R;
import challenge.tomas.pt.imagegalleryapp.data.FlickerSearchData;
import challenge.tomas.pt.imagegalleryapp.http.client.HttpClient;
import challenge.tomas.pt.imagegalleryapp.network.MobileNetworkManager;
import challenge.tomas.pt.imagegalleryapp.network.WifiNetworkManager;
import io.reactivex.Single;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

import static android.content.Context.INPUT_METHOD_SERVICE;
import static challenge.tomas.pt.imagegalleryapp.utils.ImageGalleryUtils.*;

public class MainFragment extends Fragment {

    private MobileNetworkManager mobileNetworkManager;
    private WifiNetworkManager wifiNetworkManager;

    private TextView checkInternet;

    private TextInputEditText tagSearch;
    private TextInputEditText textSearch;

    private ImageButton tagButtonSearch;
    private ImageButton textButtonSearch;

    private String searchPhotosResponse;
    private String searchSizesResponse;

    private OnFragmentInteractionListener mListener;

    private HttpClient client = new HttpClient();

    private ArrayList<FlickerSearchData> flickerSearchDataList = new ArrayList<>();

    public MainFragment() { }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mobileNetworkManager = MobileNetworkManager.getInstance(((MainActivity) getActivity()).getImageGalleryApplication());
        wifiNetworkManager = WifiNetworkManager.getInstance(((MainActivity) getActivity()).getImageGalleryApplication());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        if (container != null) {
            container.removeAllViews();
        }

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_main, container, false);

        initializeViews(view);
        onListeners();

        return view;
    }

    private void onListeners() {
        tagButtonSearch.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                checkInternet.setText(R.string.loading);
                if(checkInternetConnectivity()){
                    Single.just(TAGS).subscribeOn(Schedulers.newThread()).subscribe(new Consumer<String>() { //Assyctask has too many code :)
                        @Override
                        public void accept(@NonNull String s) throws Exception {
                            String tagEndpoint = BASE_URL + GET_PHOTOS_SEARCH + API_KEY +
                                    GET_TAG + tagSearch.getText() + GET_PER_PAGE + ITEMS_PER_PAGE + ENDPOINT_ENDING;
                            Log.d("Sending ", tagEndpoint);
                            searchPhotosResponse = client.getRequest(tagEndpoint);
                            Log.d("TAG search server response: ", searchPhotosResponse);
                            viewListResults(SEARCH_OPTION_TAG, tagSearch.getText().toString());
                        }
                    });
                }
                else
                    checkInternet.setText(R.string.check_internet_connection);

                hideKeyboard(v);
            }
        });

        textButtonSearch.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                checkInternet.setText(R.string.loading);
                if(checkInternetConnectivity()){
                    Single.just(TITLE).subscribeOn(Schedulers.newThread()).subscribe(new Consumer<String>() { //Assyctask has too many code :)
                            @Override
                            public void accept(@NonNull String s) throws Exception {
                                String textEndpoint = BASE_URL + GET_PHOTOS_SEARCH + API_KEY +
                                        GET_TEXT + textSearch.getText() + GET_PER_PAGE + ITEMS_PER_PAGE + ENDPOINT_ENDING;
                                Log.d("Sending ", textEndpoint);
                                searchPhotosResponse = client.getRequest(textEndpoint);
                                Log.d("Text search server response: ", searchPhotosResponse);
                                viewListResults(SEARCH_OPTION_TEXT, textSearch.getText().toString());
                            }
                        });
                }
                else
                    checkInternet.setText(R.string.check_internet_connection);

                hideKeyboard(v);
            }
        });
    }

    private void viewListResults(int searchOption, String textToSearch) {
        fillSearchData();

        ListFragment fragmentList = ListFragment.newInstance(searchOption, textToSearch);
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragment, fragmentList).addToBackStack(SEARCH_OPTION);
        fragmentTransaction.commit();
    }

    private void fillSearchData(){
        flickerSearchDataList.clear();
        try {
            JSONObject jsonPhotosSearchResult = new JSONObject(searchPhotosResponse);

            JSONArray photoArray = jsonPhotosSearchResult.getJSONObject(PHOTOS).getJSONArray(PHOTO);

            for(int i = 0; i < photoArray.length(); i++){
                JSONObject onePhoto = photoArray.getJSONObject(i);
                final String oneID = onePhoto.getString(ID);

                String sizesEndpoint = BASE_URL + GET_PHOTOS_SEARCH + API_KEY + GET_TEXT + textSearch.getText() + ENDPOINT_ENDING;
                Log.d("Sending ", sizesEndpoint);
                searchSizesResponse = client.getRequest(BASE_URL + GET_PHOTO_SIZE + API_KEY + GET_PHOTO_ID + oneID + ENDPOINT_ENDING);
                Log.d("Sizes server response: ", searchSizesResponse);

                JSONObject jsonSizesSearchResult = new JSONObject(searchSizesResponse);
                JSONArray sizeArray = jsonSizesSearchResult.getJSONObject(SIZES).getJSONArray(SIZE);

                FlickerSearchData flickerSearchData = new FlickerSearchData();

                for(int j = 0; j < sizeArray.length(); j++){
                    JSONObject oneSize = sizeArray.getJSONObject(j);
                    if(oneSize.has(LABEL) && oneSize.has(SOURCE) && oneSize.getString(LABEL).equals(LARGE_SQUARE)){
                        flickerSearchData.setPhotoID(onePhoto.getString(ID));
                        flickerSearchData.setTitle(onePhoto.getString(TITLE));
                        flickerSearchData.setSquaredImage(new Pair<>(oneSize.getString(SOURCE), urlToBitmap(oneSize.getString(SOURCE))));
                    }
                    else if(oneSize.has(LABEL) && oneSize.has(SOURCE) && oneSize.getString(LABEL).equals(LARGE)){
                        flickerSearchData.setFullscreenImage(new Pair<>(oneSize.getString(SOURCE), urlToBitmap(oneSize.getString(SOURCE))));
                    }
                }

                flickerSearchDataList.add(flickerSearchData);
            }
        }catch (JSONException je){
            Log.e("Error", "Error on parsing json");
            je.printStackTrace();
        }

        ((MainActivity) getActivity()).setFlickerSearchData(flickerSearchDataList);
    }

    private static Bitmap urlToBitmap(String urlPath) {

        if(Strings.isNullOrEmpty(urlPath)){
            return BitmapFactory.decodeResource(Resources.getSystem(), R.drawable.no_image_found);
        }

        try {
            URL url = new URL(urlPath);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap myBitmap = BitmapFactory.decodeStream(input);
            return myBitmap;
        } catch (MalformedURLException e) {
            return BitmapFactory.decodeResource(Resources.getSystem(), R.drawable.no_image_found);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private void initializeViews(View view) {
        checkInternet = view.findViewById(R.id.internetCheck);
        tagSearch = view.findViewById(R.id.tagSearch);
        textSearch = view.findViewById(R.id.textSearch);
        tagButtonSearch = view.findViewById(R.id.tagButtonSearch);
        textButtonSearch = view.findViewById(R.id.textButtonSearch);
    }

    private boolean checkInternetConnectivity( ) {
        if(mobileNetworkManager.isMobileDataAvailable() || wifiNetworkManager.hasConnectivity())
            return true;
        else
            return false;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onImageButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    private void hideKeyboard(View view) {
        InputMethodManager inputMethodManager = (InputMethodManager) getActivity().getSystemService(INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }
}
