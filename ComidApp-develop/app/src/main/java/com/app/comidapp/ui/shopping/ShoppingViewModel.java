package com.app.comidapp.ui.shopping;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.app.comidapp.data.model.Cart;

public class ShoppingViewModel extends ViewModel {
    private MutableLiveData<Cart> cart;

    public ShoppingViewModel() {
        cart = new MutableLiveData<>();
    }

    public MutableLiveData<Cart> getCart() {
        return cart;
    }

    public void setCart(Cart cart) {
        this.cart.setValue(cart);
    }
}
