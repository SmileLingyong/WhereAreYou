package com.example.smile.whereareyou;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Color;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    private Toolbar toolbar;
    private FloatingActionButton addButton;
    private DrawerLayout mDrawerLayout;

    //结束启动界面,转换到主页
    public static void start(Activity activity) {
        activity.startActivity(new Intent(activity, MainActivity.class));
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
    }

    public void initView() {

        toolbar = (Toolbar) findViewById(R.id.toolbar_main);
        toolbar.setTitle("WhereAreYou");
        toolbar.setTitleTextColor(Color.parseColor("#ffffff"));
        setSupportActionBar(toolbar);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        NavigationView navView = (NavigationView) findViewById(R.id.nav_view);

        //导航栏中加载打开抽屉栏图标
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.ic_menu);
        }

        //设置点击头像事件
        //这样做就相当于在navView上又添加了一个header layout布局，所以这样写的话，我们需要在布局文件中把
        //app:headerLayout="@layout/nav_header"去掉
        View navHeaderView = navView.inflateHeaderView(R.layout.nav_header);
        ImageView headLogin = (ImageView) navHeaderView.findViewById(R.id.head_login_image);
        headLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent intent_login = new Intent(MainActivity.this, LoginActivity.class);
//                startActivity(intent_login);
            }
        });

        navView.setCheckedItem(R.id.nav_home);
        navView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.nav_home:
                        mDrawerLayout.closeDrawers();
                        break;
                    case R.id.nav_news:
                        mDrawerLayout.closeDrawers();
//                        Intent intent_news = new Intent(MainActivity.this, NewsActivity.class);
//                        startActivity(intent_news);
                        break;
                    case R.id.nav_theme:
                        ChangeTheme();
                        break;
                    case R.id.nav_aboutme:
//                        Intent intent_aboutme = new Intent(MainActivity.this, AboutmeActivity.class);
//                        startActivity(intent_aboutme);
                        mDrawerLayout.closeDrawers();
                        break;
                    case R.id.nav_setting:
//                        backUpFunction();
//
//
                        mDrawerLayout.closeDrawers();
                        break;
                    default:
                        mDrawerLayout.closeDrawers();
                        break;
                }

                return true;
            }
        });

        addButton = (FloatingActionButton) findViewById(R.id.add_button);
        addButton.setOnClickListener(this);




    }


    //导航栏上抽屉栏按钮点击事件
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                mDrawerLayout.openDrawer(GravityCompat.START);
                break;
        }
        return true;
    }

    // 通过使用获取系统当前主题样式进行判断，若当前为夜间模式；
    // 则使用SharePreferences设置 theme 值为 0,并将当前主题设置为 白天模式。
    // 白天模式即通过同样的方式设置
    public void ChangeTheme() {
        SharedPreferences sp = getSharedPreferences("user_settings", MODE_PRIVATE);
        if ((getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK)
                == Configuration.UI_MODE_NIGHT_YES) {
            sp.edit().putInt("theme", 0).apply();
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        } else {
            sp.edit().putInt("theme", 1).apply();
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        }
        // 设置更换主题的动画效果，并重绘当前屏幕
        getWindow().setWindowAnimations(R.style.WindowAnimationFadeInOut);
        recreate();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.add_button:
//                Intent intent = new Intent(MainActivity.this, NoteEditActivity.class);
//                startActivity(intent);
                break;
        }
    }
}
