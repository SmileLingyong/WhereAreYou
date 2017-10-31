package com.example.smile.whereareyou.util;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.PopupWindow;
import android.widget.Toast;

import com.example.smile.whereareyou.R;

/**
 * Created by lly54 on 2017/10/15.
 */

public class ShareLocationDialog extends PopupWindow {

    private Button singleLocationBtnDialog;
    private Button realTimeLocationBtnDialog;
    private View mMenuView;


    @SuppressLint({"InflateParams", "ClickableViewAccessibility"})
    public ShareLocationDialog(final Activity context, OnClickListener singleLocationListener, OnClickListener realTimeLocationListener) {
        super(context);
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mMenuView = inflater.inflate(R.layout.dialog_start_location, null);
        singleLocationBtnDialog = (Button) mMenuView.findViewById(R.id.btn_dialog_single_loc);
        realTimeLocationBtnDialog = (Button) mMenuView.findViewById(R.id.btn_dialog_real_time_loc);

        this.setContentView(mMenuView);
        this.setWidth(LayoutParams.MATCH_PARENT);
        this.setHeight(LayoutParams.WRAP_CONTENT);
        this.setFocusable(true);
        this.setAnimationStyle(R.style.user_center_anim_style);
        ColorDrawable dw = new ColorDrawable(0xb0000000);
        this.setBackgroundDrawable(dw);
        mMenuView.setOnTouchListener(new OnTouchListener() {

            public boolean onTouch(View v, MotionEvent event) {

                int height = mMenuView.findViewById(R.id.pop_layout).getTop();
                int y = (int) event.getY();
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    if (y < height) {
                        dismiss();  // 销毁弹出框
                    }
                }
                return true;
            }
        });

        singleLocationBtnDialog.setOnClickListener(singleLocationListener); // 设置单次按钮点击事件
        realTimeLocationBtnDialog.setOnClickListener(realTimeLocationListener); // 设置实时按钮点击事件
    }
}
