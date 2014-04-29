package org.codelearn.twitter;

import org.codelearn.twitter.models.Tweet;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

/**
 * An {@link Activity} that displays a tweet in detail. Tapping on any Tweet in
 * {@link TweetListActivity} brings the user to this Activity. The selected Tweet is passed as an
 * Intent parameter
 */
public class TweetDetailActivity extends Activity {

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_tweet_detail);
    Tweet value = (Tweet) getIntent().getSerializableExtra("SelectedTweet");
    TextView u1 = (TextView) findViewById(R.id.tweetTitle);

    TextView u2 = (TextView) findViewById(R.id.tweetBody);
    u1.setText(value.getTitle().toString());
    u2.setText(value.getBody().toString());

  }
}
