package com.openthos.greenify.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.openthos.greenify.R;
import com.openthos.greenify.entity.AppInfo;
import com.openthos.greenify.listener.OnListClickListener;

import java.util.List;

public class AppListAdapter extends BasicAdapter {
    private List<AppInfo> mDatas;
    private OnListClickListener mOnListClickListener;

    public AppListAdapter(Context context, List<AppInfo> datas) {
        super(context);
        mDatas = datas;
    }

    @Override
    public int getCount() {
        return mDatas != null ? mDatas.size() : 0;
    }

    @Override
    public Object getItem(int position) {
        return mDatas != null ? mDatas.get(position) : null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.app_list_item, parent, false);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        AppInfo appInfo = mDatas.get(position);
        holder.icon.setImageDrawable(appInfo.getIcon());
        holder.name.setText(appInfo.getAppName());
        if (appInfo.isPush() && !appInfo.isAdd()) {
            holder.push.setVisibility(View.VISIBLE);
            holder.push.setImageDrawable(mContext.getResources().getDrawable(R.mipmap.ic_tip_gcm));
        } else {
            holder.push.setVisibility(View.GONE);
        }
        holder.layout.setTag(appInfo.getPackageName());
        return convertView;
    }

    @Override
    public void refresh() {
        notifyDataSetChanged();
    }

    public void setOnListClickListener(OnListClickListener onListClickListener) {
        mOnListClickListener = onListClickListener;
    }

    private class ViewHolder implements View.OnClickListener {
        private LinearLayout layout;
        private ImageView icon;
        private TextView name;
        private ImageView push;

        public ViewHolder(View view) {
            layout = (LinearLayout) view.findViewById(R.id.layout);
            icon = (ImageView) view.findViewById(R.id.icon);
            name = (TextView) view.findViewById(R.id.name);
            push = (ImageView) view.findViewById(R.id.push);
            layout.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (mOnListClickListener != null) {
                mOnListClickListener.onListClickListener(view, (String) view.getTag());
            }
        }
    }
}
