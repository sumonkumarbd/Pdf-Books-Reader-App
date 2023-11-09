package com.sumonkmr.coustompdfreaderapps;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerView,recyclerView2,recyclerView3;
    private RecyclerView.Adapter adapter,adapter2,adapter3;
    private RecyclerView.LayoutManager layoutManager,layoutManager2,layoutManager3;
    Context context;
    HashMap<String,String> pdfTemp;
    List<HashMap<String,String>> pdfList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Add Hashmap in List
        pdfList = new ArrayList<>();
        pdfList.add(getPdf("কল্যাণী","জীবনানন্দ দাশ", String.valueOf(R.drawable.kalyani_jibananda_das)));
        pdfList.add(getPdf("এসেছ তুমি রচিত হতে","কোয়েল তালুকদার", String.valueOf(R.drawable.esecho_tumi_rachito_hote)));

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView2 = findViewById(R.id.recyclerView2);
        recyclerView3 = findViewById(R.id.recyclerView3);
        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        recyclerView.setHasFixedSize(true);
        recyclerView2.setHasFixedSize(true);
        recyclerView3.setHasFixedSize(true);

        // specify the view adapter
        adapter = new MyAdapter(this,pdfList);
        adapter2 = new MyAdapter(this,pdfList);
        adapter3 = new MyAdapter(this,pdfList);
        recyclerView.setAdapter(adapter);
        recyclerView2.setAdapter(adapter2);
        recyclerView3.setAdapter(adapter3);

        // specify the view layout manager
        layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        layoutManager2 = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        layoutManager3 = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView2.setLayoutManager(layoutManager2);
        recyclerView3.setLayoutManager(layoutManager3);

    }

    private HashMap<String, String> getPdf(String title, String author, String cover) {

        pdfTemp = new HashMap<>();
        pdfTemp.put("title",title);
        pdfTemp.put("author",author);
        pdfTemp.put("cover",cover);

        return pdfTemp;
    }


}