package app.thecity.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

import androidx.browser.customtabs.CustomTabsClient;
import androidx.browser.customtabs.CustomTabsIntent;
import androidx.browser.customtabs.CustomTabsServiceConnection;
import androidx.browser.customtabs.CustomTabsSession;

import java.util.ArrayList;
import java.util.List;

/**
 * Diese Klasse ist ein Hilfsklasse zum Verwalten der Verbindung zum Custom Tabs Service.
 */
public class CustomTabsHelper {

    private static final String TAG = "CustomTabsHelper";
    static final String STABLE_PACKAGE = "com.android.chrome";
    static final String BETA_PACKAGE = "com.chrome.beta";
    static final String DEV_PACKAGE = "com.chrome.dev";
    static final String LOCAL_PACKAGE = "com.google.android.apps.chrome";
    private static final String ACTION_CUSTOM_TABS_CONNECTION = "android.support.customtabs.action.CustomTabsService";

    private CustomTabsSession mCustomTabsSession;
    private CustomTabsClient mClient;
    private CustomTabsServiceConnection mConnection;

    /**
     * Öffnet die URL in einem Custom Tab, wenn möglich. Andernfalls fällt es auf das Öffnen in einem WebView zurück.
     *
     * @param activity         Die Host-Aktivität.
     * @param customTabsIntent ein CustomTabsIntent, das verwendet werden soll, wenn Custom Tabs verfügbar sind.
     * @param uri              die Uri, die geöffnet werden soll.
     * @param fallback         ein CustomTabFallback, das verwendet wird, wenn Custom Tabs nicht verfügbar sind.
     */
    public static void openCustomTab(Activity activity, CustomTabsIntent customTabsIntent, Uri uri, CustomTabFallback fallback) {
        String packageName = getPackageNameToUse(activity);

        // Wenn wir keinen Paketnamen finden können, bedeutet dies, dass kein Browser installiert ist, der Chrome Custom Tabs unterstützt.
        // Daher greifen wir auf die WebView zurück.
        if (packageName == null) {
            if (fallback != null) {
                fallback.openUri(activity, uri);
            }
        } else {
            customTabsIntent.intent.setPackage(packageName);
            customTabsIntent.launchUrl(activity, uri);
        }
    }

    /**
     * Wird als Fallback verwendet, um die Uri zu öffnen, wenn Custom Tabs nicht verfügbar sind.
     */
    public interface CustomTabFallback {
        /**
         * @param activity Die Aktivität, die die Uri öffnen möchte.
         * @param uri      Die Uri, die vom Fallback geöffnet werden soll.
         */
        void openUri(Activity activity, Uri uri);
    }

    private static String sPackageNameToUse;

    /**
     * Durchsucht alle Apps, die VIEW-Intents verarbeiten, und haben einen Warmup-Service.
     * Wählt die vom Benutzer gewählte aus, falls vorhanden, andernfalls gibt es einen
     * gültigen Paketnamen zurück.
     * <p>
     * Dies ist <strong>nicht</strong> threadsicher.
     *
     * @param context {@link Context} für den Zugriff auf {@link PackageManager}.
     * @return Der empfohlene Paketname, der für die Verbindung zu Komponenten von Custom Tabs verwendet werden soll.
     */
    public static String getPackageNameToUse(Context context) {
        if (sPackageNameToUse != null) return sPackageNameToUse;

        PackageManager pm = context.getPackageManager();
        // Standard VIEW-Intent-Handler abrufen.
        Intent activityIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.example.com"));
        ResolveInfo defaultViewHandlerInfo = pm.resolveActivity(activityIntent, 0);
        String defaultViewHandlerPackageName = null;
        if (defaultViewHandlerInfo != null) {
            defaultViewHandlerPackageName = defaultViewHandlerInfo.activityInfo.packageName;
        }

        // Alle Apps abrufen, die VIEW-Intents verarbeiten können.
        List<ResolveInfo> resolvedActivityList = pm.queryIntentActivities(activityIntent, 0);
        List<String> packagesSupportingCustomTabs = new ArrayList<>();
        for (ResolveInfo info : resolvedActivityList) {
            Intent serviceIntent = new Intent();
            serviceIntent.setAction(ACTION_CUSTOM_TABS_CONNECTION);
            serviceIntent.setPackage(info.activityInfo.packageName);
            if (pm.resolveService(serviceIntent, 0) != null) {
                packagesSupportingCustomTabs.add(info.activityInfo.packageName);
            }
        }

        // Nun enthält packagesSupportingCustomTabs alle Apps, die sowohl VIEW-Intents als auch Serviceanrufe verarbeiten können.
        if (packagesSupportingCustomTabs.isEmpty()) {
            sPackageNameToUse = null;
        } else if (packagesSupportingCustomTabs.size() == 1) {
            sPackageNameToUse = packagesSupportingCustomTabs.get(0);
        } else if (!TextUtils.isEmpty(defaultViewHandlerPackageName) && !hasSpecializedHandlerIntents(context, activityIntent) && packagesSupportingCustomTabs.contains(defaultViewHandlerPackageName)) {
            sPackageNameToUse = defaultViewHandlerPackageName;
        } else if (packagesSupportingCustomTabs.contains(STABLE_PACKAGE)) {
            sPackageNameToUse = STABLE_PACKAGE;
        } else if (packagesSupportingCustomTabs.contains(BETA_PACKAGE)) {
            sPackageNameToUse = BETA_PACKAGE;
        } else if (packagesSupportingCustomTabs.contains(DEV_PACKAGE)) {
            sPackageNameToUse = DEV_PACKAGE;
        } else if (packagesSupportingCustomTabs.contains(LOCAL_PACKAGE)) {
            sPackageNameToUse = LOCAL_PACKAGE;
        }
        return sPackageNameToUse;
    }

    /**
     * Wird verwendet, um zu überprüfen, ob ein spezialisierter Handler für einen bestimmten Intent vorhanden ist.
     *
     * @param intent Der zu überprüfende Intent.
     * @return Ob ein spezialisierter Handler für den angegebenen Intent vorhanden ist.
     */
    private static boolean hasSpecializedHandlerIntents(Context context, Intent intent) {
        try {
            PackageManager pm = context.getPackageManager();
            List<ResolveInfo> handlers = pm.queryIntentActivities(intent, PackageManager.GET_RESOLVED_FILTER);
            if (handlers.size() == 0) {
                return false;
            }
            for (ResolveInfo resolveInfo : handlers) {
                IntentFilter filter = resolveInfo.filter;
                if (filter == null) continue;
                if (filter.countDataAuthorities() == 0 || filter.countDataPaths() == 0)
                    continue;
                if (resolveInfo.activityInfo == null) continue;
                return true;
            }
        } catch (RuntimeException e) {
            Log.e(TAG, "Runtime exception while getting specialized handlers");
        }
        return false;
    }

}
