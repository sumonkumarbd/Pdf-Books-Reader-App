package com.sumonkmr.coustompdfreaderapps;

import static com.sumonkmr.coustompdfreaderapps.MainActivity.PdfFileName;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;

import com.airbnb.lottie.LottieAnimationView;
import com.github.barteksc.pdfviewer.PDFView;
import com.github.barteksc.pdfviewer.listener.OnLoadCompleteListener;

public class PdfViewer extends AppCompatActivity {

    PDFView pdfView;
    LottieAnimationView loading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pdf_viewer);
        pdfView = findViewById(R.id.pdfView);
        loading = findViewById(R.id.loading);
        pdfView.setVisibility(View.INVISIBLE);
        loading.setVisibility(View.VISIBLE);

        pdfView.fromAsset(PdfFileName)
                .pageSnap(true)
                .swipeHorizontal(true)
                .fitEachPage(true)
                .onLoad(nbPages -> {
                    pdfView.setVisibility(View.VISIBLE);
                    loading.setVisibility(View.GONE);
                })
                .load();
    }
}