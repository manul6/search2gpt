# search2gpt

an android app that captures device searches and opens them directly in the perplexity app or perplexity.ai in your browser.

## features

- **native android search integration**: captures queries from android's global search
- **perplexity app deep linking**: opens queries directly in the perplexity app if installed
- **smart fallback**: automatically opens in browser if perplexity app is not installed
- **no setup required**: no api keys or configuration needed
- **lightweight**: minimal app with fast response times
- **privacy-focused**: searches go directly to perplexity without intermediary servers

## setup instructions

### quick setup

1. clone this repository:
   ```bash
   git clone <repository-url>
   cd search2gpt
   ```

2. build and install:
   - open the project in android studio
   - sync gradle files
   - build and run the app on your device/emulator

alternatively, using command line:
```bash
./gradlew assembleDebug
./gradlew installDebug
```

that's it! no api keys or configuration required.

## usage

### method 1: android search widget

1. open your device's search widget (usually on home screen)
2. type your question
3. the search2gpt app will automatically open perplexity.ai with your query

### method 2: launch the app directly

1. open the search2gpt app from your app drawer
2. the app will be ready to receive search queries
3. use your device's search function to ask questions

## example queries

- "what's the weather like today?"
- "explain quantum computing"
- "latest news about ai"
- "how to cook pasta"
- "what happened in 1969?"

## technical details

### architecture

- **language**: java
- **minimum sdk**: android 7.0 (api level 24)
- **target sdk**: android 14 (api level 34)
- **functionality**: browser intent launcher with url encoding

### project structure

```
search2gpt/
├── app/
│   ├── src/main/
│   │   ├── java/com/example/search2gpt/
│   │   │   └── SearchActivity.java          # main activity handling searches
│   │   ├── res/
│   │   │   ├── layout/
│   │   │   │   └── activity_search.xml      # main ui layout
│   │   │   ├── xml/
│   │   │   │   └── searchable.xml           # search configuration
│   │   │   └── values/
│   │   │       ├── strings.xml              # app strings
│   │   │       ├── colors.xml               # color palette
│   │   │       └── themes.xml               # material design theme
│   │   └── AndroidManifest.xml              # app configuration
│   └── build.gradle                         # app-level dependencies
├── build.gradle                             # project-level configuration
├── settings.gradle                          # gradle settings
```

### how it works

1. app receives search intent from android system
2. url-encodes the search query for safety
3. attempts to open query in perplexity app using deep links (tries multiple schemes)
4. if perplexity app is not installed, falls back to browser with url: `https://www.perplexity.ai/?q=encoded_query`
5. user gets results directly from perplexity app or perplexity.ai

## troubleshooting

### "no browser app found" error

- ensure you have a web browser installed (chrome, firefox, etc.)
- check that your browser is enabled and set as default if needed

### search not working

- make sure the app is installed and enabled as a search provider
- restart your device after installation
- check android settings > apps > default apps > assist & voice input

### app doesn't open from search

- verify the app has the correct search intent filters
- try clearing the search app's cache and data
- reinstall the app if issues persist

## advantages of this approach

- **zero configuration**: works immediately after installation
- **app-first experience**: prioritizes native perplexity app for better performance
- **automatic fallback**: seamlessly switches to browser if app not installed
- **always up-to-date**: uses latest perplexity features
- **no api costs**: free to use without api limits
- **privacy**: direct connection to perplexity, no intermediary

## future enhancements

- support for other search engines (google, bing, duckduckgo)
- custom url patterns and parameters
- search provider selection settings
- custom browser selection
- user preference for app vs browser

## license

this project is open source. feel free to modify and distribute according to your needs.

## contributing

contributions are welcome! please feel free to submit pull requests or open issues for bugs and feature requests.