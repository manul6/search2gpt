package com.example.search2gpt;

import android.app.SearchManager;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.SearchRecentSuggestions;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

public class SearchActivity extends AppCompatActivity {
    
    private TextView titleText;
    private TextView responseView;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        
        initializeViews();
        handleIntent(getIntent());
    }
    
    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        handleIntent(intent);
    }
    
    private void initializeViews() {
        titleText = findViewById(R.id.titleText);
        responseView = findViewById(R.id.responseView);
    }
    
    private void handleIntent(Intent intent) {
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            if (query != null && !query.trim().isEmpty()) {
                openPerplexityInBrowser(query.trim());
            } else {
                showError("no search query provided");
            }
        } else if (Intent.ACTION_VIEW.equals(intent.getAction())) {
            // handle custom search2gpt:// scheme from suggestions
            Uri data = intent.getData();
            if (data != null && "search2gpt".equals(data.getScheme())) {
                String query = data.getQueryParameter("q");
                if (query != null && !query.trim().isEmpty()) {
                    openPerplexityInBrowser(query.trim());
                } else {
                    showError("no query parameter found");
                }
            }
        }
    }
    
    private void openPerplexityInBrowser(String query) {
        try {
            // encode the query for url safety
            String encodedQuery = URLEncoder.encode(query, StandardCharsets.UTF_8.toString());
            
            // try deep link first (Perplexity app)
            String deepLinkUrl = "perplexity://search?q=" + encodedQuery;
            
            // update ui to show what's happening
            updateResponse("opening perplexity for: \"" + query + "\"");
            
            // create intent to open Perplexity app via deep link
            Intent appIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(deepLinkUrl));
            appIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            
            // check if the Perplexity app can handle this intent
            if (appIntent.resolveActivity(getPackageManager()) != null) {
                // Perplexity app is installed, open it
                try {
                    startActivity(appIntent);
                    
                    // save query to recent suggestions database
                    SearchRecentSuggestions suggestions = new SearchRecentSuggestions(this,
                        SearchSuggestionProvider.AUTHORITY, 
                        SearchSuggestionProvider.MODE);
                    suggestions.saveRecentQuery(query, null);
                    
                    // show success message
                    Toast.makeText(this, "opening perplexity app...", Toast.LENGTH_SHORT).show();
                    
                } catch (android.content.ActivityNotFoundException e) {
                    // fallback to browser if deep link fails
                    openPerplexityInBrowserFallback(query, encodedQuery);
                }
            } else {
                // Perplexity app not installed, fallback to browser
                openPerplexityInBrowserFallback(query, encodedQuery);
            }
            
        } catch (Exception e) {
            showError("error opening perplexity: " + e.getMessage());
        }
    }
    
    private void openPerplexityInBrowserFallback(String query, String encodedQuery) {
        try {
            // create the perplexity web url
            String perplexityUrl = "https://www.perplexity.ai/?q=" + encodedQuery;
            
            // update ui
            updateResponse("perplexity app not found, opening browser for: \"" + query + "\"");
            
            // create intent to open browser
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(perplexityUrl));
            browserIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            
            // try to start the activity directly - let android handle browser selection
            try {
                startActivity(browserIntent);
                
                // save query to recent suggestions database
                SearchRecentSuggestions suggestions = new SearchRecentSuggestions(this,
                    SearchSuggestionProvider.AUTHORITY, 
                    SearchSuggestionProvider.MODE);
                suggestions.saveRecentQuery(query, null);
                
                // show success message
                Toast.makeText(this, "opening perplexity.ai in browser...", Toast.LENGTH_SHORT).show();
                
            } catch (android.content.ActivityNotFoundException e) {
                // if no browser found, try with explicit browser package
                showError("no browser app found - trying alternative...");
                tryAlternativeBrowser(perplexityUrl, query);
            }
            
        } catch (Exception e) {
            showError("error opening browser: " + e.getMessage());
        }
    }
    
    private void tryAlternativeBrowser(String url, String query) {
        try {
            // create a chooser to let user pick browser
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            Intent chooser = Intent.createChooser(browserIntent, "open with browser");
            chooser.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            
            startActivity(chooser);
            
            // save query to recent suggestions database
            SearchRecentSuggestions suggestions = new SearchRecentSuggestions(this,
                SearchSuggestionProvider.AUTHORITY, 
                SearchSuggestionProvider.MODE);
            suggestions.saveRecentQuery(query, null);
            
            Toast.makeText(this, "opening perplexity.ai...", Toast.LENGTH_SHORT).show();
            
        } catch (Exception e) {
            showError("no browser available: " + e.getMessage());
        }
    }
    
    private void updateResponse(String text) {
        runOnUiThread(() -> {
            responseView.setText(text);
        });
    }
    
    private void showError(String errorMessage) {
        runOnUiThread(() -> {
            responseView.setText("❌ " + errorMessage);
            Toast.makeText(SearchActivity.this, errorMessage, Toast.LENGTH_LONG).show();
        });
    }
}