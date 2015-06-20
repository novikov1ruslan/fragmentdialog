package com.ivygames.morskoiboi.ui.view;

import java.util.Set;

import org.commons.logger.Ln;

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

public class DeviceListLayout extends NotepadLinearLayout implements View.OnClickListener {
	public interface DeviceListActions {
		void ensureDiscoverable();

		void doDiscover();

		void selectDevice(String info);
	}

	private DeviceListActions mListener;
	private final ArrayAdapter<String> mDevicesAdapter;
	private ListView mDevicesListView;
	private View mDiscoveryAnimation;
	private TextView mTitleView;
	private final Animation mRotation;
	private View mEnsureButton;

	public DeviceListLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
		mDevicesAdapter = new ArrayAdapter<String>(getContext(), R.layout.device_name);
		mRotation = AnimationUtils.loadAnimation(context, R.anim.clockwise_refresh);
		mRotation.setRepeatCount(Animation.INFINITE);
	}

	public void setListener(DeviceListActions screenActions) {
		mListener = screenActions;
	}

	@Override
	protected void onFinishInflate() {
		super.onFinishInflate();
		mTitleView = (TextView) findViewById(R.id.title);
		mDevicesListView = (ListView) findViewById(android.R.id.list);
		mDevicesListView.setAdapter(mDevicesAdapter);
		mDevicesListView.setOnItemClickListener(mDeviceClickListener);
		mDiscoverBtn = findViewById(R.id.discover_btn);
		mDiscoverBtn.setOnClickListener(this);
		mDiscoveryAnimation = findViewById(R.id.discovery_animation);
		mEnsureButton = findViewById(R.id.ensure_discoverable_btn);
		mEnsureButton.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.discover_btn:
			mListener.doDiscover();
			break;

		case R.id.ensure_discoverable_btn:
			mListener.ensureDiscoverable();
			break;

		default:
			Ln.w("unprocessed bt button=" + v.getId());
			break;
		}
	}

	public void addBondedDevice(BluetoothDevice device) {
		mDevicesAdapter.add(device.getName() + "\n" + device.getAddress());
	}

	public void hideEnsureDiscoverable() {
		mEnsureButton.setVisibility(INVISIBLE);
	}

	public void showEnsureDiscoverable() {
		mEnsureButton.setVisibility(VISIBLE);
	}

	public void startDiscovery() {
		mTitleView.setText(R.string.discovering);
		mDevicesListView.setVisibility(GONE);
		mDiscoverBtn.setVisibility(GONE);
		// mEmptyView.setVisibility(GONE);
		mDiscoveryAnimation.setVisibility(VISIBLE);
		mDiscoveryAnimation.startAnimation(mRotation);
	}

	public void cancelDiscovery() {
		mDiscoveryAnimation.clearAnimation();
		mDiscoveryAnimation.setVisibility(GONE);
		mDevicesListView.setVisibility(VISIBLE);
		mDiscoverBtn.setVisibility(VISIBLE);
		if (mDevicesAdapter.isEmpty()) {
			mTitleView.setText(R.string.none_found);
		} else {
			mTitleView.setText(R.string.select_device);
		}
	}

	// The on-click listener for all devices in the ListViews
	private final OnItemClickListener mDeviceClickListener = new OnItemClickListener() {
		@Override
		public void onItemClick(AdapterView<?> av, View v, int arg2, long arg3) {
			mListener.selectDevice(((TextView) v).getText().toString());
		}
	};
	private View mDiscoverBtn;

	public void connectingTo(String name) {
		mTitleView.setText(getResources().getString(R.string.connecting_to, name));
	}

	public void connectionFailed() {
		mTitleView.setText(R.string.select_device);
	}

	public void setBondedDevices(Set<BluetoothDevice> bondedDevices) {
		mDevicesAdapter.clear();
		for (BluetoothDevice device : bondedDevices) {
			addBondedDevice(device);
			Ln.v(device + " added");
		}
		// mDevicesAdapter.notifyDataSetChanged();
	}
}
