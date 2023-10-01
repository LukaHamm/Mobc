package app.thecity.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.gson.Gson;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import app.thecity.R;
import app.thecity.activity.ActivityMain;
import app.thecity.activity.LoginActivity;
import app.thecity.connection.API;
import app.thecity.connection.RestAdapter;
import app.thecity.connection.callbacks.CallbackUser;
import app.thecity.model.User;
import app.thecity.model.UserInfo;
import retrofit2.Call;
import retrofit2.Response;

public class LoginFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_login, container, false);
        API api = RestAdapter.createMobcApi();
        // Initialisiere Views und setze Klick-Listener für den Einloggen-Button
        TextView signupButton = rootView.findViewById(R.id.signupButton);
        EditText usernameText = rootView.findViewById(R.id.usernameEditText);
        EditText passwordText = rootView.findViewById(R.id.passwordEditText);

        signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((LoginActivity) requireActivity()).switchFragments();
            }
        });

        Button loginButton =rootView.findViewById(R.id.loginButton);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String password = passwordText.getText().toString();
                String username = usernameText.getText().toString();
                System.out.println("User: " + username);
                System.out.println("Password: " + password);
                UserInfo userInfo = new UserInfo(username, password);
                Call<CallbackUser> callbackUserCall = api.login(userInfo);
                if (username != null && !username.isEmpty() && password != null && !password.isEmpty()) {
                    callbackUserCall.enqueue(new retrofit2.Callback<CallbackUser>() {
                        @Override
                        public void onResponse(Call<CallbackUser> call, Response<CallbackUser> response) {
                            if (response.isSuccessful()) {
                                CallbackUser resp = response.body();
                                User user = resp.user;
                                Log.d("MeineActivity", "Benutzername: " + user.name);
                                try {
                                    File file = new File(getContext().getFilesDir(), "userdata.json");
                                    FileWriter fileWriter = new FileWriter(file);
                                    BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
                                    bufferedWriter.write(new Gson().toJson(user));
                                    bufferedWriter.close();
                                } catch (IOException e) {
                                    return;
                                }

                                Intent intent = new Intent(getActivity(), ActivityMain.class);
                                startActivity(intent);
                            }else {
                                Toast.makeText(getContext(),"E-Mail oder Passwort falsch", Toast.LENGTH_LONG).show();
                            }

                        }

                        @Override
                        public void onFailure(Call<CallbackUser> call, Throwable t) {
                            Log.e("onFailure", t.getMessage());
                        }
                    });
                }else {
                    Toast.makeText(getContext(),"Bitte füllen Sie alle Felder aus", Toast.LENGTH_LONG).show();
                }
            }

        });

        return rootView;
    }
}