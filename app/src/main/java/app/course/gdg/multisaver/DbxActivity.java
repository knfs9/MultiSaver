package app.course.gdg.multisaver;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleExpandableListAdapter;

import com.dropbox.client2.DropboxAPI;
import com.dropbox.client2.android.AndroidAuthSession;
import com.dropbox.client2.session.AccessTokenPair;
import com.dropbox.client2.session.AppKeyPair;
import com.dropbox.client2.session.Session;
import com.dropbox.client2.session.TokenPair;
import com.dropbox.core.DbxRequestConfig;
import com.dropbox.core.v1.DbxClientV1;
import com.dropbox.core.v1.DbxEntry;
import com.dropbox.core.v2.DbxClientV2;
import com.dropbox.core.v2.files.ListFolderResult;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import javax.xml.parsers.DocumentBuilderFactory;

import app.course.gdg.multisaver.Utils.Constants;
import app.course.gdg.multisaver.tasks.ListFolderTask;
import app.course.gdg.multisaver.tasks.ListFolderTaskV1;
import app.course.gdg.multisaver.tasks.UploadTask;


public class DbxActivity extends Activity implements View.OnClickListener {


    private static final int PICK_IMAGE_FROM_GALLERY = 0;

    private Uri mUri;
    private ImageView mImageView;
    private Button uploadButton;
    private Button showFoldersButton;
    EditText editText;

    private DbxRequestConfig config;
    private DropboxAPI<AndroidAuthSession> dropboxAPI;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mImageView = new ImageView(getApplicationContext());

        uploadButton = (Button) findViewById(R.id.uploadPhotoButton);
        showFoldersButton = (Button) findViewById(R.id.showFoldersButton);
        editText = (EditText) findViewById(R.id.textView);

        showFoldersButton.setOnClickListener(this);

        uploadButton.setOnClickListener(this);


        config = new DbxRequestConfig(Constants.DROPBOX_NAME, Locale.getDefault().toString());
        ListView listView = (ListView)findViewById(R.id.listView1);

//        Map map = getRootFolder("/");
//
//        ArrayAdapter<String> adapter2 = new ArrayAdapter<String>(this,	android.R.layout.simple_list_item_1, new ArrayList<String>(map.keySet()));
//        Intent intent = getIntent();
//        String login = intent.getStringExtra("login");
//        System.out.println(login);
//        listView.setAdapter(adapter2);
    }

    private void login(){
        AppKeyPair appKeyPair = new AppKeyPair(Constants.APP_KEY, Constants.APP_KEY_SEC);
        AndroidAuthSession session;

        SharedPreferences prefs = getSharedPreferences(Constants.DROPBOX_NAME, 0);
        String key = prefs.getString(Constants.APP_KEY, null);
        String secret = prefs.getString(Constants.APP_KEY_SEC, null);

        if(key != null && secret != null){
            AccessTokenPair accessTokenPair = new AccessTokenPair(key, secret);
            session = new AndroidAuthSession(appKeyPair, accessTokenPair);
        }else{
            session = new AndroidAuthSession(appKeyPair);
        }
        dropboxAPI = new DropboxAPI(session);
    }

    //Restore image after device rotation
    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        String imageUri = savedInstanceState.getString("imageUri", null);
        if (imageUri != null) {
            parseGalleryData(Uri.parse(imageUri));
        }
    }

    //Save uri to restore last image after rotation
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mUri != null) {
            outState.putString("imageUri", mUri.toString());
        }
    }

    @Override
    protected void onResume() {
        super.onResume();


    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.uploadPhotoButton:
                openGallery();
                break;
            case R.id.showFoldersButton:
                //editText.setText(cloud.getRootFolder(""));
                Map map = getRootFolder("");
                System.out.println(map.toString());
                break;

        }
    }

    //Example of startActivityForResult(...)
    private void openGallery() {
        Intent pickGalleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
        pickGalleryIntent.setType("image/*");
        startActivityForResult(
                Intent.createChooser(pickGalleryIntent, "Select Picture"), PICK_IMAGE_FROM_GALLERY);
    }

    //Handle result returned from Gallery
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        String str = data.getDataString();

        if (requestCode == PICK_IMAGE_FROM_GALLERY && resultCode == RESULT_OK) {
            Uri imageUri = data.getData();
            String filepath = data.getDataString();

            if (imageUri != null) {
                parseGalleryData(imageUri);
                try {
                    uploadFile(imageUri.toString());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void parseGalleryData(Uri imageUri) {
        InputStream imageStream;
        try {
            imageStream = getContentResolver().openInputStream(imageUri);
            Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
            mImageView.setImageBitmap(selectedImage);
            mUri = imageUri;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public Map<String, String> getRootFolder(String folder) {
        try {
            Map<String, String> map ;
            ListFolderTask folderTask = new ListFolderTask(this,dropboxAPI);

            map = new ListFolderTask(this,dropboxAPI).execute(folder).get();

            return map;

        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return null;
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    public void uploadFile(String file) throws IOException{
        new UploadTask(this, dropboxAPI).execute(file);
    }

}
