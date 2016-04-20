package app.course.gdg.multisaver.tasks;

import android.content.Context;
import android.os.AsyncTask;

import com.dropbox.core.DbxException;
import com.dropbox.core.v1.DbxClientV1;

import com.dropbox.core.v1.DbxEntry;
import com.dropbox.core.v2.files.ListFolderResult;

/**
 * Created by Ratus on 19.04.2016.
 */
public class ListFolderTaskV1 extends AsyncTask<String, Void, DbxEntry.WithChildren> {
    private Context context;
    private DbxClientV1 clientV1;
    public ListFolderTaskV1(Context context, DbxClientV1 clientV1){
        this.context = context;
        this.clientV1 = clientV1;
    }
    @Override
    protected DbxEntry.WithChildren doInBackground(String... params) {
        try {
            return clientV1.getMetadataWithChildren(params[0]);
        } catch (DbxException e) {
            e.printStackTrace();
        }
        return null;
    }
}
