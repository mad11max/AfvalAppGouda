# AfvalAppGouda
What type of garbage to set for retreival in the city of Gouda (Netherlands)?

This android app helps you with that.
It takes into account the exceptions like Eastern, Christmas and such.

It can be found in the Play store using searchterm:
'Afvalrooster Gouda'.

The app uses 3 permissions:

      <uses-permission android:name="android.permission.INTERNET"/>
      <uses-permission android:name="android.permission.ACCESS_NETWORK_STATE"/>
      <uses-permission android:name="com.google.android.gms.permission.AD_ID"/>

All 3 are needed to let the Advertisment be shown and function correcty.

No other information entered during the usage of the app leaves the device.
What neighborhood is chosen is stored locally and upon full removal of the app this userdata is also gone.

Before version 3.7 the app also required this permission:

    <uses-permission android:name="android.permission.RECEIVE_BOOT_COMPLETED"/>

But with version 3.7 this is no longer a requirement