package com.example.smile.whereareyou.activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.smile.whereareyou.R;
import com.example.smile.whereareyou.activity.MainActivity;
import com.example.smile.whereareyou.activity.RegisterActivity;
import com.example.smile.whereareyou.db.User;
import com.example.smile.whereareyou.util.HttpUtil;
import com.example.smile.whereareyou.util.Utility;
import com.google.gson.Gson;

import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by lly54 on 2017/10/12.
 */

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private UserLoginTask mAuthTask = null;

    private ProgressBar mLoginProgress;
    private ImageView logo;
    private ScrollView scrollView;
    private EditText et_number;
    private EditText et_password;
    private ImageView iv_clean_number;
    private ImageView iv_clean_password;
    private ImageView iv_show_password;
    private Button btn_login;
    private TextView text_registered;
    private TextView text_forget_password;
    private int screenHeight = 0;   //屏幕高度
    private int keyHeight = 0;      //软键盘弹起后所占高度
    private float scale = 0.8f;     //logo缩放比例

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //设置输入法不弹出
        setContentView(R.layout.activity_login);
        AndroidBug5497Workaround.assistActivity(this);

        initView();
        initListener();
    }

    private void initView() {
        mLoginProgress = (ProgressBar) findViewById(R.id.login_progress);
        logo = (ImageView) findViewById(R.id.login_logo);
        scrollView = (ScrollView) findViewById(R.id.scrollView);
        et_number = (EditText) findViewById(R.id.et_number);
        et_password = (EditText) findViewById(R.id.et_password);
        iv_clean_number = (ImageView) findViewById(R.id.iv_clean_number);
        iv_clean_password = (ImageView) findViewById(R.id.iv_clean_password);
        iv_show_password = (ImageView) findViewById(R.id.iv_show_pwd);
        btn_login = (Button) findViewById(R.id.btn_login);
        text_registered = (TextView) findViewById(R.id.text_registered);
        text_forget_password = (TextView) findViewById(R.id.text_forget_password);

        //获取屏幕高度
        screenHeight = this.getResources().getDisplayMetrics().heightPixels;
        keyHeight = screenHeight / 3;   //键盘弹起高度为屏幕的1/3

        //设置全屏
        if (isFullScreen(this)) {
            AndroidBug5497Workaround.assistActivity(this);
        }

    }

    public boolean isFullScreen(Activity activity) {
        return (activity.getWindow().getAttributes().flags &
                WindowManager.LayoutParams.FLAG_FULLSCREEN) == WindowManager.LayoutParams.FLAG_FULLSCREEN;
    }


    private void initListener() {
        iv_clean_number.setOnClickListener(this);
        iv_clean_password.setOnClickListener(this);
        iv_show_password.setOnClickListener(this);
        btn_login.setOnClickListener(this);
        text_registered.setOnClickListener(this);
        text_forget_password.setOnClickListener(this);


        //设置输入账号监听事件
        et_number.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!TextUtils.isEmpty(s) && iv_clean_number.getVisibility() == View.GONE) {
                    iv_clean_number.setVisibility(View.VISIBLE);
                } else if (TextUtils.isEmpty(s)) {
                    iv_clean_number.setVisibility(View.GONE);
                }
            }
        });


        //设置输入密码监听事件
        et_password.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!TextUtils.isEmpty(s) && iv_clean_password.getVisibility() == View.GONE) {
                    iv_clean_password.setVisibility(View.VISIBLE);
                } else if (TextUtils.isEmpty(s)) {
                    iv_clean_password.setVisibility(View.GONE);
                }
                if (s.toString().isEmpty())
                    return;
                if (!s.toString().matches("[A-Za-z0-9]+")) {
                    String temp = s.toString();
                    Toast.makeText(LoginActivity.this, "请输入数字或字母", Toast.LENGTH_SHORT).show();
                    s.delete(temp.length() - 1, temp.length());
                    et_password.setSelection(s.length());
                }
            }
        });

        scrollView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });

        findViewById(R.id.root).addOnLayoutChangeListener(new ViewGroup.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
              /* old是改变前的左上右下坐标点值，没有old的是改变后的左上右下坐标点值
              现在认为只要控件将Activity向上推的高度超过了1/3屏幕高，就认为软键盘弹起*/
                if (oldBottom != 0 && bottom != 0 && (oldBottom - bottom > keyHeight)) {
                    Log.e("wenzhihao", "up------>" + (oldBottom - bottom));
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            scrollView.smoothScrollTo(0, scrollView.getHeight());
                        }
                    }, 0);
                    zoomIn(logo, (oldBottom - bottom) - keyHeight);
                } else if (oldBottom != 0 && bottom != 0 && (bottom - oldBottom > keyHeight)) {
                    Log.e("wenzhihao", "down------>" + (bottom - oldBottom));
                    //键盘收回后，logo恢复原来大小，位置同样回到初始位置
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            scrollView.smoothScrollTo(0, scrollView.getHeight());
                        }
                    }, 0);
                    zoomOut(logo, (bottom - oldBottom) - keyHeight);
                }
            }
        });
    }

    /**
     * 缩小
     *
     * @param view
     */
    public void zoomIn(final View view, float dist) {
        view.setPivotY(view.getHeight());
        view.setPivotX(view.getWidth() / 2);
        AnimatorSet mAnimatorSet = new AnimatorSet();
        ObjectAnimator mAnimatorScaleX = ObjectAnimator.ofFloat(view, "scaleX", 1.0f, scale);
        ObjectAnimator mAnimatorScaleY = ObjectAnimator.ofFloat(view, "scaleY", 1.0f, scale);
        ObjectAnimator mAnimatorTranslateY = ObjectAnimator.ofFloat(view, "translationY", 0.0f, -dist);

        mAnimatorSet.play(mAnimatorTranslateY).with(mAnimatorScaleX);
        mAnimatorSet.play(mAnimatorScaleX).with(mAnimatorScaleY);
        mAnimatorSet.setDuration(200);
        mAnimatorSet.start();
    }

    /**
     * f放大
     *
     * @param view
     */
    public void zoomOut(final View view, float dist) {
        view.setPivotY(view.getHeight());
        view.setPivotX(view.getWidth() / 2);
        AnimatorSet mAnimatorSet = new AnimatorSet();

        ObjectAnimator mAnimatorScaleX = ObjectAnimator.ofFloat(view, "scaleX", scale, 1.0f);
        ObjectAnimator mAnimatorScaleY = ObjectAnimator.ofFloat(view, "scaleY", scale, 1.0f);
        ObjectAnimator mAnimatorTranslateY = ObjectAnimator.ofFloat(view, "translationY", view.getTranslationY(), 0);

        mAnimatorSet.play(mAnimatorTranslateY).with(mAnimatorScaleX);
        mAnimatorSet.play(mAnimatorScaleX).with(mAnimatorScaleY);
        mAnimatorSet.setDuration(200);
        mAnimatorSet.start();
    }


    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.iv_clean_number:
                et_number.setText("");
                break;
            case R.id.iv_clean_password:
                et_password.setText("");
                break;
            case R.id.iv_show_pwd:
                if (et_password.getInputType() != InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD) {
                    et_password.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                    iv_show_password.setImageResource(R.drawable.login_pass_visuable);
                } else {
                    et_password.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    iv_show_password.setImageResource(R.drawable.login_pass_gone);
                }
                String pwd = et_password.getText().toString();
                if (!TextUtils.isEmpty(pwd))
                    et_password.setSelection(pwd.length());
                break;
            case R.id.btn_login:
                // 尝试登录
                attemptLogin();
                break;
            case R.id.text_registered:
//                Toast.makeText(this, "注册新用户", Toast.LENGTH_SHORT).show();
                Intent createAccount = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(createAccount);
                break;
            case R.id.text_forget_password:
                Toast.makeText(this, "暂不支持找回密码", Toast.LENGTH_SHORT).show();
                break;

            default:
                break;
        }

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Toast.makeText(this, "取消登录！", Toast.LENGTH_SHORT).show();
        finish();
    }

    /**
     * 进行登录操作
     */
    private void attemptLogin() {
        if (mAuthTask != null) {
            return;
        }

        // 重置错误提示
        et_number.setError(null);
        et_password.setError(null);

        // 获取登录信息
        String email = et_number.getText().toString();
        String password = et_password.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // 检查密码填写是否符合规则
        if (TextUtils.isEmpty(password)) {
            et_password.setError(getString(R.string.error_field_required));
        } else if (!isPasswordValid(password)) {
            et_password.setError(getString(R.string.error_invalid_password));
            focusView = et_password;
            cancel = true;
        }

        if (TextUtils.isEmpty(email)) {
            et_number.setError(getString(R.string.error_field_required));
            focusView = et_number;
            cancel = true;
        } else if (!isEmailValid(email)) {
            et_number.setError(getString(R.string.error_invalid_email));
            focusView = et_number;
            cancel = true;
        }

        if (cancel) {
            focusView.requestFocus();
        } else {
            showProgress(true);
            mAuthTask = new UserLoginTask(email, password);
            mAuthTask.execute((Void) null);
//            Toast.makeText(this, "登录成功", Toast.LENGTH_SHORT).show();
        }

    }

    private boolean isEmailValid(String email) {
        if (email == null || email.equals("")) return false;
        Pattern p = Pattern.compile("\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*");
        Matcher m = p.matcher(email);
        return m.matches();
        // 简单匹配是否包含 "@"
        //return email.contains("@");
    }

    private boolean isPasswordValid(String password) {
        return password.length() >= 4;
    }

    /**
     * 显示loading进度条
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

        scrollView.setVisibility(show ? View.GONE : View.VISIBLE);
        scrollView.animate().setDuration(shortAnimTime).alpha(
                show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationCancel(Animator animation) {
                scrollView.setVisibility(show ? View.GONE : View.VISIBLE);
            }
        });

        mLoginProgress.setVisibility(show ? View.VISIBLE : View.GONE);
        mLoginProgress.animate().setDuration(shortAnimTime).alpha(
                show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationCancel(Animator animation) {
                mLoginProgress.setVisibility(show ? View.VISIBLE : View.GONE);
            }
        });
    }

    private void refresh() {
        finish();
        Intent backLoginActivity = new Intent(LoginActivity.this, LoginActivity.class);
        startActivity(backLoginActivity);
    }

    /**
     * 异步登录操作
     */
    public class UserLoginTask extends AsyncTask<Void, Void, Boolean> {

        private final String mEmail;
        private final String mPassword;

        UserLoginTask(String email, String password) {
            mEmail = email;
            mPassword = password;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            final String loginUrl = "http://47.93.20.40:8080/map-track/login";
            final RequestBody requestBody = new FormBody.Builder()
                    .add("username", mEmail)
                    .add("password", mPassword)
                    .build();
            HttpUtil.sendOkHttpRequest(loginUrl, requestBody, new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    e.printStackTrace();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            // 网络不通引起的失败
                            Toast.makeText(LoginActivity.this, "请检查你的网络", Toast.LENGTH_SHORT).show();
                            refresh();
                        }
                    });
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    JSONObject loginResponse = Utility.parseResponse(response);
                    final String msg = loginResponse.optString("msg");
                    final String status = loginResponse.optString("status");
//                    Log.d("msg", msg);
//                    Log.d("status", status);
                    // if status == 0 ,登录成功，if status == -1，登录失败
                    if (0 == Integer.parseInt(status)) {
                        LoginActivity.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(LoginActivity.this, msg, Toast.LENGTH_SHORT).show();
                            }
                        });
                        // 跳转到 Main Activity 并且更新信息
                        Intent goMainActivity = new Intent(LoginActivity.this, MainActivity.class);
                        startActivity(goMainActivity);
                        // 更新本地的SharedPreference信息
                        SharedPreferences sharedPreferences = getSharedPreferences("userInfo", Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString("username", mEmail);
                        editor.putString("password", mPassword);
                        editor.apply();
                    } else if (-1 == Integer.parseInt(status)) {
                        LoginActivity.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(LoginActivity.this, msg, Toast.LENGTH_SHORT).show();
                                refresh();
                            }
                        });
                    }
                }
            });
            return true;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            mAuthTask = null;

            super.onPostExecute(aBoolean);
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
        }
    }
}
