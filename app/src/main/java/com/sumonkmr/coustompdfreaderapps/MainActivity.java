package com.sumonkmr.coustompdfreaderapps;

import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.PagerSnapHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.sumonkmr.coustompdfreaderapps.adapters.FourthLayAdapter;
import com.sumonkmr.coustompdfreaderapps.adapters.MainLayAdapter;
import com.sumonkmr.coustompdfreaderapps.adapters.SecoundLayAdapter;
import com.sumonkmr.coustompdfreaderapps.adapters.ThirdLayAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    HashMap<String, String> pdfTemp;
    List<HashMap<String, String>> popularPdf, newPdf, bangladeshiPdf, interNationalPdf;
    ImageView dashboard_logo;
    ScrollView parentScrollView;
    private RecyclerView recyclerViewForTrendingSec, recyclerViewForNewSec, recyclerViewForCat1, recyclerViewForCat2;
    private int currentPosition = 0;
    public static String PdfFileName;
    TextView trendingTag;
    private ProgressBar progressBarTrending, progressBarNew, progressBarDesi, progressBarInt, canvasBar;
    public int maxScroll;
    private boolean isProgressBarVisible = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//       ***Call Functions Here****
        HookUps();//For HookUps xml with java
        PDFs();//All Pdf List
        RecyclerDefiner();//Settings of RecyclerView
        BackPress(); //OnBackPress.

    }//onCreate Finished.

    //    (((Custom Functions)))
    private void HookUps() {
        parentScrollView = findViewById(R.id.parentScrollView);
        canvasBar = findViewById(R.id.canvasBar);
        dashboard_logo = findViewById(R.id.dashboard_logo);
        recyclerViewForTrendingSec = findViewById(R.id.recyclerView);
        recyclerViewForNewSec = findViewById(R.id.recyclerView2);
        recyclerViewForCat1 = findViewById(R.id.recyclerView3);
        recyclerViewForCat2 = findViewById(R.id.recyclerView4);
        trendingTag = findViewById(R.id.trendingTag);
        progressBarTrending = findViewById(R.id.progressBarTrending);
        progressBarNew = findViewById(R.id.progressBarNew);
        progressBarDesi = findViewById(R.id.progressBarDesi);
        progressBarInt = findViewById(R.id.progressBarInt);
    }

    private void RecyclerDefiner() {
        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        recyclerViewForTrendingSec.setHasFixedSize(true);
        recyclerViewForNewSec.setHasFixedSize(true);
        recyclerViewForCat1.setHasFixedSize(true);
        recyclerViewForCat2.setHasFixedSize(true);

        // specify the view adapter
        RecyclerView.Adapter trending_sec_adapter = new MainLayAdapter(this, popularPdf);
        RecyclerView.Adapter new_sec_adapter = new SecoundLayAdapter(this, newPdf);
        RecyclerView.Adapter cat1_sec_adapter = new ThirdLayAdapter(this, bangladeshiPdf);
        RecyclerView.Adapter cat2_sec_adapter = new FourthLayAdapter(this, interNationalPdf);

        recyclerViewForTrendingSec.setAdapter(trending_sec_adapter);
        recyclerViewForNewSec.setAdapter(new_sec_adapter);
        recyclerViewForCat1.setAdapter(cat1_sec_adapter);
        recyclerViewForCat2.setAdapter(cat2_sec_adapter);

        // specify the view layout manager
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        RecyclerView.LayoutManager layoutManager2 = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        RecyclerView.LayoutManager layoutManager3 = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        RecyclerView.LayoutManager layoutManager4 = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        recyclerViewForTrendingSec.setLayoutManager(layoutManager);
        recyclerViewForNewSec.setLayoutManager(layoutManager2);
        recyclerViewForCat1.setLayoutManager(layoutManager3);
        recyclerViewForCat2.setLayoutManager(layoutManager4);


        // Start automatic sliding
        RecViewAutoScroll(recyclerViewForTrendingSec, trending_sec_adapter, 3000);

        // Manual Progressbar for recyclerView
        ManualProgressBars(recyclerViewForNewSec, progressBarNew);
        ManualProgressBars(recyclerViewForCat1, progressBarDesi);
        ManualProgressBars(recyclerViewForCat2, progressBarInt);


        ScrollViewCustomize();


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

//        AutoProgressBar Call
        AutoProgressBar(recyclerView, progressBarTrending);


    }//autoScroll

    private void ManualProgressBars(RecyclerView recyclerView, ProgressBar progressBar) {
        recyclerView.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                // Calculate the scroll progress
                int scrolled = recyclerView.computeHorizontalScrollOffset();
                int scrollRange = recyclerView.computeHorizontalScrollRange() - recyclerView.computeHorizontalScrollExtent();
                int progress = (int) (100 * (float) scrolled / scrollRange);
                // Calculate the maximum scroll range
                maxScroll = recyclerView.computeVerticalScrollRange() - recyclerView.computeVerticalScrollExtent();
                if (!isProgressBarVisible && progress > 0) {
                    progressBar.setVisibility(View.VISIBLE);
                    isProgressBarVisible = true;
                } else if (isProgressBarVisible && progress == 0) {
                    return;
                }
                progressBar.setProgress(progress);
                SetProgressFullColor(progressBar, progress);

            }
        });
    }

    private void AutoProgressBar(RecyclerView recyclerView, ProgressBar progressBar) {
        // Calculate the maximum scroll range
        maxScroll = recyclerView.computeVerticalScrollRange() - recyclerView.computeVerticalScrollExtent();
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                // Calculate the scroll progress
                int scrolled = recyclerView.computeHorizontalScrollOffset();
                int scrollRange = recyclerView.computeHorizontalScrollRange() - recyclerView.computeHorizontalScrollExtent();
                int progress = (int) (100 * (float) scrolled / scrollRange);

                // Update the ProgressBar
                progressBar.setProgress(progress);
                SetProgressFullColor(progressBar, progress);
            }

        });

    }

    private void SetProgressFullColor(ProgressBar progressBar, int progress) {
        int newProgressColor;
        if (progress == 100) {
            newProgressColor = ContextCompat.getColor(MainActivity.this, R.color.red);
        } else {
            newProgressColor = ContextCompat.getColor(MainActivity.this, R.color.yellow);
        }
        progressBar.setProgressDrawable(ContextCompat.getDrawable(MainActivity.this, R.drawable.title_progress_secendry));
        progressBar.getProgressDrawable().setColorFilter(newProgressColor, android.graphics.PorterDuff.Mode.SRC_IN);
    }


    private void ScrollViewCustomize() {
        parentScrollView.isSmoothScrollingEnabled();
        parentScrollView.computeScroll();
        parentScrollView.setOnScrollChangeListener((v, scrollX, scrollY, oldScrollX, oldScrollY) -> {
            int maxScrollAmount = parentScrollView.getChildAt(0).getHeight() - parentScrollView.getHeight();
            canvasBar.setMax(maxScrollAmount);
            canvasBar.setProgress(scrollY);

            if (scrollY == maxScrollAmount) {
                getWindow().setStatusBarColor(getColor(R.color.red));
            } else {
                getWindow().setStatusBarColor(getColor(R.color.yellow));
            }
        });
    }


    private HashMap<String, String> getPdf(String title, String author, String fileName, String cover) {

        pdfTemp = new HashMap<>();
        pdfTemp.put("title", title);
        pdfTemp.put("author", author);
        pdfTemp.put("cover", cover);
        pdfTemp.put("fileName", fileName);

        return pdfTemp;
    }

    public List setPdf(List pdfLibrary, String bookName, String authorName, String fileName, int coverPage) {
        pdfLibrary.add(getPdf(bookName.replace("_", " ").toUpperCase(), authorName.replace("_", " ").toUpperCase(), fileName, String.valueOf(coverPage)));
        return pdfLibrary;
    }

    public void PDFs() {
//      ==========================================
//      Trending Pdf List
//      ==========================================
        popularPdf = new ArrayList<>();
        setPdf(popularPdf, "মোবাইল অ্যাপে ক্যারিয়ার", "জুবায়ের হোসেন", "", R.drawable.mobile_app);
        setPdf(popularPdf, "spoken_english_by_munzereen_shahid", "munzereen_shahid", "spoken_english_munzereen_shahid.pdf", R.drawable.spoken_english_munzereen_shahid);
        setPdf(popularPdf, "the_time_machine By H. G. Wells", "H. G. Wells", "the_time_machine.pdf", R.drawable.the_time_machine);
        setPdf(popularPdf, "smart_carier_by_sohan_haydar", "sohan_haydar", "smart_carier_by_sohan_haydar.pdf", R.drawable.smart_carier_by_sohan_haydar);
        setPdf(popularPdf, "ghore_bose_ay_korun_by_joyita_benarjee", "joyita_benarjee", "ghore_bose_ay_korun_by_joyita_banerji.pdf", R.drawable.ghore_bose_ay_korun_by_joyita_banerji);
        setPdf(popularPdf, "sobar_jonne_vocabulary_by_munzereen_shahid", "munzereen_shahid", "sobar_jonne_vocabulary_by_munzereen_shahid.pdf", R.drawable.sobar_jonne_vocabulary_by_munzereen_shahid);
        setPdf(popularPdf, "rich_dad_poor_dad_Book by Robert Kiyosaki and Sharon Lechter", "Robert Kiyosaki and Sharon Lechter", "rich_dad_poor_dad.pdf", R.drawable.rich_dad_poor_dad);
        setPdf(popularPdf, "a_christmas_carol_by_charles_dickens_by_Charles Dickens", "Charles Dickens", "a_christmas_carol_by_charles_dickens.pdf", R.drawable.a_christmas_carol_by_charles_dickens);

//      ==========================================
//      New Pdf List
//      ==========================================
        newPdf = new ArrayList<>();
        setPdf(newPdf, "graphics_design_er_asol_fanda_by_asif_hossen", "asif_hossen", "graphics_design_er_asol_fanda_by_asif_hossen.pdf", R.drawable.graphics_design_er_asol_fanda_by_asif_hossen);
        setPdf(newPdf, "smart_carier_by_sohan_haydar", "sohan_haydar", "smart_carier_by_sohan_haydar.pdf", R.drawable.smart_carier_by_sohan_haydar);
        setPdf(newPdf, "mobile_photography_by_sadman_sakib", "sadman_sakib", "mobile_photography_by_sadman_sakib.pdf", R.drawable.mobile_photography_by_sadman_sakib);
        setPdf(newPdf, "ghore_bose_ay_korun_by_joyita_benarjee", "joyita_benarjee", "ghore_bose_ay_korun_by_joyita_banerji.pdf", R.drawable.ghore_bose_ay_korun_by_joyita_banerji);
        setPdf(newPdf, "spoken_english_by_munzereen_shahid", "munzereen_shahid", "spoken_english_munzereen_shahid.pdf", R.drawable.spoken_english_munzereen_shahid);
        setPdf(newPdf, "sobar_jonne_vocabulary_by_munzereen_shahid", "munzereen_shahid", "sobar_jonne_vocabulary_by_munzereen_shahid.pdf", R.drawable.sobar_jonne_vocabulary_by_munzereen_shahid);


//      ==========================================
//      Bangladeshi Pdf List
//      ==========================================
        bangladeshiPdf = new ArrayList<>();
        setPdf(bangladeshiPdf, "মোবাইল অ্যাপে ক্যারিয়ার", "জুবায়ের হোসেন", "", R.drawable.mobile_app);
        setPdf(bangladeshiPdf, "spoken_english_by_munzereen_shahid", "munzereen_shahid", "spoken_english_munzereen_shahid.pdf", R.drawable.spoken_english_munzereen_shahid);
        setPdf(bangladeshiPdf, "sobar_jonne_vocabulary_by_munzereen_shahid", "munzereen_shahid", "sobar_jonne_vocabulary_by_munzereen_shahid.pdf", R.drawable.sobar_jonne_vocabulary_by_munzereen_shahid);
        setPdf(bangladeshiPdf, "smart_carier_by_sohan_haydar", "sohan_haydar", "smart_carier_by_sohan_haydar.pdf", R.drawable.smart_carier_by_sohan_haydar);
        setPdf(bangladeshiPdf, "ghore_bose_ay_korun_by_joyita_benarjee", "joyita_benarjee", "ghore_bose_ay_korun_by_joyita_banerji.pdf", R.drawable.ghore_bose_ay_korun_by_joyita_banerji);
        setPdf(bangladeshiPdf, "mobile_photography_by_sadman_sakib", "sadman_sakib", "mobile_photography_by_sadman_sakib.pdf", R.drawable.mobile_photography_by_sadman_sakib);
        setPdf(bangladeshiPdf, "graphics_design_er_asol_fanda_by_asif_hossen", "asif_hossen", "graphics_design_er_asol_fanda_by_asif_hossen.pdf", R.drawable.graphics_design_er_asol_fanda_by_asif_hossen);
        setPdf(bangladeshiPdf, "কল্যাণী - জীবনানন্দ দাশ", "জীবনানন্দ দাশ", "", R.drawable.kalyani_jibananda_das);
        setPdf(bangladeshiPdf, "এসেছ তুমি রচিত হতে - কোয়েল তালুকদার", "কোয়েল তালুকদার", "", R.drawable.esecho_tumi_rachito_hote);


//      ==========================================
//      Bangladeshi Pdf List
//      ==========================================
        interNationalPdf = new ArrayList<>();
        setPdf(interNationalPdf, "rich_dad_poor_dad_Book by Robert Kiyosaki and Sharon Lechter", "Robert Kiyosaki and Sharon Lechter", "rich_dad_poor_dad.pdf", R.drawable.rich_dad_poor_dad);
        setPdf(interNationalPdf, "the_time_machine By H. G. Wells", "H. G. Wells", "the_time_machine.pdf", R.drawable.the_time_machine);
        setPdf(interNationalPdf, "the_oldest_word_by_Johnny_Firic", "Johnny Firic", "the_oldest_word.pdf", R.drawable.the_oldest_word);
        setPdf(interNationalPdf, "1001_motivational_quotes_for_success_by_Thomas J. Vilord", "Thomas J. Vilord", "one_thausen_one_motivational_quotes_for_success.pdf", R.drawable.one_thausen_one_motivational_quotes_for_success);
        setPdf(interNationalPdf, "20000_leagues_under_the_sea_by_Jules Verne", "Jules Verne", "twinty_thusen_leagues_under_the_sea.pdf", R.drawable.twinty_thusen_leagues_under_the_sea);
        setPdf(interNationalPdf, "a_christmas_carol_by_charles_dickens_by_Charles Dickens", "Charles Dickens", "a_christmas_carol_by_charles_dickens.pdf", R.drawable.a_christmas_carol_by_charles_dickens);
        setPdf(interNationalPdf, "a_connecticut_yankee_in_king_arthurs_court_by_mark_twain", "mark_twain", "a_connecticut_yankee_in_king_arthurs_court_by_mark_twain.pdf", R.drawable.a_connecticut_yankee_in_king_arthurs_court_by_mark_twain);
        setPdf(interNationalPdf, "a_tale_of_three_lions_by_henry_rider_haggard", "henry_rider_haggard", "a_tale_of_three_lions_by_henry_rider_haggard.pdf", R.drawable.a_tale_of_three_lions_by_henry_rider_haggard);

    }

    private void BackPress() {
        // Set up a callback to handle the back button press
        OnBackPressedCallback callback = new OnBackPressedCallback(true /* enabled by default */) {
            @Override
            public void handleOnBackPressed() {
                // Custom handling for the back button press
                // You can show a dialog or perform any custom action here
                new AlertDialog.Builder(MainActivity.this)
                        .setTitle("Exit")
                        .setMessage("Are you sure you want to exit?")
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // Close the application
                                finish();
                            }
                        })
                        .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // Dismiss the dialog
                                dialog.dismiss();
                            }
                        })
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();
            }

            // If you want to proceed with the default behavior (closing the activity), call the following:
            // MyComponentActivity.super.onBackPressed();
        };

        // Add the callback to the OnBackPressedDispatcher
        getOnBackPressedDispatcher().addCallback(this, callback);

        // ... rest of your activity initialization code
    }






}//Main Class