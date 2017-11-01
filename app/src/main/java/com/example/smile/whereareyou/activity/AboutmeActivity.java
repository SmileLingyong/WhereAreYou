package com.example.smile.whereareyou.activity;

import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.smile.whereareyou.R;

/**
 * Created by lly54 on 2017/10/31.
 */

public class AboutmeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_aboutme);

        ImageView userHead = (ImageView) findViewById(R.id.user_head);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        CollapsingToolbarLayout collapsingToolbar = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        TextView tvThanks = (TextView) findViewById(R.id.tv_thanks);
        TextView tvBlog = (TextView) findViewById(R.id.tv_blog);

        collapsingToolbar.setTitle("WhereAreYou");
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();    //设置默认返回上一级按钮
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    //添加返回按钮点击事件
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
