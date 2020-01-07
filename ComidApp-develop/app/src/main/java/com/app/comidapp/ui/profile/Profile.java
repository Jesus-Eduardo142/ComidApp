package com.app.comidapp.ui.profile;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.app.comidapp.R;
import com.app.comidapp.data.APIConfig;
import com.app.comidapp.data.model.Cart;
import com.app.comidapp.data.model.Food;
import com.app.comidapp.data.model.User;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Profile extends AppCompatActivity {

    // Var
    private String token;
    private User user;

    private ArrayList<Cart> data;
    private ArrayAdapter<Cart> arrayAdapter;

    // Widgets
    private EditText usernameP;
    private EditText passwordP;
    private EditText cityP;
    private EditText addressP;
    private EditText cpP;

    private ListView cartList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        initVar();
        initWidgets();
        fillList();
    }

    private void initVar() {
        user = (User) getIntent().getSerializableExtra("user");
        token = getIntent().getStringExtra("token");
        data = new ArrayList<>();
    }

    private void initWidgets() {
        usernameP = findViewById(R.id.usernameP);
        passwordP = findViewById(R.id.passwordP);
        cityP = findViewById(R.id.cityP);
        addressP = findViewById(R.id.addressP);
        cpP = findViewById(R.id.cpP);

        cartList = findViewById(R.id.cartList);

        Toolbar toolbar = findViewById(R.id.profileBar);
        toolbar.setTitle("Perfil");
        setSupportActionBar(toolbar);

        usernameP.setText(user.getUsername());
        cityP.setText(user.getCity());
        addressP.setText(user.getAddress());
        cpP.setText(user.getCp());
    }

    private void fillList() {
        try {
            for (Cart cart : new CartTask(APIConfig.API_URL + "cart").execute().get()) {

                if (cart.getUserId() == user.getId()) {
                    cart.setDate(cart.getDate().substring(0, 10) + " | " + cart.getDate().substring(11, 19));
                    data.add(cart);
                }
            }
        } catch (ExecutionException | InterruptedException e) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, e);
        }

        arrayAdapter = new ArrayAdapter<>(Profile.this, android.R.layout.simple_list_item_1, data);
        cartList.setAdapter(arrayAdapter);
    }

    public void save(View view) {
        user.setUsername(usernameP.getText().toString());
        user.setPassword(passwordP.getText().toString());
        user.setCity(cityP.getText().toString());
        user.setAddress(addressP.getText().toString());
        user.setCp(cpP.getText().toString());

        try {
            User u = new UpdateUserTask(APIConfig.API_URL + "user/" + user.getId()).execute(user).get();

            if (u == null) {
                Toast.makeText(getBaseContext(), "Error!", Toast.LENGTH_SHORT).show();
            }
            else {
                user = u;

                usernameP.setText(user.getUsername());
                cityP.setText(user.getCity());
                addressP.setText(user.getAddress());
                cpP.setText(user.getCp());

                Toast.makeText(getBaseContext(), "Actualizado con exito!", Toast.LENGTH_SHORT).show();
            }
        } catch (ExecutionException | InterruptedException e) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, e);
        }
    }

    private class CartTask extends AsyncTask<Void, Void, Cart[]> {

        private URL url;
        private HttpURLConnection connection;

        private CartTask(String url) {
            try {
                this.url = new URL(url);
            } catch (MalformedURLException ex) {
                Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
            }
        }

        @Override
        protected Cart[] doInBackground(Void... voids) {
            try {
                String result = getCarts();

                JsonArray arrayC = new JsonParser().parse(result).getAsJsonArray();
                Cart[] carts = new Cart[arrayC.size()];

                for (int j = 0; j < arrayC.size(); j++) {
                    JsonObject object = new JsonParser().parse(arrayC.get(j).toString()).getAsJsonObject();
                    JsonArray array = object.get("items").getAsJsonArray();

                    List<Food> foods = new ArrayList<>();
                    Gson gson = new Gson();

                    for (int i = 0; i < array.size(); i++) {
                        foods.add(gson.fromJson(array.get(i).getAsString(), Food.class));
                    }

                    carts[j] = new Cart(
                            object.get("id").getAsInt(),
                            object.get("UserId").getAsInt(),
                            foods,
                            object.get("total").getAsDouble(),
                            object.get("date").getAsString()
                    );
                }

                return carts;
            } catch (RuntimeException e) {
                Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, e);
                return null;
            }
        }

        private String getCarts() {
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

    private class UpdateUserTask extends AsyncTask<User, Void, User> {

        private URL url;
        private HttpURLConnection connection;

        private UpdateUserTask(String url) {
            try {
                this.url = new URL(url);
            } catch (MalformedURLException ex) {
                Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
            }
        }

        @Override
        protected User doInBackground(User... user) {
            try {
                return new Gson().fromJson(put(user[0]), User.class);
            } catch (RuntimeException e) {
                Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, e);
                return null;
            }
        }

        public String put(Object entity) {
            try {
                connection = (HttpURLConnection) url.openConnection();

                connection.setDoOutput(true);
                connection.setRequestMethod("PUT");
                connection.setRequestProperty("Content-Type", "application/json");

                String input = new Gson().toJson(entity);

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
}
