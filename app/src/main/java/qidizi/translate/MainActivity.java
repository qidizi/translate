package qidizi.translate;

import android.app.*;
import android.os.*;
import android.widget.*;
import android.content.*;

public class MainActivity extends Activity 
{
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        Intent it = new Intent(this, MyService.class);
        stopService(it);
        ComponentName cn;
        String tip;

        try
        {
            cn = startService(it);
            tip = null == cn ? "失败" : cn.getClassName();
        }
        catch (Exception e)
        {
            tip = e.getMessage();
        }

        Toast.makeText(this, "启动：" + tip, Toast.LENGTH_SHORT).show();
        finish();
    }
}
