package nl.pee65;

import java.text.DateFormatSymbols;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import android.util.Log;

public class AfvalCalc {
	public static final String DATEPATTERN_DD_MM_YYYY = "dd-MM-yyyy";
	public static final String DATEPATTERN_D_M_YYYY = "d-M-yyyy";
	public static final String DATEPATTERN_EEEE_DD_MM_YYYY = "EEEE dd-MM-yyyy";

	/** The Constant MAX_WEEKNR inclusive. */
	public static final int MAX_WEEKNR = 54;

	/** The Constant MIN_WEEKNR inclusive. */
	public static final int MIN_WEEKNR = 0;
	private static final long DAG_IN_MILLIS = 86400000L;// 24 * 60 * 60 * 1000;

	private Map<String, String> uitzonderingmap;

	private HashMap<AFVALTYPE, Boolean> weeknrmap;
	private Map<AFVALTYPE, Integer> dagvanweekmap;

	private int IJKJAAR = 2015;
	// rooster volgend jaar is pas bekend aan eind van jaar

	private Wijk wijk;// = Wijk.PLASWIJK

	private boolean isZakWijk = false;
	private boolean eindVanJaarMetWeeknrFratsen;
    private List<Integer> onevens;
    private List<Integer> evens;
	private List<Integer> alle;

	public AfvalCalc(Wijk wijk) {
		this.wijk = wijk;
		init();

	}
	
	public boolean isEindVanJaarMetWeeknrFratsen() {
		return eindVanJaarMetWeeknrFratsen;
	}

	public Wijk getWijk() {
		return wijk;
	}

	private void init() {
		weeknrmap = new HashMap<AFVALTYPE, Boolean>();
		dagvanweekmap = new HashMap<AFVALTYPE, Integer>();
		uitzonderingmap = new HashMap<String, String>();

		evens = new ArrayList<Integer>();
		onevens = new ArrayList<Integer>();
		alle = new ArrayList<Integer>();

		for (int i = MIN_WEEKNR; i <= MAX_WEEKNR; i++) {
			if (i % 2 == 0) {
				evens.add(i);
			} else {
				onevens.add(i);
			}
			alle.add(i);
		}

		isZakWijk = (wijk.isZakEven() == true && wijk.isZakOneven() == true);

		/*
		 * weeknrmap.put(AFVALTYPE.GRIJS, Collections.unmodifiableList(evens));
		 * weeknrmap.put(AFVALTYPE.ORANJE, Collections.unmodifiableList(evens));
		 * weeknrmap.put(AFVALTYPE.BLAUW,
		 * Collections.unmodifiableList(onevens));
		 * weeknrmap.put(AFVALTYPE.GROEN,
		 * Collections.unmodifiableList(onevens));
		 * 
		 * dagvanweekmap.put(AFVALTYPE.GRIJS, Calendar.THURSDAY);
		 * dagvanweekmap.put(AFVALTYPE.ORANJE, Calendar.THURSDAY);
		 * dagvanweekmap.put(AFVALTYPE.GROEN, Calendar.THURSDAY);
		 * dagvanweekmap.put(AFVALTYPE.BLAUW, Calendar.MONDAY);
		 * 
		 * uitzonderingmap = new HashMap<String, String>();
		 * uitzonderingmap.put(("30-04-2013"), ("04-05-2013"));
		 * uitzonderingmap.put(("09-05-2013"), ("11-05-2013"));
		 * uitzonderingmap.put(("20-05-2013"), ("24-05-2013"));
		 * uitzonderingmap.put(("26-12-2013"), ("28-12-2013"));
		 */
		if (isZakWijk) {
			weeknrmap.put(AFVALTYPE.ZAK, true);
			dagvanweekmap.put(AFVALTYPE.ZAK, wijk.getZakDag());
		} else {
			weeknrmap.put(AFVALTYPE.GRIJS, wijk.isGrijsEven());
			weeknrmap.put(AFVALTYPE.BLAUW, wijk.isBlauwEven());
			weeknrmap.put(AFVALTYPE.GROEN, wijk.isGroenEven() );

			dagvanweekmap.put(AFVALTYPE.GRIJS, wijk.getGrijsDag());
			dagvanweekmap.put(AFVALTYPE.GROEN, wijk.getGroenDag());
			dagvanweekmap.put(AFVALTYPE.BLAUW, wijk.getBlauwDag());
		}

		// plastic wordt door heel gouda gedaan om de week
		weeknrmap.put(AFVALTYPE.ORANJE, wijk.isOranjeEven() );
		dagvanweekmap.put(AFVALTYPE.ORANJE, wijk.getOranjeDag());
		uitzonderingmap = wijk.getUitzonderingen();
	}

	/**
	 * Datum voor.
	 * 
	 * @param string
	 *            the string
	 * @return the date
	 */
	protected Date datumVoor(String string) {
		try {
			return new SimpleDateFormat(DATEPATTERN_DD_MM_YYYY).parse(string);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return null;
	}

	public String stringVoor(Date d) {
		return new SimpleDateFormat(DATEPATTERN_DD_MM_YYYY).format(d);

	}

	public String stringVoorKort(Date d) {
		return new SimpleDateFormat(DATEPATTERN_D_M_YYYY).format(d);

	}

	public String stringVoorDisplay(Date d) {
		return new SimpleDateFormat(DATEPATTERN_EEEE_DD_MM_YYYY, new Locale("nl")).format(d);

	}

	private GregorianCalendar getNewCal(boolean clear) {
		GregorianCalendar calendar = new GregorianCalendar(new Locale("nl"));
		if (clear) {
			calendar.clear();
		}
		return calendar;
	}

	public int calcWeekNrVanBelang(int delta) {
		return getCalVanBelang(delta).get(Calendar.WEEK_OF_YEAR);
	}

	public int calcYearVanBelang() {
		Calendar cal = getCalVanBelang(0);
		int j= cal.get(Calendar.YEAR);
//		if(cal.get(cal.MONTH)==11 && calcWeekNrVanBelang()==1){
//			j++;
//			eindVanJaarMetWeeknrFratsen=true;
//		}
//
		return j;
	}

	public Calendar getCalVanBelang(int delta) {
		GregorianCalendar cal = getNewCal(false);
		cal.add(Calendar.DATE, delta);// 0 of 2 dagen erbij, welke week is het dan?
		// dat is de interessante week om wat van te weten
		return cal;
	}

	public int calcWeekNr() {
		return calcWeekNr(new Date());
	}

	public int calcWeekNr(Date date) {
		GregorianCalendar cal = getNewCal(true);
		cal.setTime(date);
		return cal.get(Calendar.WEEK_OF_YEAR);
	}

	public List<AfvalOphaalMoment> getMomenten(Calendar calGevraagd) {
		int weeknr = calcWeekNr(calGevraagd.getTime());
		Log.d(getClass().getSimpleName(), "getMomenten " + weeknr);
		List<AfvalOphaalMoment> list1 = calcMomenten(calGevraagd);
		List<AfvalOphaalMoment> listWeekErna = calcMomenten(weekLater(calGevraagd));
		for (AfvalOphaalMoment afvalOphaalMoment : listWeekErna) {
			Date date = afvalOphaalMoment.getOphaaldag();
			GregorianCalendar cal2 = getNewCal(true);
			cal2.setTime(date);
			int dagnr = cal2.get(Calendar.DAY_OF_WEEK);
			if ((dagnr == Calendar.SATURDAY || dagnr == Calendar.SUNDAY )&& isNaarVorenTovEigenlijk(afvalOphaalMoment)) {
				list1.add(afvalOphaalMoment);
			}
		}
		return list1;
	}

	private Calendar weekLater(Calendar calGevraagd) {
		Calendar c = Calendar.getInstance();
		c.setTime(calGevraagd.getTime());
		c.add(Calendar.WEEK_OF_YEAR,1);
		return c;
	}

	private boolean isNaarVorenTovEigenlijk(AfvalOphaalMoment mom) {
		if(mom.getEigenlijkeOphaaldag()==null){
			return false;
		}
		return mom.getEigenlijkeOphaaldag().compareTo(mom.getOphaaldag())>0;
	}

	private List<AfvalOphaalMoment> calcMomenten(Calendar calGevraagd) {
		Log.d(getClass().getSimpleName(), "calcMomenten "+calGevraagd.getTime());
		int weeknr = calcWeekNr(calGevraagd.getTime());
		List<AfvalOphaalMoment> lijst = new ArrayList<AfvalOphaalMoment>();
        //int jaar =calcYearVanBelang();
		Set<Entry<AFVALTYPE, Boolean>> entrySet = weeknrmap.entrySet();
		for (Entry<AFVALTYPE, Boolean> entry : entrySet) {

            boolean b = entry.getValue();
            Log.d(getClass().getSimpleName(),entry.getKey()+" -> "+b);
			List<Integer> weeknrs = getWeeknrs(calGevraagd, b);
			if(isZakWijk && entry.getKey()==AFVALTYPE.ZAK){
				weeknrs=alle;
			}
			if (weeknrs.contains(weeknr)) {
                Log.d(getClass().getSimpleName(),entry.getKey()+ ",calcMomenten : weeknrs "+weeknrs);
                // ahah we got one
				AfvalOphaalMoment moment = new AfvalOphaalMoment();
				moment.setAfvaltype(entry.getKey());
				Integer dagnr = dagvanweekmap.get(entry.getKey());
				calcDate(moment,  dagnr, true, calGevraagd );

				lijst.add(moment);
			}
		}

		Collections.sort(lijst);
		return lijst;
	}

    private List<Integer> getWeeknrs(Calendar calGevraagd,Boolean even) {

        boolean b = even;

        b = wisselNodig(b, calGevraagd);

        Log.d(getClass().getSimpleName(), "cal " + calGevraagd.getTime() + ", b is " + b + " even is " + even);
        return (b)?evens:onevens;
    }

    private boolean wisselNodig(boolean b,Calendar c) {
        // als weeknr > 53 of jaar > 2105 dan moeten we even gaan kijken wat we doen met de boolean b.
        // defintie van welke weken voor bijv grijs is gebasserd op 2015
        // Stel grijs is in week 53 (oneven) in 2015 , dan is het niet in week 1 van 2016
        // Maar beide , 53 3 en 1 zijn oneven.
        // remedie :
        // Tel aantal wken in de jaren na 2015 bij elkaar op en als dat oneven is dan is er dus een wissel nodig --> b=!b

		//
//		int jaar = c.get(c.YEAR);
		int jaar = correctJaar(c);
        log("wisselNodig "+b+ ", j is "+jaar );
        if(jaar > IJKJAAR ){
//            Calendar cal = Calendar.getInstance(new Locale("nl"));
//            cal.setLenient(true);
//            cal.set(cal.YEAR, jaar);
//            cal.set(cal.WEEK_OF_YEAR, weeknr);
//            cal.set(cal.DAY_OF_WEEK,4);
//
//            int w= cal.get(Calendar.WEEK_OF_YEAR);
//            int getal=(weeknr-1)/w;
//            jaar = cal.get(cal.YEAR)+getal;
            log("wisselNodig tsapje vereder  "+b+ ",jaar is "+jaar );
            int aantal=0;
            for (int i=IJKJAAR ; i<jaar; i++){
                GregorianCalendar gcal = getNewCal(true, i, 1,1);

                int maxweek=gcal.getActualMaximum(gcal.WEEK_OF_YEAR);
                log("maxweek van "+i+ " is "+maxweek);
                aantal += maxweek;
            }
            if(aantal % 2 !=0){
				log("ja een inverrs van "+b +" , dus "+!b);
                return !b;
            }
        }
        return b;
    }



	private GregorianCalendar getNewCal(boolean b, int j, int m, int d) {
        GregorianCalendar gcal = getNewCal(b);
        gcal.set(gcal.YEAR,j);
        gcal.set(gcal.MONTH, m);
        gcal.set(gcal.DATE,d);
        return gcal;
    }

	public int correctJaar(Calendar c) {
		int jaar = c.get(c.YEAR);
		// extra note  1jan2016  is een lastige: is week 53 maar ook 2016
		// door jaar te --en corrigeer ik dat
		if(c.get(c.WEEK_OF_YEAR)>51 && c.get(Calendar.DAY_OF_YEAR)<8){
			jaar--;
		}
		return jaar;
	}

    private void log(String s) {
        Log.d(getClass().getSimpleName(),s);
    }

    protected Date calcDate(AfvalOphaalMoment moment, Integer dagnr, boolean gebruikUitzonderingen, Calendar calGevraagd) {
		GregorianCalendar cal = getNewCal(true);
		cal.setTime(calGevraagd.getTime());
		cal.set(Calendar.DAY_OF_WEEK, dagnr);
		log("calcDate, cal is " + cal.getTime());
		Date date = cal.getTime();
		String s = stringVoor(date);
		String s2 = stringVoorKort(date);
		if (gebruikUitzonderingen && (uitzonderingmap.containsKey(s) || uitzonderingmap.containsKey(s2))) {
			String newval = uitzonderingmap.get(s);
			if (newval == null) {
				newval = uitzonderingmap.get(s2);
			}
			moment.setEigenlijkeOphaaldag(date);
			date = datumVoor(newval);
			GregorianCalendar cal2 = getNewCal(true);
			cal2.setTime(date);

			StringBuilder remarkHolder = new StringBuilder();
			remarkHolder.append(dagNaam(cal2.get(Calendar.DAY_OF_WEEK)));
			remarkHolder.append(" ");
			remarkHolder.append(newval);
			remarkHolder.append(" i.p.v. ");
			remarkHolder.append(dagNaam(dagnr));
			remarkHolder.append(" ");
			remarkHolder.append(s2);
			moment.setRemark(remarkHolder.toString());
		}
		 moment.setOphaaldag(date);
		return date;
	}

	protected String dagNaam(Integer dagnr) {
		DateFormatSymbols symbols = new DateFormatSymbols(new Locale("nl"));
		return symbols.getWeekdays()[dagnr];
	}

	public String stringVoorDisplay(Calendar c) {
		GregorianCalendar cal = getNewCal(true);
		cal.setTime(c.getTime());

		cal.set(Calendar.DAY_OF_WEEK, cal.getFirstDayOfWeek());
		Date beginVanDeWeek = cal.getTime();
		cal.add(Calendar.DATE, 6);
		Date eindVandeWeek = cal.getTime();

		StringBuilder b = new StringBuilder(String.valueOf(cal.get(Calendar.WEEK_OF_YEAR)));
		b.append(" (");
		b.append(stringVoorDisplay(beginVanDeWeek));
		b.append(" t/m ");
		b.append(stringVoorDisplay(eindVandeWeek));
		b.append(")");

		return b.toString();
	}

	/**
	 * Checks if is next day or this morning.
	 * 
	 * @param ophaaldate
	 *            the ophaaldate
	 * @return true, if is next day or this morning
	 */
	public boolean isNextDayOrThisMorning(Date ophaaldate) {
		GregorianCalendar cal = getNewCal(true);
		cal.setTime(ophaaldate);
		cal.set(Calendar.HOUR_OF_DAY, 9);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);

		Date ophaaltijdstip = cal.getTime();

		Date nutijdstip = getNewCal(false).getTime();

		if (nutijdstip.before(ophaaltijdstip)) {
			long verschil = ophaaltijdstip.getTime() - nutijdstip.getTime();
			return (verschil < DAG_IN_MILLIS);
		}
		return false;
	}

}
