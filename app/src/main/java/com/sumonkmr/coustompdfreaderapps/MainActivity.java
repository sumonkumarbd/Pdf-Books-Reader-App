package com.sumonkmr.coustompdfreaderapps;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.PagerSnapHelper;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    Context context;
    HashMap<String, String> pdfTemp;
    List<HashMap<String, String>> pdfList;
    private RecyclerView recyclerView, recyclerView2, recyclerView3;
    private RecyclerView.Adapter adapter, adapter2, adapter3;
    private RecyclerView.LayoutManager layoutManager, layoutManager2, layoutManager3;
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
        AllPDFs();//All Pdf List
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
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView2 = findViewById(R.id.recyclerView2);
        recyclerView3 = findViewById(R.id.recyclerView3);
    }

    public void AllPDFs() {
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
        recyclerView.setHasFixedSize(true);
        recyclerView2.setHasFixedSize(true);
        recyclerView3.setHasFixedSize(true);

        // specify the view adapter
        adapter = new MyAdapter(this, pdfList);
        adapter2 = new MyAdapter(this, pdfList);
        adapter3 = new MyAdapter(this, pdfList);
        recyclerView.setAdapter(adapter);
        recyclerView2.setAdapter(adapter2);
        recyclerView3.setAdapter(adapter3);

        RecyclerCustomize(recyclerView, adapter);
        RecyclerCustomize(recyclerView2, adapter2);
        RecyclerCustomize(recyclerView3, adapter3);

        // specify the view layout manager
        layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        layoutManager2 = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        layoutManager3 = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView2.setLayoutManager(layoutManager2);
        recyclerView3.setLayoutManager(layoutManager3);

        PagerSnapHelper snapHelper = new PagerSnapHelper();
        snapHelper.attachToRecyclerView(recyclerView);


        // Start automatic sliding
            autoScroll();


        }//RecyclerDefiner()

        private void autoScroll () {
            final Handler handler = new Handler();
            final Runnable runnable = new Runnable() {
                @Override
                public void run() {
                    if (currentPosition  == adapter.getItemCount() - 1 ) {
                        currentPosition = 0;
                    }else {
                        currentPosition++;
                    }
                    recyclerView.smoothScrollToPosition(currentPosition);
                    handler.postDelayed(this, 1000); // Adjust the delay as needed
                    Log.d("currentPosition", "getItemCount: "+adapter.getItemCount()+ " currentPosition: "+ currentPosition);
                }
            };

            recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                    super.onScrollStateChanged(recyclerView, newState);
                    if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                        // If the RecyclerView is not scrolling, resume auto-scrolling
                        handler.postDelayed(runnable, 3000); // Adjust the delay as needed
                    } else {
                        // If the RecyclerView is scrolling, remove callbacks to stop auto-scrolling
                        handler.removeCallbacks(runnable);
                    }
                }
            });

            // Start the initial auto-scrolling task
            handler.postDelayed(runnable, 3000); // Adjust the delay as needed
        }


    }//Main Class