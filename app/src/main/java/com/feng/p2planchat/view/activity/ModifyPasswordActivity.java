package com.feng.p2planchat.view.activity;

import android.database.sqlite.SQLiteDatabase;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.feng.p2planchat.R;
import com.feng.p2planchat.base.BaseActivity;
import com.feng.p2planchat.base.BasePresenter;
import com.feng.p2planchat.config.Constant;
import com.feng.p2planchat.db.AccountDatabaseHelper;
import com.feng.p2planchat.db.AccountOperation;
import com.feng.p2planchat.util.SoftKeyboardUtil;
import com.feng.p2planchat.util.UserUtil;

import java.util.UUID;

public class ModifyPasswordActivity extends BaseActivity implements View.OnClickListener{

    private static final String TAG = "fzh";

    private ImageView mBackIv;
    private EditText mNewPasswordEt;
    private TextView mModifyTv;
    private ProgressBar mProgressBar;

    private AccountOperation mAccountOperation;

    @Override
    protected void doBeforeSetContentView() {

    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_modify_password;
    }

    @Override
    protected BasePresenter getPresenter() {
        return null;
    }

    @Override
    protected void initData() {
        AccountDatabaseHelper helper = new AccountDatabaseHelper(this,
                Constant.DATABASE_ACCOUNT, null, 1);
        SQLiteDatabase db = helper.getWritableDatabase();
        mAccountOperation = new AccountOperation(db);
    }

    @Override
    protected void initView() {
        mBackIv = findViewById(R.id.iv_modify_password_back);
        mBackIv.setOnClickListener(this);

        mNewPasswordEt = findViewById(R.id.et_modify_password_password);

        mModifyTv = findViewById(R.id.tv_modify_password_modify);
        mModifyTv.setOnClickListener(this);

        mProgressBar = findViewById(R.id.pb_modify_password_progress_bar);
    }

    @Override
    protected void doInOnCreate() {

    }

    @Override
    protected boolean isHideTitleBar() {
        return true;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_modify_password_back:
                finish();
                break;
            case R.id.tv_modify_password_modify:
                mProgressBar.setVisibility(View.VISIBLE);
                //隐藏软键盘
                SoftKeyboardUtil.hideSoftKeyboard(this);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        //更改密码
                        modifyPassword(mNewPasswordEt.getText().toString());
                    }
                }, 1000);
                break;
            default:
                break;
        }
    }

    private void modifyPassword(String newPassword) {
        newPassword = newPassword.replaceAll(" ", "");
        if (check(newPassword)) {
            //更新数据库中当前用户的密码
            String name = UserUtil.readFromInternalStorage(this).getUserName();
            Log.d(TAG, "pre : database = " + mAccountOperation.showAll());
            mAccountOperation.updatePasswordByName(name, newPassword);
            Log.d(TAG, "after : database = " + mAccountOperation.showAll());
            showShortToast("修改密码成功");
            finish();
        }
        mProgressBar.setVisibility(View.GONE);
    }

    /**
     * 检查密码的输入
     *
     * @param newName
     */
    private boolean check(String newName) {
        //判空
        if (newName.equals("")) {
            showShortToast("密码不能为空");
            return false;
        }

        return true;
    }
}
