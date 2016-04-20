package app.course.gdg.multisaver.tasks;

import android.content.Context;
import android.os.AsyncTask;

import com.dropbox.client2.DropboxAPI;
import com.dropbox.client2.exception.DropboxException;
import com.dropbox.core.DbxException;
import com.dropbox.core.v1.DbxEntry;
import com.dropbox.core.v2.DbxClientV2;
import com.dropbox.core.v2.files.FileMetadata;
import com.dropbox.core.v2.files.ListFolderResult;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Ratus on 17.04.2016.
 */
public class ListFolderTask extends AsyncTask<String, Void, Map<String, String>> {
    private final Context context;
    private final DropboxAPI dropboxAPI;

    public ListFolderTask(Context context, DropboxAPI dropboxAPI){
        this.dropboxAPI = dropboxAPI;
        this.context = context;
    }

    @Override
    protected Map doInBackground(String... params) {
        try {
            Map<String, String> temp = new HashMap();
            DropboxAPI.Entry dir = dropboxAPI.metadata(params[0], 1000, null, true, null);

            for(DropboxAPI.Entry entry : dir.contents){
                if(entry.isDir){
                    temp.put(entry.fileName(),"dir");
                }else{
                    temp.put(entry.fileName(), "file");
                }
            }
            return temp;

        } catch (DropboxException e) {
            e.printStackTrace();
        }
        return null;
    }


}
