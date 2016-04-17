package app.course.gdg.multisaver;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ImageView;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;




public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    DbxCloud cloud = new DbxCloud(this);

    private static final int PICK_IMAGE_FROM_GALLERY = 0;

    private Uri mUri;
    private ImageView mImageView;
    EditText editText;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mImageView = new ImageView(getApplicationContext());

        Button uploadButton = (Button) findViewById(R.id.uploadPhotoButton);
        Button showFoldersButton = (Button) findViewById(R.id.showFoldersButton);
        editText = (EditText) findViewById(R.id.textView);

        showFoldersButton.setOnClickListener(this);


        uploadButton.setOnClickListener(this);

        //SimpleExpandableListAdapter adapter = new SimpleExpandableListAdapter()
//        ExpandableListView expandableListView = (ExpandableListView) findViewById(R.id.expView);

        //expandableListView.setAdapter();
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
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.uploadPhotoButton:
                openGallery();
                break;
            case R.id.showFoldersButton:
                editText.setText(cloud.getUserFolders());

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
                    cloud.uploadFile(imageUri.toString());
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

    //Example of text sharing
    private void shareText() {
        Intent shareIntent = new Intent(android.content.Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "My First Android App");
        shareIntent.putExtra(android.content.Intent.EXTRA_TEXT, "Hello Study Jams!");
        startActivity(Intent.createChooser(shareIntent, "Share Via"));
    }

}
