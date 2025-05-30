package com.sumonkmr.the_reader.adapters;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.pdf.PdfRenderer;
import android.os.Build;
import android.os.ParcelFileDescriptor;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.squareup.picasso.Picasso;
import com.sumonkmr.the_reader.R;
import com.sumonkmr.the_reader.models.PdfModel;
import com.sumonkmr.the_reader.PdfViewer;

import java.io.File;
import java.util.List;

public class PopularCatAdapter extends RecyclerView.Adapter<PopularCatAdapter.PdfViewHolder> {

    Context context;
    private final List<PdfModel> pdfList;

    public PopularCatAdapter(Context context, List<PdfModel> pdfList) {
        this.pdfList = pdfList;
        this.context = context;
    }

    @NonNull
    @Override
    public PdfViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.pdf_card, parent, false);  // `item_pdf` is the layout you provided.
        return new PdfViewHolder(view);
    }

    @Override
    public void onBindViewHolder(PdfViewHolder holder, int position) {
        PdfModel pdf = pdfList.get(position);
        holder.title.setText(pdf.getTitle());
        holder.author.setText(pdf.getAuthor());



        holder.cover.setOnClickListener(v -> {
            String pdfUrl = pdf.getPdfUrl();
            if (pdfUrl.isEmpty()){
                Toast.makeText(context, "Coming soon...", Toast.LENGTH_SHORT).show();
            }else {
                Intent intent = new Intent(context, PdfViewer.class);
                intent.putExtra("pdfFile", pdfUrl);
                intent.putExtra("fileName", pdf.getTitle());
                intent.putExtra("position", position);
                context.startActivity(intent);
            }
        });


        Picasso.get()
                .load(pdf.getCoverUrl())
                .placeholder(R.drawable.intro_cover_two)
                .error(R.drawable.intro_cover)
                .into(holder.cover);


    }

    @Override
    public int getItemCount() {
        return pdfList.size();

    }

    public static class PdfViewHolder extends RecyclerView.ViewHolder {
        ImageView cover;
        ImageView newTag;
        TextView title;
        TextView author;

        public PdfViewHolder(View itemView) {
            super(itemView);
            cover = itemView.findViewById(R.id.cover);
            newTag = itemView.findViewById(R.id.newTag);
            title = itemView.findViewById(R.id.titleName);
            author = itemView.findViewById(R.id.authorName);
        }
    }
}
