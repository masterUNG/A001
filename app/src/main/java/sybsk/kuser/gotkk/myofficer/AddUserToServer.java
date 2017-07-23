package sybsk.kuser.gotkk.myofficer;

import android.content.Context;
import android.os.AsyncTask;
import android.provider.Settings;

import com.squareup.okhttp.FormEncodingBuilder;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

/**
 * Created by User on 7/18/2017.
 */

public class AddUserToServer extends AsyncTask<Void , Void , String>{  //extends เพื่อให้ต่อเน็ตเรื่อยๆทำงานเบื้องหลัง
    //Explicit
    private Context context;
    private static final String urlPHP = "ftp://ftp.swiftcodingthai.com/add_user_got.php";
    private String nameString, userString, passwordString, imageString;

    public AddUserToServer(Context context, String nameString, String userString, String passwordString, String imageString) {
        this.context = context;
        this.nameString = nameString;
        this.userString = userString;
        this.passwordString = passwordString;
        this.imageString = imageString;
    }

    @Override
    protected String doInBackground(Void... params) { //ทำงานเบื้องหลัง
        try {
            OkHttpClient okHttpClient = new OkHttpClient();
            RequestBody requestBody = new FormEncodingBuilder()     //ผูกตัวแปรเป็นก้อนเดียวกัน
                    .add("isAdd", "true")
                    .add("Name", nameString)
                    .add("User", userString)
                    .add("Password", passwordString)
                    .add("Image", imageString)
                    .build();

            Request.Builder builder = new Request.Builder();
            Request request = builder.url(urlPHP).post(requestBody).build();
            Response response = okHttpClient.newCall(request).execute();
            return response.body().string();
        } catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }
}
