package com.ivygames.morskoiboi.screen.internet;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ivygames.morskoiboi.R;

import org.commons.logger.Ln;

public class WaitFragment extends Fragment {
    private static final String TAG = "WAIT";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        boolean recreating = savedInstanceState != null;
        if (recreating) {
            return null;
        }

        View layout = inflater.inflate(R.layout.wait, container, false);

        Ln.d(this + " screen created");
        return layout;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Ln.v(this + " fragment destroyed");
    }

    private String debugSuffix() {
        return "(" + (hashCode() % 1000) + ")";
    }

    @Override
    public String toString() {
        return TAG + debugSuffix();
    }
}
