package com.sumonkmr.the_reader;

import androidx.appcompat.app.AppCompatActivity;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.toolbox.Volley;
import com.github.barteksc.pdfviewer.PDFView;

import java.io.File;
import java.io.FileOutputStream;

public class PdfViewer extends AppCompatActivity {

    Context context;
    PDFView pdfView;
    LinearLayout pageTV;
    RelativeLayout header_lay;
    TextView currentPageTV, totalPageTV;
    LottieAnimationView loading;
    ImageView fullscreen_button;
    ProgressBar progressBar;
    private int totalPages;
    private int currentPage = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pdf_viewer);
        context = PdfViewer.this;
        Hookups();
        pageTV.setVisibility(View.INVISIBLE);
        pdfView.setVisibility(View.INVISIBLE);
        Intent intent = getIntent();
        String filePath = intent.getStringExtra("pdfFile");
        String fileName = intent.getStringExtra("fileName");
        int position = intent.getIntExtra("position", 0);

        InputStreamVolleyRequest request = new InputStreamVolleyRequest(
                Request.Method.GET,
                filePath,
                response -> {
                    try {
                        assert fileName != null;
                        File file = new File(getCacheDir(), fileName);
                        FileOutputStream outputStream = new FileOutputStream(file);
                        outputStream.write(response);
                        outputStream.close();

                        if (file.exists()) {
                            pdfView.fromFile(file)
                                    .pageSnap(true)
                                    .onPageChange((page, pageCount) -> {
                                        currentPage = page;
                                        updateProgressBar();
                                    })
                                    .onLoad(nbPages -> {
                                        totalPages = nbPages;
                                        updateProgressBar();
                                        pdfView.setVisibility(View.VISIBLE);
                                        pageTV.setVisibility(View.VISIBLE);
                                        fullscreen_button.setVisibility(View.VISIBLE);
                                        loading.setVisibility(View.GONE);
                                    })
                                    .enableSwipe(true)
                                    .pageFling(true)
                                    .load();
                        } else {
                            Log.d("File", "File not found: " + file);
                            Toast.makeText(context, "File download failed. Try again.", Toast.LENGTH_SHORT).show();
                        }

                        Log.d("Download", "File downloaded: " + file.getAbsolutePath());

                    } catch (Exception e) {
                        Log.e("Download", "Saving error: ", e);
                        Toast.makeText(context, "Failed to save file: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    // Log status code if available
                    if (error.networkResponse != null) {
                        Log.e("VolleyError", "Status Code: " + error.networkResponse.statusCode);
                    }else if (error instanceof ServerError) {
//                        Toast.makeText(context, "Server error (500). Please try again later.", Toast.LENGTH_LONG).show();
                        Log.d("Server", "onCreate: "+"Server error");
                    } else if (error instanceof TimeoutError) {
                        Toast.makeText(context, "Request timed out. Please check your internet connection.", Toast.LENGTH_LONG).show();
                    } else if (error instanceof NoConnectionError) {
                        Toast.makeText(context, "No internet connection.", Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(context, "Unexpected error occurred.", Toast.LENGTH_LONG).show();
                    }

                    loading.setVisibility(View.GONE);
                    pdfView.setVisibility(View.GONE);
                    fullscreen_button.setVisibility(View.GONE);
                    pageTV.setVisibility(View.GONE);
                }
        );

// Retry Policy (optional but recommended for unstable servers)
        request.setRetryPolicy(new DefaultRetryPolicy(
                8000, // Timeout in ms (8 seconds)
                2,    // Retry twice
                1.0f  // Backoff multiplier
        ));

// Add request to queue
        Volley.newRequestQueue(context).add(request);


// Add request to queue
        Volley.newRequestQueue(context).add(request);


        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(request);

        fullscreen_button.setOnClickListener(v -> {
            if (fullscreen_button.getTag().toString().contains("open")) {
                header_lay.setVisibility(View.GONE);
                fullscreen_button.setImageDrawable(getDrawable(R.drawable.full_screen_icon_close));
                fullscreen_button.setTag("close");
            } else if (fullscreen_button.getTag().toString().contains("close")) {
                header_lay.setVisibility(View.VISIBLE);
                fullscreen_button.setImageDrawable(getDrawable(R.drawable.full_screen_icon_open));
                fullscreen_button.setTag("open");
            }
        });

    }

    private void Hookups() {
        pdfView = findViewById(R.id.pdfView);
        pageTV = findViewById(R.id.pageTV);
        header_lay = findViewById(R.id.header_lay);
        fullscreen_button = findViewById(R.id.fullscreen_button);
        loading = findViewById(R.id.loading);
        currentPageTV = findViewById(R.id.currentPageTV);
        totalPageTV = findViewById(R.id.totalPageTV);
        progressBar = findViewById(R.id.progressBar);
    }

    private void updateProgressBar() {
        // Calculate the progress percentage based on the current page and total pages
        currentPageTV.setText(String.valueOf(currentPage));
        totalPageTV.setText(String.valueOf(totalPages));
        int progress = (int) (((float) (currentPage + 1) / totalPages) * 100);
        ObjectAnimator progressAnimator = ObjectAnimator.ofInt(progressBar, "progress", progress);
        progressAnimator.setDuration(300); // Set the duration of the animation in milliseconds
        progressAnimator.start();

        // Update the progress bar
        progressBar.setProgress(progress);
        Log.d("TAG", "updateProgressBar: " + progress + " " + totalPages);

    }
}