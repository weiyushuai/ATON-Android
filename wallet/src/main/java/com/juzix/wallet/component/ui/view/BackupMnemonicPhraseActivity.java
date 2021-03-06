package com.juzix.wallet.component.ui.view;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.ContextCompat;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import com.juzix.wallet.R;
import com.juzix.wallet.app.Constants;
import com.juzix.wallet.component.ui.base.BaseActivity;
import com.juzix.wallet.component.ui.dialog.CommonTipsDialogFragment;
import com.juzix.wallet.component.ui.dialog.OnDialogViewClickListener;
import com.juzix.wallet.component.widget.CommonTitleBar;
import com.juzix.wallet.entity.IndividualWalletEntity;
import com.juzix.wallet.utils.JZWalletUtil;

public class BackupMnemonicPhraseActivity extends BaseActivity implements View.OnClickListener {

    private final static String TAG = BackupMnemonicPhraseActivity.class.getSimpleName();

    public static void actionStart(Context context, String password, IndividualWalletEntity walletEntity) {
        actionStart(context, password, walletEntity, 0);
    }

    public static void actionStart(Context context, String password, IndividualWalletEntity walletEntity, int type) {
        Intent intent = new Intent(context, BackupMnemonicPhraseActivity.class);
        intent.putExtra(Constants.Extra.EXTRA_PASSWORD, password);
        intent.putExtra(Constants.Extra.EXTRA_WALLET, walletEntity);
        intent.putExtra(Constants.Extra.EXTRA_TYPE, type);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_SECURE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_backup_mnemonic_phrase);
        showTipsDialog();
        initView();
        IndividualWalletEntity walletEntity = getIntent().getParcelableExtra(Constants.Extra.EXTRA_WALLET);
        setMnemonic(JZWalletUtil.decryptMnenonic(walletEntity.getKey(), walletEntity.getMnemonic(), getIntent().getStringExtra(Constants.Extra.EXTRA_PASSWORD)));
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            exit();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    private void initView() {
        ((CommonTitleBar)findViewById(R.id.commonTitleBar)).setLeftDrawableClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                exit();
            }
        });
        findViewById(R.id.sc_next).setOnClickListener(this);
    }

    private void exit(){
        CommonTipsDialogFragment.createDialogWithTwoButton(ContextCompat.getDrawable(getContext(), R.drawable.icon_dialog_tips),
                string(R.string.backup_exit_tips),
                string(R.string.confirm),
                new OnDialogViewClickListener() {
                    @Override
                    public void onDialogViewClick(DialogFragment fragment, View view, Bundle extra) {
                        if (fragment != null){
                            fragment.dismiss();
                        }
                        if (getIntent().getIntExtra(Constants.Extra.EXTRA_TYPE, 0) == 0){
                            MainActivity.actionStart(BackupMnemonicPhraseActivity.this);
                        }
                        BackupMnemonicPhraseActivity.this.finish();
                    }
                },string(R.string.cancel),
                new OnDialogViewClickListener() {
                    @Override
                    public void onDialogViewClick(DialogFragment fragment, View view, Bundle extra) {
                        if (fragment != null) {
                            fragment.dismiss();
                        }
                    }
                }).show(getSupportFragmentManager(), "showTips");
    }

    private void setMnemonic(String text){
        String[] words = text.split(" ");
        if (words != null && words.length == 12){
            findViewById(R.id.sc_next).setEnabled(true);
            ((TextView) findViewById(R.id.tv_mnemonic1)).setText(words[0]);
            ((TextView) findViewById(R.id.tv_mnemonic2)).setText(words[1]);
            ((TextView) findViewById(R.id.tv_mnemonic3)).setText(words[2]);
            ((TextView) findViewById(R.id.tv_mnemonic4)).setText(words[3]);
            ((TextView) findViewById(R.id.tv_mnemonic5)).setText(words[4]);
            ((TextView) findViewById(R.id.tv_mnemonic6)).setText(words[5]);
            ((TextView) findViewById(R.id.tv_mnemonic7)).setText(words[6]);
            ((TextView) findViewById(R.id.tv_mnemonic8)).setText(words[7]);
            ((TextView) findViewById(R.id.tv_mnemonic9)).setText(words[8]);
            ((TextView) findViewById(R.id.tv_mnemonic10)).setText(words[9]);
            ((TextView) findViewById(R.id.tv_mnemonic11)).setText(words[10]);
            ((TextView) findViewById(R.id.tv_mnemonic12)).setText(words[11]);
        }else {
            findViewById(R.id.sc_next).setEnabled(false);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.sc_next:
                VerificationMnemonicActivity.actionStart(getContext(), getIntent().getStringExtra(Constants.Extra.EXTRA_PASSWORD),
                        getIntent().getParcelableExtra(Constants.Extra.EXTRA_WALLET), getIntent().getIntExtra(Constants.Extra.EXTRA_TYPE, 0));
                BackupMnemonicPhraseActivity.this.finish();
                break;
        }
    }

    private void showTipsDialog() {
        CommonTipsDialogFragment.createDialogWithTitleAndOneButton(ContextCompat.getDrawable(this, R.drawable.icon_dialog_tips), string(R.string.donotScreenshot), string(R.string.backupMnemonicResume), string(R.string.understood), new OnDialogViewClickListener() {
            @Override
            public void onDialogViewClick(DialogFragment fragment, View view, Bundle extra) {
                if (fragment != null){
                    fragment.dismiss();
                }
            }
        }).show(getSupportFragmentManager(), "showTips");
    }
}
