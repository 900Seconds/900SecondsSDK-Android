package com.livestreamsdk.example;

import java.util.ArrayList;
import java.util.Date;

import android.app.ActionBar.LayoutParams;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.livestreamsdk.Nine00SecondsSDK;
import com.livestreamsdk.Nine00SecondsSDK.RequestErrorType;
import com.livestreamsdk.data.StreamData;
import com.livestreamsdk.data.StreamsListData;

public class StreamsListActivity extends Activity {
	private ArrayList<StreamData> streams = new ArrayList<StreamData>();
	private ListView listView;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.streams_list_activity);
		listView = (ListView) findViewById(R.id.listView);
//		getActionBar().setDisplayHomeAsUpEnabled(true);
		
        // Create a progress bar to display while the list loads
        ProgressBar progressBar = new ProgressBar(this);
        
        LinearLayout progressContainer = new LinearLayout(this);
        progressContainer.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,
                LayoutParams.MATCH_PARENT, Gravity.CENTER));
        
        progressBar.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT,
                LayoutParams.WRAP_CONTENT, Gravity.CENTER));
        progressBar.setIndeterminate(true);
        progressContainer.addView(progressBar);
        listView.setEmptyView(progressContainer);
        
        // Must add the progress bar to the root of the layout
        ViewGroup root = (ViewGroup) findViewById(android.R.id.content);
        progressContainer.setGravity(Gravity.CENTER);
        root.addView(progressContainer);
        
        listView.setAdapter(new StreamsListAdapter(streams));
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override 
            public void onItemClick(AdapterView<?> arg0, View arg1,int position, long arg3)
            { 
            	StreamData streamData = (StreamData) listView.getAdapter().getItem(position);
                Intent intent = new Intent(StreamsListActivity.this, PlayStreamActivity.class);
                intent.putExtra(PlayStreamActivity.INTENT_STREAM_ID, streamData.getStreamId());
                StreamsListActivity.this.startActivity(intent);
            }
		});
		Nine00SecondsSDK.fetchRecentStreams(new Nine00SecondsSDK.RequestStreamsListCallback() {
			
			@Override
			public void onFault(RequestErrorType error) {
			}
			
			@Override
			public void onComplete(final StreamsListData streams) {
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						StreamsListActivity.this.streams.clear();
						StreamsListActivity.this.streams.addAll(streams.getStreams());
						((BaseAdapter) listView.getAdapter()).notifyDataSetChanged(); 
					}
				});
			}
		}, false);
	}
	
	private class StreamsListAdapter extends ArrayAdapter<StreamData> {

		public StreamsListAdapter(ArrayList<StreamData> streams) {
			super(StreamsListActivity.this, 0, streams);
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View cell = convertView;
			if (cell == null) {
				cell = LayoutInflater.from(StreamsListActivity.this).inflate(android.R.layout.simple_list_item_2, parent, false);
			}
			TextView text1 = (TextView) cell.findViewById(android.R.id.text1);
			TextView text2 = (TextView) cell.findViewById(android.R.id.text2);
			StreamData streamData = getItem(position);
			Date date;
			date = streamData.getCreatedAt();
			String dateText = DateUtils.getRelativeDateTimeString(StreamsListActivity.this, date.getTime(), 1, 3,0).toString();
			if (streamData.isLive())
				dateText += " (LIVE)";
			text1.setText(dateText);
			text2.setText(streamData.getStreamId());
			return cell;
		}

	}
}
