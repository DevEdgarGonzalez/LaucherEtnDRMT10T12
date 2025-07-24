package com.actia.help_aboutus;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.actia.mexico.launcher_t12_generico_2018.R;


public class WebViewFragment extends Fragment {

    private static final String ARG_URL = "URL";
    private static final String ARG_ENABLE_NAVIGATION = "ENABLE_NAVIGATION";
    private static final String ARG_SHOW_URL = "SHOW_URL";
    private static final String ARG_SHOW_TITLE = "SHOW_TITLE";

    private String url;
    private boolean enableNavigation;
    private boolean showUrl;
    private boolean showTitle;
    private ProgressBar pb_fwv;
    private ProgressBar progressBar;
    private ImageView imgvBack_fwv;
    private ImageView imgvRefresh_fwv;
    private TextView tvUrl_fwv;


    private WebView wvContainerWeb_fwv;

    public WebViewFragment() {
    }

    public static WebViewFragment newInstance(String url, boolean enableNavigation, boolean showUrl, boolean showTitle) {
        WebViewFragment fragment = new WebViewFragment();
        Bundle args = new Bundle();
        args.putString(ARG_URL, url);
        args.putBoolean(ARG_ENABLE_NAVIGATION, enableNavigation);
        args.putBoolean(ARG_SHOW_URL, showUrl);
        args.putBoolean(ARG_SHOW_TITLE, showTitle);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            url = getArguments().getString(ARG_URL);
            enableNavigation = getArguments().getBoolean(ARG_ENABLE_NAVIGATION);
            showUrl = getArguments().getBoolean(ARG_SHOW_URL);
            showTitle = getArguments().getBoolean(ARG_SHOW_TITLE);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_web_view, container, false);
    }

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        wvContainerWeb_fwv = view.findViewById(R.id.wvContainerWeb_fwv);
        pb_fwv = view.findViewById(R.id.pb_fwv);
        tvUrl_fwv = view.findViewById(R.id.tvUrl_fwv);
        imgvBack_fwv = view.findViewById(R.id.imgvBack_fwv);
        imgvRefresh_fwv = view.findViewById(R.id.imgvRefresh_fwv);
        progressBar = view.findViewById(R.id.progressBar);


        if (showUrl)
            tvUrl_fwv.setText(url);


        wvContainerWeb_fwv.loadUrl(url);
        wvContainerWeb_fwv.getSettings().setJavaScriptEnabled(true);
        wvContainerWeb_fwv.setWebViewClient(new WebClientLauncher(pb_fwv));

        if (enableNavigation == false) {
            imgvRefresh_fwv.setVisibility(View.INVISIBLE);
            imgvBack_fwv.setVisibility(View.INVISIBLE);
        }
        wvContainerWeb_fwv.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                super.onProgressChanged(view, newProgress);
                progressBar.setProgress(newProgress);
                managerLoad(newProgress);

            }

            @Override
            public void onReceivedTitle(WebView view, String title) {
                super.onReceivedTitle(view, title);
                if (showTitle)
                    tvUrl_fwv.setText(title);
            }

            @Override
            public void onReceivedIcon(WebView view, Bitmap icon) {
                super.onReceivedIcon(view, icon);
            }

            @Override
            public void onReceivedTouchIconUrl(WebView view, String url, boolean precomposed) {
                super.onReceivedTouchIconUrl(view, url, precomposed);
                tvUrl_fwv.setText(url);
            }

        });

        imgvBack_fwv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (wvContainerWeb_fwv.canGoBack()) {
                    wvContainerWeb_fwv.goBack();
                } else {
                    getActivity().onBackPressed();
                }
            }
        });

        imgvRefresh_fwv.setOnClickListener(new View.OnClickListener() {
                                               @Override
                                               public void onClick(View v) {
                                                   wvContainerWeb_fwv.reload();
                                               }
                                           }
        );

    }

    private void managerLoad(int progress) {

        int stateElementsLoading;
        int stateElementsComplete;

        if (progress < 100) {
            stateElementsLoading = View.VISIBLE;
            stateElementsComplete = View.INVISIBLE;
        } else {
            stateElementsLoading = View.INVISIBLE;
            stateElementsComplete = View.VISIBLE;
        }

        progressBar.setVisibility(stateElementsLoading);

        wvContainerWeb_fwv.setVisibility(stateElementsComplete);
        if (enableNavigation) {
            imgvBack_fwv.setVisibility(stateElementsComplete);
            imgvRefresh_fwv.setVisibility(stateElementsComplete);
        }


    }


}