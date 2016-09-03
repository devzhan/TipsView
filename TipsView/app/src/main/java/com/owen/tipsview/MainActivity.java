package com.owen.tipsview;

import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.owen.tipsview.view.ToolTipManage;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private  Button bt1;
    private  Button bt2;
    private  Button bt3;
    private  Button bt4;
    private  Button bt5;
    private ToolTipManage mToolTipManage;
    private ArrayList<String> resourcesIds=new ArrayList<>();
    private ArrayList<View> views=new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        //延时1s等待控件绘制
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
               showTips();
            }
        }, 1000);

    }



    private void showTips() {
        mToolTipManage.showGuide(this,views,resourcesIds);
    }

    private void initView() {
        mToolTipManage=ToolTipManage.getInstance(this);
         bt1= (Button) findViewById(R.id.bt1);
         bt2= (Button) findViewById(R.id.bt2);
         bt3= (Button) findViewById(R.id.bt3);
         bt4= (Button) findViewById(R.id.bt4);
         bt5= (Button) findViewById(R.id.bt5);
        views.add(bt1);
        views.add(bt2);
        views.add(bt3);
        views.add(bt4);
        views.add(bt5);
        resourcesIds.add("bt_main");
        resourcesIds.add("bt_message");
        resourcesIds.add("bt_built");
        resourcesIds.add("bt_contact");
        resourcesIds.add("bt_personal");
    }
}
