package com.ruslan.fragmentdialog;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import org.commons.logger.Ln;

public class FragmentAlertDialog extends DialogFragment implements OnClickListener, DialogInterface {
	public static final String TAG = "DIALOG";
	
	static final String TITLE = "TITLE";
	static final String MESSAGE = "MESSAGE";
	static final String POSITIVE_BUTTON_TEXT = "POSITIVE_BUTTON_TEXT";
	static final String NEGATIVE_BUTTON_TEXT = "NEGATIVE_BUTTON_TEXT";
	
	/**
	 * helper method that shows note like dialog that has title message and OK
	 * button, that dismisses the dialog
	 * 
	 * @param fm
	 *            fragment manager
	 * @param tag
	 *            dialog tag to identify it
	 * @param msgId
	 *            resource id for the string to be displayed in the body
	 */
	public static void showNote(FragmentManager fm, String tag, int msgId) {
		showNote(fm, tag, View.NO_ID, msgId);
	}

	/**
	 * helper method that shows note like dialog that has title message and OK
	 * button, that dismisses the dialog
	 * 
	 * @param tag
	 *            dialog tag to identify it
	 * @param titleId
	 *            resource id for the string to be displayed at the title,
	 *            passing View.NO_ID disables title
	 * @param msgId
	 *            resource id for the string to be displayed in the body
	 */
	public static void showNote(FragmentManager fm, String tag, int titleId, int msgId) {
		AlertDialogBuilder builder = new AlertDialogBuilder();
		if (titleId != View.NO_ID) {
			builder.setTitle(titleId);
		}
		builder.setMessage(msgId).create().show(fm, tag);
	}

	protected boolean hasTitle;

	protected int noTitleLayout;

	protected boolean isSingleButton;

	private DialogInterface.OnClickListener positiveButtonListener;

	private OnCancelListener onCancelListener;

	private DialogInterface.OnClickListener negativeButtonListener;

	private View layout;

	@Override
	public void cancel() {
		if (onCancelListener != null) {
			onCancelListener.onCancel(this);
		}
	}

	private View createView() {
		return createView(getActivity().getLayoutInflater());
	}

	private View createView(LayoutInflater inflater) {
		// first read the arguments if present
		CharSequence title = getTextFromArguments(TITLE);
		CharSequence positiveButtonText = getTextFromArguments(POSITIVE_BUTTON_TEXT);
		CharSequence negativeButtonText = getTextFromArguments(NEGATIVE_BUTTON_TEXT);

		// these are just used for optimization + clarity
		boolean hasPositiveText = !TextUtils.isEmpty(positiveButtonText);
		boolean hasNegativeText = !TextUtils.isEmpty(negativeButtonText);

		isSingleButton = !(hasPositiveText && hasNegativeText);
		hasTitle = !TextUtils.isEmpty(title);

		// Inflate and set the layout for the dialog
		// Pass null as the parent view because its going in the dialog layout
		layout = inflater.inflate(getLayout(), null);

		// after layout is inflated - take care of layout specific widgets
		onLayoutCreated(layout);

		Button positiveButton = getPositiveButton(layout);
		Button negativeButton = getNegativeButton(layout);
		TextView dialogTitle = (TextView) layout.findViewById(R.id.dialog_title);

		// register listeners for buttons
		// we will listen for the clicks and forward them to the external
		// listener if present
		if (positiveButton != null) { // positive button might not be supplied
			positiveButton.setOnClickListener(this);
		}
		if (negativeButton != null) { // negative button might not be supplied
			negativeButton.setOnClickListener(this);
		}

		// Set button labels and visibility - visibility depends on if the text
		// for the button is set
		// In case there is only one button make the other one invisible.
		// In case both are invisible leave positive visible (it has 'OK" text
		// set in xml - we rely on that)
		if (hasPositiveText) {
			positiveButton.setVisibility(View.VISIBLE);
			positiveButton.setText(positiveButtonText);
		}

		if (hasNegativeText) {
			negativeButton.setVisibility(View.VISIBLE);
			negativeButton.setText(negativeButtonText);
		} else {
			positiveButton.setVisibility(View.VISIBLE);
		}

		if (hasTitle) {
			dialogTitle.setText(title);
		}

		return layout;
	}
	
	public View getContentView() {
		return layout;
	}

	protected int getLayout() {
		noTitleLayout = isSingleButton ? R.layout.alert_dialog_no_title_single : R.layout.alert_dialog_no_title;
		return hasTitle ? R.layout.alert_dialog : noTitleLayout;
	}

	protected Button getNegativeButton(View viewGroup) {
		return (Button) viewGroup.findViewById(R.id.negative_button);
	}

	protected Button getPositiveButton(View viewGroup) {
		return (Button) viewGroup.findViewById(R.id.positive_button);
	}

	private CharSequence getTextFromArguments(String key) {
		Bundle arguments = getArguments();
		if (arguments.containsKey(key)) {
			
			Object val = arguments.get(key);
			if (val instanceof CharSequence) {
				return (CharSequence) val;
			}
			else if (val instanceof Integer) {			
				return getText((Integer) val); 
			}
			else {
				Ln.w("wrong class: " + val.getClass());
			}
		}
		
		return "";
	}

	@Override
	public void onCancel(DialogInterface dialog) {
		super.onCancel(dialog);

		if (onCancelListener != null) { // cancel listener might not be supplied
			onCancelListener.onCancel(dialog);
		}
	}

	@Override
	public void onClick(View v) {
		int id = v.getId();
		if (id == R.id.negative_button) {
			if (negativeButtonListener != null) {
				negativeButtonListener.onClick(this, id);
			}
		} else if (id == R.id.positive_button) {
			if (positiveButtonListener != null) {
				positiveButtonListener.onClick(this, id);
			}
		}

		// dismiss the dialog after click
		dismiss();
	}

	@NonNull
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		// create dialog and set its layout
		Dialog dialog = new Dialog(getActivity(), R.style.Dialog);
		dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);

		dialog.setCanceledOnTouchOutside(false);
		dialog.setContentView(createView());

		// set cancel listener if present, XXX done in fragment
		// dialog.setOnCancelListener(onCancelListener);
		return dialog;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return createView(inflater);
	}

	/**
	 * takes care of layout specific widgets
	 */
	protected void onLayoutCreated(View viewGroup) {

		// if we used layout with title we should inflate stub with title-less
		// layout (we already have title)
		if (hasTitle) {
			ViewStub stub = (ViewStub) viewGroup.findViewById(R.id.stub);
			stub.setLayoutResource(noTitleLayout);
			stub.inflate();
		}

		TextView dialogMessage = (TextView) viewGroup.findViewById(R.id.dialog_text);
		if (dialogMessage != null) {
			CharSequence message = getTextFromArguments(MESSAGE);
			if (TextUtils.isEmpty(message)) {
				dialogMessage.setVisibility(View.GONE);
			} else {
				dialogMessage.setVisibility(View.VISIBLE);
				dialogMessage.setText(message);
			}
		}
	}

	public void setCancelListener(OnCancelListener onCancelListener) {
		this.onCancelListener = onCancelListener;
	}

	public void setNegativeButtonListener(DialogInterface.OnClickListener negativeButtonListener) {
		this.negativeButtonListener = negativeButtonListener;
	}

	public void setPositiveButtonListener(DialogInterface.OnClickListener positiveButtonListener) {
		this.positiveButtonListener = positiveButtonListener;
	}

	@Override
	public void show(FragmentManager manager, String tag) {
		Fragment fragment = manager.findFragmentByTag(TAG);
		if (fragment != null) {
			manager.beginTransaction().remove(fragment).commitAllowingStateLoss();
		}
		show(manager.beginTransaction(), tag);
	}

	@Override
	public int show(FragmentTransaction transaction, String tag) {
		transaction.add(this, tag);
		// transaction.addToBackStack(null);
		return transaction.commitAllowingStateLoss();
	}
}
