package app.thecity.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import app.thecity.R;
import app.thecity.fragment.LoginFragment;
import app.thecity.fragment.RegisterFragment;

public class LoginActivity extends AppCompatActivity {

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