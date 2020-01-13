package nl.pee65;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import androidx.core.app.NotificationCompat;
//import androidx.core.app.NotificationCompatBuilder;
import android.util.Log;
import android.view.View;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

/**
 * Created by madmax on 2-1-2018.
 */
public class AlarmReceiver extends BroadcastReceiver {

    private static final long ACHTENVEERTIG_UUR = 48 *60*60*1000L;
    AfvalCalc calc;
    DatabaseHandler db;
    boolean avond = false;
    boolean ochtend = false;
    private Calendar nu;
    private boolean isnuavond;
    String tag = getClass().getSimpleName();

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(getClass().getSimpleName(), "\n\n\nI got called !!!!!!!!!\n\n\n");
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        avond = prefs.getBoolean("pref_avond", false);
        ochtend = prefs.getBoolean("pref_morgen", false);
        Log.d(tag, "en prefmorgen: " + ochtend);
        Log.d(tag, "en prefavond: " + avond);
        if (!avond && !ochtend) {
            Log.d(tag, "!avond && !ochtend, so returning " );
            return;
        }
        db = new DatabaseHandler(context);
        Wijk wijk = db.getWijk();
        if (wijk == null) {
            Log.d(tag, "wijk is null, so returning " );
            return;
        }
        nu = Calendar.getInstance(new Locale("nl"));
        isnuavond = AfvalCalc.isAvond(nu);
        calc = new AfvalCalc(wijk);
        Set<AfvalOphaalMoment> setje = new HashSet<>();
        if (avond) {
            Log.d(tag, "avond calculatie van momenten: " );
            setje.addAll(calc.getMomenten(calc.getCalVanBelang(1)));
            Log.d(tag, "avond calculatie van momenten: setje is nu "+setje );
        }
        if (ochtend) {
            Log.d(tag, "niet-avond calculatie van momenten: " );
            setje.addAll(calc.getMomenten(calc.getCalVanBelang(0)));
            Log.d(tag, "niet-avond calculatie van momenten: setje is nu "+setje );
        }

        if (setje.isEmpty()) {
            Log.d(tag, "setje is empty, returning " );
            return;
        }
        filter(setje,context);
    }

    private void filter(Set<AfvalOphaalMoment> moments, Context context) {
        Set<AfvalOphaalMoment>  setje = new HashSet<>();
        // A // lijstje voor hele week, stel di, wo en vr is er iets
        // omdat het nu bijv maandag is doen alleen een notif als
        // 1) iemand avond-notif wil
        // 2) het nu avond is
        // 3) er momenten zijn voor morgen

        // OF:

        // B // lijstje voor hele week, stel di, wo en vr is er iets
      //  omdat het nu bijv dinsdag is doen alleen een notif als
        // 1) iemand ochtend-notif wil
        // 2) het nu geen avond is
        // 3) er momenten zijn voor vandaag

        if(avond && isnuavond){
            Log.d(tag, "avond && isnuavond " );
           setje.addAll( filter(moments, calc.getCalVanBelang(1)) );

            Log.d(tag, "avond && isnuavond: setje is nu "+setje );
        }
        if(ochtend && !isnuavond){
            Log.d(tag, "ochtend && !isnuavond " );
            setje.addAll( filter(moments, nu) );
            Log.d(tag, "ochtend && !isnuavond: setje is nu "+setje );
        }
        if(!setje.isEmpty()){
            sendNotification(context,setje);


        }
    }

    private Set<AfvalOphaalMoment> filter(Set<AfvalOphaalMoment> moments, Calendar calVanBelang) {
        Set<AfvalOphaalMoment>  setje = new HashSet<>();
        String dagnaam = calc.dagNaam(calVanBelang.getTime());
        for (AfvalOphaalMoment m:moments  ) {
            if(calc.dagNaam(m.getOphaaldag()).equals(dagnaam) && max48uurVerschil(m.getOphaaldag() ,calVanBelang.getTime())){
                setje.add(m);
            }
        }
        return setje;
    }

    private boolean max48uurVerschil(Date d1, Date d2){
        return
                Math.abs(d1.getTime()-d2.getTime()) < ACHTENVEERTIG_UUR;
    }

    public void sendNotification(Context context, Set<AfvalOphaalMoment> moments) {
        List<AfvalOphaalMoment> momenten = new ArrayList<>();
        momenten.addAll(moments);
        Collections.sort(momenten);
        Date d = momenten.get(0).getOphaaldag();

        Intent notificationIntent = new Intent(context, AfvalRoosterActivity.class);

        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
                | Intent.FLAG_ACTIVITY_SINGLE_TOP);

        PendingIntent intent = PendingIntent.getActivity(context, 0,
                notificationIntent, 0);
        //Get an instance of NotificationManager//

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(context)
                        .setSmallIcon(R.drawable.ic_stat_delete)
                        .setContentTitle(createttitle(d,momenten))
                        .setContentText(createtext(d,momenten)).setContentIntent(intent);


        // Gets an instance of the NotificationManager service//

        NotificationManager mNotificationManager =

                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        // When you issue multiple notifications about the same type of event,
        // it’s best practice for your app to try to update an existing notification
        // with this new information, rather than immediately creating a new notification.
        // If you want to update this notification at a later date, you need to assign it an ID.
        // You can then use this ID whenever you issue a subsequent notification.
        // If the previous notification is still visible, the system will update this existing notification,
        // rather than create a new one. In this example, the notification’s ID is 001//
        Notification notif = mBuilder.build();




        notif.flags |= Notification.FLAG_AUTO_CANCEL;


        mNotificationManager.notify(001,notif);
    }

    private String createttitle(Date d, List<AfvalOphaalMoment> momenten) {
        return "Afval-alert";
//        StringBuilder b = new StringBuilder();
//        b.append(calc.dagNaam(d));
//        b.append(": ");
//        for (AfvalOphaalMoment m : momenten) {
//            b.append(m.getAfvaltype().name());
//            b.append(" ");
//        }
//        return b.toString().trim();
    }


    private String createtext(Date d, List<AfvalOphaalMoment> momenten) {
        StringBuilder b = new StringBuilder();
        for (AfvalOphaalMoment m : momenten) {
            b.append(calc.dagNaam(d));
            b.append(": ");
            b.append(m.getAfvaltype().name());
            b.append("\n");
        }
        return b.toString().trim();
    }

}
