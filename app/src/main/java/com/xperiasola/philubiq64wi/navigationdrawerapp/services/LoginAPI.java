package com.xperiasola.philubiq64wi.navigationdrawerapp.services;

import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by philUbiq64wi on 2/6/2018.
 */

public class LoginAPI extends StringRequest {
    //** Set 000WebHost Domain "https://invitationapp.000webhostapp.com"
    //** Call Login.php Services inside public_html
    private static final String LOGIN_REQUEST_URL = "https://invitationapp.000webhostapp.com/Login.php";
    private Map<String, String> params;

    public LoginAPI(String EMAIL, String PASSWORD, Response.Listener<String> listener) {
        super(Method.POST, LOGIN_REQUEST_URL, listener, null);

        //** Set parameters
        params = new HashMap<>();
        params.put("EMAIL", EMAIL);
        params.put("PASSWORD", PASSWORD);
    }

    //** Return parameters
    @Override
    public Map<String, String> getParams() {
        return params;
    }
}
