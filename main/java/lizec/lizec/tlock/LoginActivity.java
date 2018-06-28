package lizec.lizec.tlock;

import android.Manifest;
import android.content.ContentUris;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.github.andyken.draggablegridview.DraggableGridView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.Objects;

import lizec.lizec.tlock.aes.database.AESMap;
import lizec.lizec.tlock.aes.gen.KeyGen;
import lizec.lizec.tlock.base.NoScreenShotActivity;
import lizec.lizec.tlock.file.FileHelper;

public class LoginActivity extends NoScreenShotActivity {
    private static final String preferenceName = "info";
    private static final String hasRegisterStr = "HasRegister";
    private static final int CHOOSE_PHOTO = 2;
    DraggableGridView dragView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Objects.requireNonNull(getSupportActionBar()).hide();
        initDragView();
        init();
    }

    private void init() {
        Button login = findViewById(R.id.btnLogin);
        TextView hint = findViewById(R.id.txtHint);
        SharedPreferences preferences = getSharedPreferences(preferenceName,MODE_PRIVATE);
        if(!preferences.getBoolean(hasRegisterStr,false)){
            login.setText("注册");
            hint.setText("请选择2至6张照片作为密码");
            ImageView iv = findViewById(R.id.imageView);
            iv.setImageDrawable(getResources().getDrawable(R.drawable.key,null));
        }

        login.setOnClickListener(v -> login());
        findViewById(R.id.btnChoose).setOnClickListener(v -> openAlbum());
    }

    private void initDragView(){
        dragView = findViewById(R.id.dragView);
        dragView.setOnItemClickListener((parent, view, position, id) -> dragView.removeView(view));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == CHOOSE_PHOTO){
            if(resultCode == RESULT_OK){
                handleImageOnKitKat(data);
            }
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode)
        {
            case 1:
                if(grantResults.length>0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    openAlbum();
                }
                else {
                    Toast.makeText(this,"您拒绝了读取权限, 因此无法访问相册",
                            Toast.LENGTH_SHORT).show();
                }
                break;
            default:
                break;
        }
    }

    private void openAlbum() {
        if(!checkNum()){
            Toast.makeText(this,"最多选择6张图片作为密码", Toast.LENGTH_SHORT).show();
            return;
        }

        if(ContextCompat.checkSelfPermission(LoginActivity.this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(LoginActivity.this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},1);
        }
        else{
            Intent intent = new Intent("android.intent.action.GET_CONTENT");
            intent.setType("image/*");
            startActivityForResult(intent,CHOOSE_PHOTO);
        }
    }

    private boolean checkNum(){
        return dragView.getChildCount() < 6;
    }

    private void handleImageOnKitKat(Intent data) {
        String imagePath = null;
        Uri uri = data.getData();
        if(DocumentsContract.isDocumentUri(this,uri)){
            String docId = DocumentsContract.getDocumentId(uri);
            if("com.android.providers.media.documents".equals(Objects.requireNonNull(uri).getAuthority())){
                String id = docId.split(":")[1];
                String selection = MediaStore.Images.Media._ID + "=" + id;
                imagePath = getImagePath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, selection);
            }
            else if("con.android.providers.downloads.documents".equals(uri.getAuthority())){
                Uri contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"),Long.valueOf(docId));
                imagePath = getImagePath(contentUri,null);
            }
        }
        else if("content".equalsIgnoreCase(Objects.requireNonNull(uri).getScheme())){
            imagePath = getImagePath(uri,null);
        }
        else if("file".equalsIgnoreCase(uri.getScheme())){
            imagePath = uri.getPath();
        }

        displayImage(imagePath);
    }

    private void displayImage(String imagePath) {
        if(imagePath != null){
            Bitmap origin = BitmapFactory.decodeFile(imagePath);
            ImageView imageView = new ImageView(this);

            imageView.setImageBitmap(origin);
            Log.i("图片回调", "displayImage: 图片显示回调");
            dragView.addView(imageView);
        }
        else {
            Toast.makeText(this,"图片获取失败",Toast.LENGTH_SHORT).show();
        }
    }

    private String getImagePath(Uri uri, String selection) {
        String path = null;

        Cursor cursor = getContentResolver().query(uri,null,selection,null,null);
        if(cursor != null){
            if(cursor.moveToFirst()){
                path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
            }
            cursor.close();
        }

        return path;
    }

    private void login() {
        ArrayList<byte[]> imageBytes = getImageBytes();

        if(imageBytes.size() < 2 || imageBytes.size() > 6){
            Toast.makeText(this,"请选择2至6张图片",Toast.LENGTH_LONG).show();
            return;
        }

        byte[] pwd = tryGenPwd(imageBytes);
        if(pwd != null){
            Intent intent = new Intent(this,MainActivity.class);
            intent.putExtra("pwd",pwd);
            startActivity(intent);
            finish();
        }
        else{
            Toast.makeText(this,"密码无效,请重新输入",Toast.LENGTH_LONG).show();
        }
    }

    @NonNull
    private ArrayList<byte[]> getImageBytes() {
        ArrayList<byte[]> imageBytes = new ArrayList<>(3);
        int count = dragView.getChildCount();
        for(int i=0;i<count;i++) {
            ImageView view = (ImageView) dragView.getChildAt(i);
            view.setDrawingCacheEnabled(false);
            view.setDrawingCacheEnabled(true);
            Bitmap bitmap = view.getDrawingCache(true);

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
            imageBytes.add(baos.toByteArray());
        }
        return imageBytes;
    }

    private byte[] tryGenPwd(ArrayList<byte[]> imageBytes) {
        try{
            File dataFile = new File(getFilesDir(),"pwd.data");
            byte[] pwd = KeyGen.genPassword(imageBytes);
            SharedPreferences preferences = getSharedPreferences(preferenceName,MODE_PRIVATE);
            if(preferences.getBoolean(hasRegisterStr,false)){
                // 尝试读取数据, 如果没有异常就说明密码和数据文件正确
                byte[] data = FileHelper.fileToByte(dataFile);
                new AESMap(pwd,data);
            }
            else{
                // 初始化map
                AESMap map = new AESMap(pwd);
                byte[] data = map.encode();
                FileHelper.byteToFile(dataFile,data);
                SharedPreferences.Editor editor = preferences.edit();
                editor.putBoolean(hasRegisterStr,true);
                editor.apply();
            }
            return pwd;
        } catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }
}
