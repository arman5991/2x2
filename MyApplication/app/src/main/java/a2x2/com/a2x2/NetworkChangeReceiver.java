package a2x2.com.a2x2;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class NetworkChangeReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(final Context context, Intent intent) {
        Intent i = new Intent("status");
        context.sendBroadcast(i);
    }
}