package sybsk.kuser.gotkk.myofficer;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import org.jibble.simpleftp.SimpleFTP;

import java.io.File;

public class SignUpActivity extends AppCompatActivity {

    //Explicit
    private EditText nameEditText, userEditText, passwordEditText;
    private ImageView imageView;
    private Button button;
    private String nameString, userString, passwordString, pathImageString, nameImageString;
    private Uri uri;
    private String tag = "23JulyV1";
    private boolean aBoolean = true;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        //Bind Widget
        nameEditText = (EditText) findViewById(R.id.editText3);
        userEditText = (EditText) findViewById(R.id.editText4);
        passwordEditText = (EditText) findViewById(R.id.editText5);
        imageView = (ImageView) findViewById(R.id.imageView3);
        button = (Button) findViewById(R.id.button3);

        //Get Button Click
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //get value from edit text
                nameString = nameEditText.getText().toString().trim();      //trim คือตัดช่องว่างที่กรอกมา
                userString = userEditText.getText().toString().trim();
                passwordString = passwordEditText.getText().toString().trim();

                //Check space
                if (nameString.equals("") || userString.equals("") || passwordString.equals("")) {
                    //have space
                    MyAlert myAlert = new MyAlert(SignUpActivity.this, "Have Space", "Please Fill All Blank");
                    myAlert.myDialog();
                } else if (aBoolean) {
                    //Not Choose Image
                    MyAlert myAlert = new MyAlert(SignUpActivity.this, "ยังไม่ได้เลือกรูปภาพ", "โปรดเลือกรูปภาพ");
                    myAlert.myDialog();
                } else {
                    //Image OK
                    upLoadImage();
                    upLoadString();
                }
            }
        });
        //Image Controller
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);  //ACTION_GET_CONTENT ส่งสิ่ง สิ่งนี้ไปยังโปรแกรมอื่น
                intent.setType("image/*");  //image/* ส่งสิ่งที่สามรถเปิดรูปภาพได้
                startActivityForResult(intent.createChooser(intent, "Please Choose App"), 1); //ให้ทำงานจนเสร็จแล้วส่งค่ากลับมาที่เมธอดตรงนี้
            }
        });


    }// Main Method

    private void upLoadString() {
        try {
            AddUserToServer addUserToServer = new AddUserToServer(SignUpActivity.this, nameString
                    , userString, passwordString, "http://swiftcodingthai.com/image/rm4it/" + nameImageString);
            addUserToServer.execute();
            if (Boolean.parseBoolean(addUserToServer.get())) {
                finish();
            } else {
                Toast.makeText(SignUpActivity.this,"Cannot Upload",Toast.LENGTH_SHORT).show();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    private void upLoadImage() {
        try {
            //Change Policy
            StrictMode.ThreadPolicy threadPolicy = new StrictMode.ThreadPolicy.Builder().permitAll().build();   //อนุญาติให้ทำได้ทุกอย่าง
            StrictMode.setThreadPolicy(threadPolicy);   //คุณสามารถเข้าถึงโปโทรคอลได้เรียบร้อยแล้ว

            SimpleFTP simpleFTP = new SimpleFTP();
            simpleFTP.connect("ftp.swiftcodingthai.com", 21, "rm4it@swiftcodingthai.com", "Abc12345");
            simpleFTP.bin();    //หมายความว่าเปลี่ยนรูปภาพให้กลายเป็นตัวอักษรก่อน
            simpleFTP.cwd("image");     //ใส่ชื่อ Folder ที่ server
            simpleFTP.stor(new File(pathImageString));  //ดึงภาพจากตำแหน่งมาทำงาน
            simpleFTP.disconnect();

            Toast.makeText(SignUpActivity.this, "Upload Finish", Toast.LENGTH_SHORT).show();    //LENGTH_SHORT โชว์ข้อความ 4 วินาที

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) { //requestCode = 1 resultCode ดูว่าทำสำเสร็จหรือไม่
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {  //RESULT_OK คือการทำงานสมบูรณ์แล้ว
            aBoolean = false;
            uri = data.getData();
            showImage(uri);

            pathImageString = findPathImage(uri);
          //  nameImageString = pathImageString.substring(pathImageString.lastIndexOf("/"));
            Log.d("23JulyV1", "path ==> " + pathImageString);
            Log.d(tag, "name ==> " + nameImageString);
        }
    }

    private String findPathImage(Uri uri) {
        String result = null;
        String[] strings = new String[]{MediaStore.Images.Media.DATA};
        Cursor cursor = getContentResolver().query(uri, strings, null, null, null); //cursor การประมวนผลข้อมูลในแรม
        if (cursor != null) {
            cursor.moveToFirst();       //moveToFirst() คือการประมวนผลจากบนลงล่าง
            int i = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            Log.d(tag, "i ==> " + i);
            result = cursor.getString(i);
            Log.d(tag, "Result ==> " + result);
        } else {
            result = uri.getPath(); //กรณีมีรูปเดียว
        }
        return result;
    }

    private void showImage(Uri uri) {
        try {
            Bitmap bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(uri));
            imageView.setImageBitmap(bitmap);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
