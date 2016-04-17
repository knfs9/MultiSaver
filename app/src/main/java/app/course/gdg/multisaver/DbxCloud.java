package app.course.gdg.multisaver;

import android.annotation.TargetApi;
import android.app.Activity;
import android.os.Build;

import com.dropbox.core.DbxException;
import com.dropbox.core.DbxRequestConfig;
import com.dropbox.core.v1.DbxClientV1;
import com.dropbox.core.v2.DbxClientV2;
import com.dropbox.core.v2.files.FileMetadata;
import com.dropbox.core.v2.files.ListFolderResult;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutionException;

import app.course.gdg.multisaver.tasks.ListFolderTask;
import app.course.gdg.multisaver.tasks.UploadTask;

/**
 * Created by Ratus on 16.04.2016.
 */
public class DbxCloud {
    private static final String ACCESS_TOKEN = "o79-kJLA3KUAAAAAAAABqHKIsxIBF6epyJ9T2PD8fSdNgIcAVavvgvvivqgVtzKv";

    private DbxClientV2 clientV2;
    private DbxClientV1 clientV1;
    private DbxRequestConfig config;
    private Activity activity;


    public DbxCloud(Activity activity){
        config = new DbxRequestConfig("dropbox/java-tutorial", "ru");
        clientV2 = new DbxClientV2(config, ACCESS_TOKEN);
        this.activity = activity;
    }

    public String getUserFolders() {
        try {
            ListFolderResult result =  new ListFolderTask(activity,clientV2).execute("").get();
            return result.toStringMultiline();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return null;
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    public void uploadFile(String file) throws IOException{
        new UploadTask(activity, clientV2).execute(file);
    }
}
