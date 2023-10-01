package app.thecity.utils;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;

import androidx.core.content.ContextCompat;

import java.util.ArrayList;
import java.util.List;

import app.thecity.data.SharedPref;

public abstract class PermissionUtil {

    // Berechtigungen, die für die Anwendung erforderlich sind
    public static final String STORAGE = Manifest.permission.WRITE_EXTERNAL_STORAGE;
    public static final String LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;

    public static final String[] PERMISSION_ALL = {
            Manifest.permission.ACCESS_FINE_LOCATION
    };



    // Erhalte die verweigerten Berechtigungen
    public static String[] getDeniedPermission(Activity act) {
        List<String> permissions = new ArrayList<>();
        for (int i = 0; i < PERMISSION_ALL.length; i++) {
            int status = 0;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                status = act.checkSelfPermission(PERMISSION_ALL[i]);
            }
            if (status != PackageManager.PERMISSION_GRANTED) {
                permissions.add(PERMISSION_ALL[i]);
            }
        }

        return permissions.toArray(new String[permissions.size()]);
    }

    // Überprüfe und fordere die Benachrichtigungsberechtigung an
    public static void checkAndRequestNotification(Activity act) {
        SharedPref sharedPref = new SharedPref(act);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(act, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_DENIED
                    && !sharedPref.getNeverAskAgain(Manifest.permission.POST_NOTIFICATIONS)) {
                String[] permissions = {Manifest.permission.POST_NOTIFICATIONS};
                act.requestPermissions(permissions, 200);
                sharedPref.setNeverAskAgain(Manifest.permission.POST_NOTIFICATIONS, true);
            }
        }
    }


    // Überprüfe, ob die Berechtigung erteilt wurde
    public static boolean isGranted(Context ctx, String permission) {
        if (!Tools.needRequestPermission()) return true;
        return (ctx.checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED);
    }

    // Überprüfe, ob die Standortberechtigung erteilt wurde
    public static boolean isLocationGranted(Context ctx) {
        return isGranted(ctx, Manifest.permission.ACCESS_FINE_LOCATION);
    }


}
