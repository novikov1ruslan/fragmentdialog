package com.ivygames.morskoiboi.ui.view;

import java.util.PriorityQueue;

import org.commons.logger.Ln;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Rect;
import android.os.Build;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.RelativeLayout;

import com.ivygames.morskoiboi.R;
import com.ivygames.morskoiboi.model.Board;
import com.ivygames.morskoiboi.model.Ship;

public class BoardSetupLayout extends RelativeLayout implements View.OnClickListener {
	public interface BoardSetupLayoutListener {
		void done();

		void autoSetup();

		/**
		 * @param showTips
		 *            true if tips are shown
		 */
		void toggleTips(boolean showTips);
	}

	public BoardSetupLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	private SetupBoardView mBoardView;
	private BoardSetupLayoutListener mScreenActions;
	private View mHelpButton;
	private View mTipsPannel;
	private Rect mHelpBounds;

	public void setScreenActionsListener(BoardSetupLayoutListener listener) {
		mScreenActions = listener;
	}

	@Override
	protected void onFinishInflate() {
		super.onFinishInflate();
		if (isInEditMode()) {
			return;
		}

		mBoardView = (SetupBoardView) findViewById(R.id.board_view);
		findViewById(R.id.auto_setup).setOnClickListener(this);
		findViewById(R.id.done).setOnClickListener(this);
		View closeButton = findViewById(R.id.close_button);
		if (closeButton != null) {
			closeButton.setOnClickListener(this);
		}
		mHelpButton = findViewById(R.id.help_button);
		if (mHelpButton != null) {
			mHelpButton.setOnClickListener(this);
		}
		mTipsPannel = findViewById(R.id.tips_pannel);

		mHelpBounds = new Rect();

		View htmlContainer = findViewById(R.id.html_container);
		if (htmlContainer != null) {
			Ln.v("HTML container found, loading help");
			final WebView webView = (WebView) htmlContainer;
			setBackgroundTransparent(webView);

			webView.setWebViewClient(new WebViewClient() {
				@Override
				public void onPageFinished(WebView view, String url) {
					setBackgroundTransparent(webView);
				}
			});

			// TODO: try to load in XML
			webView.loadUrl("file:///android_res/raw/setup_help.html");
		}
	}

	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	private void setBackgroundTransparent(View view) {
		view.setBackgroundColor(0x00000000);
		if (Build.VERSION.SDK_INT >= 11) {
			view.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
		}
	}

	@Override
	public void onClick(View v) {
		int id = v.getId();

		if (id == R.id.auto_setup) {
			mScreenActions.autoSetup();
		} else if (id == R.id.done) {
			mScreenActions.done();
		} else if (id == R.id.close_button) {
			hideTips();
			mScreenActions.toggleTips(false);
		} else if (id == R.id.help_button) {
			showTips();
			mScreenActions.toggleTips(true);
		}
	}

	public void setBoard(Board board, PriorityQueue<Ship> fleet) {
		mBoardView.setBoard(board);
		mBoardView.setFleet(fleet);
	}

	@Override
	public boolean dispatchTouchEvent(MotionEvent ev) {
		if (mTipsPannel != null && mTipsPannel.getVisibility() == VISIBLE) {
			return super.dispatchTouchEvent(ev);
		}

		if (mHelpButton != null) {
			mHelpBounds.left = mHelpButton.getLeft();
			mHelpBounds.top = mHelpButton.getTop();
			mHelpBounds.right = mHelpButton.getRight();
			mHelpBounds.bottom = mHelpButton.getBottom();
			boolean contains = mHelpBounds.contains((int) ev.getX(), (int) ev.getY());
			if (contains) {
				ev.setLocation(ev.getX() - mHelpBounds.left, ev.getY() - mHelpBounds.top);
				mHelpButton.onTouchEvent(ev);
				return true;
			}
		}
		return super.dispatchTouchEvent(ev);
	}

	public boolean isSet() {
		return mBoardView.isSet();
	}

	public void showTips() {
		if (mTipsPannel != null) {
			mTipsPannel.setVisibility(VISIBLE);
		}

		if (mHelpButton != null) {
			mHelpButton.setVisibility(GONE);
		}
	}

	public void hideTips() {
		if (mTipsPannel != null) {
			mTipsPannel.setVisibility(GONE);
		}

		if (mHelpButton != null) {
			mHelpButton.setVisibility(VISIBLE);
		}
	}
}
