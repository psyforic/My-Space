package com.metrorez.myspace.admin.data;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;

import com.metrorez.myspace.R;
import com.metrorez.myspace.admin.model.AdminUser;
import com.metrorez.myspace.admin.model.City;

import java.util.ArrayList;
import java.util.List;

public class Constants {

    public static Resources getStrRes(Context context) {
        return context.getResources();
    }


    public static List<City> getCityData(Context ctx) {
        List<City> items = new ArrayList<>();
        String s_name[] = ctx.getResources().getStringArray(R.array.city_names);
        String s_complaints[] = ctx.getResources().getStringArray(R.array.city_no_complaints);
        TypedArray drw_arr = ctx.getResources().obtainTypedArray(R.array.city_photos);

        items.add(new City("816417fa-cdf1-49ae-bbd6-0368b38b4362", s_name[0], s_complaints[0], drw_arr.getResourceId(0, -1)));
        items.add(new City("311da6d5-ad82-4221-b613-26e8dbca793e", s_name[1], s_complaints[1], drw_arr.getResourceId(0, -1)));
        items.add(new City("d74c631d-501b-4db9-94ce-57c9a91b5fb4", s_name[2], s_complaints[0], drw_arr.getResourceId(0, -1)));

        return items;
    }

//    public static List<AdminUser> getUsersData(Context ctx) {
//        List<AdminUser> items = new ArrayList<>();
//        String s_arr[] = ctx.getResources().getStringArray(R.array.users_names);
//        TypedArray drw_arr = ctx.getResources().obtainTypedArray(R.array.users_photos);
//        for (int i = 0; i < s_arr.length; i++) {
//            AdminUser fr = new AdminUser(i, s_arr[i], drw_arr.getResourceId(i, -1));
//            items.add(fr);
//        }
//        return items;
//    }
}
