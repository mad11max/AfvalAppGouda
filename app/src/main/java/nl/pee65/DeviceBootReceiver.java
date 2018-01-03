package nl.pee65;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.util.Calendar;

/**
 * Created by madmax on 2-1-2018.
 */
public class DeviceBootReceiver extends BroadcastReceiver {
    private static  final int INTERVAL = 1000*60*60*12;

static boolean debug=false;
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals("android.intent.action.BOOT_COMPLETED")) {
           zetalarm(context);
        }

    }

    public static void zetalarm(Context context) {
         /* Setting the alarm here */
        Intent alarmIntent = new Intent(context, AlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, alarmIntent, 0);

        AlarmManager manager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);



        manager.setRepeating(AlarmManager.RTC_WAKEUP, eesrteMaalDatHet6UurIs(),debug?60000: INTERVAL, pendingIntent);

        Log.d(DeviceBootReceiver.class.getSimpleName(),"\n\n\n\nI got called ...........\n\n\n ");
    }


    private static long eesrteMaalDatHet6UurIs() {
        if(debug){
            return System.currentTimeMillis();
        }
        Calendar cal = Calendar.getInstance();
        int uur=cal.get(cal.HOUR_OF_DAY);
        if(uur > 18){
            cal.set(cal.HOUR_OF_DAY, 6);
            cal.add(cal.DATE,1);
        }else if(uur < 6){
            cal.set(cal.HOUR_OF_DAY, 6);
        }else{
            cal.set(cal.HOUR_OF_DAY, 18);
        }
        cal.set(cal.MINUTE,0);
        Log.d(DeviceBootReceiver.class.getSimpleName(),"eesrteMaalDatHet6UurIs : "+cal.getTime());
        return cal.getTime().getTime();
    }
}
