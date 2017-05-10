package ch.dben.library;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.api.services.books.model.Volume;

import java.util.LinkedList;
import java.util.List;

import ch.dben.library.content.BooksSearchAsyncTask;
import ch.dben.library.content.ImageDownloadAsyncTask;
import ch.dben.library.content.SearchResult;

/**
 * An activity representing a list of Books. This activity
 * has different presentations for handset and tablet-size devices. On
 * handsets, the activity presents a list of items, which when touched,
 * lead to a {@link BookDetailActivity} representing
 * item details. On tablets, the activity presents the list of items and
 * item details side-by-side using two vertical panes.
 */
public class BookListActivity extends AppCompatActivity {

    private static final String TAG = BookListActivity.class.getSimpleName();
    /**
     * Whether or not the activity is in two-pane mode, i.e. running on a tablet
     * device.
     */
    private boolean mTwoPane;
    private SimpleItemRecyclerViewAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_list);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle(getTitle());

        final SearchView searchView = (SearchView) findViewById(R.id.search);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                Log.d(TAG, "Searching for: " + query);

                new BooksSearchAsyncTask(query) {
                    @Override
                    protected void onPostExecute(Void v) {
                        adapter.setBooks(SearchResult.getCurrent());
                    }
                }.execute();

                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        View recyclerView = findViewById(R.id.book_list);
        assert recyclerView != null;
        setupRecyclerView((RecyclerView) recyclerView);

        if (findViewById(R.id.book_detail_container) != null) {
            // The detail container view will be present only in the
            // large-screen layouts (res/values-w900dp).
            // If this view is present, then the
            // activity should be in two-pane mode.
            mTwoPane = true;
        }
    }

    private void setupRecyclerView(@NonNull RecyclerView recyclerView) {
        adapter = new SimpleItemRecyclerViewAdapter();
        recyclerView.setAdapter(adapter);
    }

    public class SimpleItemRecyclerViewAdapter
            extends RecyclerView.Adapter<SimpleItemRecyclerViewAdapter.ViewHolder> {

        private List<Volume> mValues = new LinkedList<>();

        public SimpleItemRecyclerViewAdapter() {
        }

        void setBooks(List<Volume> books) {
            mValues  = books;
            notifyDataSetChanged();
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.book_list_content, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, int position) {
            holder.mItem = mValues.get(position);
            holder.mTitleView.setText(mValues.get(position).getVolumeInfo().getTitle());
            holder.mContentView.setText(mValues.get(position).getVolumeInfo().getPublishedDate());

            try {
                new ImageDownloadAsyncTask(holder.mThumbnail, holder.mItem.getVolumeInfo().getImageLinks().getThumbnail()).execute();
            } catch (NullPointerException e) {
                holder.mThumbnail.setImageResource(android.R.drawable.stat_notify_error);
            }

            holder.mView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mTwoPane) {
                        Bundle arguments = new Bundle();
                        arguments.putString(BookDetailFragment.ARG_ITEM_ID, holder.mItem.getId());
                        BookDetailFragment fragment = new BookDetailFragment();
                        fragment.setArguments(arguments);
                        getSupportFragmentManager().beginTransaction()
                                .replace(R.id.book_detail_container, fragment)
                                .commit();
                    } else {
                        Context context = v.getContext();
                        Intent intent = new Intent(context, BookDetailActivity.class);
                        intent.putExtra(BookDetailFragment.ARG_ITEM_ID, holder.mItem.getId());

                        context.startActivity(intent);
                    }
                }
            });
        }

        @Override
        public int getItemCount() {
            return mValues.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            public final View mView;
            public final TextView mTitleView;
            public final TextView mContentView;
            public final ImageView mThumbnail;
            public Volume mItem;

            public ViewHolder(View view) {
                super(view);
                mView = view;
                mTitleView = (TextView) view.findViewById(R.id.title);
                mContentView = (TextView) view.findViewById(R.id.content);
                mThumbnail = (ImageView) view.findViewById(R.id.thumbImage);
            }
        }
    }
}
