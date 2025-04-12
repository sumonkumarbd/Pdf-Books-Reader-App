package com.sumonkmr.coustompdfreaderapps;

import static com.android.volley.VolleyLog.TAG;

import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.loader.content.CursorLoader;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class UploadPdfActivity extends AppCompatActivity {
    private static final int PICK_PDF_REQUEST = 1;
    private static final int PICK_THUMBNAIL_REQUEST = 2;

    private EditText etTitle, etAuthor, etCategory, etDescription;
    private Button btnSelectPdf, btnSelectThumbnail, btnUpload;
    private ImageView imgThumbnail;
    private TextView tvSelectedPdf;

    private String selectedPdfPath = null;
    private String selectedThumbnailPath = null;
    private ProgressDialog progressDialog;
    private Uri selectedPdfUri = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_pdf);

        // Initialize views
        etTitle = findViewById(R.id.etTitle);
        etAuthor = findViewById(R.id.etAuthor);
        etCategory = findViewById(R.id.etCategory);
        etDescription = findViewById(R.id.etDescription);
        btnSelectPdf = findViewById(R.id.btnSelectPdf);
        btnSelectThumbnail = findViewById(R.id.btnSelectThumbnail);
        btnUpload = findViewById(R.id.btnUpload);
        imgThumbnail = findViewById(R.id.imgThumbnail);
        tvSelectedPdf = findViewById(R.id.tvSelectedPdf);

        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);

        // Set click listeners
        btnSelectPdf.setOnClickListener(v -> selectPdf());
        btnSelectThumbnail.setOnClickListener(v -> selectThumbnail());
        btnUpload.setOnClickListener(v -> validateAndUpload());
    }

    private void selectPdf() {
        Intent intent = new Intent();
        intent.setType("application/pdf");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select PDF"), PICK_PDF_REQUEST);
    }

    private void selectThumbnail() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Thumbnail"), PICK_THUMBNAIL_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && data != null && data.getData() != null) {
            if (requestCode == PICK_PDF_REQUEST) {
                // Store the URI directly
                selectedPdfUri = data.getData();

                // Get the file name to display to the user
                String fileName = getFileNameFromUri(selectedPdfUri);
                tvSelectedPdf.setText("Selected: " + fileName);

                // Try to get path (but don't worry if it fails)
                try {
                    selectedPdfPath = getRealPathFromURI(selectedPdfUri);
                } catch (Exception e) {
                    selectedPdfPath = null;
                    // It's okay if this fails, we'll use the URI directly
                }

            } else if (requestCode == PICK_THUMBNAIL_REQUEST) {
                Uri imageUri = data.getData();
                try {
                    selectedThumbnailPath = getRealPathFromURI(imageUri);
                    Picasso.get()
                            .load(imageUri)
                            .placeholder(R.drawable.intro_cover_two)
                            .resize(500, 500)
                            .centerCrop()
                            .error(R.drawable.intro_cover)
                            .into(imgThumbnail);
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(this, "Error loading thumbnail", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    private String getRealPathFromURI(Uri contentUri) {
        String[] proj = {MediaStore.Images.Media.DATA};
        CursorLoader loader = new CursorLoader(this, contentUri, proj, null, null, null);
        Cursor cursor = loader.loadInBackground();
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        String result = cursor.getString(column_index);
        cursor.close();
        return result;
    }

    private void validateAndUpload() {
        String title = etTitle.getText().toString().trim();
        String author = etAuthor.getText().toString().trim();
        String category = etCategory.getText().toString().trim();
        String description = etDescription.getText().toString().trim();

        // Validate fields
        if (title.isEmpty() || author.isEmpty() || category.isEmpty()) {
            Toast.makeText(this, "Title, author and category are required", Toast.LENGTH_SHORT).show();
            return;
        }

        // Check if we have either a PDF path or URI
        if (selectedPdfUri == null && (selectedPdfPath == null || selectedPdfPath.isEmpty())) {
            Toast.makeText(this, "Please select a PDF file", Toast.LENGTH_SHORT).show();
            return;
        }

        // All validations passed, upload the PDF
        uploadPdf(title, author, category, description);
    }

    // Modified upload method that uses the Uri directly when available
    private void uploadPdf(String title, String author, String category, String description) {
        // Show progress indicator
        showProgressDialog("Uploading PDF...");

        // URL of your backend endpoint
        String uploadUrl = "https://flask-book-api-the-reader.onrender.com/api/upload";

        // Create a multipart request
        VolleyMultipartRequest volleyMultipartRequest = new VolleyMultipartRequest(
                Request.Method.POST, uploadUrl,
                response -> {
                    // Hide progress indicator
                    hideProgressDialog();

                    try {
                        // Parse the JSON response
                        JSONObject jsonObject = new JSONObject(new String(response.data));
                        String message = jsonObject.getString("message");
                        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
                        onUploadSuccess();
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(getApplicationContext(),
                                "Error parsing response", Toast.LENGTH_LONG).show();
                    }
                },
                error -> {
                    // Hide progress indicator
                    hideProgressDialog();
                    Toast.makeText(getApplicationContext(),
                            "Upload failed: " + getVolleyError(error), Toast.LENGTH_LONG).show();
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("title", title);
                params.put("author", author);
                params.put("category", category);
                params.put("description", description);
                return params;
            }

            @Override
            protected Map<String, DataPart> getByteData() {
                Map<String, DataPart> params = new HashMap<>();

                try {
                    // Try to get PDF data from URI first (preferred method)
                    if (selectedPdfUri != null) {
                        String fileName = getFileNameFromUri(selectedPdfUri);
                        byte[] pdfBytes = getBytesFromUri(selectedPdfUri);

                        if (pdfBytes != null && pdfBytes.length > 0) {
                            params.put("pdf_file", new DataPart(fileName, pdfBytes));
                        } else {
                            // URI method failed, try file path if available
                            if (selectedPdfPath != null && !selectedPdfPath.isEmpty()) {
                                File pdfFile = new File(selectedPdfPath);
                                if (pdfFile.exists()) {
                                    byte[] fileBytes = fileToBytes(pdfFile);
                                    params.put("pdf_file", new DataPart(pdfFile.getName(), fileBytes));
                                }
                            }
                        }
                    }
                    // If URI is null but we have a path, use that
                    else if (selectedPdfPath != null && !selectedPdfPath.isEmpty()) {
                        File pdfFile = new File(selectedPdfPath);
                        if (pdfFile.exists()) {
                            byte[] fileBytes = fileToBytes(pdfFile);
                            params.put("pdf_file", new DataPart(pdfFile.getName(), fileBytes));
                        }
                    }

                    // Add thumbnail if exists
                    if (selectedThumbnailPath != null && !selectedThumbnailPath.isEmpty()) {
                        File thumbnailFile = new File(selectedThumbnailPath);
                        if (thumbnailFile.exists()) {
                            byte[] thumbnailBytes = fileToBytes(thumbnailFile);
                            params.put("thumbnail", new DataPart(thumbnailFile.getName(), thumbnailBytes));
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }

                return params;
            }
        };

        // Set retry policy
        volleyMultipartRequest.setRetryPolicy(new DefaultRetryPolicy(
                60000, // Increased timeout to 60 seconds for larger files
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        // Add request to queue
        Volley.newRequestQueue(this).add(volleyMultipartRequest);
    }

    // Helper method to get filename from URI
    private String getFileNameFromUri(Uri uri) {
        String result = null;

        if (uri.getScheme() != null && uri.getScheme().equals("content")) {
            Cursor cursor = getContentResolver().query(uri, null, null, null, null);
            try {
                if (cursor != null && cursor.moveToFirst()) {
                    int nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                    if (nameIndex >= 0) {
                        result = cursor.getString(nameIndex);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (cursor != null) {
                    cursor.close();
                }
            }
        }

        if (result == null) {
            result = uri.getPath();
            int cut = result != null ? result.lastIndexOf('/') : -1;
            if (cut != -1) {
                result = result.substring(cut + 1);
            }
        }

        return result != null ? result : "document.pdf";
    }

    // Helper method to get bytes from URI
    private byte[] getBytesFromUri(Uri uri) throws IOException {
        try {
            InputStream inputStream = getContentResolver().openInputStream(uri);
            if (inputStream == null) {
                return null;
            }

            ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();
            byte[] buffer = new byte[4096];
            int len;
            while ((len = inputStream.read(buffer)) != -1) {
                byteBuffer.write(buffer, 0, len);
            }
            inputStream.close();
            return byteBuffer.toByteArray();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private String getRealPathFromPdfURI(Uri uri) {
        String result = null;
        if ("content".equals(uri.getScheme())) {
            try {
                String[] projection = { MediaStore.Files.FileColumns.DATA };
                Cursor cursor = getContentResolver().query(uri, projection, null, null, null);
                if (cursor != null && cursor.moveToFirst()) {
                    int columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.DATA);
                    result = cursor.getString(columnIndex);
                    cursor.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        // If the above method fails, try this alternative approach
        if (result == null) {
            result = uri.getPath();
            try {
                // For content URIs, try to get the actual file path
                Cursor cursor = getContentResolver().query(uri, null, null, null, null);
                if (cursor != null && cursor.moveToFirst()) {
                    int idx = cursor.getColumnIndex(MediaStore.Files.FileColumns.DISPLAY_NAME);
                    if (idx != -1) {
                        String displayName = cursor.getString(idx);
                        // Now you have the display name, but not the path
                        // For PDFs, you may need to create a temporary file
                        // or use ContentResolver to open an InputStream
                        tvSelectedPdf.setText("Selected: " + displayName);
                    }
                    cursor.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return result;
    }

    private void showProgressDialog(String message) {
        progressDialog.setMessage(message);
        progressDialog.show();
    }

    private void hideProgressDialog() {
        if (progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }

    private void onUploadSuccess() {
        // Clear form or navigate back
        finish();
    }//OnCreate


    public void uploadPdf(String filePathOrUri, String title, String author, String category,
                          String description, String thumbnailPath) {

        // Show progress indicator
        showProgressDialog("Uploading PDF...");

        // URL of your backend endpoint
        String uploadUrl = "https://flask-book-api-the-reader.onrender.com/api/upload";

        // Create a multipart request
        VolleyMultipartRequest volleyMultipartRequest = new VolleyMultipartRequest(
                Request.Method.POST, uploadUrl,
                response -> {
                    // Hide progress indicator
                    hideProgressDialog();

                    try {
                        // Parse the JSON response
                        JSONObject jsonObject = new JSONObject(new String(response.data));
                        String message = jsonObject.getString("message");
                        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();

                        // Handle successful upload (e.g., refresh list, navigate back)
                        onUploadSuccess();

                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(getApplicationContext(),
                                "Error parsing response", Toast.LENGTH_LONG).show();
                    }
                },
                error -> {
                    // Hide progress indicator
                    hideProgressDialog();

                    // Handle error
                    Toast.makeText(getApplicationContext(),"Upload failed: " + getVolleyError(error), Toast.LENGTH_LONG).show();
                    Log.d(TAG, "uploadPdf: "+getVolleyError(error));
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                // Add text parameters
                Map<String, String> params = new HashMap<>();
                params.put("title", title);
                params.put("author", author);
                params.put("category", category);
                params.put("description", description);
                return params;
            }

            @Override
            protected Map<String, DataPart> getByteData() {
                Map<String, DataPart> params = new HashMap<>();

                try {
                    byte[] pdfBytes;
                    String fileName;

                    // Check if it's a file path or URI string
                    if (filePathOrUri.startsWith("content:") || filePathOrUri.startsWith("file:")) {
                        // It's a URI string
                        Uri uri = Uri.parse(filePathOrUri);
                        fileName = getFileNameFromUri(uri);
                        pdfBytes = getBytesFromUri(uri);
                    } else {
                        // It's a file path
                        File pdfFile = new File(filePathOrUri);
                        fileName = pdfFile.getName();
                        pdfBytes = fileToBytes(pdfFile);
                    }

                    params.put("pdf_file", new DataPart(fileName, pdfBytes));

                    // Add thumbnail if exists
                    if (thumbnailPath != null && !thumbnailPath.isEmpty()) {
                        File thumbnailFile = new File(thumbnailPath);
                        byte[] thumbnailBytes = fileToBytes(thumbnailFile);
                        params.put("thumbnail", new DataPart(thumbnailFile.getName(), thumbnailBytes));
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }

                return params;
            }
        };

        // Set retry policy
        volleyMultipartRequest.setRetryPolicy(new DefaultRetryPolicy(
                30000, // 30 seconds timeout
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        // Add request to queue
        Volley.newRequestQueue(this).add(volleyMultipartRequest);
    }


    // Helper method to convert file to byte array
    private byte[] fileToBytes(File file) throws IOException {
        int size = (int) file.length();
        byte[] bytes = new byte[size];

        BufferedInputStream buf = new BufferedInputStream(new FileInputStream(file));
        buf.read(bytes, 0, bytes.length);
        buf.close();

        return bytes;
    }

    // Helper method to get error message from Volley error
    private String getVolleyError(VolleyError error) {
        if (error.networkResponse != null && error.networkResponse.data != null) {
            try {
                String errorResponse = new String(error.networkResponse.data, "UTF-8");
                JSONObject errorObj = new JSONObject(errorResponse);
                if (errorObj.has("error")) {
                    return errorObj.getString("error");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return error.getMessage() != null ? error.getMessage() : "Unknown error";
    }
}//upload Class