package ch.dben.library.content;

import android.os.AsyncTask;
import android.util.Log;

import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.extensions.android.json.AndroidJsonFactory;
import com.google.api.services.books.Books;
import com.google.api.services.books.model.Volume;

import java.io.IOException;
import java.util.List;

import ch.dben.library.BuildConfig;

public class BooksSearchAsyncTask extends AsyncTask<Void, Void, Void> {

    private static final String TAG = BooksSearchAsyncTask.class.getSimpleName();
    private final String query;

    public BooksSearchAsyncTask(String query) {
        this.query = query;
    }

    @Override
    protected Void doInBackground(Void... params) {
        // Set up Books client.
        Books books = new Books.Builder(AndroidHttp.newCompatibleTransport(), AndroidJsonFactory.getDefaultInstance(), null)
                .setApplicationName(BuildConfig.APPLICATION_ID)
                .build();

        try {
            // Executes the query
            Books.Volumes.List list = books.volumes().list(query).setProjection("LITE");
            List<Volume> volumesList = list.execute().getItems();
            Log.d(TAG, "Books found: " + volumesList.size());
            SearchResult.setResult(volumesList);

        } catch (IOException e) {
            Log.e(TAG, "Failed to get search result", e);
        }

        return null;
    }
}
