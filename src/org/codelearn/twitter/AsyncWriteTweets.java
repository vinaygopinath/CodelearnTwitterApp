package org.codelearn.twitter;

import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.util.List;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

/**
 * An {@link AsyncTask} that caches a list of tweets in a local file. This is achieved by
 * serialization of the List object containing the tweets.
 * 
 * <p>
 * To signify that the serialization task can take a non-trivial amount of time given a large number
 * of tweets, this AsyncTask introduces a delay of five seconds before the tweets are written to
 * file.
 * </p>
 */
public class AsyncWriteTweets extends AsyncTask<List<twitter4j.Status>, Void, Void> {
  private static final String TAG = "CODELEARN_WRITE_TWEETS";
  TweetListActivity _activity = null;
  private static final String TWEETS_CACHE_FILE = "tweet_cache.ser";

  public AsyncWriteTweets(TweetListActivity act) {
    _activity = act;
  }

  @Override
  protected Void doInBackground(List<twitter4j.Status>... tweets) {

    try {

      Thread.sleep(5000);
      Log.d("AsyncWriteTweets", "Called()");
      _activity.getFileStreamPath(TWEETS_CACHE_FILE).delete();
      FileOutputStream fis =
          _activity.openFileOutput(TWEETS_CACHE_FILE, Context.MODE_PRIVATE);
      ObjectOutputStream oos = new ObjectOutputStream(fis);
      oos.writeObject(tweets[0]); // vararg parameter
      oos.reset();
      oos.close();
      Log.d(TAG, "File path = "
          + _activity.getFileStreamPath(TWEETS_CACHE_FILE).getAbsolutePath().toString());
    } catch (Exception e) {
      Log.e(TAG, "Error in AsyncWriteTweets: " + e);
    }
    return null;
  }


}
