package org.codelearn.twitter;

import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.conf.Configuration;
import twitter4j.conf.ConfigurationBuilder;
import android.app.Activity;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

/**
 * An {@link Activity} that allows the user to compose a new tweet.
 */
public class ComposeTweetActivity extends Activity {

  private static final String SERVER_ADDRESS = "http://app-dev-challenge-endpoint.herokuapp.com";

  private Button _submitButton;
  private EditText _composeEdit;
  private TextView _charCountText;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_compose_tweet);
    _composeEdit = (EditText) findViewById(R.id.fld_compose);
    _submitButton = (Button) findViewById(R.id.btn_submit);
    _charCountText = (TextView) findViewById(R.id.char_count);

    _composeEdit.addTextChangedListener(new TextWatcher() {

      @Override
      public void onTextChanged(CharSequence s, int start, int before, int count) {
        if (_composeEdit.getText() != null) {
          /* Number of character remaining = 140 - current length of input */
          int remainingChars = 140 - _composeEdit.getText().toString().length();
          _charCountText.setText(String.format("%d chars remaining", remainingChars));

          /* Submit button must be disabled if input exceeds 140 chars */
          if (remainingChars < 0) {
            _submitButton.setEnabled(false);
          } else {
            _submitButton.setEnabled(true);
          }
        }
      }

      @Override
      public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

      @Override
      public void afterTextChanged(Editable s) {}
    });

    _submitButton.setOnClickListener(new OnClickListener() {

      @Override
      public void onClick(View v) {
        if (_composeEdit.getText() != null) {
          new SubmitTweetTask().execute(_composeEdit.getText().toString());
        }
      }
    });
  }

  private class SubmitTweetTask extends AsyncTask<String, Void, Void> {

    private twitter4j.Status createdStatus = null;

    @Override
    protected Void doInBackground(String... params) {
      String status = params[0];
      SharedPreferences prefs = getSharedPreferences("codelearn_twitter", MODE_PRIVATE);
      String accToken = prefs.getString(TwitterConstants.PREF_KEY_TOKEN, "");
      String accTokenSecret = prefs.getString(TwitterConstants.PREF_KEY_SECRET, "");

      Log.d("Codelearn", "acctoken = " + accToken);
      Log.d("Codelearn", "acctokenSecret = " + accTokenSecret);

      ConfigurationBuilder confbuilder = new ConfigurationBuilder();
      Configuration conf =
          confbuilder.setOAuthConsumerKey(TwitterConstants.CONSUMER_KEY)
              .setOAuthConsumerSecret(TwitterConstants.CONSUMER_SECRET)
              .setOAuthAccessToken(accToken).setOAuthAccessTokenSecret(accTokenSecret).build();
      Twitter twitter = new TwitterFactory(conf).getInstance();

      try {
        createdStatus = twitter.updateStatus(status);
      } catch (TwitterException e) {
        e.printStackTrace();
      }

      return null;
    }

    @Override
    protected void onPostExecute(Void result) {
      if (createdStatus != null) {
        Toast.makeText(getApplicationContext(), "Tweet was successfully posted", Toast.LENGTH_LONG)
            .show();
        finish();
      } else {
        Toast.makeText(getApplicationContext(),
            "Unable to submit tweet at this time. Please try again", Toast.LENGTH_LONG).show();
      }
    }

  }
}
