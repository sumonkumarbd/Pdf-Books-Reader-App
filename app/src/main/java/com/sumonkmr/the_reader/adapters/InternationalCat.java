package com.sumonkmr.the_reader.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.recyclerview.widget.RecyclerView;
import com.squareup.picasso.Picasso;
import com.sumonkmr.the_reader.R;
import com.sumonkmr.the_reader.models.PdfModel;
import com.sumonkmr.the_reader.PdfViewer;

import java.util.List;

public class InternationalCat extends RecyclerView.Adapter<InternationalCat.IntViewHolder> {

    Context context;
    private List<PdfModel> pdfList;

    public InternationalCat(Context context,List<PdfModel> pdfList) {
        this.pdfList = pdfList;
        this.context = context;
    }

    @Override
    public IntViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.pdf_card, parent, false);  // `item_pdf` is the layout you provided.
        return new IntViewHolder(view);
    }

    @Override
    public void onBindViewHolder(IntViewHolder holder, int position) {
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

    public static class IntViewHolder extends RecyclerView.ViewHolder {
        ImageView cover;
        ImageView newTag;
        TextView title;
        TextView author;

        public IntViewHolder(View itemView) {
            super(itemView);
            cover = itemView.findViewById(R.id.cover);
            newTag = itemView.findViewById(R.id.newTag);
            title = itemView.findViewById(R.id.titleName);
            author = itemView.findViewById(R.id.authorName);
        }
    }
}
