package org.codelearn.twitter;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

/**
 * The Main Activity that shows the Login screen. At present, it accepts any username and password
 * and simply takes the user to the subsequent {@linkplain TweetListActivity}. Also, the entered
 * username and password are saved in SharedPreferences so that subsequent launches of the app will
 * not show the login screen
 */
public class MainActivity extends Activity {

  private Button _loginBtn;
  private SharedPreferences _prefs;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    _loginBtn = (Button) findViewById(R.id.btn_login);
    _prefs = getSharedPreferences("codelearn_twitter", MODE_PRIVATE);

    String storedUsername = _prefs.getString("key_username", null);
    String storedPassword = _prefs.getString("key_password", null);

    if (storedUsername != null && storedPassword != null) {
      goToNextActivity();
    }

    _loginBtn.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {

        EditText usernameEdit = (EditText) findViewById(R.id.fld_username);
        EditText passwordEdit = (EditText) findViewById(R.id.fld_pwd);

        /* Check to ensure that the username and password fields are not empty */
        if (usernameEdit.getText() != null && passwordEdit.getText() != null) {
          Editor editor = _prefs.edit();
          editor.putString("key_username", usernameEdit.getText().toString());
          editor.putString("key_password", passwordEdit.getText().toString());
          editor.commit();

          goToNextActivity();
        }
      }
    });
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
