package com.ivygames.morskoiboi.ui.view;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.RelativeLayout;

import com.ivygames.morskoiboi.R;
import com.ivygames.morskoiboi.model.Board;
import com.ivygames.morskoiboi.model.Ship;

import org.commons.logger.Ln;

import java.util.PriorityQueue;

public class BoardSetupLayout extends RelativeLayout implements View.OnClickListener {
    private View mTutView;

    public interface BoardSetupLayoutListener {
        void done();

        void autoSetup();

        void showHelp();

        void dismissTutorial();
    }

    public BoardSetupLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    private SetupBoardView mBoardView;
    private BoardSetupLayoutListener mScreenActions;

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
        View helpButton = findViewById(R.id.help_button);
        if (helpButton != null) {
            helpButton.setOnClickListener(this);
        }

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

    public View setTutView(View view) {
        mTutView = view;
        return view;
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();

        switch (id) {
            case R.id.auto_setup:
                mScreenActions.autoSetup();
                break;
            case R.id.done:
                mScreenActions.done();
                break;
            case R.id.help_button:
                mScreenActions.showHelp();
                break;
            case R.id.got_it_button:
                mScreenActions.dismissTutorial();
                break;
            default:
                Ln.w("unprocessed select button =" + v.getId());
                break;
        }

    }

    public void setBoard(Board board, PriorityQueue<Ship> fleet) {
        mBoardView.setBoard(board);
        mBoardView.setFleet(fleet);
    }

    public boolean isSet() {
        return mBoardView.isSet();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        mTutView.measure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        mTutView.layout(l, t, r, b);
        float padding = getResources().getDimension(R.dimen.tut_screen_padding);

        View drag = mTutView.findViewById(R.id.drag_gesture);
        int height = mBoardView.getHeight();
        int width = mBoardView.getWidth();
        drag.setX(width / 4 - drag.getWidth() / 2);
        drag.setY(height / 8);

        View placeText = mTutView.findViewById(R.id.place_text);
        placeText.setY(drag.getY() + drag.getHeight() + padding);

        View tap = mTutView.findViewById(R.id.tap_gesture);
        tap.setX(width / 2 - tap.getWidth() / 2);
        tap.setY(height / 2);

        View rotateText = mTutView.findViewById(R.id.rotate_text);
        rotateText.setY(tap.getY() + tap.getHeight() + padding);

        View gotIt = mTutView.findViewById(R.id.got_it_button);
        gotIt.setX(mBoardView.getWidth() - gotIt.getWidth() - padding);
        gotIt.setY(mBoardView.getHeight() - gotIt.getHeight() - padding);
        gotIt.setOnClickListener(this);
    }

}
