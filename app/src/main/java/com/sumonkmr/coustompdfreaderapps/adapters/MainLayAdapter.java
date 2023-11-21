package com.sumonkmr.coustompdfreaderapps.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import com.sumonkmr.coustompdfreaderapps.MainActivity;
import com.sumonkmr.coustompdfreaderapps.PdfViewer;
import com.sumonkmr.coustompdfreaderapps.R;

import java.util.HashMap;
import java.util.List;

public class MainLayAdapter extends RecyclerView.Adapter<MainLayAdapter.ViewHolder> {

    Context context;
    HashMap<String,String> pdfTemp;
    List<HashMap<String,String>> pdfList;

    public MainLayAdapter(Context c, List<HashMap<String, String>> pdfList) {
        this.context = c;
        this.pdfList = pdfList;
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.large_pdf_card, parent, false);
        return new ViewHolder(view);

    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        pdfTemp = pdfList.get(position);
        String bookTitle,bookAuthor,bookCover,fileName;
        bookTitle = pdfTemp.get("title");
        bookAuthor = pdfTemp.get("author");
        bookCover = pdfTemp.get("cover");
        fileName = pdfTemp.get("fileName");

        holder.titleName.setText(bookTitle);
        holder.authorName.setText(bookAuthor);
        holder.pdfCover.setImageResource(Integer.parseInt(bookCover));
         holder.pdfCover.setOnClickListener(v -> {
             MainActivity.PdfFileName = fileName;
             if (fileName.isEmpty()){
                 Toast.makeText(context, "Coming soon...", Toast.LENGTH_SHORT).show();
             }else {
                 Intent intent = new Intent(context, PdfViewer.class);
                 context.startActivity(intent);
             }
         });

         SetAnim(holder.itemView,position);

//        if (bookTitle.length() <= 10){
//            holder.newTag.setVisibility(View.VISIBLE);
//            Log.d("bookTitle length", "bookTitle length: "+bookTitle.length());
//        }else {
//            Log.d("bookTitle length", "bookTitle length: "+bookTitle.length());
//        }


    }

    @Override
    public int getItemCount() {
       return pdfList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView titleName ,authorName ,fileName;
        public ImageView pdfCover,newTag;

        public ViewHolder(View view) {
            super(view);
            titleName = view.findViewById(R.id.titleName);
            authorName = view.findViewById(R.id.authorName);
            pdfCover = view.findViewById(R.id.cover);
            newTag = view.findViewById(R.id.newTag);
        }
    }


    private void SetAnim(View view,int position){
        Animation anim = AnimationUtils.loadAnimation(context,R.anim.right_to_left);
        view.startAnimation(anim);
    }
}