package com.sumonkmr.coustompdfreaderapps;

import static com.sumonkmr.coustompdfreaderapps.MainActivity.PdfFileName;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;
import com.github.barteksc.pdfviewer.PDFView;
import com.github.barteksc.pdfviewer.listener.OnLoadCompleteListener;
import com.github.barteksc.pdfviewer.listener.OnPageChangeListener;
import com.github.barteksc.pdfviewer.listener.OnPageScrollListener;

public class PdfViewer extends AppCompatActivity {

    PDFView pdfView;
    LinearLayout pageTV;
    TextView currentPageTV, totalPageTV;
    LottieAnimationView loading;
    ImageView canvasBar;
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
                    loading.setVisibility(View.GONE);
                })
                .enableSwipe(true)
                .pageFling(true)
                .load();

    }

    private void Hookups() {
        pdfView = findViewById(R.id.pdfView);
        pageTV = findViewById(R.id.pageTV);
        loading = findViewById(R.id.loading);
        canvasBar = findViewById(R.id.canvasBar);
        currentPageTV = findViewById(R.id.currentPageTV);
        totalPageTV = findViewById(R.id.totalPageTV);
        progressBar = findViewById(R.id.progressBar);
    }

    private void updateProgressBar() {
        // Calculate the progress percentage based on the current page and total pages
        currentPageTV.setText(String.valueOf(currentPage));
        totalPageTV.setText(String.valueOf(totalPages));
        int progress = (int) (((float) (currentPage + 1) / totalPages) * 100);

        // Update the progress bar
        progressBar.setProgress(progress);
    }
}