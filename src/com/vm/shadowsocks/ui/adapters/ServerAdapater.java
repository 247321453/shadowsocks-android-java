package com.vm.shadowsocks.ui.adapters;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.vm.shadowsocks.tunnel.ServerInfo;

public class ServerAdapater extends BaseAdapterPlus<ServerInfo> {
    public ServerAdapater(Context context) {
        super(context);
    }

    @Override
    protected View createView(int position, ViewGroup parent) {
        View view = inflate(android.R.layout.simple_list_item_1, null);
        return view;
    }

    @Override
    protected void attach(View view, ServerInfo item, int position) {
        TextView textView = (TextView) view;
        textView.setText(item.name);
    }
}
