package com.example.search2gpt;

import android.app.SearchManager;
import android.content.SearchRecentSuggestionsProvider;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.net.Uri;
import android.provider.BaseColumns;

public class SearchSuggestionProvider extends SearchRecentSuggestionsProvider {
    
    public static final String AUTHORITY = "com.example.search2gpt.suggestions";
    public static final int MODE = 1 | 2; // DATABASE_MODE_QUERIES | DATABASE_MODE_2LINES
    
    public SearchSuggestionProvider() {
        setupSuggestions(AUTHORITY, MODE);
    }
    
    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                       String[] selectionArgs, String sortOrder) {
        
        // performance: samsung finder demotes providers that take >100ms
        long startTime = System.currentTimeMillis();
        
        // try the parent's query for recent suggestions, but handle samsung finder's custom URIs
        Cursor cursor = null;
        try {
            cursor = super.query(uri, projection, selection, selectionArgs, sortOrder);
        } catch (IllegalArgumentException e) {
            // samsung finder may use URI formats that SearchRecentSuggestionsProvider doesn't recognize
            // this is expected - we'll provide our own suggestions instead
            android.util.Log.d("SearchSuggestionProvider", "parent provider couldn't handle URI: " + uri + " - providing custom suggestions");
            cursor = null;
        }
        
        // if no recent suggestions or parent couldn't handle URI, create optimized suggestions
        if (cursor == null || cursor.getCount() == 0) {
            String query = (selectionArgs != null && selectionArgs.length > 0) ? selectionArgs[0] : "";
            if (!query.isEmpty()) {
                // optimized column projection for maximum ranking
                MatrixCursor matrixCursor = new MatrixCursor(new String[]{
                    BaseColumns._ID,
                    SearchManager.SUGGEST_COLUMN_TEXT_1,
                    SearchManager.SUGGEST_COLUMN_TEXT_2,
                    SearchManager.SUGGEST_COLUMN_ICON_1,
                    SearchManager.SUGGEST_COLUMN_INTENT_ACTION,
                    SearchManager.SUGGEST_COLUMN_INTENT_DATA,
                    SearchManager.SUGGEST_COLUMN_RATING_SCORE
                });
                
                // add 3-6 high-quality suggestions (optimal range for finder)
                matrixCursor.addRow(new Object[]{
                    0,
                    "search \"" + query + "\" with perplexity.ai",
                    "get ai-powered answers from the web",
                    android.R.drawable.ic_menu_search, // built-in search icon
                    "android.intent.action.VIEW",
                    "search2gpt://search?q=" + Uri.encode(query),
                    10.0f // maximum rating score
                });
                
                matrixCursor.addRow(new Object[]{
                    1,
                    "perplexity.ai: " + query,
                    "ai search engine for accurate answers",
                    android.R.drawable.ic_menu_info_details,
                    "android.intent.action.VIEW",
                    "search2gpt://perplexity?q=" + Uri.encode(query),
                    10.0f // maximum rating score
                });
                
                matrixCursor.addRow(new Object[]{
                    2,
                    "ask ai about \"" + query + "\"",
                    "powered by search2gpt",
                    android.R.drawable.ic_dialog_info,
                    "android.intent.action.VIEW",
                    "search2gpt://quick?q=" + Uri.encode(query),
                    10.0f // maximum rating score
                });
                
                // performance check: ensure we stay under 100ms
                long elapsed = System.currentTimeMillis() - startTime;
                if (elapsed > 50) { // warn at 50ms to stay well under 100ms limit
                    android.util.Log.w("SearchSuggestionProvider", 
                        "query took " + elapsed + "ms - samsung finder may demote slow providers");
                }
                
                return matrixCursor;
            }
        }
        
        return cursor;
    }
} 