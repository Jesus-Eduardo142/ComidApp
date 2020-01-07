package com.app.comidapp.ui.login;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.app.comidapp.R;
import com.app.comidapp.data.APIConfig;
import com.app.comidapp.data.model.User;
import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SignUp extends AppCompatActivity {

    // Widgets
    private EditText newUsername;
    private EditText newPassword;
    private EditText newCity;
    private EditText newAddress;
    private EditText newPostalCode;

    // Data
    private String json;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        initWidgets();
    }

    private void initWidgets() {
        newUsername = findViewById(R.id.newUsername);
        newPassword = findViewById(R.id.newPassword);
        newCity = findViewById(R.id.newCity);
        newAddress = findViewById(R.id.newAddress);
        newPostalCode = findViewById(R.id.newPostalCode);
    }

    public void signUp(View view) {

        String result = null;

        User user = new User(
                newUsername.getText().toString(),
                newPassword.getText().toString(),
                "user",
                newCity.getText().toString(),
                newAddress.getText().toString(),
                newPostalCode.getText().toString()
        );

        json = new Gson().toJson(user);

        SignUpTask task = new SignUpTask(APIConfig.API_URL + "user");

        try {
            result = task.execute().get();
        } catch (ExecutionException | InterruptedException e) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, e);
        }

        if (result != null) {
            Toast.makeText(getBaseContext(), "Registrado completado!", Toast.LENGTH_LONG).show();

            finish();
        }
        else {
            Toast.makeText(getBaseContext(), "Error!", Toast.LENGTH_LONG).show();
        }
    }

    private class SignUpTask extends AsyncTask<Void, Void, String> {

        private URL url;
        private HttpURLConnection connection;

        public SignUpTask(String url) {
            try {
                this.url = new URL(url);
            } catch (MalformedURLException ex) {
                Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
            }
        }

        @Override
        protected String doInBackground(Void... voids) {

            return new Gson().fromJson(post(), User.class).toString();
        }

        private String post() {
            try {
                connection = (HttpURLConnection) url.openConnection();

                connection.setDoOutput(true);
                connection.setRequestMethod("POST");
                connection.setRequestProperty("Content-Type", "application/json");

                String input = json;

                OutputStream os = connection.getOutputStream();

                os.write(input.getBytes());

                os.flush();

                if (connection.getResponseCode() != 201) {
                    throw new RuntimeException("Failed: Http error code: " + connection.getResponseCode());
                }

                BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));

                return br.readLine();
            } catch (IOException ex) {
                Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
            } finally {
                connection.disconnect();
            }

            return null;
        }
    }
}
