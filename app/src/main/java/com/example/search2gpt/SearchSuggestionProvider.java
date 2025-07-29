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
        
        // first try the parent's query for recent suggestions
        Cursor cursor = super.query(uri, projection, selection, selectionArgs, sortOrder);
        
        // if no recent suggestions, create a stub suggestion to prove the provider works
        if (cursor == null || cursor.getCount() == 0) {
            String query = (selectionArgs != null && selectionArgs.length > 0) ? selectionArgs[0] : "";
            if (!query.isEmpty()) {
                MatrixCursor matrixCursor = new MatrixCursor(new String[]{
                    BaseColumns._ID,
                    SearchManager.SUGGEST_COLUMN_TEXT_1,
                    SearchManager.SUGGEST_COLUMN_TEXT_2,
                    SearchManager.SUGGEST_COLUMN_INTENT_ACTION,
                    SearchManager.SUGGEST_COLUMN_INTENT_DATA
                });
                
                matrixCursor.addRow(new Object[]{
                    0,
                    "ask perplexity for '" + query + "'",
                    "get ai-powered answers from the web",
                    "android.intent.action.VIEW",
                    "search2gpt://search?q=" + Uri.encode(query)
                });
                
                return matrixCursor;
            }
        }
        
        return cursor;
    }
} 