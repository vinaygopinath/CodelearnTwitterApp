package org.codelearn.twitter;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.List;

import twitter4j.ResponseList;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.conf.Configuration;
import twitter4j.conf.ConfigurationBuilder;
import android.app.Activity;
import android.app.ListActivity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;


/**
 * An {@link Activity} that displays a list of tweets. An empty {@link TweetAdapter} is created,
 * following which an attempt is made to show cached tweets (if any). Further,
 * {@link FetchTweetsTask} is called to asynchronously update the ListView with new tweets.
 * 
 */
public class TweetListActivity extends ListActivity {

  private static final String TAG = "CODELEARN_TWEET_LIST";

  private ArrayAdapter<twitter4j.Status> _tweetAdapter;
  private List<twitter4j.Status> _tweetList = new ArrayList<twitter4j.Status>();
  private SharedPreferences _prefs;
  private Twitter _twitter;
  private static final String TWEETS_CACHE_FILE = "tweet_cache.ser";
  private FetchTweetsTask _fetchTweetsTask;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_tweet_list);
    _prefs = getSharedPreferences("codelearn_twitter", MODE_PRIVATE);

    _tweetAdapter = new TweetAdapter(this, _tweetList);
    setListAdapter(_tweetAdapter);

    String accToken = _prefs.getString(TwitterConstants.PREF_KEY_TOKEN, "");
    String accTokenSecret = _prefs.getString(TwitterConstants.PREF_KEY_SECRET, "");

    ConfigurationBuilder confbuilder = new ConfigurationBuilder();
    Configuration conf =
        confbuilder.setOAuthConsumerKey(TwitterConstants.CONSUMER_KEY)
            .setOAuthConsumerSecret(TwitterConstants.CONSUMER_SECRET).setOAuthAccessToken(accToken)
            .setOAuthAccessTokenSecret(accTokenSecret).build();
    _twitter = new TwitterFactory(conf).getInstance();

    ObjectInputStream ois = null;
    List<twitter4j.Status> cachedTweetsList = new ArrayList<twitter4j.Status>();
    try {
      ois = new ObjectInputStream(openFileInput(TWEETS_CACHE_FILE));
      cachedTweetsList = (List<twitter4j.Status>) ois.readObject();
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

  /**
   * This method ensures that only one AsyncTask is running at a time to fetch new tweets.
   */
  private void getLatestTweets() {
    if (_fetchTweetsTask == null) {
      _fetchTweetsTask = new FetchTweetsTask();
      _fetchTweetsTask.execute();
    }
  }

  private class FetchTweetsTask extends AsyncTask<Void, Void, Void> {

    private ResponseList<twitter4j.Status> timelineTweets;

    @Override
    protected Void doInBackground(Void... params) {
      try {
        timelineTweets = _twitter.getHomeTimeline();
        new AsyncWriteTweets(TweetListActivity.this).execute(timelineTweets);
      } catch (TwitterException e) {
        e.printStackTrace();
      }
      return null;
    }

    @Override
    protected void onPostExecute(Void result) {
      _fetchTweetsTask = null;
      renderTweets(timelineTweets);
    }

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
  public void renderTweets(List<Status> additionalTweetList) {
    if (additionalTweetList == null) {
      return;
    }
    for (Status tweet : additionalTweetList) {
      _tweetList.add(tweet);
    }

    _tweetAdapter.notifyDataSetChanged();
  }

  @Override
  protected void onListItemClick(ListView l, View v, int position, long id) {
    Intent intent = new Intent(this, TweetDetailActivity.class);
    intent.putExtra("SelectedTweet", _tweetAdapter.getItem(position));
    startActivity(intent);

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
