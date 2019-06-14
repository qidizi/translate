package qidizi.translate;

import android.app.*;
import android.content.*;
import android.graphics.*;
import android.os.*;
import android.provider.Settings;
import android.util.*;
import android.view.*;
import android.webkit.*;
import android.widget.*;

import java.net.*;

public class MyService extends Service {
    @Override
    public void onCreate() {
        // TODO: Implement this method
        super.onCreate();
        //service类中，只能放到onCreate之后，context才存在
        final ClipboardManager cm = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);

        cm.addPrimaryClipChangedListener(new ClipboardManager.OnPrimaryClipChangedListener() {
            @Override
            public void onPrimaryClipChanged() {
                ClipData cd = cm.getPrimaryClip();

                if (null == cd) {
                    return;
                }

                if (!cd.getDescription().hasMimeType(ClipDescription.MIMETYPE_TEXT_PLAIN)) {
                    return;
                }

                CharSequence cs = cd.getItemAt(0).getText();

                if (null == cs) {
                    return;
                }

                String text = cs.toString();
                showText(text);
            }
        });
    }


    @Override
    public IBinder onBind(Intent p1) {
        // TODO: Implement this method
        return null;
    }

    public void showText(String text) {
        if (text.length() > 1000) {
            Toast.makeText(this, "内容太长", Toast.LENGTH_SHORT).show();
            return;
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // 23或以上加强了悬浮窗口权限控制；SDK在23以下，不用管.
            if (!Settings.canDrawOverlays(this)) {
                //若没有权限，提示获取.
                Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION);
                Toast.makeText(this, "请允许本应用使用悬浮窗后再试", Toast.LENGTH_LONG).show();
                startActivity(intent);
                return;
            }
        }

        int window_type = 0;
        //8.0系统加强后台管理，禁止在其他应用和窗口弹提醒弹窗，如果要弹，必须使用TYPE_APPLICATION_OVERLAY
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            window_type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        }else {
            window_type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;
        }

        final WindowManager wm = (WindowManager) this.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics dm = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(dm);

        final Button btn = new Button(this);
        btn.setText("关闭");
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT, 0, dm.heightPixels / 2,
                window_type,
                WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL,
                PixelFormat.TRANSLUCENT);
        lp.gravity = Gravity.CENTER_VERTICAL | Gravity.TOP;
        wm.addView(btn, lp);


        final WebView web = new WebView(this);
        web.requestFocus();
        web.setBackgroundColor(Color.TRANSPARENT);
        web.getSettings().setJavaScriptEnabled(true);

        web.loadUrl("https://cn.bing.com/dict/search?q=" + URLEncoder.encode(text));
        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.MATCH_PARENT, dm.heightPixels / 2, 0, 0,
                window_type,
                WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL,
                PixelFormat.TRANSLUCENT);
        // layoutParams.setTitle("Window test");
        layoutParams.gravity = Gravity.TOP | Gravity.START;
        wm.addView(web, layoutParams);


        btn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Code here executes on main thread after user presses button

                wm.removeView(web);
                web.clearFocus();
                web.removeAllViews();
                web.clearView();
                web.destroy();
                wm.removeView(btn);


            }
        });
    }
}
