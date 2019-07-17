package challenge.tomas.pt.imagegalleryapp.views;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import challenge.tomas.pt.imagegalleryapp.R;

import static challenge.tomas.pt.imagegalleryapp.utils.ImageGalleryUtils.SEARCH_OPTION;
import static challenge.tomas.pt.imagegalleryapp.utils.ImageGalleryUtils.SEARCH_OPTION_TAG;
import static challenge.tomas.pt.imagegalleryapp.utils.ImageGalleryUtils.SEARCH_OPTION_TEXT;
import static challenge.tomas.pt.imagegalleryapp.utils.ImageGalleryUtils.TEXT_TO_SEARCH;

public class ListFragment extends Fragment {

    private int searchOption;
    private String textToSearch;
    private OnFragmentInteractionListener mListener;

    //Header
    private TextView listHeader;

    //Floating button
    FloatingActionButton fab;

    //ListView
    private SearchListAdapter adapter;
    private GridView gridView;

    public ListFragment() {
        // Required empty public constructor
    }

    public static ListFragment newInstance(int searchOption, String textToSearch) {
        ListFragment fragment = new ListFragment();
        Bundle args = new Bundle();
        args.putInt(SEARCH_OPTION, searchOption);
        args.putString(TEXT_TO_SEARCH, textToSearch);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            searchOption = getArguments().getInt(SEARCH_OPTION);
            textToSearch = getArguments().getString(TEXT_TO_SEARCH);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        if (container != null) {
            container.removeAllViews();
        }

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_list, container, false);

        initializeViews(view);
        initializeListeners();

        return view;
    }

    private void initializeViews(View view) {
        fab = view.findViewById(R.id.fab);

        //list header
        listHeader = view.findViewById(R.id.searchBy);
        if(searchOption == SEARCH_OPTION_TAG)
            listHeader.setText(getResources().getString(R.string.search_by_tag) + " " + textToSearch);
        if(searchOption == SEARCH_OPTION_TEXT)
            listHeader.setText(getResources().getString(R.string.search_by_text) + " " + textToSearch);

        //ListView
        gridView = view.findViewById(R.id.grid_view);

        adapter = new SearchListAdapter(((MainActivity) getActivity()).getFlickerSearchData(), ((MainActivity) getActivity()).getImageGalleryApplication().getApplicationContext());
        gridView.setAdapter(adapter);
    }

    private void initializeListeners() {

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(((MainActivity) getActivity()).getFlickerSearchData().get(position).getFullscreenImage().first));
                getContext().startActivity(browserIntent);
            }
        });

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MainFragment mainFragment = new MainFragment();
                FragmentManager fragmentManager = getFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.fragment, mainFragment);
                fragmentTransaction.commit();
            }
        });
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
