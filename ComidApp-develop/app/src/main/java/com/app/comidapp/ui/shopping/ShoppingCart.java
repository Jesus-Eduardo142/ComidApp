package com.app.comidapp.ui.shopping;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.app.comidapp.R;
import com.app.comidapp.data.APIConfig;
import com.app.comidapp.data.model.Cart;
import com.app.comidapp.data.model.Food;
import com.app.comidapp.util.CustomAdapter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
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
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ShoppingCart extends AppCompatActivity {

    // Var
    private String token;
    private boolean deleted;
    private Cart cart;
    private List<Food> data;
    private CustomAdapter adapter;

    // Widgets
    private TextView quantity;
    private TextView total;

    private FloatingActionButton buy;

    private ListView foodList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shopping_cart);

        initVar();
        initWidgets();
        initListeners();
        fillList();
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent();

        intent.putExtra("cart", cart);
        intent.putExtra("deleted", deleted);
        setResult(RESULT_CANCELED, intent);
        finish();
    }

    private void initVar() {
        token = getIntent().getStringExtra("token");
        deleted = false;
        cart = (Cart) getIntent().getSerializableExtra("cart");
        data = cart.getItems();
    }

    private void initWidgets() {
        quantity = findViewById(R.id.total_platillos);
        total = findViewById(R.id.total_price);

        buy = findViewById(R.id.buy);

        foodList = findViewById(R.id.shoppingCart);

        Toolbar toolbar = findViewById(R.id.cartToolbar);
        toolbar.setTitle("Mi carrito");
        setSupportActionBar(toolbar);
    }

    public void initListeners() {
        buy.setOnClickListener(v -> buy());
        foodList.setOnItemLongClickListener((parent, view, position, id) -> {

            data.remove(position);
            refreshItems();
            deleted = true;

            Toast.makeText(getBaseContext(), "Eliminado!", Toast.LENGTH_SHORT).show();

            return true;
        });
    }

    private void fillList() {
        adapter = new CustomAdapter(this, data, true);
        foodList.setAdapter(adapter);

        int quantity = 0;

        for (Food food : data) {
            quantity += food.getQuantity();
        }

        this.quantity.setText(String.valueOf(quantity));
        this.total.setText(String.valueOf(cart.getTotal()));
    }

    private void refreshItems() {
        adapter.notifyDataSetChanged();
        foodList.invalidateViews();
        foodList.refreshDrawableState();
    }

    private String getDate() {
        Calendar calendar = new GregorianCalendar();

        String date = "YEAR-MONTH-DAYTHOUR:MINUTE:SECONDSZ";

        int sec = calendar.get(Calendar.SECOND);
        String SECONDS = sec > 10 ? String.valueOf(sec) : "0" + sec;
        int minute = calendar.get(Calendar.MINUTE);
        String MINUTES = minute > 10 ? String.valueOf(minute) : "0" + minute;
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        String DAYS = day > 10 ? String.valueOf(day) : "0" + day;
        int month = calendar.get(Calendar.MONTH);
        String MONTHS = month > 10 ? String.valueOf(++month) : "0" + (++month);

        date = date.replace("YEAR", String.valueOf(calendar.get(Calendar.YEAR)));
        date = date.replace("MONTH", MONTHS);
        date = date.replace("DAY", DAYS);
        date = date.replace("HOUR", String.valueOf(calendar.get(Calendar.HOUR_OF_DAY)));
        date = date.replace("MINUTE", MINUTES);
        date = date.replace("SECONDS", SECONDS);

        return date;
    }

    private void buy() {
        cart.setDate(getDate());

        Gson gson = new Gson();
        String cartJson = gson.toJson(cart);
        JsonObject cartJsonObject = new JsonParser().parse(cartJson).getAsJsonObject();
        JsonArray items = new JsonArray();

        for (Food f : cart.getItems()) {
            items.add("{'id': " + f.getId() +
                    ",'name': '" + f.getName() +
                    "','description': '" + f.getDescription() +
                    "','quantity': " + f.getQuantity() +
                    ",'price': " + f.getPrice() +
                    ",'StoreId': " + f.getStoreId() + "}");
        }
        cartJsonObject.add("items", items);

        try {
            cart = new CartTask(APIConfig.API_URL + "cart").execute(cartJsonObject.toString()).get();

            if (cart != null) {
                for (Food food : cart.getItems()) {
                    new UpdateFoodTask(APIConfig.API_URL + "food/" + food.getId()).execute(food).get();
                }

                setResult(RESULT_OK);
                finish();
            }
            else {
                Toast.makeText(getBaseContext(), "Error!", Toast.LENGTH_SHORT).show();
            }
        } catch (ExecutionException | InterruptedException e) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, e);
        }
    }

    private class CartTask extends AsyncTask<String, Void, Cart> {

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
        protected Cart doInBackground(String... json) {
            try {
                String result = post(json[0]);

                JsonObject object = new JsonParser().parse(result).getAsJsonObject();
                JsonArray array = object.get("items").getAsJsonArray();

                List<Food> foods = new ArrayList<>();
                Gson gson = new Gson();

                for (int i = 0; i < array.size(); i++) {
                    foods.add(gson.fromJson(array.get(i).getAsString(), Food.class));
                }

                return new Cart(
                        object.get("id").getAsInt(),
                        object.get("UserId").getAsInt(),
                        foods,
                        object.get("total").getAsDouble(),
                        object.get("date").getAsString()
                );
            } catch (RuntimeException e) {
                Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, e);
                return null;
            }
        }

        private String post(String json) {
            try {
                connection = (HttpURLConnection) url.openConnection();

                connection.setDoOutput(true);
                connection.setRequestMethod("POST");
                connection.setRequestProperty("Content-Type", "application/json");

                OutputStream os = connection.getOutputStream();

                os.write(json.getBytes());

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

    private class UpdateFoodTask extends AsyncTask<Food, Void, Food> {

        private URL url;
        private HttpURLConnection connection;

        private UpdateFoodTask(String url) {
            try {
                this.url = new URL(url);
            } catch (MalformedURLException ex) {
                Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
            }
        }

        @Override
        protected Food doInBackground(Food... foods) {
            try {
                return new Gson().fromJson(put(foods[0]), Food.class);
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
