package org.codelearn.twitter;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

import org.codelearn.twitter.models.Tweet;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;


public class TweetListActivity extends ListActivity{



    private ArrayAdapter tweetItemArrayAdapter;
    private List<Tweet> tweets = new ArrayList<Tweet>();
    private List<Tweet> tweetsRead=new ArrayList<Tweet>();
    private static final String TWEETS_CACHE_FILE = "tweet_cache.ser";
    private static final long serialVersionUID = 1L;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);	
		setContentView(R.layout.activity_tweet_list);
		
		for ( int i = 0; i < 30; i++ ) {
		    Tweet tweet = new Tweet();
		    tweet.setTitle("A nice header for Tweet # " +i);
		    tweet.setBody("Some random body text for the tweet # " +i);
		    tweets.add(tweet);
		}
		
		try
		
		{
			FileInputStream fis = openFileInput(TWEETS_CACHE_FILE);
			ObjectInputStream ois = new ObjectInputStream(fis);
			tweetsRead = ( List<Tweet> ) ois.readObject();
		}
		catch(Exception e)
		{
			
		}
		
		try
		{
		FileOutputStream fos=openFileOutput(TWEETS_CACHE_FILE, MODE_PRIVATE);
		ObjectOutputStream oos = new ObjectOutputStream(fos);
	    oos.writeObject(tweets);
	    oos.close();
	    fos.close();
		}
		catch(Exception e)
		{	
		}
		
	tweetItemArrayAdapter = new TweetAdapter(this, tweetsRead);
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
