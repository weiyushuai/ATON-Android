package com.juzix.wallet.component.ui.view;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.juzix.wallet.R;
import com.juzix.wallet.component.ui.base.BaseActivity;
import com.juzix.wallet.component.widget.ShadowContainer;
import com.juzix.wallet.config.AppSettings;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

public class OperateMenuActivity extends BaseActivity {

    private final static String TAG = OperateMenuActivity.class.getSimpleName();

    @BindView(R.id.sc_import_wallet)
    ShadowContainer scImportWallet;
    @BindView(R.id.sc_create_wallet)
    ShadowContainer scCreateWallet;

    Unbinder unbinder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_operate_menu);
        unbinder = ButterKnife.bind(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (!AppSettings.getInstance().getOperateMenuFlag()) {
            this.finish();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (unbinder != null) {
            unbinder.unbind();
        }
    }

    @OnClick({R.id.sc_create_wallet, R.id.sc_import_wallet})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.sc_create_wallet:
                CreateIndividualWalletActivity.actionStart(this);
                break;
            case R.id.sc_import_wallet:
                ImportIndividualWalletActivity.actionStart(this);
                break;
            default:
                break;
        }
    }

    public static void actionStart(Context context) {
        Intent intent = new Intent(context, OperateMenuActivity.class);
        context.startActivity(intent);
    }
}
