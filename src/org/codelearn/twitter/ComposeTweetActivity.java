package org.codelearn.twitter;

import org.codelearn.twitter.models.CodelearnTwitterAPI;
import org.codelearn.twitter.models.Tweet;

import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RestAdapter.LogLevel;
import retrofit.RetrofitError;
import retrofit.client.Response;
import android.app.Activity;
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
          CodelearnTwitterAPI codelearnTwitterAPI =
              new RestAdapter.Builder().setEndpoint(SERVER_ADDRESS).setLogLevel(LogLevel.FULL)
                  .build().create(CodelearnTwitterAPI.class);

          Tweet tweet = new Tweet();
          tweet.setBody(_composeEdit.getText().toString());
          /* A random Tweet title based on the current time */
          tweet.setTitle("Tweet #" + (System.currentTimeMillis() / 1000));

          codelearnTwitterAPI.submitTweet(tweet, _submitTweetCallback);
        }
      }
    });
  }

  /**
   * The callback that is invoked when the submit tweet network call is completed. A simple Toast
   * message is displayed to the user conveying the result of the network call.
   */
  private Callback<Void> _submitTweetCallback = new Callback<Void>() {

    @Override
    public void failure(RetrofitError arg0) {
      Toast.makeText(getApplicationContext(),
          "Unable to submit tweet at this time. Please try again", Toast.LENGTH_LONG).show();
    }

    @Override
    public void success(Void arg0, Response arg1) {
      Toast.makeText(getApplicationContext(), "Tweet was successfully posted", Toast.LENGTH_LONG)
          .show();
      finish();
    }
  };
}
