package com.sumonkmr.the_reader;

import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.HttpHeaderParser;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class InputStreamVolleyRequest extends Request<byte[]> {

    private final Response.Listener<byte[]> listener;
    private Map<String, String> params = Collections.emptyMap();
    private Map<String, String> headers;

    public InputStreamVolleyRequest(
            int method,
            String url,
            Response.Listener<byte[]> listener,
            Response.ErrorListener errorListener
    ) {
        super(method, url, errorListener);
        this.listener = listener;
        this.params = params != null ? params : new HashMap<>();
        this.headers = new HashMap<>();
    }

    /**
     * Optional: Allow setting custom headers (like authorization tokens)
     */
    public void setCustomHeaders(Map<String, String> headers) {
        this.headers = headers;
    }

    @Override
    protected Map<String, String> getParams() throws AuthFailureError {
        return params;
    }

    @Override
    public Map<String, String> getHeaders() throws AuthFailureError {
        // Return custom headers if available
        return headers != null ? headers : super.getHeaders();
    }

    @Override
    protected Response<byte[]> parseNetworkResponse(NetworkResponse response) {
        int statusCode = response.statusCode;
        Log.d("VolleyResponse", "Status Code: " + statusCode);

        if (statusCode >= 400) {
            Log.e("VolleyResponse", "Server returned error: " + statusCode);
        }

        return Response.success(response.data, HttpHeaderParser.parseCacheHeaders(response));
    }

    @Override
    protected void deliverResponse(byte[] response) {
        listener.onResponse(response);
    }
}
