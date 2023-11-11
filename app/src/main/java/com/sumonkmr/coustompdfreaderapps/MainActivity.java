package com.sumonkmr.coustompdfreaderapps;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.PagerSnapHelper;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ScrollView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    Context context;
    HashMap<String, String> pdfTemp;
    List<HashMap<String, String>> pdfList;
    ImageView canvasBar;
    ScrollView parentScrollView;
    private RecyclerView recyclerViewForTrendingSec, recyclerViewForNewSec, recyclerViewForCat1,recyclerViewForCat2;
    private RecyclerView.Adapter trending_sec_adapter, new_sec_adapter, cat1_sec_adapter,cat2_sec_adapter;
    private RecyclerView.LayoutManager layoutManager, layoutManager2, layoutManager3, layoutManager4;
    Handler handler;
    Runnable autoScrollRunnable;
    private int currentPosition = 0;
    private static final int DELAY_MS = 3000; // Delay between slides in milliseconds
    private static final int INITIAL_POSITION = 0; // Initial position of the RecyclerView

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//       ***Call Functions Here****
        HookUps();//For HookUps xml with java
        TrendingPDFs();//All Pdf List
        RecyclerDefiner();//Settings of RecyclerView

    }//onCreate Finished.

    //    (((Custom Functions)))
    private HashMap<String, String> getPdf(String title, String author, String cover) {

        pdfTemp = new HashMap<>();
        pdfTemp.put("title", title);
        pdfTemp.put("author", author);
        pdfTemp.put("cover", cover);

        return pdfTemp;
    }

    private void HookUps() {
        parentScrollView = findViewById(R.id.parentScrollView);
        canvasBar = findViewById(R.id.canvasBar);
        recyclerViewForTrendingSec = findViewById(R.id.recyclerView);
        recyclerViewForNewSec = findViewById(R.id.recyclerView2);
        recyclerViewForCat1 = findViewById(R.id.recyclerView3);
        recyclerViewForCat2 = findViewById(R.id.recyclerView4);
    }

    public void TrendingPDFs() {
        pdfList = new ArrayList<>();
        pdfList.add(getPdf("কল্যাণী", "জীবনানন্দ দাশ", String.valueOf(R.drawable.kalyani_jibananda_das)));
        pdfList.add(getPdf("এসেছ তুমি রচিত হতে", "কোয়েল তালুকদার", String.valueOf(R.drawable.esecho_tumi_rachito_hote)));
        pdfList.add(getPdf("কল্যাণী", "জীবনানন্দ দাশ", String.valueOf(R.drawable.kalyani_jibananda_das)));
        pdfList.add(getPdf("এসেছ তুমি রচিত হতে", "কোয়েল তালুকদার", String.valueOf(R.drawable.esecho_tumi_rachito_hote)));
        pdfList.add(getPdf("কল্যাণী", "জীবনানন্দ দাশ", String.valueOf(R.drawable.kalyani_jibananda_das)));
        pdfList.add(getPdf("এসেছ তুমি রচিত হতে", "কোয়েল তালুকদার", String.valueOf(R.drawable.esecho_tumi_rachito_hote)));
        pdfList.add(getPdf("কল্যাণী", "জীবনানন্দ দাশ", String.valueOf(R.drawable.kalyani_jibananda_das)));
        pdfList.add(getPdf("এসেছ তুমি রচিত হতে", "কোয়েল তালুকদার", String.valueOf(R.drawable.esecho_tumi_rachito_hote)));

    }

    private void RecyclerCustomize(RecyclerView recyclerView, RecyclerView.Adapter adapter) {

    }

    private void RecyclerDefiner() {
        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        recyclerViewForTrendingSec.setHasFixedSize(true);
        recyclerViewForNewSec.setHasFixedSize(true);
        recyclerViewForCat1.setHasFixedSize(true);
        recyclerViewForCat2.setHasFixedSize(true);

        // specify the view adapter
        trending_sec_adapter = new MainLayAdapter(this, pdfList);
        new_sec_adapter = new SecoundLayAdapter(this, pdfList);
        cat1_sec_adapter = new ThirdLayAdapter(this, pdfList);
        cat2_sec_adapter = new FourthLayAdapter(this, pdfList);

        recyclerViewForTrendingSec.setAdapter(trending_sec_adapter);
        recyclerViewForNewSec.setAdapter(new_sec_adapter);
        recyclerViewForCat1.setAdapter(cat1_sec_adapter);
        recyclerViewForCat2.setAdapter(cat2_sec_adapter);

        // specify the view layout manager
        layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        layoutManager2 = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        layoutManager3 = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        layoutManager4 = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        recyclerViewForTrendingSec.setLayoutManager(layoutManager);
        recyclerViewForNewSec.setLayoutManager(layoutManager2);
        recyclerViewForCat1.setLayoutManager(layoutManager3);
        recyclerViewForCat2.setLayoutManager(layoutManager4);


        // Start automatic sliding
        RecViewAutoScroll(recyclerViewForTrendingSec, trending_sec_adapter,3000);
//        autoScroll(recyclerViewForNewSec, new_sec_adapter,4000);
//        autoScroll(recyclerViewForCat1, cat1_sec_adapter,5000);
//        autoScroll(recyclerViewForCat2, cat2_sec_adapter,6000);
        ScrollViewCustomize();


        // Customize RecyclerView
        RecyclerCustomize(recyclerViewForTrendingSec, trending_sec_adapter);
        RecyclerCustomize(recyclerViewForNewSec, new_sec_adapter);
        RecyclerCustomize(recyclerViewForCat1, cat1_sec_adapter);
        RecyclerCustomize(recyclerViewForCat2, cat2_sec_adapter);


    }//RecyclerDefiner()

    private void RecViewAutoScroll(RecyclerView recyclerView, RecyclerView.Adapter adapter, int duration) {

        PagerSnapHelper snapHelper = new PagerSnapHelper();
        snapHelper.attachToRecyclerView(recyclerView);
        final Handler handler = new Handler();
        final Runnable runnable = new Runnable() {
            @Override
            public void run() {
                if (currentPosition == adapter.getItemCount() - 1) {
                    currentPosition = 0;
                } else {
                    currentPosition++;
                }
                recyclerView.smoothScrollToPosition(currentPosition);
                handler.postDelayed(this, duration); // Adjust the delay as needed
                Log.d("currentPosition", "getItemCount: " + adapter.getItemCount() + " currentPosition: " + currentPosition);
            }
        };

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    // If the RecyclerView is not scrolling, resume auto-scrolling
                    handler.postDelayed(runnable, duration); // Adjust the delay as needed
                } else {
                    // If the RecyclerView is scrolling, remove callbacks to stop auto-scrolling
                    handler.removeCallbacks(runnable);
                }
            }
        });

        // Start the initial auto-scrolling task
        handler.postDelayed(runnable, duration); // Adjust the delay as needed



    }//autoScroll


    private void ScrollViewCustomize(){
        parentScrollView.isSmoothScrollingEnabled();
        parentScrollView.computeScroll();
        parentScrollView.setOnScrollChangeListener(new View.OnScrollChangeListener() {
            @Override
            public void onScrollChange(View v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                if (oldScrollY >= 150 && oldScrollY <= 250){
                    canvasBar.setImageDrawable(getDrawable(R.color.red));
                }else if (oldScrollY >= 250 && oldScrollY <= 350){
                    canvasBar.setImageDrawable(getDrawable(R.color.deep_blue));
                }else if (oldScrollY >= 350 && oldScrollY <= 450){
                    canvasBar.setImageDrawable(getDrawable(R.color.stela));
                }else if (oldScrollY >= 450 && oldScrollY <= 550){
                    canvasBar.setImageDrawable(getDrawable(R.color.yellow));
                }else if (oldScrollY >= 550 && oldScrollY <= 650){
                    canvasBar.setImageDrawable(getDrawable(R.color.stela));
                }else {
                    canvasBar.setImageDrawable(getDrawable(R.color.shadow));
                }
                Log.d("ForScrolling", "onScrollChange: "+scrollX+ " "+scrollX+ " "+ oldScrollX+" "+oldScrollY);
            }
        });
    }




}//Main Class