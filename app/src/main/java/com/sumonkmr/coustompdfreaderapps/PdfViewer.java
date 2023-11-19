package com.sumonkmr.coustompdfreaderapps;

import static com.sumonkmr.coustompdfreaderapps.MainActivity.PdfFileName;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.animation.ObjectAnimator;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;
import com.github.barteksc.pdfviewer.PDFView;
import com.github.barteksc.pdfviewer.listener.OnLoadCompleteListener;
import com.github.barteksc.pdfviewer.listener.OnPageChangeListener;
import com.github.barteksc.pdfviewer.listener.OnPageScrollListener;

public class PdfViewer extends AppCompatActivity {

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
        Hookups();

        pageTV.setVisibility(View.INVISIBLE);
        pdfView.setVisibility(View.INVISIBLE);
        loading.setVisibility(View.VISIBLE);

        pdfView.fromAsset(PdfFileName)
                .pageSnap(true)
                .swipeHorizontal(true)
                .fitEachPage(true)
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

        fullscreen_button.setOnClickListener(v -> {
            if (fullscreen_button.getTag().toString().contains("open")){
                header_lay.setVisibility(View.GONE);
                fullscreen_button.setImageDrawable(getDrawable(R.drawable.full_screen_icon_close));
                fullscreen_button.setTag("close");
            }else if (fullscreen_button.getTag().toString().contains("close")){
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
    }
}