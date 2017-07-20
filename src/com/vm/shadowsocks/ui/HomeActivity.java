package com.vm.shadowsocks.ui;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.Toast;

import com.vm.shadowsocks.R;
import com.vm.shadowsocks.core.LocalVpnService;
import com.vm.shadowsocks.core.ProxyConfig;
import com.vm.shadowsocks.tunnel.ServerInfo;
import com.vm.shadowsocks.tunnel.TunnelMethod;
import com.vm.shadowsocks.ui.adapters.ServerAdapater;

import java.util.Calendar;

public class HomeActivity extends Activity implements LocalVpnService.onStatusChangedListener {
    ListView mListView;
    Switch mSwitch;
    Button mButton;
    Calendar mCalendar;
    ServerInfo mServerInfo;
    private ServerAdapater mServerAdapater;
    private static final int START_VPN_SERVICE_REQUEST_CODE = 1985;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        mListView = $(R.id.listview);
        mSwitch = $(R.id.turn_gloal);
        mButton = $(R.id.btn_connect);
        mCalendar = Calendar.getInstance();
        ServerInfo serverInfo = new ServerInfo();
        serverInfo.name = "测试服务器";
        serverInfo.host = "107.191.52.210";
        serverInfo.password = "549322751";
        serverInfo.port = 104;
        serverInfo.method = TunnelMethod.aes_128_cfb;
        mServerAdapater = new ServerAdapater(this);
        mServerAdapater.add(serverInfo);
        mListView.setAdapter(mServerAdapater);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mServerInfo = mServerAdapater.getItem(position);
                mButton.setEnabled(true);
                setTitle(mServerInfo.name);
            }
        });
        LocalVpnService.addOnStatusChangedListener(this);
        mButton.setEnabled(LocalVpnService.IsRunning || false);
        mSwitch.setChecked(ProxyConfig.Instance.globalMode);
        mSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                ProxyConfig.Instance.globalMode = !ProxyConfig.Instance.globalMode;
                if (ProxyConfig.Instance.globalMode) {
                    onLogReceived("Proxy global mode is on");
                } else {
                    onLogReceived("Proxy global mode is off");
                }
            }
        });
        updateUI(LocalVpnService.IsRunning);
        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!LocalVpnService.IsRunning) {
//                    LocalVpnService.IsRunning = true;
                    updateUI(true);
                    Intent intent = LocalVpnService.prepare(HomeActivity.this);
                    if (intent == null) {
                        startVPNService(mServerInfo);
                    } else {
                        startActivityForResult(intent, START_VPN_SERVICE_REQUEST_CODE);
                    }
                } else {
                    updateUI(false);
                    LocalVpnService.IsRunning = false;
                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        LocalVpnService.removeOnStatusChangedListener(this);
        super.onDestroy();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if (requestCode == START_VPN_SERVICE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                startVPNService(mServerInfo);
            } else {
                updateUI(LocalVpnService.IsRunning);
                onLogReceived("canceled.");
            }
            return;
        }
        super.onActivityResult(requestCode, resultCode, intent);
    }

    private void updateUI(final boolean running) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (running) {
                    mButton.setText("断开");
                } else {
                    mButton.setText("连接");
                }
            }
        });
    }

    @Override
    public void onStatusChanged(String status, Boolean isRunning) {
        updateUI(isRunning);
        onLogReceived(status);
        Toast.makeText(this, status, Toast.LENGTH_SHORT).show();
    }

    @SuppressLint("DefaultLocale")
    @Override
    public void onLogReceived(String logString) {
        mCalendar.setTimeInMillis(System.currentTimeMillis());
        logString = String.format("[%1$02d:%2$02d:%3$02d] %4$s\n",
                mCalendar.get(Calendar.HOUR_OF_DAY),
                mCalendar.get(Calendar.MINUTE),
                mCalendar.get(Calendar.SECOND),
                logString);

        System.out.println(logString);

        //if (textViewLog.getLineCount() > 200) {
        // textViewLog.setText("");
        //}
        //textViewLog.append(logString);
        //scrollViewLog.fullScroll(ScrollView.FOCUS_DOWN);
        //GL_HISTORY_LOGS = textViewLog.getText() == null ? "" : textViewLog.getText().toString();
    }

    private void startVPNService(ServerInfo serverInfo) {
        if (serverInfo == null) {
            Toast.makeText(this, "没有选择服务器", Toast.LENGTH_SHORT).show();
            return;
        }
        //String ProxyUrl = readProxyUrl();
        String ProxyUrl = serverInfo.getProxyUrl();
        if (!ServerInfo.isValidUrl(ProxyUrl)) {
            Toast.makeText(this, R.string.err_invalid_url, Toast.LENGTH_SHORT).show();
            updateUI(false);
            return;
        }
        //textViewLog.setText("");
        onLogReceived("starting...");
        LocalVpnService.ProxyUrl = ProxyUrl;
        startService(new Intent(this, LocalVpnService.class));
    }

    private <T> T $(int id) {
        return (T) findViewById(id);
    }
}
