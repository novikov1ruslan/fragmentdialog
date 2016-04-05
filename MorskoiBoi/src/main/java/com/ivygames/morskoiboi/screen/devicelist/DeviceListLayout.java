package com.ivygames.morskoiboi.screen.devicelist;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.ivygames.morskoiboi.R;
import com.ivygames.morskoiboi.screen.view.NotepadLinearLayout;

import org.commons.logger.Ln;

import java.util.Set;

public class DeviceListLayout extends NotepadLinearLayout implements OnItemClickListener, View.OnClickListener {
    private static final String TAG = "bluetooth";
    private final Animation mRotation;
    private View mScanBtn;

    private DeviceListActions mListener;
    private final ArrayAdapter<String> mDevicesAdapter;
    private ListView mDevicesListView;
    private View mDiscovering;

    public DeviceListLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        mDevicesAdapter = new ArrayAdapter<>(getContext(), R.layout.device_name);
        mRotation = AnimationUtils.loadAnimation(context, R.anim.clockwise_refresh);
        mRotation.setRepeatCount(Animation.INFINITE);
    }

    public void setListener(DeviceListActions screenActions) {
        mListener = screenActions;
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        mDevicesListView = (ListView) findViewById(android.R.id.list);
        mDevicesListView.setAdapter(mDevicesAdapter);
        mDevicesListView.setOnItemClickListener(this);
        mScanBtn = findViewById(R.id.scan_btn);
        mScanBtn.setOnClickListener(this);
        mDiscovering = findViewById(R.id.discovering);
    }

    public void addBondedDevice(BluetoothDevice device) {
        mDevicesAdapter.add(device.getName() + "\n" + device.getAddress());
        mDevicesAdapter.notifyDataSetChanged();
        Ln.v(TAG + ": " + device + " added");
    }

    public void setBondedDevices(Set<BluetoothDevice> bondedDevices) {
        Ln.d(TAG + ": retrieved bonded devices: " + bondedDevices);
        mDevicesAdapter.clear();
        for (BluetoothDevice device : bondedDevices) {
            addBondedDevice(device);
        }
    }

    @Override
    public void onItemClick(AdapterView<?> av, View v, int arg2, long arg3) {
        mListener.selectDevice(((TextView) v).getText().toString());
    }

    @Override
    public void onClick(View v) {
        mListener.scan();
    }

    public void enable() {
        mDevicesListView.setEnabled(true);
    }

    public void disable() {
        mDevicesListView.setEnabled(false);
    }

    public void discoveryStarted() {
        mScanBtn.setVisibility(GONE);
        mDiscovering.setVisibility(VISIBLE);
        mDiscovering.startAnimation(mRotation);
    }

    public void discoveryFinished() {
        mDiscovering.clearAnimation();
        mDiscovering.setVisibility(GONE);
        mScanBtn.setVisibility(VISIBLE);
    }
}
