package com.feng.p2planchat.view.activity;

import android.database.sqlite.SQLiteDatabase;
import android.os.Handler;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.feng.p2planchat.R;
import com.feng.p2planchat.base.BaseActivity;
import com.feng.p2planchat.base.BasePresenter;
import com.feng.p2planchat.config.Constant;
import com.feng.p2planchat.contract.IModifyNameContract;
import com.feng.p2planchat.db.AccountDatabaseHelper;
import com.feng.p2planchat.db.AccountOperation;
import com.feng.p2planchat.entity.bean.User;
import com.feng.p2planchat.presenter.ModifyNamePresenter;
import com.feng.p2planchat.util.UserUtil;

public class ModifyNameActivity extends BaseActivity<ModifyNamePresenter>
        implements View.OnClickListener, IModifyNameContract.View {

    private ImageView mBackIv;
    private EditText mNewNameEt;
    private TextView mModifyTv;
    private ProgressBar mProgressBar;

    private User mOwnInfo;
    private AccountOperation mAccountOperation;

    @Override
    protected void doBeforeSetContentView() {

    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_modify_name;
    }

    @Override
    protected ModifyNamePresenter getPresenter() {
        return new ModifyNamePresenter();
    }

    @Override
    protected void initData() {
        AccountDatabaseHelper helper = new AccountDatabaseHelper(this,
                Constant.DATABASE_ACCOUNT, null, 1);
        SQLiteDatabase db = helper.getWritableDatabase();
        mAccountOperation = new AccountOperation(db);

        mOwnInfo = UserUtil.readFromInternalStorage(this);
    }

    @Override
    protected void initView() {
        mBackIv = findViewById(R.id.iv_modify_name_back);
        mBackIv.setOnClickListener(this);

        mNewNameEt = findViewById(R.id.et_modify_name_name);

        mModifyTv = findViewById(R.id.tv_modify_name_modify);
        mModifyTv.setOnClickListener(this);

        mProgressBar = findViewById(R.id.pb_modify_name_progress_bar);
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
            case R.id.iv_modify_name_back:
                finish();
                break;
            case R.id.tv_modify_name_modify:
                //更改用户名
                mProgressBar.setVisibility(View.VISIBLE);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        modifyName(mNewNameEt.getText().toString());
                    }
                }, 300);
                break;
            default:
                break;
        }
    }

    /**
     * 更改用户名成功
     */
    @Override
    public void modifyNameSuccess() {
        mProgressBar.setVisibility(View.GONE);
        showShortToast("用户名修改成功");
        finish();
    }

    /**
     * 更改用户名失败
     *
     * @param errorMsg
     */
    @Override
    public void modifyNameError(String errorMsg) {
        mProgressBar.setVisibility(View.GONE);
        showShortToast(errorMsg);
    }

    /**
     * 更改用户名
     *
     * @param newName
     */
    private void modifyName(String newName) {
        newName = newName.replaceAll(" ", "");
        if (check(newName)) {
            mPresenter.modifyName(mOwnInfo.getUserName(), newName, this);
        } else {
            mProgressBar.setVisibility(View.GONE);
        }
    }

    /**
     * 检查用户名的输入
     *
     * @param newName
     */
    private boolean check(String newName) {
        //判空
        if (newName.equals("")) {
            showShortToast("用户名不能为空");
            return false;
        }
        //判断用户名是否存在
        if (mAccountOperation.hasName(newName)) {
            showShortToast("用户名已存在");
            return false;
        }

        return true;
    }
}