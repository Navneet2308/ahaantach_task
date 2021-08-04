package com.example.android.ahaantechtask;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.android.ahaantechtask.Utils.EndPoints;
import com.example.android.ahaantechtask.Utils.MyApplication;
import com.example.android.ahaantechtask.Utils.Utils;
import com.google.android.material.textfield.TextInputEditText;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class SignUp_Activity extends AppCompatActivity {
    String fullnameStr, firstnameStr,lastnameStr,emailStr, mobileStr,passStr,cpassStr;
    TextInputEditText fullnameEt, firstnameEt,lastnameEt,emailEt, mobileEt,passEt,cpassEt;
    Button proceedButton;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        initView();

        proceedButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fullnameStr = fullnameEt.getText().toString().trim();
                firstnameStr = firstnameEt.getText().toString().trim();
                lastnameStr = lastnameEt.getText().toString().trim();
                emailStr = emailEt.getText().toString().trim();
                mobileStr = mobileEt.getText().toString().trim();
                passStr = passEt.getText().toString().trim();
                cpassStr = cpassEt.getText().toString().trim();



                if (emailStr.equals("")) {
                    Utils.showErrorSnackbar(v, "Enter full name",SignUp_Activity.this);
                }
                else if (firstnameStr.equals("")) {
                    Utils.showErrorSnackbar(v, "Enter Your First name",SignUp_Activity.this);
                }
                else if (lastnameStr.equals("")) {
                    Utils.showErrorSnackbar(v, "Enter Your Last name",SignUp_Activity.this);
                }
                else if (emailStr.equals("")) {
                    Utils.showErrorSnackbar(v, "Enter full email address",SignUp_Activity.this);
                } else if (!Utils.isValidEmailId(emailStr)) {
                    Utils.showErrorSnackbar(v, "Please enter valid email address",SignUp_Activity.this);
                } else if (mobileStr.equals("")) {
                    Utils.showErrorSnackbar(v, "Enter mobile number",SignUp_Activity.this);
                } else if (mobileStr.length() != 10) {
                    Utils.showErrorSnackbar(v, "Mobile number should be in 10 digits",SignUp_Activity.this);
                }
                else if (passStr.equals("")) {
                    Utils.showErrorSnackbar(v, "Set Password",SignUp_Activity.this);
                }
               else {
                 if (passStr.equals(cpassStr))
                 {
                     registerMethod(fullnameStr,emailStr,mobileStr,passStr,firstnameStr,lastnameStr,proceedButton);

                 }
                 else
                 {
                     Utils.showErrorSnackbar(v, "Password and Confirm Password not matched",SignUp_Activity.this);

                 }
                }

            }
        });



    }

    private void initView() {
        fullnameEt = findViewById(R.id.nameEt);
        emailEt = findViewById(R.id.emailEt);
        mobileEt = findViewById(R.id.mobileEt);
        firstnameEt = findViewById(R.id.firstnameEt);
        lastnameEt = findViewById(R.id.lastnameEt);
        passEt=findViewById(R.id.passEt);
        cpassEt=findViewById(R.id.cpassEt);
        proceedButton=findViewById(R.id.proceedButton);
    }

    public void registerMethod(final String name,
                               final String email,
                               final String mobile,
                               final String pass,
                               final String fname,
                               final String lname, final View view) {
        Utils.showProgressDialog(SignUp_Activity.this, false);
        StringRequest request = new StringRequest(Request.Method.POST, EndPoints.REGISTER_API, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Utils.dismisProgressDialog();
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    Utils.showSuccessSnackbar(view, jsonObject.getString("message"),SignUp_Activity.this);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Utils.dismisProgressDialog();
                Utils.showErrorSnackbar(view, error.getMessage(),SignUp_Activity.this);

            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("fullName", name.trim());
                params.put("emailId", email.trim());
                params.put("mobile", mobile.trim());
                params.put("mobCountryCode", "91");
                params.put("firstName", fname.trim());
                params.put("lastName", lname.trim());
                params.put("password", pass.trim());

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