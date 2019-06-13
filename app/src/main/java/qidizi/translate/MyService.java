package qidizi.translate;
import android.app.*;
import android.content.*;
import android.graphics.*;
import android.os.*;
import android.util.*;
import android.view.*;
import android.webkit.*;
import android.widget.*;
import java.io.*;
import java.net.*;
import org.apache.http.util.*;

public class MyService extends Service
{
    @Override
    public void onCreate()
    {
        // TODO: Implement this method
        super.onCreate();
        //service类中，只能放到onCreate之后，context才存在
      final ClipboardManager cm = (ClipboardManager)getSystemService(CLIPBOARD_SERVICE);

        cm.addPrimaryClipChangedListener(new ClipboardManager.OnPrimaryClipChangedListener(){
                @Override
                public void onPrimaryClipChanged()
                {
                    ClipData cd = cm.getPrimaryClip();

                    if (null == cd)
                    {
                        return;
                    }

                    if (! cd.getDescription().hasMimeType(ClipDescription.MIMETYPE_TEXT_PLAIN))
                    {
                        return;
                    }

                    CharSequence cs= cd.getItemAt(0).getText();

                    if (null == cs)
                    {
                        return;
                    }

                    String text = cs.toString();
                    showText(text);
                }
            });
    }


    @Override
    public IBinder onBind(Intent p1)
    {
        // TODO: Implement this method
        return null;
    }

    public void showText(String text)
    {
        final WindowManager wm = (WindowManager) this.getSystemService(Context.WINDOW_SERVICE);
        final WebView web = new WebView(this);
        web.requestFocus();
        web.setBackgroundColor(Color.TRANSPARENT);
        web.getSettings().setJavaScriptEnabled(true);
        
        if(text.length() <= 1000){
            web.loadUrl("https://cn.bing.com/dict/search?q=" + URLEncoder.encode(text));
        } else {
            
        StringBuilder builder = new StringBuilder();
        try {//拼接post提交参数
            builder.append("s=").append(text);
        } catch (Exception e) {
            Toast.makeText(this.getApplicationContext(),"拼接出错：" +e.getMessage(),Toast.LENGTH_SHORT).show();
            return;
        }
        
        String postData = builder.toString();
            web.postUrl("https://cn.bing.com/dict/search", EncodingUtils.getBytes(postData, "UTF-8"));
        }
        
        DisplayMetrics dm = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(dm);
        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams(
            WindowManager.LayoutParams.MATCH_PARENT, dm.heightPixels / 2, 0, 0,
            WindowManager.LayoutParams.TYPE_PHONE,
            WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL,
            PixelFormat.TRANSLUCENT);
       // layoutParams.setTitle("Window test");
        layoutParams.gravity = Gravity.TOP|Gravity.LEFT;
        final Button btn = new Button(this);
        btn.setText("关闭");
        btn.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v)
                {
                    // Code here executes on main thread after user presses button
                  
                    wm.removeView(web);
                    web.clearFocus();                   
                    web.removeAllViews();
                    web.clearView();
                    web.destroy();
                    wm.removeView(btn);
                    

                }
            });
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams(
            WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT, 0, dm.heightPixels / 2,
            WindowManager.LayoutParams.TYPE_PHONE,
            WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL,
            PixelFormat.TRANSLUCENT);
        lp.gravity = Gravity.CENTER_VERTICAL|Gravity.TOP;
        wm.addView(btn, lp);
        wm.addView(web, layoutParams);
    }
}
