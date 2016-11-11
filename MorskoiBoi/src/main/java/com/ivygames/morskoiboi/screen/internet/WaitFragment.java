package com.ivygames.morskoiboi.screen.internet;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ivygames.common.DebugUtils;
import com.ivygames.morskoiboi.R;

import org.commons.logger.Ln;

public class WaitFragment extends Fragment {

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

    @Override
    public String toString() {
        return DebugUtils.getSimpleName(this);
    }
}
