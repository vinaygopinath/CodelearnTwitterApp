package org.codelearn.twitter;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.codelearn.twitter.models.Tweet;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
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

          Tweet tweet = new Tweet();
          tweet.setBody(_composeEdit.getText().toString());
          /* A random Tweet title based on the current time */
          tweet.setTitle("Tweet #" + (System.currentTimeMillis() / 1000));

          new SubmitTweetTask().execute(tweet);
        }
      }
    });
  }

  private class SubmitTweetTask extends AsyncTask<Tweet, Void, Void> {

    HttpResponse response;

    @Override
    protected Void doInBackground(Tweet... params) {
      Tweet tweet = params[0];
      HttpClient client = new DefaultHttpClient();
      HttpPost post = new HttpPost(SERVER_ADDRESS + "/tweets");
      try {
        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
        nameValuePairs.add(new BasicNameValuePair("body", tweet.getBody()));
        post.setEntity(new UrlEncodedFormEntity(nameValuePairs));
        response = client.execute(post);
      } catch (Exception e) {
        e.printStackTrace();
      }
      return null;
    }

    @Override
    protected void onPostExecute(Void result) {
      if (response != null && response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
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
