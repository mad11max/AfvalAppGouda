package nl.pee65;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;


import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.*;

public class AfvalRoosterActivity extends ListActivity implements
        OnItemSelectedListener {
    /*Naam advertentieblok: banner aan onderzijde
ID advertentieblok: ca-app-pub-3759259204535951/4445045820*/
    private static final String MY_AD_UNIT_ID = "ca-app-pub-3759259204535951/8467959425";//"ca-app-pub-3759259204535951/4445045820";
    private AdView adView;

    public static class MyAdapter extends ArrayAdapter<AfvalOphaalMoment> {

        private static final int MAX_IMG_SIZE = 60;
        private static int myLayout = nl.pee65.R.layout.afvalmoment_row;
        private List<AfvalOphaalMoment> momenten;
        private Context context;
        private AfvalCalc afvalCalc;


        public MyAdapter(Context ctx, List<AfvalOphaalMoment> list,
                         AfvalCalc afvalCalc) {
            super(ctx, myLayout, list);
            this.context = ctx;
            this.momenten = list;
            this.afvalCalc = afvalCalc;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View rowView = inflater.inflate(myLayout, parent, false);
            AfvalOphaalMoment moment = momenten.get(position);
            ImageView imageView = (ImageView) rowView.findViewById(R.id.icon0);
            TextView textView1 = (TextView) rowView.findViewById(R.id.text1);
            TextView textView2 = (TextView) rowView.findViewById(R.id.text2);

            textView1.setText(moment.getAfvaltype().name());
            String s = afvalCalc.stringVoorDisplay(moment.getOphaaldag());
            if (!TextUtils.isEmpty(moment.getRemark())) {
                s += " (" + moment.getRemark() + ")";
            }
            textView2.setText(s);
            imageView.setImageResource(moment.getAfvaltype().getResourceIcon());
            // imageView.setAdjustViewBounds(true);
            // imageView.setMaxWidth(MAX_IMG_SIZE);
            // imageView.setMaxHeight(MAX_IMG_SIZE);
            // Change the icon for Windows and iPhone
            // String s = values[position];
            // if (s.startsWith("iPhone")) {
            // imageView.setImageResource(R.drawable.no);
            // } else {
            // imageView.setImageResource(R.drawable.ok);
            // }

            return rowView;
        }
    }

    // private static final String afvallabel0 = "momenticon";
    // private static final String afvallabel1 = "momenttype";
    // private static final String afvallabel2 = "momentdatum";
    // private static final String[] afvallabels = new String[] { afvallabel1,
    // afvallabel2 };
    // private static final String[] matrixlabels = new String[] { "_id",
    // afvallabel1, afvallabel2 };

    private AfvalCalc afvalCalc;
    private DatabaseHandler dbhandler;
    private int weeknr;
    private Calendar representant = null;
    private Button btnVorig;
    private Button btnHuidig;
    private Button btnVolgende;
    // private Button btnStart;
    // private Button btnStop;
    // private PendingIntent mAlarmSender;
    private String tag = getClass().getSimpleName();
    private Spinner spinner;
    private Wijk wijk;
    private ArrayList<String> wijknamenrij;

    // private GestureDetector gestureDetector;
    // View.OnTouchListener gestureListener;
    //
    // private static final int SWIPE_MIN_DISTANCE = 120;
    // private static final int SWIPE_MAX_OFF_PATH = 250;
    // private static final int SWIPE_THRESHOLD_VELOCITY = 200;

    // @Override
    // public boolean onCreateOptionsMenu(Menu menu) {
    // // TODO Auto-generated method stub
    // menu.add(Menu.NONE, 0, 0, "Settings");
    // return super.onCreateOptionsMenu(menu);
    // }
    //
    // @Override
    // public boolean onOptionsItemSelected(MenuItem item) {
    // switch (item.getItemId()) {
    // case 0:
    // startActivity(new Intent(this, SettingsActivity.class));
    // return true;
    //
    //
    // }
    // return false;
    // }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(tag, "stop");
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(tag, "start");
    }

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.main);
        dbhandler = new DatabaseHandler(this);

        initSpinner();
        wijk = fetchWijk();
        if (wijk != null) {

            continueer();
        } else {
            String s = "Kies een wijk a.u.b.";
            Toast toast = Toast.makeText(this, s, Toast.LENGTH_LONG);
            toast.show();
            TextView label = (TextView) findViewById(R.id.label);
            label.setText(s);
        }

    }

    public void onItemSelected(AdapterView<?> parent, View view, int pos,
                               long id) {
        // An item was selected. You can retrieve the selected item using
        String s = (String) parent.getItemAtPosition(pos);
        Wijk w = Wijk.wijkVoorString(s);
        if (w != null) {
            wijk = w;
            persisteerWijk();
            continueer();
        }
    }

    public void onNothingSelected(AdapterView<?> parent) {
        // Another interface callback
    }

    private void persisteerWijk() {
        dbhandler.setWijk(wijk);
    }

    private Wijk fetchWijk() {
        String s = (String) spinner.getSelectedItem();
        Wijk w = Wijk.wijkVoorString(s);
        if (w == null) {
            w = dbhandler.getWijk();
            if (w != null) {
                adjusteSpinner(w);
            }
        }
        return w;
    }

    private void adjusteSpinner(Wijk w) {
        int position = wijknamenrij.indexOf(w.name());
        spinner.setSelection(position, true);
    }

    private void initSpinner() {
        spinner = (Spinner) findViewById(R.id.spinner1);

        wijknamenrij = new ArrayList<String>();
        for (Wijk w : Wijk.values()) {
            wijknamenrij.add(w.name());
        }
        Collections.sort(wijknamenrij);
        wijknamenrij.add(0, "");
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, wijknamenrij);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);

    }

    private void continueer() {
        String s = "Vandaag is het";
        TextView label = (TextView) findViewById(R.id.label);
        label.setText(s);
        afvalCalc = new AfvalCalc(wijk);// TODO uit db halen
        btnVorig = (Button) findViewById(R.id.btnVorige);
        btnHuidig = (Button) findViewById(R.id.btnHuidige);
        btnVolgende = (Button) findViewById(R.id.btnVolgende);

        OnClickListener clicklistener2 = new View.OnClickListener() {
            public void onClick(View v) {
                handleClick(v.getId());
            }

        };
        btnVorig.setOnClickListener(clicklistener2);
        btnHuidig.setOnClickListener(clicklistener2);
        btnVolgende.setOnClickListener(clicklistener2);

        initRepresnetanten();

        // Create the adView.
        adView = new AdView(this);
        adView.setAdUnitId(MY_AD_UNIT_ID);
        adView.setAdSize(AdSize.BANNER);

        // Lookup your LinearLayout assuming it's been given
        // the attribute android:id="@+id/mainLayout".
        RelativeLayout layout = (RelativeLayout) findViewById(R.id.mainlayout);

        RelativeLayout.LayoutParams rLParams = new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        rLParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, 1);
        // Add the adView to it.
        layout.addView(adView, rLParams);

        // Initiate a generic request.
        AdRequest adRequest = new AdRequest.Builder().build();

        // Load the adView with the ad request.
        adView.loadAd(adRequest);

        fillData();

    }

    private void initRepresnetanten() {
        weeknr = afvalCalc.calcWeekNrVanBelang(2);
        representant = afvalCalc.getCalVanBelang(2);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(getClass().getSimpleName(), requestCode + " onActivityResult "
                + resultCode);
    }

    @Override
    public void onContextMenuClosed(Menu menu) {
        // TODO Auto-generated method stub
        super.onContextMenuClosed(menu);
        Log.d(getClass().getSimpleName(), "ctx menu closed");
    }

    private void fillData() {

        int eigenlijkeweeknr = afvalCalc.calcWeekNr();
        int eigenlijkeJaar = afvalCalc.correctJaar(afvalCalc.getCalVanBelang(0));
        int jaar = afvalCalc.correctJaar(representant);

        TextView view = (TextView) findViewById(R.id.vandaag);
        view.setText(afvalCalc.stringVoorDisplay(new Date()));

        int weeklabel = (R.string.dezeweek);
//		if(afvalCalc.isEindVanJaarMetWeeknrFratsen()){
//			eigenlijkeweeknr = eigenlijkeweeknr % 52;
//		}
        int eig = (100 * eigenlijkeJaar) + eigenlijkeweeknr;
        int rep = (100 * jaar) + weeknr;
        Log.d(getClass().getSimpleName(), eig + " is eig en rep is "
                + rep);
        if (eig < rep) {
            weeklabel = R.string.komendeweek;
        } else if (eig > rep) {
            weeklabel = R.string.vorigeweek;
        }

        view = (TextView) findViewById(R.id.weeklabel);
        view.setText(weeklabel);

        view = (TextView) findViewById(R.id.weekwaarde);
        view.setText(afvalCalc.stringVoorDisplay(representant));
        Log.d(getClass().getSimpleName(), "fillData: representant = " + representant.getTime());
        List<AfvalOphaalMoment> momenten = afvalCalc.getMomenten(representant);

        ListAdapter adapter = new MyAdapter(this, momenten, afvalCalc);
        setListAdapter(adapter);

        enableDisableButtons();
    }

    protected void handleClick(int id) {
        switch (id) {
            case R.id.btnVorige:
                weeknr--;
                setRepresentant(-1);
                break;
            case R.id.btnVolgende:
                weeknr++;
                setRepresentant(+1);
                break;
            default:
                initRepresnetanten();
                break;
        }
        fillData();
    }

    private void setRepresentant(int i) {

        representant.add(Calendar.WEEK_OF_YEAR, i);

        Log.d(getClass().getSimpleName(), "setRepresentant " + i + ": representant = " + representant.getTime());
    }

    private void enableDisableButtons() {
        //btnVorig.setEnabled(weeknr > AfvalCalc.MIN_WEEKNR);
        //btnVolgende.setEnabled(weeknr < AfvalCalc.MAX_WEEKNR);
    }


    @Override
    public void onPause() {
        if (adView != null) {
            adView.pause();
        }
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (adView != null) {
            adView.resume();
        }
    }

    @Override
    public void onDestroy() {
        if (adView != null) {
            adView.destroy();
        }
        super.onDestroy();
    }

}

// http://developer.android.com/guide/topics/fundamentals.html
// http://developer.android.com/resources/tutorials/hello-world.html