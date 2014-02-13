package org.codelearn.twitter;

import android.app.Activity;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class MainActivity extends Activity {
	Button _loginBtn;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		_loginBtn = ( Button ) findViewById(R.id.btn_login);
		 
		_loginBtn.setOnClickListener(new View.OnClickListener() {
		      @Override
		      public void onClick(View v) {
		    	  
		          		EditText username = ( EditText ) findViewById(R.id.fld_username);
		    			  EditText password = ( EditText ) findViewById(R.id.fld_pwd);		  
		    			  SharedPreferences prefs = getSharedPreferences("codelearn_twitter", MODE_PRIVATE);
		    			  Editor edit = prefs.edit();
		    			  edit.putString("user",username.getText().toString());
		    			  
		    			  edit.commit();
		    			  edit.putString("pass",password.getText().toString());
		    			  edit.commit();
		    			  
		    			  
		      }
		  });
		
		
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

}
