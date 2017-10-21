package com.example.smile.whereareyou.activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.app.LoaderManager.LoaderCallbacks;

import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;

import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.example.smile.whereareyou.R;
import com.example.smile.whereareyou.util.HttpUtil;
import com.example.smile.whereareyou.util.Utility;

import org.json.JSONObject;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.RequestBody;
import okhttp3.Response;

import static android.Manifest.permission.READ_CONTACTS;

public class RegisterActivity extends AppCompatActivity {

    /**
     * 追踪登录活动，确保我们能取消
     */
    private UserRegisterTask mAuthTask = null;

    private EditText mEmailView;
    private EditText mPasswordView;
    private EditText mUsernameView;
    private View mProgressView;
    private View mLoginFormView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mEmailView = (EditText) findViewById(R.id.email);
        mPasswordView = (EditText) findViewById(R.id.password);
        mUsernameView = (EditText) findViewById(R.id.username);

        Button mEmailSignInButton = (Button) findViewById(R.id.email_sign_in_button);
        mEmailSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptRegister();
            }
        });

        mLoginFormView = findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.login_progress);
    }


    /**
     * 尝试进行账号注册，如果有错误会提示
     */
    private void attemptRegister() {
        if (mAuthTask != null) {
            return;
        }

        // 提交前重置错误信息
        mEmailView.setError(null);
        mPasswordView.setError(null);
        mUsernameView.setError(null);

        // 在尝试注册的时候保存email和pwd信息
        String email = mEmailView.getText().toString();
        String password = mPasswordView.getText().toString();
        String username = mUsernameView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // 检查密码填写是否符合规则
        if (TextUtils.isEmpty(password)) {
            mPasswordView.setError(getString(R.string.error_field_required));
        } else if (!isPasswordValid(password)) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
        }

        // 检查email是否合理
        if (TextUtils.isEmpty(email)) {
            mEmailView.setError(getString(R.string.error_field_required));
            focusView = mEmailView;
            cancel = true;
        } else if (!isEmailValid(email)) {
            mEmailView.setError(getString(R.string.error_invalid_email));
            focusView = mEmailView;
            cancel = true;
        }

        if (cancel) {
            // 如果表单有错误，则无法提交，并且设置错误提示为焦点
            focusView.requestFocus();
        } else {
            // 显示一个进度条，并且在运行一个后台登录任务
            showProgress(true);
            mAuthTask = new UserRegisterTask(email, password, username);
            mAuthTask.execute((Void) null);
            Toast.makeText(this, "注册成功", Toast.LENGTH_SHORT).show();
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
     * 显示loading进度条，隐藏注册界面
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // 利用Honeycomb MR2实现一些简单的动画效果
        int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

        mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        mLoginFormView.animate().setDuration(shortAnimTime).alpha(
                show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            }
        });

        mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
        mProgressView.animate().setDuration(shortAnimTime).alpha(
                show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            }
        });
    }

    private void refresh() {
        finish();
        Intent backLoginActivity = new Intent(RegisterActivity.this, RegisterActivity.class);
        startActivity(backLoginActivity);
    }

    /**
     * 异步注册Task
     */
    public class UserRegisterTask extends AsyncTask<Void, Void, Boolean> {

        private final String mEmail;
        private final String mPassword;
        private final String mUsername;

        UserRegisterTask(String email, String password, String username) {
            mEmail = email;
            mPassword = password;
            mUsername = username;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            String registerUrl = "http://47.93.20.40:8080/map-track/registe";
            final RequestBody requestBody = new FormBody.Builder()
                    .add("username", mEmail)
                    .add("password", mPassword)
                    .add("name", mUsername)
                    .build();
            HttpUtil.sendOkHttpRequest(registerUrl, requestBody, new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    // 网络不通引起的失败
                    Toast.makeText(RegisterActivity.this, "请检查你的网络", Toast.LENGTH_SHORT).show();
                    refresh();
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    JSONObject loginResponse = Utility.parseResponse(response);
                    final String msg = loginResponse.optString("msg");
                    final String status = loginResponse.optString("status");
//                    Log.d("msg", msg);
//                    Log.d("status", status);
                    // if status == 0 ,注册成功，if status == -1，注册失败
                    if (0 == Integer.parseInt(status)) {
                        RegisterActivity.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(RegisterActivity.this, msg, Toast.LENGTH_SHORT).show();
                            }
                        });
                        // 跳转到 Main Activity 并且更新信息
                        Intent goLoginActivity = new Intent(RegisterActivity.this, LoginActivity.class);
                        startActivity(goLoginActivity);
                    } else if (-1 == Integer.parseInt(status)) {
                        RegisterActivity.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(RegisterActivity.this, msg, Toast.LENGTH_SHORT).show();
                                refresh();
                            }
                        });
                    }
                }
            });
            return true;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            mAuthTask = null;
            showProgress(false);

            if (success) {
                finish();
            } else {
                mPasswordView.setError(getString(R.string.error_register_failed));
                mPasswordView.requestFocus();
            }
        }

        @Override
        protected void onCancelled() {
            mAuthTask = null;
            showProgress(false);
        }

    }
}

