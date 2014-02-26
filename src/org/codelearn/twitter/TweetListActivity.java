package org.codelearn.twitter;

import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.app.Activity;
import android.app.ListActivity;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import org.codelearn.twitter.models.Tweet;


public class TweetListActivity extends ListActivity {



    private ArrayAdapter tweetItemArrayAdapter;
    
    private List<Tweet> tweets = new ArrayList<Tweet>();
    private static final String TWEETS_CACHE_FILE = "tweet_cache.ser";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_tweet_list);
		
		for ( int i = 0; i < 20; i++ ) {
		    Tweet tweet = new Tweet();
		    tweet.setTitle("A nice header for Tweet # " +i);
		    tweet.setBody("Some random body text for the tweet # " +i);
		    tweets.add(tweet);
		}
		
		FileOutputStream fos = null;
	    ObjectOutputStream oos = null;
	    
	    try {
	        fos = openFileOutput(TWEETS_CACHE_FILE, MODE_PRIVATE);
	        oos = new ObjectOutputStream(fos);
	        oos.writeObject(tweets);
	        Log.d("codelearn", "Successfully wrote tweets to the file.");
	    } 
	    catch ( Exception e ) {

	        Log.e("codelearn", "Failed to write to serialised file.", e);

	    } finally {
	        
	        try {
	            fos.close();
	        } catch (Exception e) {
	            Log.e("codelearn", "Error closing file stream", e);
	        }
	        
	        try {
	            oos.close();
	        } catch (Exception e) {
	            Log.e("codelearn", "Error closing output stream", e);
	        }
	    }

		tweetItemArrayAdapter = new TweetAdapter(this, tweets);
		setListAdapter(tweetItemArrayAdapter);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.tweet_list, menu);
		return true;
	}

	@Override
	 protected void onListItemClick(ListView l, View v, int position, long id) {
		Intent intent = new Intent(this, TweetDetailActivity.class);
	     startActivity(intent);
	 }
}
