package org.codelearn.twitter;

import java.util.ArrayList;
import java.util.List;

import org.codelearn.twitter.models.Tweet;

import android.os.AsyncTask;
import android.util.Log;

/**
 * An {@link AsyncTask} that is supposed to make network calls to fetch new _tweets. At present, it
 * simply spoofs the network call by creating a delay of five seconds and returning a set of 30
 * generated tweets.
 * 
 * <p>
 * Once the tweets have been "fetched", it also invokes {@link AsyncWriteTweets} to cache the
 * fetched tweets in a local file
 * </p>
 * 
 * <p>
 * Depending on your implementation of fetching tweets, you may wish to skip using this class
 * altogether.
 * </p>
 */
public class AsyncFetchTweets extends AsyncTask<Void, Void, List<Tweet>> {
  private static final String TAG = "CODELEARN_FETCH_TWEETS";
  private List<Tweet> _tweets = new ArrayList<Tweet>();
  private TweetListActivity _tweetListActivity = null;


  public AsyncFetchTweets(TweetListActivity activity) {
    _tweetListActivity = activity;
  }

  @Override
  protected List<Tweet> doInBackground(Void... params) {

    try {
      Thread.sleep(5000);

      for (int i = 0; i < 30; i++) {
        Tweet tweet = new Tweet();
        tweet.setTitle("A nice header for Tweet # " + i);
        tweet.setBody("Some random body text for the tweet # " + i);
        _tweets.add(tweet);
      }

      AsyncWriteTweets writeTask = new AsyncWriteTweets(_tweetListActivity);
      writeTask.execute(_tweets);

    } catch (Exception e) {
      Log.e(TAG, "Error in AsyncFetchTweets: " + e);
    }
    return _tweets;
  }


  protected void onPostExecute(List<Tweet> fetchedTweets) {
    _tweetListActivity.renderTweets(fetchedTweets);
  }

}
