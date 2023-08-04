package app.thecity.utils;
import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

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

    // Gehe zu den Anwendungseinstellungen für Berechtigungen
    public static void goToPermissionSettingScreen(Activity activity) {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.fromParts("package", activity.getPackageName(), null));
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        activity.startActivity(intent);
    }

    // Überprüfe, ob alle Berechtigungen gewährt sind
    public static boolean isAllPermissionGranted(Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            String[] permission = PERMISSION_ALL;
            if (permission.length == 0) return false;
            for (String s : permission) {
                if (ActivityCompat.checkSelfPermission(activity, s) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }

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

    // Überprüfe und fordere die Benachrichtigungsberechtigung mit Erklärung an
    public static void checkAndRequestNotificationRationale(Activity act) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(act, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_DENIED) {
                String[] permissions = {Manifest.permission.POST_NOTIFICATIONS};
                act.requestPermissions(permissions, 200);
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

    // Überprüfe, ob die Speicherberechtigung erteilt wurde
    public static boolean isStorageGranted(Context ctx) {
        return isGranted(ctx, Manifest.permission.WRITE_EXTERNAL_STORAGE);
    }

    // Zeige ein Systemdialogfenster für die Berechtigung an (für Fragment)
    public static void showSystemDialogPermission(Fragment fragment, String perm) {
        fragment.requestPermissions(new String[]{perm}, 200);
    }

    // Zeige ein Systemdialogfenster für die Berechtigung an (für Activity)
    public static void showSystemDialogPermission(Activity act, String perm) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            act.requestPermissions(new String[]{perm}, 200);
        }
    }

    // Zeige ein Systemdialogfenster für die Berechtigung an (für Activity) mit einem bestimmten Anfragecode
    public static void showSystemDialogPermission(Activity act, String perm, int code) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            act.requestPermissions(new String[]{perm}, code);
        }
    }
}
