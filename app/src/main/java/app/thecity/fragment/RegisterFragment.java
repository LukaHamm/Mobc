package app.thecity.fragment;

import android.os.Bundle;
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

import java.time.Duration;

import app.thecity.R;
import app.thecity.activity.LoginActivity;
import app.thecity.connection.API;
import app.thecity.connection.RestAdapter;
import app.thecity.connection.callbacks.CallbackUser;
import app.thecity.model.User;
import retrofit2.Call;
import retrofit2.Response;

public class RegisterFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_register, container, false);

        // Initialisiere Views und setze Klick-Listener für den Registrieren-Button
        TextView backToLoginButton = rootView.findViewById(R.id.backToLoginButton);
        EditText usernameEditText = rootView.findViewById(R.id.usernameEditText);
        EditText nameEditText = rootView.findViewById(R.id.nameEditText);
        EditText lastnameEditText = rootView.findViewById(R.id.lastNameEditText);
        EditText emailEditText = rootView.findViewById(R.id.emaileditText);
        EditText passwordEditText = rootView.findViewById(R.id.passwordEditText);
        EditText confirmPasswordEditText = rootView.findViewById(R.id.passwordConfirmEditText);
        Button registerButton = rootView.findViewById(R.id.registerButton);
        backToLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((LoginActivity) requireActivity()).switchFragments();
            }
        });

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (usernameEditText.getText().toString().isEmpty() || nameEditText.getText().toString().isEmpty() || lastnameEditText.getText().toString().isEmpty() || emailEditText.getText().toString().isEmpty() ||
                passwordEditText.getText().toString().isEmpty() ||  confirmPasswordEditText.getText().toString().isEmpty()){
                    Toast.makeText(getContext(),"Please fill in the blanks", Toast.LENGTH_LONG).show();
                }
                else if (!passwordEditText.getText().toString().equals(confirmPasswordEditText.getText().toString())){
                    Toast.makeText(getContext(),"Passwords don't match", Toast.LENGTH_LONG).show();
                }else {
                    User user = new User(nameEditText.getText().toString(),lastnameEditText.getText().toString(),usernameEditText.getText().toString(),emailEditText.getText().toString(),passwordEditText.getText().toString());
                    API api = RestAdapter.createMobcApi();
                    Call<String> userCall = api.register(user);
                    userCall.enqueue(new retrofit2.Callback<String>() {
                        @Override
                        public void onResponse(Call<String> call, Response<String> response) {
                            Toast.makeText(getContext(),response.body(), Toast.LENGTH_LONG).show();
                        }

                        @Override
                        public void onFailure(Call<String> call, Throwable t) {

                        }
                    });
                }

            }
        });


        return rootView;
    }
}