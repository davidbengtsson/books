package ch.dben.library.content;

import com.google.api.services.books.model.Volume;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class SearchResult {

    private static final Map<String, Volume> mResult = Collections.synchronizedMap(new HashMap<String, Volume>());

    /**
     * Hidden constructor
     */
    private SearchResult() {
    }

    public static void setResult(List<Volume> result) {
        mResult.clear();
        for (Volume volume : result) {
            mResult.put(volume.getId(), volume);
        }
    }

    public static List<Volume> getCurrent() {
        return new ArrayList<Volume>(mResult.values());
    }

    public static Volume getById(String id) {
        return mResult.get(id);
    }
}
