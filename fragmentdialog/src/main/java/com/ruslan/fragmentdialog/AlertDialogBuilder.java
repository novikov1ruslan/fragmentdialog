package com.ruslan.fragmentdialog;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.os.Bundle;

public class AlertDialogBuilder {

	private Bundle args;
	private DialogInterface.OnClickListener positiveButtonListener;
	private DialogInterface.OnClickListener negativeButtonListener;
	private OnCancelListener cancelListener;

	public AlertDialogBuilder() {
		args = new Bundle();
	}

	/**
	 * Set the message to display using the given resource id.
	 * 
	 * @return This Builder object to allow for chaining of calls to set
	 *         methods
	 */
	public AlertDialogBuilder setMessage(int messageId) {
		args.putInt(FragmentAlertDialog.MESSAGE, messageId);
		return this;
	}

	public AlertDialogBuilder setMessage(CharSequence message) {
		args.putCharSequence(FragmentAlertDialog.MESSAGE, message);
		return this;
	}
	
	protected FragmentAlertDialog createInternal() {
		return new FragmentAlertDialog();
	}

	public FragmentAlertDialog create() {
		FragmentAlertDialog dialog = createInternal();
		dialog.setArguments(args);
		dialog.setPositiveButtonListener(positiveButtonListener);
		dialog.setNegativeButtonListener(negativeButtonListener);
		dialog.setCancelListener(cancelListener);
		return dialog;
	}

	/**
	 * Set the title using the given resource id.
	 * 
	 * @return This Builder object to allow for chaining of calls to set
	 *         methods
	 */
	public AlertDialogBuilder setTitle(int resId) {
		args.putInt(FragmentAlertDialog.TITLE, resId);
		return this;
	}

	/**
	 * Set the title displayed in the {@link Dialog}.
	 * 
	 * @return This Builder object to allow for chaining of calls to set
	 *         methods
	 */
	public AlertDialogBuilder setTitle(CharSequence title) {
		args.putCharSequence(FragmentAlertDialog.TITLE, title);
		return this;
	}

	/**
	 * @param text
	 *            The text to display in the positive button
	 * @return This Builder object to allow for chaining of calls to set
	 *         methods
	 */
	private AlertDialogBuilder setPositiveButton(CharSequence text) {
		args.putCharSequence(FragmentAlertDialog.POSITIVE_BUTTON_TEXT, text);
		return this;
	}

	/**
	 * @param text
	 *            The text to display in the negative button
	 * @return This Builder object to allow for chaining of calls to set
	 *         methods
	 */
	private AlertDialogBuilder setNegativeButton(CharSequence text) {
		args.putCharSequence(FragmentAlertDialog.NEGATIVE_BUTTON_TEXT, text);
		return this;
	}

	/**
	 * @return This Builder object to allow for chaining of calls to set
	 *         methods
	 */
	public AlertDialogBuilder setNegativeButton(int resId) {
		args.putInt(FragmentAlertDialog.NEGATIVE_BUTTON_TEXT, resId);
		return this;
	}

	/**
	 * @param resId
	 *            The resource id of the text to display in the positive
	 *            button
	 * @return This Builder object to allow for chaining of calls to set
	 *         methods
	 */
	public AlertDialogBuilder setPositiveButton(int resId) {
		args.putInt(FragmentAlertDialog.POSITIVE_BUTTON_TEXT, resId);
		return this;
	}

	/**
	 * Set a listener to be invoked when the positive button of the dialog
	 * is pressed.
	 * 
	 * @param text
	 *            The text to display in the positive button
	 * @param listener
	 *            The {@link DialogInterface.OnClickListener} to use.
	 * @return This Builder object to allow for chaining of calls to set
	 *         methods
	 */
	public AlertDialogBuilder setPositiveButton(CharSequence text, DialogInterface.OnClickListener listener) {
		setPositiveButton(text);
		positiveButtonListener = listener;
		return this;
	}

	/**
	 * Set a listener to be invoked when the positive button of the dialog
	 * is pressed.
	 * 
	 * @param textId
	 *            The resource id of the text to display in the positive
	 *            button
	 * @param listener
	 *            The {@link DialogInterface.OnClickListener} to use.
	 * @return This Builder object to allow for chaining of calls to set
	 *         methods
	 */
	public AlertDialogBuilder setPositiveButton(int textId, DialogInterface.OnClickListener listener) {
		setPositiveButton(textId);
		positiveButtonListener = listener;
		return this;
	}

	/**
	 * Set a listener to be invoked when the negative button of the dialog
	 * is pressed.
	 * 
	 * @param text
	 *            The text to display in the negative button
	 * @param listener
	 *            The {@link DialogInterface.OnClickListener} to use.
	 * @return This Builder object to allow for chaining of calls to set
	 *         methods
	 */
	public AlertDialogBuilder setNegativeButton(CharSequence text, DialogInterface.OnClickListener listener) {
		setNegativeButton(text);
		negativeButtonListener = listener;
		return this;
	}

	/**
	 * Set a listener to be invoked when the negative button of the dialog
	 * is pressed.
	 * 
	 * @param textId
	 *            The resource id of the text to display in the negative
	 *            button
	 * @param listener
	 *            The {@link DialogInterface.OnClickListener} to use.
	 * @return This Builder object to allow for chaining of calls to set
	 *         methods
	 */
	public AlertDialogBuilder setNegativeButton(int textId, DialogInterface.OnClickListener listener) {
		setNegativeButton(textId);
		negativeButtonListener = listener;
		return this;
	}

	/**
	 * Sets the callback that will be called if the dialog is canceled.
	 * 
	 * @return This Builder object to allow for chaining of calls to set
	 *         methods
	 */
	public AlertDialogBuilder setOnCancelListener(OnCancelListener listener) {
		this.cancelListener = listener;
		return this;
	}
}
