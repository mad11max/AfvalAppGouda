package nl.pee65;

import android.os.Bundle;
import android.preference.PreferenceActivity;

/**
 * Created by madmax on 2-1-2018.
 */
public class PrefActivity extends PreferenceActivity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);
    }
}