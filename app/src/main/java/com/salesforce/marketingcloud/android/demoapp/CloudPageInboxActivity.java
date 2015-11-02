package com.salesforce.marketingcloud.android.demoapp;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.exacttarget.etpushsdk.adapter.CloudPageListAdapter;
import com.exacttarget.etpushsdk.data.Message;

public class CloudPageInboxActivity extends BaseActivity {
    private MyCloudPageListAdapter cloudPageListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.cloudpage_inbox_layout);

        prepareDisplay();
    }

    private RadioGroup.OnCheckedChangeListener radioChangedListener = new RadioGroup.OnCheckedChangeListener() {

        @Override
        public void onCheckedChanged(RadioGroup group, int checkedId) {
            if (R.id.filterAll == checkedId) {
                cloudPageListAdapter.setDisplay(CloudPageListAdapter.DISPLAY_ALL);
            } else if (R.id.filterRead == checkedId) {
                cloudPageListAdapter.setDisplay(CloudPageListAdapter.DISPLAY_READ);
            } else if (R.id.filterUnread == checkedId) {
                cloudPageListAdapter.setDisplay(CloudPageListAdapter.DISPLAY_UNREAD);
            }
        }
    };

    private AdapterView.OnItemLongClickListener cloudPageItemDeleteListener = new AdapterView.OnItemLongClickListener() {

        @Override
        public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
            CloudPageListAdapter adapter = (CloudPageListAdapter) parent.getAdapter();
            Message message = (Message) adapter.getItem(position);

            adapter.deleteMessage(message);

            return true;
        }
    };

    private AdapterView.OnItemClickListener cloudPageItemClickListener = new AdapterView.OnItemClickListener() {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            CloudPageListAdapter adapter = (CloudPageListAdapter) parent.getAdapter();
            Message message = (Message) adapter.getItem(position);

            adapter.setMessageRead(message);

            Intent intent = new Intent(CloudPageInboxActivity.this, CloudPageActivity.class);
            intent.putExtra("_x", message.getUrl());
            startActivity(intent);
        }
    };

    private void prepareDisplay() {
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        RadioGroup filterRadioGroup;
        ListView cloudPageListView;

        filterRadioGroup = (RadioGroup) findViewById(R.id.filterRadioGroup);
        cloudPageListView = (ListView) findViewById(R.id.cloudPageListView);

        filterRadioGroup.setOnCheckedChangeListener(radioChangedListener);

        cloudPageListView.setOnItemClickListener(cloudPageItemClickListener);
        cloudPageListView.setOnItemLongClickListener(cloudPageItemDeleteListener);

        cloudPageListAdapter = new MyCloudPageListAdapter(getApplicationContext());
        cloudPageListView.setAdapter(cloudPageListAdapter);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    private class MyCloudPageListAdapter extends CloudPageListAdapter {

        public MyCloudPageListAdapter(Context appContext) {
            super(appContext);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view;
            ImageView icon;
            TextView subject;
            TextView time;

            LayoutInflater mInflater = (LayoutInflater) CloudPageInboxActivity.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            if (convertView == null) {
                view = mInflater.inflate(R.layout.cloudpage_list_item, parent, false);
            } else {
                view = convertView;
            }

            subject = (TextView) view.findViewById(R.id.cloudpageSubject);
            icon = (ImageView) view.findViewById(R.id.readUnreadIcon);
            time = (TextView) view.findViewById(R.id.timeTextView);

            Message message = (Message) getItem(position);

            if (message.getRead()) {
                icon.setImageResource(R.drawable.read);
            } else {
                icon.setImageResource(R.drawable.unread);
            }

            subject.setText(message.getSubject());

            time.setText(android.text.format.DateFormat.format("MMM dd yyyy - hh:mm a", message.getStartDate()));

            return view;
        }
    }
}
