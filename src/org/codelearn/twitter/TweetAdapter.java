package org.codelearn.twitter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class TweetAdapter extends ArrayAdapter {

	 private LayoutInflater inflater;
     
     public TweetAdapter(Activity activity, String[] items){
         super(activity, R.layout.row_tweet, items);
         inflater = activity.getWindow().getLayoutInflater();
     }
     
     @Override
     public View getView(int position, View convertView, ViewGroup parent){
         View row = inflater.inflate(R.layout.row_tweet, parent, false);
         TextView title = (TextView) row.findViewById(R.id.tweetTitle);
         title.setText(position + ". title");
         TextView body = (TextView) row.findViewById(R.id.textView2);
         body.setText(position + ". body");
         return row;
     }
	
}
