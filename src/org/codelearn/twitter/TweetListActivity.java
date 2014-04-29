package org.codelearn.twitter;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.List;

import org.codelearn.twitter.models.Tweet;

import android.app.Activity;
import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;


/**
 * An {@link Activity} that displays a list of tweets. An empty {@link TweetAdapter} is created,
 * following which an attempt is made to show cached tweets (if any). Further,
 * {@link AsyncFetchTweets} is called to asynchronously update the ListView with new tweets.
 * 
 */
public class TweetListActivity extends ListActivity {

  private static final String TAG = "CODELEARN_TWEET_LIST";

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
    AsyncFetchTweets asyc = new AsyncFetchTweets(this);
    asyc.execute();
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
}
