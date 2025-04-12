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
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.PagerSnapHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.sumonkmr.coustompdfreaderapps.adapters.BangladeshiCatAdapter;
import com.sumonkmr.coustompdfreaderapps.adapters.InternationalCat;
import com.sumonkmr.coustompdfreaderapps.adapters.NewCategoryAdapter;
import com.sumonkmr.coustompdfreaderapps.adapters.PopularCatAdapter;
import com.sumonkmr.coustompdfreaderapps.models.PdfModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {

    public static String PdfFileName;
    public int maxScroll;
    HashMap<String, String> pdfTemp;
    List<HashMap<String, String>> popularPdf, newPdf, bangladeshiPdf, interNationalPdf;
    ImageView dashboard_logo;
    ScrollView parentScrollView;
    TextView trendingTag;
    private RecyclerView recViewPopular, recyclerViewForNewSec, recyclerViewForCat1, recyclerViewForCat2;
    private int currentPosition = 0;
    private boolean isAutoScrollRunning = false;
    private ProgressBar progressBarTrending, progressBarNew, progressBarDesi, progressBarInt, canvasBar;
    private boolean isProgressBarVisible = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//       ***Call Functions Here****
        HookUps();//For HookUps xml with java
        dataBase();
        // Manual Progressbar for recyclerView
        ManualProgressBars(recViewPopular, progressBarTrending);
        ManualProgressBars(recyclerViewForCat1, progressBarDesi);
        ManualProgressBars(recyclerViewForCat2, progressBarInt);
        ScrollViewCustomize();
        BackPress(); //OnBackPress.

    }//onCreate Finished.

    //    (((Custom Functions)))
    private void HookUps() {
        parentScrollView = findViewById(R.id.parentScrollView);
        canvasBar = findViewById(R.id.canvasBar);
        dashboard_logo = findViewById(R.id.dashboard_logo);
        recViewPopular = findViewById(R.id.recyclerView);
        recyclerViewForNewSec = findViewById(R.id.recyclerView2);
        recyclerViewForCat1 = findViewById(R.id.recyclerView3);
        recyclerViewForCat2 = findViewById(R.id.recyclerView4);
        trendingTag = findViewById(R.id.trendingTag);
        progressBarTrending = findViewById(R.id.progressBarTrending);
        progressBarNew = findViewById(R.id.progressBarNew);
        progressBarDesi = findViewById(R.id.progressBarDesi);
        progressBarInt = findViewById(R.id.progressBarInt);
    }


    private void AutoProgressBar(RecyclerView recyclerView, ProgressBar progressBar) {
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                int scrolled = recyclerView.computeHorizontalScrollOffset();
                int scrollRange = recyclerView.computeHorizontalScrollRange() - recyclerView.computeHorizontalScrollExtent();

                int progress = scrollRange == 0 ? 0 : (int) (100f * scrolled / scrollRange);

                progressBar.setProgress(progress);
                SetProgressFullColor(progressBar, progress); // Your custom method
            }
        });
    }

    private void RecViewAutoScroll(RecyclerView recyclerView, RecyclerView.Adapter adapter, int duration, ProgressBar progressBar) {
        PagerSnapHelper snapHelper = new PagerSnapHelper();
        snapHelper.attachToRecyclerView(recyclerView);

        final Handler handler = new Handler();
        final Runnable runnable = new Runnable() {
            @Override
            public void run() {
                if (currentPosition >= adapter.getItemCount() - 1) {
                    currentPosition = 0;
                } else {
                    currentPosition++;
                }
                recyclerView.smoothScrollToPosition(currentPosition);
                handler.postDelayed(this, duration);
            }
        };

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    if (!isAutoScrollRunning) {
                        isAutoScrollRunning = true;
                        handler.postDelayed(runnable, duration);
                    }
                } else {
                    handler.removeCallbacks(runnable);
                    isAutoScrollRunning = false;
                }
            }
        });

        // Start scrolling
        if (!isAutoScrollRunning) {
            isAutoScrollRunning = true;
            handler.postDelayed(runnable, duration);
        }

        AutoProgressBar(recyclerView, progressBar);
    }

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

    private HashMap<String, String> getPdf(int id, String title, String author, String fileName, String thumbnail, String category, String description, int downloadCount, String uploadDate) {
        HashMap<String, String> pdfTemp = new HashMap<>();
        pdfTemp.put("id", String.valueOf(id));
        pdfTemp.put("title", title);
        pdfTemp.put("author", author);
        pdfTemp.put("category", category);
        pdfTemp.put("description", description);
        pdfTemp.put("download_count", String.valueOf(downloadCount));
        pdfTemp.put("file_name", fileName);
        pdfTemp.put("thumbnail", thumbnail);
        pdfTemp.put("upload_date", uploadDate);

        return pdfTemp;
    }

    public List<HashMap<String, String>> setPdf(List<HashMap<String, String>> pdfLibrary, int id, String title, String author, String fileName, String thumbnail, String category, String description, int downloadCount, String uploadDate) {
        pdfLibrary.add(getPdf(
                id,
                title.replace("_", " ").toUpperCase(),
                author.replace("_", " ").toUpperCase(),
                fileName,
                thumbnail,
                category,
                description,
                downloadCount,
                uploadDate
        ));
        return pdfLibrary;
    }


    public void dataBase() {
        String url = "https://flask-book-api-the-reader.onrender.com/api/pdfs";

        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.GET, url, null,
                response -> {
                    try {
                        JSONArray pdfsArray = response.getJSONArray("pdfs");

                        // Create separate lists for different categories
                        List<PdfModel> popularBooks = new ArrayList<>();
                        List<PdfModel> newBooks = new ArrayList<>();
                        List<PdfModel> bangladeshiBooks = new ArrayList<>();
                        List<PdfModel> internationalBooks = new ArrayList<>();

                        // Process all PDFs and categorize them
                        for (int i = 0; i < pdfsArray.length(); i++) {
                            JSONObject pdfObject = pdfsArray.getJSONObject(i);

                            PdfModel pdf = new PdfModel(
                                    pdfObject.getInt("id"),
                                    pdfObject.getString("title"),
                                    pdfObject.getString("author"),
                                    pdfObject.getString("category"),
                                    pdfObject.getString("description"),
                                    pdfObject.getInt("download_count"),
                                    pdfObject.getString("file_name"),
                                    pdfObject.getString("thumbnail"),
                                    pdfObject.getString("upload_date")
                            );

                            // Categorize based on your criteria
                            String category = pdf.getCategory().toLowerCase();

                            // Add to popular books if download count is high (adjust threshold as needed)
                            if (pdf.getDownloadCount() > 20) {
                                popularBooks.add(pdf);
                            }

                            // Add to new books based on upload date (last 30 days)
                            try {
                                // Parse the upload date string (assuming format like "YYYY-MM-DD" or similar)
                                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
                                Date uploadDate = dateFormat.parse(pdf.getUploadDate());

                                // Get current date
                                Date currentDate = new Date();

                                // Calculate difference in milliseconds
                                long diffInMillis = currentDate.getTime() - uploadDate.getTime();

                                // Convert to days
                                long diffInDays = TimeUnit.MILLISECONDS.toDays(diffInMillis);

                                // If book was uploaded within the last 30 days, add to new books
                                if (diffInDays <= 30) {
                                    newBooks.add(pdf);
                                }
                            } catch (ParseException e) {
                                Log.e("DATE_ERROR", "Date parsing error: " + e.getMessage());
                                // If there's an error parsing the date, we can still add the book
                                // to make sure we don't miss content due to formatting issues
                                newBooks.add(pdf);
                            }

                            // Categorize by region
                            if (category.contains("bangladesh") ||
                                    category.contains("bengali") ||
                                    category.contains("bangla")) {
                                bangladeshiBooks.add(pdf);
                            } else {
                                internationalBooks.add(pdf);
                            }
                        }

                        // Set up RecyclerView for popular books
                        RecyclerView recyclerViewPopular = findViewById(R.id.recyclerView);
                        recyclerViewPopular.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
                        recyclerViewPopular.setHasFixedSize(true);
                        PopularCatAdapter popularCatAdapter = new PopularCatAdapter(MainActivity.this, popularBooks);
                        recyclerViewPopular.setAdapter(popularCatAdapter);

                        // Set up RecyclerView for new books
                        RecyclerView recyclerViewNew = findViewById(R.id.recyclerView2);
                        recyclerViewNew.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
                        recyclerViewNew.setHasFixedSize(true);
                        NewCategoryAdapter newCategoryAdapter = new NewCategoryAdapter(MainActivity.this, newBooks);
                        recyclerViewNew.setAdapter(newCategoryAdapter);
                        // Start automatic sliding
                        RecViewAutoScroll(recyclerViewNew,newCategoryAdapter , 3000, progressBarNew);

                        // Set up RecyclerView for Bangladeshi books
                        RecyclerView recyclerViewBangladeshi = findViewById(R.id.recyclerView3);
                        recyclerViewBangladeshi.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
                        recyclerViewBangladeshi.setHasFixedSize(true);
                        recyclerViewBangladeshi.setAdapter(new BangladeshiCatAdapter(MainActivity.this, bangladeshiBooks));

                        // Set up RecyclerView for international books
                        RecyclerView recyclerViewInternational = findViewById(R.id.recyclerView4);
                        recyclerViewInternational.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
                        recyclerViewInternational.setHasFixedSize(true);
                        recyclerViewInternational.setAdapter(new InternationalCat(MainActivity.this, internationalBooks));


                    } catch (JSONException e) {
                        Log.e("PDF_ERROR", "JSON parsing error: " + e.getMessage());
                    }
                },
                error -> {
                    Log.e("PDF_ERROR", "Volley error: " + error.toString());
                }
        );

        // Add request to Volley queue
        Volley.newRequestQueue(this).add(request);
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