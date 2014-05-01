package org.codelearn.twitter;

import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;
import twitter4j.auth.RequestToken;
import twitter4j.conf.Configuration;
import twitter4j.conf.ConfigurationBuilder;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

/**
 * The Main Activity that shows the Login screen. At present, it redirects the user to the Twitter
 * authentication webpage, retrieves an auth token from the server and takes the user to
 * {@linkplain TweetListActivity}. The retrieved auth token is saved in SharedPreferences so that
 * subsequent launches of the app will not show the login screen
 */
public class MainActivity extends Activity {

  private Button _loginBtn;
  private SharedPreferences _prefs;
  private static Twitter _twitter;
  private static RequestToken _requestToken;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    _loginBtn = (Button) findViewById(R.id.btn_login);
    _prefs = getSharedPreferences("codelearn_twitter", MODE_PRIVATE);

    String accToken = _prefs.getString(TwitterConstants.PREF_KEY_TOKEN, null);
    String accTokenSecret = _prefs.getString(TwitterConstants.PREF_KEY_SECRET, null);

    if (accToken != null && accTokenSecret != null) {
      goToNextActivity();
    }

    /*
     * If the user is returning from authorizing the app, then extract the "verifier" data and
     * launch an AsyncTask to obtain an access token.
     */
    Uri uri = getIntent().getData();
    if (uri != null && uri.toString().startsWith(TwitterConstants.CALLBACK_URL)) {
      String verifier = uri.getQueryParameter(TwitterConstants.IEXTRA_OAUTH_VERIFIER);
      new LoginAccessTask().execute(verifier);
    }

    _loginBtn.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        new LoginRequestTask().execute();
      }
    });
  }

  /**
   * Takes the user to the Tweet List Activity. This method has two entry points - One, when the
   * user successfully authorizes this app with Twitter, and two, when login information is already
   * available in SharedPreferences (on a subsequent launch)
   */
  private void goToNextActivity() {
    Intent intent = new Intent(MainActivity.this, TweetListActivity.class);
    startActivity(intent);
  }

  /**
   * A simple AsyncTask that obtains a request token from Twitter and launches the browser to allow
   * the user to authorize this app with Twitter.
   */
  private class LoginRequestTask extends AsyncTask<Void, Void, Void> {

    @Override
    protected Void doInBackground(Void... params) {
      ConfigurationBuilder configurationBuilder = new ConfigurationBuilder();
      configurationBuilder.setOAuthConsumerKey(TwitterConstants.CONSUMER_KEY);
      configurationBuilder.setOAuthConsumerSecret(TwitterConstants.CONSUMER_SECRET);
      Configuration configuration = configurationBuilder.build();
      _twitter = new TwitterFactory(configuration).getInstance();

      try {
        _requestToken = _twitter.getOAuthRequestToken(TwitterConstants.CALLBACK_URL);
      } catch (TwitterException e) {
        e.printStackTrace();
      }
      return null;
    }

    @Override
    protected void onPostExecute(Void result) {
      if (_requestToken != null) {
        Toast.makeText(MainActivity.this,
            "Please authorize Codelearn Twitter app to access your Twitter account",
            Toast.LENGTH_LONG).show();
        startActivity(new Intent(Intent.ACTION_VIEW,
            Uri.parse(_requestToken.getAuthenticationURL())));
      }
    }

  }

  /**
   * A simple AsyncTask that is invoked after successful authorization of the app to obtain an
   * access token. The access token is then stored in SharedPreferences for future use.
   */
  private class LoginAccessTask extends AsyncTask<String, Void, Void> {

    @Override
    protected Void doInBackground(String... params) {
      String verifier = params[0];

      try {
        AccessToken accessToken = _twitter.getOAuthAccessToken(_requestToken, verifier);
        Editor editor = _prefs.edit();
        editor.putString(TwitterConstants.PREF_KEY_TOKEN, accessToken.getToken());
        editor.putString(TwitterConstants.PREF_KEY_SECRET, accessToken.getTokenSecret());
        editor.commit();
      } catch (Exception e) {
        e.printStackTrace();
      }
      return null;
    }

    @Override
    protected void onPostExecute(Void result) {
      goToNextActivity();
    }

  }

}
