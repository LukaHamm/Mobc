package app.thecity.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import app.thecity.R;
import app.thecity.fragment.LoginFragment;
import app.thecity.fragment.RegisterFragment;

public class LoginActivity extends AppCompatActivity {

    /**
     * Mehode zum Initialisieren der Views
     * Laden des Layouts activity_login
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.logincontainer, new LoginFragment())
                    .commit();
        }
    }

    /**
     * Methode zum hin und her wechseln zwischen Login- und RegisterFragment
     */
    public void switchFragments() {
        if (getSupportFragmentManager().findFragmentById(R.id.logincontainer) instanceof LoginFragment) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.logincontainer, new RegisterFragment())
                    .addToBackStack(null)
                    .commit();
        } else if (getSupportFragmentManager().findFragmentById(R.id.logincontainer) instanceof RegisterFragment) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.logincontainer, new LoginFragment())
                    .addToBackStack(null)
                    .commit();
        }
    }


}