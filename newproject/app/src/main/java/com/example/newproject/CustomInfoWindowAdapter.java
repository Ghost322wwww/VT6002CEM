package com.example.newproject;

import android.content.Context;
import android.graphics.Rect;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.newproject.model.PlaceInfo;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;

public class CustomInfoWindowAdapter implements GoogleMap.InfoWindowAdapter {
    private final View mWindow;
    private Context mContext;
    private ImageButton cancelButton,shareButton,bookmarkButton;

    public CustomInfoWindowAdapter(Context context) {
        mContext = context;
        mWindow = LayoutInflater.from(context).inflate(R.layout.custom_info_window, null);
    }

    private void renderWindowText(Marker marker, View view) {
        String title = marker.getTitle();
        TextView tvTitle = view.findViewById(R.id.title);

        if (!title.equals("")) {
            tvTitle.setText(title);
        }

        String snippet = marker.getSnippet();
        TextView tvSnippet = view.findViewById(R.id.snippet);

        if (!snippet.equals("")) {
            tvSnippet.setText(snippet);
        }
        PlaceInfo placeInfo = (PlaceInfo) marker.getTag();
        ImageView photo = mWindow.findViewById(R.id.placephoto);
        if (placeInfo != null && placeInfo.getPhoto() != null) {
            photo.setImageBitmap(placeInfo.getPhoto());
        }
    }

    @Override
    public View getInfoWindow(Marker marker) {
        renderWindowText(marker, mWindow);
        shareButton = mWindow.findViewById(R.id.shareButton);
        bookmarkButton = mWindow.findViewById(R.id.bookmarkButton);
        cancelButton = mWindow.findViewById(R.id.cancelButton);
        shareButton = mWindow.findViewById(R.id.shareButton);
        bookmarkButton = mWindow.findViewById(R.id.bookmarkButton);

        // Set onClick listeners
        shareButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        bookmarkButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                marker.hideInfoWindow();
            }
        });
        return mWindow;
    }

    @Override
    public View getInfoContents(Marker marker) {
        renderWindowText(marker, mWindow);
        return mWindow;
    }

    public Rect getCancelButtonRect() {
        if (cancelButton == null) {
            return null;
        }
        // Get location of cancelButton in the window
        int[] location = new int[2];
        cancelButton.getLocationInWindow(location);
        return new Rect(location[0], location[1], location[0] + cancelButton.getWidth(), location[1] + cancelButton.getHeight());
    }
}