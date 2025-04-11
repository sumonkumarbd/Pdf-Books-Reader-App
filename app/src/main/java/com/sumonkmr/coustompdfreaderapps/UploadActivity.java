package com.sumonkmr.coustompdfreaderapps;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.android.volley.VolleyError;
import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class UploadActivity extends AppCompatActivity {

    private static final String UPLOAD_URL = "http://127.0.0.1:5000/upload"; // Change to your Flask app URL

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload);

        // Call the upload function
        File file = new File("/path/to/your/file.pdf");  // File to upload
        String title = "Book Title";
        String author = "Author Name";
        String thumbnail = "path_to_thumbnail.jpg";
        String category = "Category";

        uploadFile(file, title, author, thumbnail, category);
    }

    private void uploadFile(final File file, final String title, final String author, final String thumbnail, final String category) {
        // Initialize Volley RequestQueue
        RequestQueue requestQueue = Volley.newRequestQueue(this);

        // Create Multipart Request
        StringRequest stringRequest = new StringRequest(Request.Method.POST, UPLOAD_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Toast.makeText(UploadActivity.this, "File uploaded successfully!", Toast.LENGTH_SHORT).show();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(UploadActivity.this, "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("title", title);
                params.put("author", author);
                params.put("thumbnail", thumbnail);
                params.put("category", category);
                return params;
            }

            public Map<String, File> getFileData() {
                Map<String, File> files = new HashMap<>();
                files.put("pdf_file", file);  // The file to upload
                return files;
            }
        };

        requestQueue.add(stringRequest);
    }
}
