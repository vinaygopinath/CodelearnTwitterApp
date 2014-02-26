package org.codelearn.twitter.models;

import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.database.CursorJoiner.Result;
import android.os.AsyncTask;

public class AsyncFetchTweets extends  AsyncTask<List<Tweet>, Void, Void> {
List<Tweet> tweets =new ArrayList<Tweet>();
Activity test=null;
private static final String TWEETS_CACHE_FILE = "tweet_cache.ser";
public AsyncFetchTweets(Activity act) {
	test=act;
	// TODO Auto-generated constructor stub
}
	@Override
	protected Void doInBackground(List<Tweet>... params) {
		// TODO Auto-generated method stub
		
		try
		{
			Thread.sleep(5000);
			for ( int i = 0; i < 30; i++ ) {
			    Tweet tweet = new Tweet();
			    tweet.setTitle("A nice header for Tweet # " +i);
			    tweet.setBody("Some random body text for the tweet # " +i);
			    tweets.add(tweet);
			}
			
			
			FileOutputStream fos=test.openFileOutput(TWEETS_CACHE_FILE, 0);
			ObjectOutputStream oos = new ObjectOutputStream(fos);
		    oos.writeObject(tweets);
		    oos.close();
		    fos.close();
			}
			catch(Exception e)
			{	
			}
		return null;
	}

}
