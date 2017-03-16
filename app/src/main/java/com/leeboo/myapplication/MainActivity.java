package com.leeboo.myapplication;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.alipay.sdk.app.EnvUtils;
import com.alipay.sdk.app.PayTask;

import java.util.Map;


public class MainActivity extends AppCompatActivity {
    EditText editText;
    String orderInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        editText = (EditText) findViewById(R.id.et);
        TextView pay = (TextView) findViewById(R.id.pay);
        pay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                orderInfo = editText.getText().toString().trim();
                if (!TextUtils.isEmpty(orderInfo)) {
                    Runnable payRunnable = new Runnable() {
                        @Override
                        public void run() {
                            EnvUtils.setEnv(EnvUtils.EnvEnum.SANDBOX);
                            PayTask payTask = new PayTask(MainActivity.this);
                            Map<String, String> result = payTask.payV2(orderInfo, true);
                            Message msg = new Message();
                            msg.obj = result;
                            mHandler.sendMessage(msg);
                        }
                    };
                    // 必须异步调用
                    Thread payThread = new Thread(payRunnable);
                    payThread.start();
                }
            }
        });

    }

    private Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            PayResult payResult = new PayResult((Map<String, String>) msg.obj);
            Toast.makeText(MainActivity.this, payResult.getResult(),
                    Toast.LENGTH_LONG).show();
            Log.d("===", "" + payResult.getResult());
        }
    };
}
