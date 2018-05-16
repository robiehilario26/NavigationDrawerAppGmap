package com.xperiasola.philubiq64wi.navigationdrawerapp.services;

import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by philUbiq64wi on 2/5/2018.
 */


public class RegisterAPI extends StringRequest {
    //** Set 000WebHost Domain "https://invitationapp.000webhostapp.com"
    //** Call Register.php Services inside public_html
    private static final String REGISTER_REQUEST_URL = "https://invitationapp.000webhostapp.com/Register.php";
    private Map<String, String> params;

    public RegisterAPI(String EMAIL, String NAME, String AGE, String PASSWORD, Response.Listener<String> listener) {
        super(Method.POST, REGISTER_REQUEST_URL, listener, null);
    //** Set parameters
        params = new HashMap<>();
        params.put("EMAIL", EMAIL);
        params.put("NAME", NAME);
        params.put("AGE", AGE);
        params.put("PASSWORD", PASSWORD);
    }
    //** Return parameters
    @Override
    public Map<String, String> getParams() {
        return params;
    }
}
