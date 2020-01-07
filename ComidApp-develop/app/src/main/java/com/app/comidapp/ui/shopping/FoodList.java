package com.app.comidapp.ui.shopping;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.app.comidapp.R;
import com.app.comidapp.data.APIConfig;
import com.app.comidapp.data.model.Cart;
import com.app.comidapp.data.model.Food;
import com.app.comidapp.data.model.User;
import com.app.comidapp.ui.profile.Profile;
import com.app.comidapp.util.CustomAdapter;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class FoodList extends AppCompatActivity {

    // Var
    private String token;
    private User user;
    private Cart cart;
    private Food selectedFood;
    private boolean emptyCart;

    private ArrayList<Food> data;
    private CustomAdapter adapter;

    // Widgets
    private TextView name;
    private TextView description;
    private TextView price;
    private TextView addQuantity;

    private Button addItem;

    private ListView foodList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_list);

        initVar();
        initWidgets();
        initListeners();
        fillList();

        Toast.makeText(getBaseContext(), "Bienvenido " + user.getUsername(), Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1) {
            if (resultCode == RESULT_CANCELED && data != null) {
                cart = (Cart) data.getSerializableExtra("cart");
                boolean deleted = data.getBooleanExtra("deleted", false);

                if(deleted) {
                    this.data = new ArrayList<>();
                    fillList();

                    for(Food food: cart.getItems()) {
                        this.data.forEach(food1 -> {
                            if(food.getId() == food1.getId()) {
                                food1.setQuantity(food1.getQuantity() - food.getQuantity());
                            }
                        });
                    }

                    refreshItems();
                }
            }
            if (resultCode == RESULT_OK) {
                cart = null;
                initVar();
                fillList();
            }
        }
    }

    private void initVar() {
        user = (User) getIntent().getSerializableExtra("user");
        token = getIntent().getStringExtra("token");
        data = new ArrayList<>();

        if (cart == null) {
            cart = new Cart(0, user.getId(), new ArrayList<>(), 0.0);
        }

        emptyCart = cart.getItems().isEmpty();
    }

    private void initWidgets() {
        name = findViewById(R.id.name);
        description = findViewById(R.id.description);
        price = findViewById(R.id.price);
        addQuantity = findViewById(R.id.addQuantity);

        addItem = findViewById(R.id.addItem);

        foodList = findViewById(R.id.foodList);

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Selecciona tu orden");
        setSupportActionBar(toolbar);
    }

    private void initListeners() {
        foodList.setOnItemClickListener((parent, view, position, id) -> {

            selectItem(position);

            if (addQuantity.getText().toString().equals("")) {
                addQuantity.setError("Digite una cantidad");
            }
            else {
                addQuantity.setText("");
            }
        });
        addItem.setOnClickListener(v -> addItem());
    }

    private void fillList() {
        try {
            data.addAll(Arrays.asList(new FoodTask(APIConfig.API_URL + "food").execute().get()));
        } catch (ExecutionException | InterruptedException e) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, e);
        }

        adapter = new CustomAdapter(this, data, false);
        foodList.setAdapter(adapter);
    }

    private void refreshItems() {
        adapter.notifyDataSetChanged();
        foodList.invalidateViews();
        foodList.refreshDrawableState();
    }

    private void selectItem(int position) {
        selectedFood = data.get(position);

        name.setText(selectedFood.getName());
        description.setText(selectedFood.getDescription());
        price.setText(String.valueOf(selectedFood.getPrice()));
    }

    private void addItem() {

        if (addQuantity.getText().toString().equals("")) {
            addQuantity.setError("Digite una cantidad");
        }
        else {
            Food food = new Food(
                    selectedFood.getId(),
                    selectedFood.getName(),
                    selectedFood.getDescription(),
                    Integer.parseInt(addQuantity.getText().toString()),
                    selectedFood.getPrice(),
                    selectedFood.getStoreId());

            double price = food.getPrice() * food.getQuantity();

            if (food.getQuantity() > selectedFood.getQuantity()) {
                Toast.makeText(getBaseContext(), "Inventario insuficiente!", Toast.LENGTH_SHORT).show();
            }
            else {
                cart.getItems().add(food);
                cart.addToTotal(price);

                Toast.makeText(getBaseContext(), "Agregado!", Toast.LENGTH_SHORT).show();

                selectedFood.setQuantity(selectedFood.getQuantity() - food.getQuantity());
                refreshItems();
            }

            addQuantity.setText("");
            emptyCart = cart.getItems().isEmpty();
        }
    }

    public void goToCart(View view) {
        if (emptyCart) {
            Snackbar.make(view, "Tu carro esta vacío!", Snackbar.LENGTH_SHORT).show();
        }
        else {
            Intent intent = new Intent(getBaseContext(), ShoppingCart.class);

            intent.putExtra("cart", cart);
            intent.putExtra("token", token);

            startActivityForResult(intent, 1);
        }
    }

    public void goToProfile(View view) {
        Intent intent = new Intent(getBaseContext(), Profile.class);

        intent.putExtra("user", user);
        intent.putExtra("token", token);

        startActivity(intent);
    }

    private class FoodTask extends AsyncTask<Void, Void, Food[]> {

        private URL url;
        private HttpURLConnection connection;

        private FoodTask(String url) {
            try {
                this.url = new URL(url);
            } catch (MalformedURLException ex) {
                Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
            }
        }

        @Override
        protected Food[] doInBackground(Void... voids) {

            return new Gson().fromJson(getFood(), Food[].class);
        }

        private String getFood() {
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
