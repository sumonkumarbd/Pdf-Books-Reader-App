package com.sumonkmr.the_reader;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.PagerSnapHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.TimeoutError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.navigation.NavigationView;
import com.sumonkmr.the_reader.adapters.BangladeshiCatAdapter;
import com.sumonkmr.the_reader.adapters.InternationalCat;
import com.sumonkmr.the_reader.adapters.NewCategoryAdapter;
import com.sumonkmr.the_reader.adapters.PopularCatAdapter;
import com.sumonkmr.the_reader.models.PdfModel;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    public int maxScroll;
    ImageView dashboard_logo;
    ScrollView parentScrollView;
    TextView trendingTag;
    private RecyclerView recViewPopular, recyclerViewForNewSec, recyclerViewForCat1, recyclerViewForCat2;
    private int currentPosition = 0;
    private boolean isAutoScrollRunning = false;
    private ProgressBar progressBarTrending, progressBarNew, progressBarDesi, progressBarInt, canvasBar;
    private boolean isProgressBarVisible = false;
    DrawerLayout drawer;
    private Handler autoScrollHandler = new Handler();

    private SwipeRefreshLayout swipeRefreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);

        // Set up ActionBarDrawerToggle
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        navigationView.setNavigationItemSelectedListener(this);

        // Select Home by default if this is the first time loading
        if (savedInstanceState == null) {
            navigationView.setCheckedItem(R.id.nav_home);
        }

        // Call Functions Here
        HookUps(); // For HookUps xml with java
          apiManager();
        // Manual Progressbar for recyclerView
        ManualProgressBars(recViewPopular, progressBarTrending);
        ManualProgressBars(recyclerViewForCat1, progressBarDesi);
        ManualProgressBars(recyclerViewForCat2, progressBarInt);
        ScrollViewCustomize();
        setupBackPress(); // OnBackPress


        // Configure SwipeRefreshLayout
        swipeRefreshLayout.setColorSchemeResources(
                R.color.yellow,
                R.color.red,
                R.color.deep_blue
        );

        // Set up refreshing listener
//        swipeRefreshLayout.setOnRefreshListener(this::dataBase);
        swipeRefreshLayout.setOnRefreshListener(this::apiManager);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Clean up all handlers to prevent memory leaks
        if (autoScrollHandler != null) {
            autoScrollHandler.removeCallbacksAndMessages(null);
        }
    }

    // Custom Functions
    private void HookUps() {
        parentScrollView = findViewById(R.id.parentScrollView);
        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);
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

                // Prevent division by zero
                if (scrollRange <= 0) {
                    progressBar.setProgress(0);
                    return;
                }

                int progress = (int) (100f * scrolled / scrollRange);
                progressBar.setProgress(progress);
                SetProgressFullColor(progressBar, progress);
            }
        });
    }

    private void RecViewAutoScroll(RecyclerView recyclerView, RecyclerView.Adapter adapter, int duration, ProgressBar progressBar) {
        // 🚨 Fix: Remove any existing fling listener before attaching SnapHelper
        if (recyclerView.getOnFlingListener() != null) {
            recyclerView.setOnFlingListener(null);
        }

        PagerSnapHelper snapHelper = new PagerSnapHelper();
        snapHelper.attachToRecyclerView(recyclerView);

        final Runnable runnable = new Runnable() {
            @Override
            public void run() {
                if (currentPosition >= adapter.getItemCount() - 1) {
                    currentPosition = 0;
                } else {
                    currentPosition++;
                }
                recyclerView.smoothScrollToPosition(currentPosition);
                autoScrollHandler.postDelayed(this, duration);
            }
        };

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    if (!isAutoScrollRunning) {
                        isAutoScrollRunning = true;
                        autoScrollHandler.postDelayed(runnable, duration);
                    }
                } else {
                    autoScrollHandler.removeCallbacks(runnable);
                    isAutoScrollRunning = false;
                }
            }
        });

        // Start scrolling
        if (!isAutoScrollRunning) {
            isAutoScrollRunning = true;
            autoScrollHandler.postDelayed(runnable, duration);
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

                // Prevent division by zero
                if (scrollRange <= 0) {
                    progressBar.setProgress(0);
                    return;
                }

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
            if (maxScrollAmount <= 0) {
                return; // Prevent division by zero or negative values
            }
            canvasBar.setMax(maxScrollAmount);
            canvasBar.setProgress(scrollY);

            if (scrollY == maxScrollAmount) {
                getWindow().setStatusBarColor(getColor(R.color.red));
            } else {
                getWindow().setStatusBarColor(getColor(R.color.yellow));
            }
        });
    }


//    #################################################################################
    private HashMap<String, String> getEBook(
            int id,
            String title,
            String author,
            String category,
            String description,
            String pdfFile,
            String coverImage,
            String publisher,
            String language,
            String publicationDate,
            String uploadDate,
            boolean featured
    ) {
        HashMap<String, String> eBook = new HashMap<>();
        eBook.put("id", String.valueOf(id));
        eBook.put("title", title);
        eBook.put("author", author);
        eBook.put("language", language);
        eBook.put("category", category);
        eBook.put("description", description);
        eBook.put("pdf_file", pdfFile);
        eBook.put("cover_image", coverImage);
        eBook.put("upload_date", uploadDate);
        eBook.put("publisher", publisher);
        eBook.put("publication_date", publicationDate);
        eBook.put("featured", String.valueOf(featured));
        return eBook;
    }




        private List<HashMap<String, String>> setEBook(
                List<HashMap<String, String>> eBookLibrary,
                int id,
                String title,
                String author,
                String category,
                String description,
                String pdfFile,
                String coverImage,
                String publisher,
                String language,
                String publicationDate,
                String uploadDate,
                boolean featured
        ) {
            eBookLibrary.add(getEBook(
                    id,
                    title,
                    author,
                    category,
                    description,
                    pdfFile,
                    coverImage,
                    publisher,
                    language,
                    publicationDate,
                    uploadDate,
                    featured
            ));
            return eBookLibrary;
        }

    public void apiManager() {
    String url = "https://the-reader-ebook.vercel.app/api/books/";

    JsonArrayRequest request = new JsonArrayRequest(
            Request.Method.GET, url, null,
            response -> {

                try {
                    // Separate lists for different categories
                    List<PdfModel> popularBooks = new ArrayList<>();
                    List<PdfModel> newBooks = new ArrayList<>();
                    List<PdfModel> bangladeshiBooks = new ArrayList<>();
                    List<PdfModel> internationalBooks = new ArrayList<>();

                    for (int i = 0; i < response.length(); i++) {
                        JSONObject jsonObject = response.getJSONObject(i);

                        PdfModel pdf = new PdfModel(
                                jsonObject.getInt("id"),
                                jsonObject.getString("title"),
                                jsonObject.getString("slug"),
                                jsonObject.getString("author"),
                                jsonObject.getString("category"),
                                jsonObject.getString("language"),
                                jsonObject.getString("publisher"),
                                jsonObject.getString("description"),
                                jsonObject.getString("pdf_url"),
                                jsonObject.getString("cover_url"),
                                jsonObject.getString("upload_date"),
                                jsonObject.getString("publication_date"),
                                jsonObject.getBoolean("featured")
                        );


                        // Categorize popular (based on featured flag)
                        if (pdf.isFeatured()) {
                            popularBooks.add(pdf);
                        }

                        // Categorize new (uploaded in last 30 days)
                        try {
                            SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.US);
                            dateFormat.setLenient(false);

                            Date uploadDate = dateFormat.parse(pdf.getUploadDate());
                            Date currentDate = new Date();
                            assert uploadDate != null;

                            // Normalize times to avoid time differences affecting results
                            Calendar cal = Calendar.getInstance();
                            cal.setTime(uploadDate);
                            cal.set(Calendar.HOUR_OF_DAY, 0);
                            cal.set(Calendar.MINUTE, 0);
                            cal.set(Calendar.SECOND, 0);
                            cal.set(Calendar.MILLISECOND, 0);
                            uploadDate = cal.getTime();

                            cal.setTime(currentDate);
                            cal.set(Calendar.HOUR_OF_DAY, 0);
                            cal.set(Calendar.MINUTE, 0);
                            cal.set(Calendar.SECOND, 0);
                            cal.set(Calendar.MILLISECOND, 0);
                            currentDate = cal.getTime();

                            long diffInMillis = currentDate.getTime() - uploadDate.getTime();
                            long diffInDays = TimeUnit.MILLISECONDS.toDays(diffInMillis);

                            if (diffInDays <= 30) {
                                newBooks.add(pdf);
                            }
                        } catch (ParseException e) {
                            Log.e("DATE_ERROR", "Date parsing error: " + e.getMessage());
                            newBooks.add(pdf); // Fail-safe
                        }


                        // Categorize region
                        String language = pdf.getLanguage().toLowerCase();
                        if (language.contains("bangla") || language.contains("bangladesh")) {
                            bangladeshiBooks.add(pdf);
                        } else {
                            internationalBooks.add(pdf);
                        }
                    }

                    // Setup RecyclerView: Popular Books
                    recViewPopular.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
                    recViewPopular.setHasFixedSize(true);
                    PopularCatAdapter popularCatAdapter = new PopularCatAdapter(MainActivity.this, popularBooks);
                    recViewPopular.setAdapter(popularCatAdapter);

                    // Setup RecyclerView: New Books
                    recyclerViewForNewSec.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
                    recyclerViewForNewSec.setHasFixedSize(true);
                    NewCategoryAdapter newCategoryAdapter = new NewCategoryAdapter(MainActivity.this, newBooks);
                    recyclerViewForNewSec.setAdapter(newCategoryAdapter);
                    RecViewAutoScroll(recyclerViewForNewSec, newCategoryAdapter, 3000, progressBarNew);

                    // Setup RecyclerView: Bangladeshi Books
                    recyclerViewForCat1.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
                    recyclerViewForCat1.setHasFixedSize(true);
                    recyclerViewForCat1.setAdapter(new BangladeshiCatAdapter(MainActivity.this, bangladeshiBooks));

                    // Setup RecyclerView: International Books
                    recyclerViewForCat2.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
                    recyclerViewForCat2.setHasFixedSize(true);
                    recyclerViewForCat2.setAdapter(new InternationalCat(MainActivity.this, internationalBooks));

                } catch (JSONException e) {
                    Log.e("PDF_ERROR", "JSON parsing error: " + e.getMessage());
                } finally {
                    swipeRefreshLayout.setRefreshing(false); // ✅ Stop refreshing
                }
            },
            error -> {
                swipeRefreshLayout.setRefreshing(false);
                if (error instanceof TimeoutError) {
                    Toast.makeText(MainActivity.this, "Server is taking too long to respond. Please try again.", Toast.LENGTH_LONG).show();
                } else if (error instanceof NoConnectionError) {
                    Toast.makeText(MainActivity.this, "No internet connection.", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(MainActivity.this, "Something went wrong.", Toast.LENGTH_LONG).show();
                    Log.d("apiDjango_error", "Error: " + error.getMessage());
                }
            }
    );

// Retry policy for handling slow network
    int timeoutMs = 10000; // 10 seconds
    request.setRetryPolicy(new DefaultRetryPolicy(
            timeoutMs,
            DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
            DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

// Add the request to the Volley queue
    Volley.newRequestQueue(this).add(request);

}
//    #################################################################################

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here
        int id = item.getItemId();

        if (id == R.id.nav_home) {
            // Handle the home action
            // For example: loadHomeFragment();
        } else if (id == R.id.nav_my_documents) {
            // Handle the my documents action
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://the-reader-ebook.vercel.app/"));
            startActivity(browserIntent);

        } else if (id == R.id.nav_privacy) {
            // Handle the about action
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://the-reader-ebook.vercel.app/privacy"));
            startActivity(browserIntent);
            // For example: showAboutDialog();
        }

        // Close the drawer
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void setupBackPress() {
        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (drawer.isDrawerOpen(GravityCompat.START)) {
                    drawer.closeDrawer(GravityCompat.START);
                } else {
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
            }
        };

        // Add the callback to the OnBackPressedDispatcher
        getOnBackPressedDispatcher().addCallback(this, callback);
    }
}