package com.example.android.ahaantechtask;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.FileUtils;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.android.ahaantechtask.Utils.EndPoints;
import com.example.android.ahaantechtask.Utils.ImageResizer;
import com.example.android.ahaantechtask.Utils.MyApplication;
import com.example.android.ahaantechtask.Utils.SPCsnstants;
import com.example.android.ahaantechtask.Utils.Utils;
import com.example.android.ahaantechtask.Utils.VolleyMultipartRequest;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import de.hdodenhof.circleimageview.CircleImageView;
import id.zelory.compressor.Compressor;
import okhttp3.internal.Util;

public class ProfilePage extends AppCompatActivity {


    TextView nameTv,
            mobileTv,
            emailTv;
    CircleImageView profileIv;
    ImageView cameraim, editim, closeim;
    LinearLayout showLayout, editLayout, editL;
    Button logoutButton;
    private String pictureImagePath = "", image_path = "";
    public static String file_path;
    public String imageBase64 = "";
    boolean ishave_profilepic = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_page);
        initView();
    }

    public void initView() {
        logoutButton = findViewById(R.id.logoutButton);
        nameTv = findViewById(R.id.nameTv);

        cameraim = findViewById(R.id.cameraim);
        mobileTv = findViewById(R.id.mobileTv);
        emailTv = findViewById(R.id.emailTv);
        profileIv = findViewById(R.id.profileIv);




        getprofile_img(nameTv);


        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logout();
            }
        });


        cameraim.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Dexter.withActivity(ProfilePage.this)
                        .withPermissions(
                                Manifest.permission.READ_EXTERNAL_STORAGE,
                                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                Manifest.permission.CAMERA)
                        .withListener(new MultiplePermissionsListener() {
                            @Override
                            public void onPermissionsChecked(MultiplePermissionsReport report) {
                                if (report.areAllPermissionsGranted()) {
                                    showFileChooser();
                                }

                                // check for permanent denial of any permission
                                if (report.isAnyPermissionPermanentlyDenied()) {

                                }

                            }

                            @Override
                            public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                                token.continuePermissionRequest();
                            }
                        })
                        .onSameThread()
                        .check();

            }
        });

        nameTv.setText(MyApplication.mSp.getKey(SPCsnstants.name));
        mobileTv.setText(MyApplication.mSp.getKey(SPCsnstants.mobile));
        emailTv.setText(MyApplication.mSp.getKey(SPCsnstants.email));

    }

    private void showFileChooser() {
        Intent pickPhoto = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        Intent chooser = Intent.createChooser(pickPhoto, "Select Image To Upload");
        chooser.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[]{intent});
        startActivityForResult(chooser, 1);

    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1 && resultCode == RESULT_OK && data != null) {

            Uri uri;
            try {
                uri = data.getData();
                String file = Utils.getPath(ProfilePage.this, uri);

                if (uri.toString().startsWith("content://")) {

                    Cursor cursor = null;
                    try {

                        cursor = getContentResolver().query(uri, null, null, null, null);

                    } finally {

                        cursor.close();

                    }

                }

                file_path = file;
                image_path = file_path;
                Bitmap bitmap = BitmapFactory.decodeFile(file_path);
                profileIv.setImageBitmap(bitmap);
                if (ishave_profilepic)
                {
                    update_image();
                }
                else
                {
                    upload_image();
                }

            } catch (Exception e) {
                Bitmap bitmap = (Bitmap) data.getExtras().get("data");
                ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
                uri = getImageUri(ProfilePage.this, bitmap);
                String file = Utils.getPath(ProfilePage.this, uri);
                file_path = file;
                image_path = file_path;
                profileIv.setImageBitmap(bitmap);
                if (ishave_profilepic)
                {
                    update_image();
                }
                else
                {
                    upload_image();
                }
            }


        }



    }

    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, String.valueOf(System.currentTimeMillis()),
                null);
        return Uri.parse(path);
    }



    public void logout() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(ProfilePage.this);
        alertDialogBuilder.setMessage("Are you sure you want to logout ?");
        alertDialogBuilder.setPositiveButton("Yes",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        MyApplication.mSp.setKey(SPCsnstants.IS_LOGGED_IN, SPCsnstants.NO);
                        MyApplication.mSp.setKey(SPCsnstants.id, "");
                        MyApplication.mSp.setKey(SPCsnstants.name, "");
                        MyApplication.mSp.setKey(SPCsnstants.mobile, "");
                        MyApplication.mSp.setKey(SPCsnstants.email, "");
                        MyApplication.mSp.setKey(SPCsnstants.email_verify, "");
                        Intent intent = new Intent(ProfilePage.this, LoginActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        finish();
                    }
                });

        alertDialogBuilder.setNegativeButton("No",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {

                    }
                });
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }


    public void getprofile_img(final View view) {
        Utils.showProgressDialog(ProfilePage.this, false);
        StringRequest request = new StringRequest(Request.Method.GET, EndPoints.GET_PROFILEPIC_API + "/all", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Utils.dismisProgressDialog();
                try {
                    JSONArray jsonArray = new JSONArray(response);
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        if (jsonObject.getString("usersRegId").equals(MyApplication.mSp.getKey(SPCsnstants.id))) {
                            String profile_url = jsonObject.getString("profilePic");
                            MyApplication.mSp.setKey(SPCsnstants.profile_pic, profile_url);
                            Picasso.get().load(EndPoints.BASE_URL + MyApplication.mSp.getKey(SPCsnstants.profile_pic)).into(profileIv);
                            ishave_profilepic = true;
                        }
                        else

                        {
                            ishave_profilepic = false;
                        }

                    }

                } catch (JSONException e) {
                    e.printStackTrace();

                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Utils.dismisProgressDialog();
                Toast.makeText(ProfilePage.this, "" + error.getMessage(), Toast.LENGTH_SHORT).show();

            }
        });
        request.setRetryPolicy(new DefaultRetryPolicy(
                30000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        MyApplication.mRequestQue.add(request);
    }


    private void update_image() {
        String unique1 = UUID.randomUUID().toString();
        Utils.showProgressDialog(ProfilePage.this, false);
        VolleyMultipartRequest volleyMultipartRequest = new VolleyMultipartRequest(Request.Method.PUT, EndPoints.UPDATE_PROFILEPIC_API+"/"+MyApplication.mSp.getKey(SPCsnstants.id),
                new Response.Listener<NetworkResponse>() {
                    @Override
                    public void onResponse(NetworkResponse response) {
                        Utils.dismisProgressDialog();
//                        try {
//                            JSONObject jsonObject = new JSONObject(new String(response.data));
//                        } catch (JSONException e) {
//                            e.printStackTrace();
//                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Utils.dismisProgressDialog();
                        Toast.makeText(ProfilePage.this, "error"+error.toString(), Toast.LENGTH_SHORT).show();

                    }
                }) {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            protected Map<String, DataPart> getByteData() {
                Map<String, DataPart> params = new HashMap<>();
                params.put("profilePic", new DataPart(unique1 + ".jpg", fileToByte()));
                return params;
            }
        };

        volleyMultipartRequest.setRetryPolicy(new DefaultRetryPolicy(
                30000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        MyApplication.mRequestQue.add(volleyMultipartRequest);

    }




    private void upload_image() {
        String unique1 = UUID.randomUUID().toString();
        Utils.showProgressDialog(ProfilePage.this, false);
        VolleyMultipartRequest volleyMultipartRequest = new VolleyMultipartRequest(Request.Method.PUT, EndPoints.UPLOAD_PROFILEPIC_API+"/"+MyApplication.mSp.getKey(SPCsnstants.id),
                new Response.Listener<NetworkResponse>() {
                    @Override
                    public void onResponse(NetworkResponse response) {
                        Utils.dismisProgressDialog();
//                        try {
//                            JSONObject jsonObject = new JSONObject(new String(response.data));
//                        } catch (JSONException e) {
//                            e.printStackTrace();
//                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Utils.dismisProgressDialog();
                        Toast.makeText(ProfilePage.this, "error"+error.toString(), Toast.LENGTH_SHORT).show();

                    }
                }) {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            protected Map<String, DataPart> getByteData() {
                Map<String, DataPart> params = new HashMap<>();
                params.put("profilePic", new DataPart(unique1 + ".jpg", fileToByte()));
                return params;
            }
        };

        volleyMultipartRequest.setRetryPolicy(new DefaultRetryPolicy(
                30000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        MyApplication.mRequestQue.add(volleyMultipartRequest);

    }




    private byte[] fileToByte() {
        byte[] bytes = null;
        File imgfile = new File(image_path);
        try {
            bytes = org.apache.commons.io.FileUtils.readFileToByteArray(imgfile);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return bytes;
    }



}