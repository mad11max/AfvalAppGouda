package nl.pee65;


import java.io.IOException;
import java.io.InputStream;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import android.content.res.AssetManager;
import android.text.TextUtils;
import android.util.Log;

/**
 * @see 'www.gouda.nl/gemeente/Wonen/Afval_gladheid_Cyclus/Ophaalschema_per_wijk/'
 * @author madmax
 *
 */
public enum Wijk {
    PLASWIJK(  Calendar.MONDAY , Calendar.FRIDAY, Calendar.THURSDAY, Calendar.MONDAY,       0,         !false,false,true,!true,false,false,"21-4-2014=19-4-2014, 29-5-2014=31-5-2014, 25-12-2014=20-12-2014, 06-04-2015=04-04-2015, 14-05-2015=16-05-2015")
    //                  groen             blauw                oranje           grijs      zak      even groe,  blau ,ora,grij,zak,zakon




    , BLOEMENDAAL(Calendar.MONDAY , Calendar.WEDNESDAY, Calendar.TUESDAY, Calendar.MONDAY, 0,               !false,true,false,!true,false,false,"9-6-2014=7-6-2014, 27-04-2015=25-04-2015,25-05-2015=30-05-2015")


    , GOUDA_NOORD(Calendar.WEDNESDAY , Calendar.FRIDAY, Calendar.THURSDAY, 0, 0,                        !false,true,false,!true,false,false,"9-6-2014=7-6-2014, 27-04-2015=25-04-2015, 05-05-2015=09-05-2015, 25-05-2015=30-05-2015")


    , ACHTERWILLENS(Calendar.WEDNESDAY , Calendar.FRIDAY, Calendar.THURSDAY, Calendar.WEDNESDAY, 0      ,!false,!false,true,!true,false,false,"29-5-2014=31-5-2014, 25-12-2014=20-12-2014, 26-12-2014=27-12-2014, 05-05-2015=09-05-2015, 25-12-2015=19-12-2015")


    , NIEUWE_PARK(Calendar.MONDAY , Calendar.FRIDAY, Calendar.THURSDAY, Calendar.MONDAY, 0              ,!false,false,true,!true,false,false,"29-5-2014=31-5-2014, 25-12-2014=20-12-2014, 26-12-2014=27-12-2014, 14-05-2015=16-05-2015, 25-12-2015=19-12-2015")


    , BINNENSTAD_NOORD(Calendar.MONDAY     , -1   , Calendar.WEDNESDAY,-1                               , 0,            !false,false,true,!true,!true, false,"21-4-2014=22-4-2014, 9-6-2014=10-6-2014, 24-12-2014=23-12-2014, 05-05-2015=06-05-2015")

//                  groen             blauw                oranje           grijs           zak      even groe,  blau ,ora,grij,zak,zakon

    , KORT_HAARLEM(Calendar.WEDNESDAY , Calendar.FRIDAY, Calendar.THURSDAY, Calendar.WEDNESDAY,0       ,!false,false,true,!true,false,false,"29-5-2014=31-5-2014, 25-12-2014=20-12-2014, 26-12-2014=27-12-2014, 14-05-2015=16-05-2015, 25-12-2015=19-12-2015")


    , GOUDA_OOST(Calendar.WEDNESDAY , Calendar.FRIDAY, Calendar.TUESDAY,0,0                            ,!false,!false,!true,true,false,false,"05-05-2015=09-05-2015")


    , GOVERWELLE_WEST(Calendar.WEDNESDAY , Calendar.FRIDAY, Calendar.TUESDAY, Calendar.WEDNESDAY, 0    ,!false,!false,!true,!true,false,false,"05-05-2015=09-05-2015")


    , GOVERWELLE_OOST(Calendar.WEDNESDAY , Calendar.FRIDAY, Calendar.TUESDAY, Calendar.WEDNESDAY, 0     ,!false,false,!true,!true,false,false,"29-5-2014=31-5-2014, 25-12-2014=20-12-2014, 26-12-2014=27-12-2014,01-01-2015=03-01-2015,14-05-2015=16-05-2015,05-05-2015=09-05-2015, 25-12-2015=19-12-2015")


  //  , BINNENSTAD_ZUID(0   , 0 , Calendar.WEDNESDAY, 0                                    ,Calendar.MONDAY              ,false,false,true,true,true,true,"21-4-2014=22-4-2014, 9-6-2014=10-6-2014, 24-12-2014=23-12-2014, 06-04-2015=07-04-2015, 27-04-2015=28-04-2015, 25-05-2015=26-05-2015")


    , KORTE_AKKEREN(Calendar.WEDNESDAY , Calendar.TUESDAY, Calendar.THURSDAY, Calendar.WEDNESDAY, 0      ,!false,false,!true,!true,false,false,"21-4-2014=19-4-2014, 9-6-2014=7-6-2014, 01-01-2015=03-01-2015,06-04-2015=04-04-2015,27-04-2015=25-04-2015, 05-05-2015=09-05-2015, 25-05-2015=30-05-2015")


    , STOLWIJKERSLUIS(Calendar.WEDNESDAY , Calendar.FRIDAY, Calendar.TUESDAY, Calendar.WEDNESDAY, 0      ,!false,!false,!true, false,false,false,"29-5-2014=31-5-2014, 25-12-2014=20-12-2014, 01-01-2015=03-01-2015,05-05-2015=09-05-2015, 14-05-2015=16-05-2015");


    private int groenDag;
    public int getGroenDag() {
        return groenDag;
    }

    public int getBlauwDag() {
        return blauwDag;
    }

    public int getOranjeDag() {
        return oranjeDag;
    }

    public int getGrijsDag() {
        return grijsDag;
    }

    public int getZakDag() {
        return zakDag;
    }



    private int blauwDag;
    private int oranjeDag;
    private int grijsDag;
    private int zakDag;

    private boolean groenEven;
    private boolean blauwEven;
   // private  boolean oranjeEven;
    private boolean grijsEven;
    private boolean zakEven;
    private boolean zakOneven;

    private Wijk(int groenDag, int blauwDag, int oranjeDag, int grijsDag,
                 int zakDag, boolean groenEven, boolean blauwEven,
                 boolean oranjeEven, boolean grijsEven, boolean zakEven,
                 boolean zakOneven, String uitz) {
        this.groenDag = groenDag;
        this.blauwDag = blauwDag;
        this.oranjeDag = oranjeDag;
        this.grijsDag = grijsDag;
        this.zakDag = zakDag;
        this.groenEven = groenEven;
        this.blauwEven = blauwEven;
        //this.oranjeEven = oranjeEven;
        this.grijsEven = grijsEven;
        this.zakEven = zakEven;
        this.zakOneven = zakOneven;
        Properties p = new Properties();

        AssetManager am = App.getContext().getAssets();
        try {
            InputStream is=am.open(""+this.name()+".ini");
            p.load(is);
            Log.d(getClass().getSimpleName(), "\n\n\n\n\n\nJAJAJAJAJAJAJAJAJA : " + name());
            is.close();
        } catch (Exception e) {
            Log.d(getClass().getSimpleName(),name()+", "+e);
        }

        Log.d(getClass().getSimpleName(), "ff props doen");
        Set<String> strings1 = p.stringPropertyNames();
        for(String s1: strings1){
            uitzonderingen.put(s1,p.getProperty(s1));
        }
        Log.d(getClass().getSimpleName(),"ff props gedaan:"+p);

        String[] strings = TextUtils.split(uitz, ",");
        for (String string : strings) {
            // string : datum=datum
            if(!TextUtils.isEmpty(string)){
                String[] stukjes = TextUtils.split(string.trim(), "=");
                String s1 = stukjes[0];
                String s2 = stukjes[1];
                uitzonderingen.put(s1 , s2);
            }
        }
    }



    private Map<String, String> uitzonderingen = new HashMap<String, String>();



    public Map<String, String> getUitzonderingen() {
        return uitzonderingen;
    }

    public boolean isGroenEven() {
        return groenEven;
    }



    public boolean isBlauwEven() {
        return blauwEven;
    }



//    public boolean isOranjeEven() {
//        return oranjeEven;
//    }



    public boolean isGrijsEven() {
        return grijsEven;
    }



    public boolean isZakEven() {
        return zakEven;
    }



    public boolean isZakOneven() {
        return zakOneven;
    }

    public static Wijk wijkVoorString(String s){
        if(TextUtils.isEmpty(s)){
            return null;
        }
        return Wijk.valueOf(s.toUpperCase());
    }
}

