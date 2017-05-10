package ch.dben.library.content;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageView;

import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class ImageDownloadAsyncTask extends AsyncTask<Void, Void, Bitmap> {
    private static final String TAG = ImageDownloadAsyncTask.class.getSimpleName();
    private final WeakReference<ImageView> imageViewReference;
    private final URL url;

    public ImageDownloadAsyncTask(ImageView imageView, String url) {
        imageViewReference = new WeakReference<>(imageView);
        try {
            this.url = new URL(url);
        } catch (MalformedURLException e) {
            throw new IllegalArgumentException("url not valid", e);
        }
    }

    @Override
    protected Bitmap doInBackground(Void... params) {
        HttpURLConnection urlConnection = null;
        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            int statusCode = urlConnection.getResponseCode();
            if (statusCode != HttpURLConnection.HTTP_OK) {
                return null;
            }

            InputStream inputStream = urlConnection.getInputStream();
            if (inputStream != null) {
                return BitmapFactory.decodeStream(inputStream);
            }
        } catch (Exception e) {
            Log.w(TAG, "Error downloading image", e);
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
        }

        return null;
    }

    @Override
    protected void onPostExecute(Bitmap bitmap) {
        if (isCancelled()) {
            bitmap = null;
        }

        if (imageViewReference.get() != null) {
            ImageView imageView = imageViewReference.get();
            if (imageView != null) {
                if (bitmap != null) {
                    imageView.setImageBitmap(bitmap);
                } else {
                    Drawable placeholder = imageView.getContext().getResources().getDrawable(android.R.drawable.stat_notify_error);
                    imageView.setImageDrawable(placeholder);
                }
            }
        }
    }
}