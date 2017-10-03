package example.com.classappp.forceLoginOut;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.preference.DialogPreference;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;

import example.com.classappp.LoginActivity;

/**
 * Created by mick2017 on 2017/9/13.
 */

public class BaseActivity extends AppCompatActivity {
    private ForceOfflineReceiver receiver;
  @Override
    protected void onCreate(Bundle saveInstanceState){
      super.onCreate(saveInstanceState);
      ActivityCollector.addActivity(this);
  }
  @Override
  protected void onResume(){
      super.onResume();
      IntentFilter intentFilter=new IntentFilter();
      intentFilter.addAction("com.example.broadcastbestpractice.FORCE_OFFLINE");
      receiver=new ForceOfflineReceiver();
      registerReceiver(receiver,intentFilter);
  }
    @Override
    protected void onPause(){
        super.onPause();
        if(receiver!=null){
            unregisterReceiver(receiver);
            receiver=null;
        }
    }
    @Override
    protected void onDestroy(){
        super.onDestroy();
        ActivityCollector.removeActivity(this);
    }

    class ForceOfflineReceiver extends BroadcastReceiver{

        @Override
        public void onReceive(final Context context, Intent intent) {
            AlertDialog.Builder builder=new AlertDialog.Builder(context);
            builder.setTitle("Warning");
            builder.setMessage("You are forced to be offline. Please try to login again.");
            builder.setCancelable(false);
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    ActivityCollector.finishAll();
                    Intent intent=new Intent(context, LoginActivity.class);
                    context.startActivity(intent);
                }
            });
            builder.show();
        }
    }
}
