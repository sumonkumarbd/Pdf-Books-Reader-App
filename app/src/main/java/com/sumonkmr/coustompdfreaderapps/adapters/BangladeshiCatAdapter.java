package com.sumonkmr.coustompdfreaderapps.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.sumonkmr.coustompdfreaderapps.PdfModel;
import com.sumonkmr.coustompdfreaderapps.PdfViewer;
import com.sumonkmr.coustompdfreaderapps.R;

import java.util.List;

public class BangladeshiCatAdapter extends RecyclerView.Adapter<BangladeshiCatAdapter.DesiCatViewHolder> {

    Context context;
    private List<PdfModel> pdfList;

    public BangladeshiCatAdapter(Context context,List<PdfModel> pdfList) {
        this.pdfList = pdfList;
        this.context = context;
    }

    @Override
    public DesiCatViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.pdf_card, parent, false);  // `item_pdf` is the layout you provided.
        return new DesiCatViewHolder(view);
    }

    @Override
    public void onBindViewHolder(DesiCatViewHolder holder, int position) {
        PdfModel pdf = pdfList.get(position);
        holder.title.setText(pdf.getTitle());
        holder.author.setText(pdf.getAuthor());

        // Here you would load the image into the ImageView (e.g., using Glide or Picasso)
        String thumbnailUrl = "https://flask-book-api-the-reader.onrender.com/api/thumbnail/"+pdf.getId();
        Glide.with(holder.cover.getContext())
                .load(thumbnailUrl)  // Load image from URL
                .into(holder.cover);

        String pdfFile = "https://flask-book-api-the-reader.onrender.com/api/download/"+pdf.getId();
        holder.cover.setOnClickListener(v -> {
            String fileName = pdf.getFileName();
            if (fileName.isEmpty()){
                Toast.makeText(context, "Coming soon...", Toast.LENGTH_SHORT).show();
            }else {
                Intent intent = new Intent(context, PdfViewer.class);
                intent.putExtra("pdfFile", pdfFile);
                intent.putExtra("fileName", pdf.getFileName());
                intent.putExtra("position", position);
                context.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return pdfList.size();
    }

    public static class DesiCatViewHolder extends RecyclerView.ViewHolder {
        ImageView cover;
        ImageView newTag;
        TextView title;
        TextView author;

        public DesiCatViewHolder(View itemView) {
            super(itemView);
            cover = itemView.findViewById(R.id.cover);
            newTag = itemView.findViewById(R.id.newTag);
            title = itemView.findViewById(R.id.titleName);
            author = itemView.findViewById(R.id.authorName);
        }
    }

}
