package org.codelearn.twitter;

import org.codelearn.twitter.models.CodelearnTwitterAPI;
import org.codelearn.twitter.models.User;
import org.codelearn.twitter.models.UserLoginResponse;

import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

/**
 * The Main Activity that shows the Login screen. At present, it accepts any username and password,
 * retrieves an auth token from the server and takes the user to {@linkplain TweetListActivity}. The
 * retrieved auth token is saved in SharedPreferences so that subsequent launches of the app will
 * not show the login screen
 */
public class MainActivity extends Activity {

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

          /* Create an instance of CodelearnTwitterAPI */
          CodelearnTwitterAPI codelearnTwitterAPI =
              new RestAdapter.Builder().setEndpoint(SERVER_ADDRESS).build()
                  .create(CodelearnTwitterAPI.class);

          User user = new User();
          user.setUsername(username);
          user.setPassword(password);

          /*
           * Call the login POST method of the API and provide a callback to be invoked
           * asynchronously when the network call is completed (resulting in success or failure)
           */

          codelearnTwitterAPI.login(user, _loginCallback);
          toggleLoadingScreen(true);
        }
      }
    });
  }

  /**
   * The callback that is invoked when the login HTTP call is completed. In case of success, the
   * auth token is saved in SharedPreferences, and the user is taken to TweetListActivity.
   * 
   * In case of failure, a Toast message is displayed to the user.
   */
  private Callback<UserLoginResponse> _loginCallback = new Callback<UserLoginResponse>() {

    @Override
    public void failure(RetrofitError arg0) {
      Toast.makeText(getApplicationContext(), "Unable to login. Please try again",
          Toast.LENGTH_LONG).show();
      toggleLoadingScreen(false);
    }

    @Override
    public void success(UserLoginResponse userLoginResp, Response response) {
      if (userLoginResp != null) {
        Editor editor = _prefs.edit();
        editor.putString(KEY_AUTHTOKEN, userLoginResp.getToken());
        editor.commit();

        goToNextActivity();
      }
    }
  };

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
