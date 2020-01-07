package com.app.comidapp.util;

import com.app.comidapp.R;

public class File {

    public static int findImage(int id) {
        switch (id) {
            case 4: return R.drawable.pizza_peperonni;
            case 5: return R.drawable.pizza_napolitana;
            case 6: return R.drawable.pizza_pastor;
            case 7: return R.drawable.mc_trio;
            case 8: return R.drawable.big_mac;
            case 9: return R.drawable.hamburguesa;
            case 10: return R.drawable.hamburguesa_con_queso;
            case 11: return R.drawable.rollos_california;
            case 12: return R.drawable.yakimeshi;
            case 13: return R.drawable.rollos_philadelphia;
            case 14: return R.drawable.teka_maki;
            case 15: return R.drawable.sunomono;
            case 16: return R.drawable.popcorn_chicken;
            case 17: return R.drawable.kruncher;
            case 18: return R.drawable.big_box;
            case 19: return R.drawable.mega_box;
            case 20: return R.drawable.cruji_alitas;
            case 21: return R.drawable.pure;
            default: return R.drawable.placeholder;
        }
    }
}
