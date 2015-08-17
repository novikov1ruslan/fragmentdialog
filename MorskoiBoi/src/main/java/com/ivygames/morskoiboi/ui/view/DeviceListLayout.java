package com.ivygames.morskoiboi.ui.view;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.ivygames.morskoiboi.R;

import org.commons.logger.Ln;

import java.util.Set;

public class DeviceListLayout extends NotepadLinearLayout implements OnItemClickListener {
    private static final String TAG = "bluetooth";

    public interface DeviceListActions {
        void selectDevice(String info);
    }

    private DeviceListActions mListener;
    private final ArrayAdapter<String> mDevicesAdapter;
    private ListView mDevicesListView;
//    private final Animation mRotation;

    public DeviceListLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        mDevicesAdapter = new ArrayAdapter<String>(getContext(), R.layout.device_name);
//        mRotation = AnimationUtils.loadAnimation(context, R.anim.clockwise_refresh);
//        mRotation.setRepeatCount(Animation.INFINITE);
    }

    public void setListener(DeviceListActions screenActions) {
        mListener = screenActions;
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
//        mTitleView = (TextView) findViewById(R.id.title);
        mDevicesListView = (ListView) findViewById(android.R.id.list);
        mDevicesListView.setAdapter(mDevicesAdapter);
        mDevicesListView.setOnItemClickListener(this);

//        mTitleView.setText(R.string.discovering); // TODO: static
    }

    public void addBondedDevice(BluetoothDevice device) {
        mDevicesAdapter.add(device.getName() + "\n" + device.getAddress());
        mDevicesAdapter.notifyDataSetChanged();
        Ln.v(TAG + ": " + device + " added");
    }

//    public void connectingTo(String name) {
//        mTitleView.setText(getResources().getString(R.string.connecting_to, name));
//    }
//
//    public void connectionFailed() {
//        mTitleView.setText(R.string.select_device);
//    }

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

    public void enable() {
        mDevicesListView.setEnabled(true);
    }

    public void disable() {
        mDevicesListView.setEnabled(false);
    }
}
