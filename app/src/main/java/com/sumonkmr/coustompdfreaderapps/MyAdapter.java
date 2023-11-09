package com.sumonkmr.coustompdfreaderapps;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import java.util.HashMap;
import java.util.List;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {

    Context context;
    HashMap<String,String> pdfTemp;
    List<HashMap<String,String>> pdfList;

    public MyAdapter(Context c,List<HashMap<String, String>> pdfList) {
        this.context = c;
        this.pdfList = pdfList;
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.pdf_card, parent, false);
        return new ViewHolder(view);

    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        pdfTemp = pdfList.get(position);
        String bookTitle,bookAuthor,bookCover;
        bookTitle = pdfTemp.get("title");
        bookAuthor = pdfTemp.get("author");
        bookCover = pdfTemp.get("cover");

        holder.authorName.setText(bookAuthor);
        holder.pdfCover.setImageResource(Integer.parseInt(bookCover));
                holder.pdfCover.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context,bookTitle, Toast.LENGTH_SHORT).show();
            }
        });

    }

    @Override
    public int getItemCount() {
       return pdfList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView authorName;
        public ImageView pdfCover;

        public ViewHolder(View view) {
            super(view);
            authorName = view.findViewById(R.id.authorName);
            pdfCover = view.findViewById(R.id.cover);
        }
    }
}