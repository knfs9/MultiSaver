package app.course.gdg.multisaver.tasks;

import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;

import com.dropbox.core.DbxException;
import com.dropbox.core.v2.DbxClientV2;
import com.dropbox.core.v2.files.FileMetadata;
import com.dropbox.core.v2.files.WriteMode;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import app.course.gdg.multisaver.Utils.UriHelpers;

/**
 * Created by Ratus on 17.04.2016.
 */
public class UploadTask extends AsyncTask<String, Void, FileMetadata> {
    private final Context context;
    private final DbxClientV2 clientV2;


    public UploadTask(Context context, DbxClientV2 clientV2){
        this.clientV2 = clientV2;
        this.context = context;
    }
    @Override
    protected FileMetadata doInBackground(String... params) {
        String localUri = params[0];
        File localFile = UriHelpers.getFileForUri(context, Uri.parse(localUri));

        if (localFile != null) {
            //String remoteFolderPath = params[1];

            // Note - this is not ensuring the name is a valid dropbox file name
            String remoteFileName = localFile.getName();
            InputStream inputStream = null;
            try {
                inputStream = new FileInputStream(localFile);
                return clientV2.files().uploadBuilder("/" + remoteFileName)
                        .withMode(WriteMode.OVERWRITE)
                        .uploadAndFinish(inputStream);
            } catch (DbxException | IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }
}
