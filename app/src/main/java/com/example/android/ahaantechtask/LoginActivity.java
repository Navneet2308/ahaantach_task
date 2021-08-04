package com.example.android.ahaantechtask;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.android.ahaantechtask.Utils.EndPoints;
import com.example.android.ahaantechtask.Utils.MyApplication;
import com.example.android.ahaantechtask.Utils.SPCsnstants;
import com.example.android.ahaantechtask.Utils.Utils;
import com.google.android.material.textfield.TextInputEditText;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {
    private TextInputEditText mobileEt,passwordEt;
    public static String mobile = "",password= "";
    Button proceedButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        initView();
    }

    public void initView() {

        proceedButton = findViewById(R.id.proceedButton);
        mobileEt=findViewById(R.id.etMobile);
        passwordEt=findViewById(R.id.etPassword);


        proceedButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mobile=mobileEt.getText().toString().trim();
                password=passwordEt.getText().toString().trim();

                if (mobile.equals(""))
                {
                    Utils.showErrorSnackbar(proceedButton,"Please Enter Mobile Number",LoginActivity.this);
                }
                else if (password.equals(""))
                {
                    Utils.showErrorSnackbar(proceedButton,"Please Enter Password",LoginActivity.this);

                }
                else
                {
                    loginpassword(v);

                }








            }
        });



        findViewById(R.id.registerTv).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, SignUp_Activity.class);
                startActivity(intent);
            }
        });
}

    public void loginpassword(final View view) {
        Utils.showProgressDialog(LoginActivity.this, false);
        StringRequest request = new StringRequest(Request.Method.POST, EndPoints.LOGIN_API, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Utils.dismisProgressDialog();
                try {
                  JSONArray jsonArray = new JSONArray(response);
                  JSONObject jsonObject = jsonArray.getJSONObject(0);

                    MyApplication.mSp.setKey(SPCsnstants.IS_LOGGED_IN, SPCsnstants.YES);
                    MyApplication.mSp.setKey(SPCsnstants.id, jsonObject.getString("usersRegId"));
                    MyApplication.mSp.setKey(SPCsnstants.email, jsonObject.getString("emailId"));
                    MyApplication.mSp.setKey(SPCsnstants.mobile, jsonObject.getString("mobile"));
                    MyApplication.mSp.setKey(SPCsnstants.name, jsonObject.getString("firstName")+""+jsonObject.getString("lastName"));
                    MyApplication.mSp.setKey(SPCsnstants.country_code, jsonObject.getString("mobCountryCode"));
                    MyApplication.mSp.setKey(SPCsnstants.email_verify, jsonObject.getString("emailVerify"));

                    Intent intent = new Intent(LoginActivity.this, ProfilePage.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish();


                } catch (JSONException e) {
                    e.printStackTrace();

                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Utils.dismisProgressDialog();
                Toast.makeText(LoginActivity.this, ""+error.getMessage(), Toast.LENGTH_SHORT).show();

            }
        }) {
            @Override
            protected Map<String, String> getParams()  {
                Map<String, String> params = new HashMap<>();
                params.put("username", mobile);
                params.put("password",password);
                return params;
            }
        };
        request.setRetryPolicy(new DefaultRetryPolicy(
                30000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        MyApplication.mRequestQue.add(request);
    }



}