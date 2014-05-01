package org.codelearn.twitter;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.codelearn.twitter.models.Tweet;

import android.app.Activity;
import android.app.ListActivity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;


/**
 * An {@link Activity} that displays a list of tweets. An empty {@link TweetAdapter} is created,
 * following which an attempt is made to show cached tweets (if any). Further,
 * {@link AsyncFetchTweets} is called to asynchronously update the ListView with new tweets.
 * 
 */
public class TweetListActivity extends ListActivity {

  private static final String TAG = "CODELEARN_TWEET_LIST";
  private static final String SERVER_ADDRESS = "http://app-dev-challenge-endpoint.herokuapp.com";

  private ArrayAdapter<Tweet> _tweetAdapter;
  private List<Tweet> _tweetList = new ArrayList<Tweet>();
  private static final String TWEETS_CACHE_FILE = "tweet_cache.ser";

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_tweet_list);

    _tweetAdapter = new TweetAdapter(this, _tweetList);
    setListAdapter(_tweetAdapter);

    ObjectInputStream ois = null;
    List<Tweet> cachedTweetsList = new ArrayList<Tweet>();
    try {
      ois = new ObjectInputStream(openFileInput(TWEETS_CACHE_FILE));
      cachedTweetsList = (List<Tweet>) ois.readObject();
    } catch (Exception e) {
      Log.e(TAG, "An error occurred while trying to retrieve cached tweets " + e.getMessage());
      e.printStackTrace();
    } finally {
      if (ois != null) {
        try {
          ois.close();
        } catch (IOException e) {
          e.printStackTrace();
        }
      }
    }
    renderTweets(cachedTweetsList);

    getLatestTweets();
  }

  private void getLatestTweets() {
    new FetchTweetsTask().execute();
  }

  /**
   * Shows the given List of Tweets in the ListView. Note that this method appends the given list of
   * Tweets to the existing list of Tweets rather than overriding it.
   * 
   * <p>
   * The method {@code ArrayAdapter.notifyDataSetChanged()} is used to update the UI
   * 
   * @param additionalTweetList a List of Tweet objects that are to be added to the ListView.
   */
  public void renderTweets(List<Tweet> additionalTweetList) {
    for (Tweet tweet : additionalTweetList) {
      _tweetList.add(0, tweet);
    }
    _tweetAdapter.notifyDataSetChanged();
  }

  @Override
  protected void onListItemClick(ListView l, View v, int position, long id) {
    Intent intent = new Intent(this, TweetDetailActivity.class);
    intent.putExtra("SelectedTweet", _tweetAdapter.getItem(position));
    startActivity(intent);

  }

  private class FetchTweetsTask extends AsyncTask<Void, Void, Void> {

    private List<Tweet> fetchedTweetList;

    @Override
    protected Void doInBackground(Void... params) {
      HttpClient client = new DefaultHttpClient();
      HttpGet getReq = new HttpGet(SERVER_ADDRESS + "/tweets");
      try {
        HttpResponse response = client.execute(getReq);
        if (response != null && response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
          Type listType = new TypeToken<ArrayList<Tweet>>() {}.getType();
          fetchedTweetList =
              new Gson().fromJson(new InputStreamReader(response.getEntity().getContent()),
                  listType);
          AsyncWriteTweets writeTask = new AsyncWriteTweets(TweetListActivity.this);
          writeTask.execute(fetchedTweetList);
        }
      } catch (Exception e) {
        e.printStackTrace();
      }
      return null;
    }

    @Override
    protected void onPostExecute(Void result) {

      if (fetchedTweetList != null) {
        renderTweets(fetchedTweetList);
      } else {
        Toast.makeText(getApplicationContext(), "Unable to fetch tweets at this time",
            Toast.LENGTH_LONG).show();
      }
    }
  }

  public boolean onCreateOptionsMenu(Menu menu) {
    getMenuInflater().inflate(R.menu.tweet_list, menu);
    return true;
  };

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    if (item.getItemId() == R.id.action_compose) {
      Intent composeIntent = new Intent(this, ComposeTweetActivity.class);
      startActivity(composeIntent);
      return true;
    } else if (item.getItemId() == R.id.action_refresh) {
      getLatestTweets();
    }
    return super.onOptionsItemSelected(item);
  }
}
