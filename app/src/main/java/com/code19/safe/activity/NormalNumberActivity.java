package com.code19.safe.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.TextView;

import com.code19.safe.R;
import com.code19.safe.db.CommonNumberDao;

/**
 * Created by Gh0st on 2015/9/7.
 * 19:25
 */
public class NormalNumberActivity extends Activity {

    private static final String TAG = "NormalNumberActivity";
    private ExpandableListView mListview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_normalnumber);
        mListview = (ExpandableListView) findViewById(R.id.nn_listview);
        ExpandableListAdapter adapter = new NormalNumberAdapter();
        mListview.setAdapter(adapter);
        mListview.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
            @Override
            public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
               /* Log.i(TAG, "点击了listview" + groupPosition);
                String groupTitle = CommonNumberDao.getGroupTitle(NormalNumberActivity.this, groupPosition);
                Log.i(TAG, "点击了listview" + groupPosition + ":" + groupTitle);*/
                //设置点击效果
                //当没有任何条目打开时，点击某个条目就打开某个条目
                //当有条目打开时，点击的也是此条目就，关闭这个条目，如果不是点击的不是这个条目，就先关闭已经打开的条目并打开点击的条目
                return false; //返回值，是否要去拦截grop的点击
            }
        });

    }

    private class NormalNumberAdapter extends BaseExpandableListAdapter {


        @Override
        public int getGroupCount() {
            return CommonNumberDao.getGroupCount(NormalNumberActivity.this);
        }

        @Override
        public int getChildrenCount(int groupPosition) {
            return CommonNumberDao.getChildCount(NormalNumberActivity.this, groupPosition);
        }

        @Override
        public Object getGroup(int groupPosition) {

            return null;
        }

        @Override
        public Object getChild(int groupPosition, int childPosition) {
            return null;
        }

        @Override
        public long getGroupId(int groupPosition) {
            return 0;
        }

        @Override
        public long getChildId(int groupPosition, int childPosition) {
            return 0;
        }

        @Override
        public boolean hasStableIds() {
            return false;
        }

        @Override
        public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
            GroupViewHolder gHolder = null;
            if (convertView == null) {
                convertView = new TextView(NormalNumberActivity.this);
                gHolder = new GroupViewHolder();
                gHolder.mGroupTextView = (TextView) convertView;
                //在这儿给父级item设置样式
                convertView.setTag(gHolder);
            } else {
                gHolder = (GroupViewHolder) convertView.getTag();
            }
            //给view设置数据
            gHolder.mGroupTextView.setText(CommonNumberDao.getGroupTitle(NormalNumberActivity.this, groupPosition));
            return convertView;
        }

        @Override
        public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
            ChildViewHolder cHolder = null;
            if (convertView == null) {
                convertView = new TextView(NormalNumberActivity.this);
                cHolder = new ChildViewHolder();
                cHolder.mChiledTextView = (TextView) convertView;
                //给孩子item设置样式
                convertView.setTag(cHolder);
            } else {
                cHolder = (ChildViewHolder) convertView.getTag();
            }
            cHolder.mChiledTextView.setText(CommonNumberDao.getChildContent(NormalNumberActivity.this, groupPosition, childPosition));
            return convertView;
        }

        @Override
        public boolean isChildSelectable(int groupPosition, int childPosition) {
            return true; //设置孩子item可以点击
        }
    }

    private static class GroupViewHolder {
        TextView mGroupTextView;
    }

    private static class ChildViewHolder {
        TextView mChiledTextView;
    }
}
