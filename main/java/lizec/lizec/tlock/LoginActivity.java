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
import android.widget.ImageView;
import android.widget.Toast;

import com.github.andyken.draggablegridview.DraggableGridView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

import lizec.lizec.tlock.aes.database.AESMap;
import lizec.lizec.tlock.file.FileHelper;
import lizec.lizec.tlock.base.NoScreenShotActivity;

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
        dragView = findViewById(R.id.dragView);
        findViewById(R.id.btnChoose).setOnClickListener(v -> openAlbum());
        findViewById(R.id.btnLogin).setOnClickListener(v -> login());
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

        try{
            File dataFile = new File(getFilesDir(),"pwd.data");
            byte[] pwd = genPassword(imageBytes);
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

            Intent intent = new Intent(this,MainActivity.class);
            intent.putExtra("pwd",pwd);
            startActivity(intent);
        } catch (Exception e){
            e.printStackTrace();
            Toast.makeText(this,"密码无效,请重新输入",Toast.LENGTH_LONG).show();
        }
    }

    private byte[] genPassword(List<byte[]> list) throws GeneralSecurityException {

        byte[] b0 = list.get(0);
        int len0 = b0.length > 1024? 1024:b0.length;

        final String salt = new String(b0,0,len0);
        //System.out.println(salt);
        Log.i("Salt", "genPassword: GenSalt");

        int count = list.size();
        StringBuilder p = new StringBuilder();
        for(int i=1;i<count;i++){
            byte[] bytes = list.get(i);
            int len = bytes.length > 1024? 1024:bytes.length;

            p.append(new String(bytes, 0, len));
        }

        String pwd = p.toString();

        return hashPassword(pwd,salt);
    }


    private byte[] hashPassword(String password, String salt) throws GeneralSecurityException {
        // 即使用SHA1作为散列函数的PBKDF2算法
        // http://www.rfc-editor.org/rfc/rfc2898.txt
        // https://docs.oracle.com/javase/8/docs/technotes/guides/security/StandardNames.html#SecretKeyFactory
        // 可以通过调节迭代次数和长度来改变运算时间和破解难度
        final String algorithm = "PBKDF2WithHmacSHA1";
        PBEKeySpec pbeKeySpec = new PBEKeySpec(password.toCharArray(), salt.getBytes(), 4096, 128);
        SecretKeyFactory kFactory=SecretKeyFactory.getInstance(algorithm);
        SecretKey secretKey = kFactory.generateSecret(pbeKeySpec);
        return secretKey.getEncoded();
    }

}
