package com.ivygames.morskoiboi.ui.view;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.ivygames.morskoiboi.R;

@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class HelpLayout extends NotepadRelativeLayout {

	public HelpLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	@Override
	protected void onFinishInflate() {
		super.onFinishInflate();

		final WebView webView = (WebView) findViewById(R.id.html_container);
		setBackgroundTransparent(webView);

		webView.setWebViewClient(new WebViewClient() {
			@Override
			public void onPageFinished(WebView view, String url) {
				setBackgroundTransparent(webView);
			}
		});

		// TODO: try to load in XML
		webView.loadUrl("file:///android_res/raw/battleship_help2.html");
	}

	private void setBackgroundTransparent(View view) {
		view.setBackgroundColor(0x00000000);
		if (Build.VERSION.SDK_INT >= 11) {
			setLayerType(View.LAYER_TYPE_SOFTWARE, null);
		}
	}
}
