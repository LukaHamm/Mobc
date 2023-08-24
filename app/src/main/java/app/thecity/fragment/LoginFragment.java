package app.thecity.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import app.thecity.R;
import app.thecity.activity.LoginActivity;
import app.thecity.connection.API;
import app.thecity.connection.RestAdapter;
import app.thecity.connection.callbacks.CallbackDevice;
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
        TextView greeting = rootView.findViewById(R.id.greeting);

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
                System.out.println("Password: " +password);
                UserInfo userInfo = new UserInfo(username,password);
                Call<CallbackUser> callbackUserCall= api.login(userInfo);
                callbackUserCall.enqueue(new retrofit2.Callback<CallbackUser>() {
                    @Override
                    public void onResponse(Call<CallbackUser> call, Response<CallbackUser> response) {
                        CallbackUser resp = response.body();
                        User user = resp.user;
                        greeting.setText("Hello+ " + user.name + " your token is " + user.token + ", your id is " + user._id);
                    }

                    @Override
                    public void onFailure(Call<CallbackUser> call, Throwable t) {
                    }
                });
            }
        });

        return rootView;
    }
}