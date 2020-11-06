package com.shzj.behavior.fragment;

import android.content.Context;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import com.google.gson.Gson;
import com.shzj.behavior.fragment.dummy.DummyContent;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.Inflater;

import edu.zjff.shzj.R;
import edu.zjff.shzj.entity.Data;
import edu.zjff.shzj.entity.DataRoot;
import edu.zjff.shzj.util.Constant;
import edu.zjff.shzj.util.HttpUtil;
import edu.zjff.shzj.util.UserUtil;
import edu.zjff.shzj.util.anyUtil;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;


/**
 * A fragment representing a list of Items.
 * <p/>
 */
public class ItemFragment1 extends Fragment {
    // TODO: Customize parameters
    private int mColumnCount = 1;
    private final static String PARAMS_ID = "PARAMS_ID";
    public  ArrayList<?> items;
    public ItemFragment1(ArrayList<?> item) {
        items=item;
        mColumnCount=items.size();

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_item_list1, container, false);
        // Set the adapter
        if (view instanceof androidx.recyclerview.widget.RecyclerView ) {
            Context context = view.getContext();
            RecyclerView recyclerView = (RecyclerView) view;

            MyItemRecyclerViewAdapter adapter= new MyItemRecyclerViewAdapter(items,context);

            if (mColumnCount <= 1) {
                recyclerView.setLayoutManager(new LinearLayoutManager(context));
            } else {
                recyclerView.setLayoutManager(new GridLayoutManager(context, 3));
            }
            recyclerView.setAdapter(adapter);

            }
        return view;
    }


}