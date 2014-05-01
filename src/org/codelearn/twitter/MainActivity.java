package org.codelearn.twitter;

import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.codelearn.twitter.models.User;
import org.codelearn.twitter.models.UserLoginResponse;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.gson.Gson;

/**
 * The Main Activity that shows the Login screen. At present, it accepts any username and password,
 * retrieves an auth token from the server and takes the user to {@linkplain TweetListActivity}. The
 * retrieved auth token is saved in SharedPreferences so that subsequent launches of the app will
 * not show the login screen
 */
public class MainActivity extends Activity {

  private static final String TAG = "Codelearn";

  private Button _loginBtn;
  private View _loadingLayout;
  private SharedPreferences _prefs;

  private static final String KEY_AUTHTOKEN = "key_authtoken";
  private static final String SERVER_ADDRESS = "http://app-dev-challenge-endpoint.herokuapp.com";

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    _loginBtn = (Button) findViewById(R.id.btn_login);
    _loadingLayout = findViewById(R.id.loadingLayout);
    _prefs = getSharedPreferences("codelearn_twitter", MODE_PRIVATE);

    String storedAuthToken = _prefs.getString(KEY_AUTHTOKEN, null);
    Log.d(TAG, "Saved authToken = " + storedAuthToken);
    if (storedAuthToken != null) {
      goToNextActivity();
    }

    _loginBtn.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {

        EditText usernameEdit = (EditText) findViewById(R.id.fld_username);
        EditText passwordEdit = (EditText) findViewById(R.id.fld_pwd);

        /* Check to ensure that the username and password fields are not empty */
        if (usernameEdit.getText() != null && passwordEdit.getText() != null) {

          String username = usernameEdit.getText().toString();
          String password = passwordEdit.getText().toString();

          User user = new User();
          user.setUsername(username);
          user.setPassword(password);

          new AsyncLogin().execute(user);
          toggleLoadingScreen(true);
        }
      }
    });
  }

  private class AsyncLogin extends AsyncTask<User, Void, Void> {

    private UserLoginResponse userResponse;

    @Override
    protected Void doInBackground(User... params) {
      User user = params[0];
      HttpClient client = new DefaultHttpClient();
      HttpPost post = new HttpPost(SERVER_ADDRESS + "/login");
      try {
        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
        nameValuePairs.add(new BasicNameValuePair("username", user.getUsername()));
        nameValuePairs.add(new BasicNameValuePair("password", user.getPassword()));
        post.setEntity(new UrlEncodedFormEntity(nameValuePairs));
        HttpResponse response = client.execute(post);

        userResponse =
            new Gson().fromJson(new InputStreamReader(response.getEntity().getContent()),
                UserLoginResponse.class);
      } catch (Exception e) {
        e.printStackTrace();
      }
      return null;
    }

    @Override
    protected void onPostExecute(Void result) {
      if (userResponse != null) {
        Log.d(TAG, "Login success " + userResponse.getToken());
        Editor editor = _prefs.edit();
        editor.putString(KEY_AUTHTOKEN, userResponse.getToken());
        editor.commit();

        goToNextActivity();
      } else {
        Log.d(TAG, "Login error");
        Toast.makeText(getApplicationContext(), "Unable to login. Please try again",
            Toast.LENGTH_LONG).show();
        toggleLoadingScreen(false);
      }

    }
  }

  /**
   * Toggles the display of a loading screen with a progress bar. This is displayed when the user
   * clicks the login button and the login network call has not yet returned.
   * 
   * @param showLayout boolean, indicating whether the layout should be shown (true) or hidden
   *        (false)
   */
  private void toggleLoadingScreen(boolean showLayout) {
    if (showLayout) {
      _loadingLayout.setVisibility(View.VISIBLE);
    } else {
      _loadingLayout.setVisibility(View.GONE);
    }
  }

  /**
   * Takes the user to the Tweet List Activity. This method has two entry points - One, when the
   * user logs in and clicks the login button, and two, when login information is already available
   * in SharedPreferences (on a subsequent launch)
   */
  private void goToNextActivity() {
    Intent intent = new Intent(MainActivity.this, TweetListActivity.class);
    startActivity(intent);
  }

}
