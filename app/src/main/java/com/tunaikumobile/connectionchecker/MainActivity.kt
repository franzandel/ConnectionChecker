package com.tunaikumobile.connectionchecker

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.location.LocationManager
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.NetworkInfo
import android.net.wifi.WifiInfo
import android.net.wifi.WifiManager
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.telephony.TelephonyManager
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

//        if (isLocnEnabled(this)) {
//            txtNetworkName.text = getWifiName(this)
//        }
        txtNetworkName.text = getCurrentNetworkNew(this)
    }

    fun getCurrentNetworkDeprecated() {
//        val connMgr =
//            this.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
//        val wifi = connMgr.getNetworkInfo(ConnectivityManager.TYPE_WIFI)
//        val mobile = connMgr.getNetworkInfo(ConnectivityManager.TYPE_MOBILE)
//        when {
//            wifi!!.isConnectedOrConnecting -> Toast.makeText(this, "Wifi", Toast.LENGTH_LONG).show()
//            mobile!!.isConnectedOrConnecting -> Toast.makeText(
//                this,
//                "Mobile 3G ",
//                Toast.LENGTH_LONG
//            ).show()
//            else -> Toast.makeText(this, "No Network ", Toast.LENGTH_LONG).show()
//        }

        val cm =
            applicationContext.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork = cm.activeNetworkInfo
        if (activeNetwork != null) {
            // connected to the internet
            when (activeNetwork.type) {
                ConnectivityManager.TYPE_WIFI -> {
                }
                ConnectivityManager.TYPE_MOBILE -> {
                }
                else -> {
                }
            }// connected to wifi
            // connected to mobile data
        } else {
            // not connected to the internet
        }
    }

    private fun getCurrentNetworkNew(context: Context): String {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val nw = connectivityManager.activeNetwork ?: return "No Connection"
            val actNw = connectivityManager.getNetworkCapabilities(nw) ?: return "No Connection"
            return when {
                actNw.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> "Wifi"
                actNw.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> "Cellular"
                //for other device how are able to connect with Ethernet
                actNw.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> "Ethernet"
                else -> "No Connection"
            }
        } else {
            val nwInfo = connectivityManager.activeNetworkInfo ?: return "No Connection"
            return "No Connection"
        }
    }

    fun getSimOperatorName(): String {
        val telephonyManager =
            applicationContext.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager

        // BOTH HAVE SAME RESULTS
        return telephonyManager.networkOperatorName
//        return telephonyManager.simOperatorName
    }

    private fun getWifiName(context: Context): String? {
        val manager =
            context.applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
        if (manager.isWifiEnabled) {
            val wifiInfo = manager.connectionInfo
            if (wifiInfo != null) {
                val state = WifiInfo.getDetailedStateOf(wifiInfo.supplicantState)
                if (state == NetworkInfo.DetailedState.CONNECTED || state == NetworkInfo.DetailedState.OBTAINING_IPADDR) {
                    return wifiInfo.ssid
                }
            }
        }
        return null
    }

    fun getWifiName2(): String {
        val wifiMgr = applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
        val wifiInfo = wifiMgr.connectionInfo
        val name = wifiInfo.ssid
        return name
    }

    // Android 9 requires User to enable Location before getting Wifi SSID
    private fun isLocnEnabled(context: Context): Boolean {
        var locnProviders: List<*>? = null
        try {
            val lm =
                context.applicationContext.getSystemService(Activity.LOCATION_SERVICE) as LocationManager
            locnProviders = lm.getProviders(true)

            return locnProviders!!.size != 0
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            if (BuildConfig.DEBUG) {
                if (locnProviders == null || locnProviders.isEmpty()) {
                    Log.d("1234", "Location services disabled")
                    context.startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
                } else
                    Log.d("1234", "locnProviders: $locnProviders")
            }
        }
        return false
    }
}
