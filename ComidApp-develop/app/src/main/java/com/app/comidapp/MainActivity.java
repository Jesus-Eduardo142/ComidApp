package com.app.comidapp;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.app.comidapp.data.APIConfig;
import com.app.comidapp.data.model.User;
import com.app.comidapp.ui.login.SignUp;
import com.app.comidapp.ui.shopping.FoodList;
import com.google.gson.Gson;
import com.google.gson.JsonParser;

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

public class MainActivity extends AppCompatActivity {

    // Widgets
    private EditText usernameIn;
    private EditText passwordIn;

    // Data
    private String token;
    private User user;
    private String json;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initVar();
        initWidgets();
    }

    private void initVar() {

    }

    private void initWidgets() {
        usernameIn = findViewById(R.id.usernameIn);
        passwordIn = findViewById(R.id.passwordIn);
    }

    public void login(View view) {

        String token = null;
        boolean admin = false;

        User userIn = new User(
                usernameIn.getText().toString(),
                passwordIn.getText().toString()
        );

        json = new Gson().toJson(userIn);

        LoginTask task;

        try {
            task = new LoginTask(APIConfig.API_URL + "login/user");
            token = task.execute().get();
            admin = false;

            if (token == null) {
                task = new LoginTask(APIConfig.API_URL + "login/admin");
                token = task.execute().get();
                admin = true;
            }

        } catch (ExecutionException | InterruptedException e) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, e);
            Toast.makeText(getBaseContext(), "Error!", Toast.LENGTH_SHORT).show();
        }

        try {
            if (token != null) {

                if (admin) {
                    for (User u : new UserTask(APIConfig.API_URL + "user?role=admin").execute().get()) {
                        if (u.getUsername().equals(userIn.getUsername())) {
                            user = u;
                        }
                    }
                }
                else {
                    for (User u : new UserTask(APIConfig.API_URL + "user?role=user").execute().get()) {
                        if (u.getUsername().equals(userIn.getUsername())) {
                            user = u;
                        }
                    }
                }

                Intent intent = new Intent(getBaseContext(), FoodList.class);

                intent.putExtra("token", token);
                intent.putExtra("user", user);

                startActivity(intent);
            }
            else {
                Toast.makeText(getBaseContext(), "Verifique su información!", Toast.LENGTH_SHORT).show();
            }
        } catch (ExecutionException | InterruptedException ex) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void goToSignUp(View view) {
        Intent intent = new Intent(getBaseContext(), SignUp.class);
        startActivity(intent);
    }

    private class LoginTask extends AsyncTask<Void, Void, String> {

        private URL url;
        private HttpURLConnection connection;

        private LoginTask(String url) {
            try {
                this.url = new URL(url);
            } catch (MalformedURLException ex) {
                Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
            }
        }

        @Override
        protected String doInBackground(Void... voids) {
            try {
                return new JsonParser().parse(post())
                        .getAsJsonObject()
                        .get("token")
                        .getAsString();
            } catch (RuntimeException e) {
                return null;
            }
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

                if (connection.getResponseCode() != 200) {
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

    private class UserTask extends AsyncTask<Void, Void, User[]> {

        private URL url;
        private HttpURLConnection connection;

        private UserTask(String url) {
            try {
                this.url = new URL(url);
            } catch (MalformedURLException ex) {
                Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
            }
        }

        @Override
        protected User[] doInBackground(Void... voids) {

            return new Gson().fromJson(getUsers(), User[].class);
        }

        private String getUsers() {
            try {
                connection = (HttpURLConnection) url.openConnection();

                connection.setRequestMethod("GET");
                connection.setRequestProperty("Accept", "application/json");

                if (connection.getResponseCode() != 200) {
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
