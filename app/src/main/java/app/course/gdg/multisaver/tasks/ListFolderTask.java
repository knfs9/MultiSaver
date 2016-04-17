package app.course.gdg.multisaver.tasks;

import android.content.Context;
import android.os.AsyncTask;

import com.dropbox.core.DbxException;
import com.dropbox.core.v2.DbxClientV2;
import com.dropbox.core.v2.files.FileMetadata;
import com.dropbox.core.v2.files.ListFolderResult;

/**
 * Created by Ratus on 17.04.2016.
 */
public class ListFolderTask extends AsyncTask<String, Void, ListFolderResult> {
    private final Context context;
    private final DbxClientV2 clientV2;

    public ListFolderTask(Context context, DbxClientV2 clientV2){
        this.clientV2 = clientV2;
        this.context = context;
    }

    @Override
    protected ListFolderResult doInBackground(String... params) {
        try {
            return clientV2.files().listFolder(params[0]);
        } catch (DbxException e) {
            e.printStackTrace();
        }
        return null;
    }


}
