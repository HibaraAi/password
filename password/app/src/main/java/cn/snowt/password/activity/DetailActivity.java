package cn.snowt.password.activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import cn.snowt.password.R;
import cn.snowt.password.entity.Key;
import cn.snowt.password.service.KeyService;
import cn.snowt.password.service.impl.KeyServiceImpl;
import cn.snowt.password.util.BaseUtils;
import cn.snowt.password.util.SimpleResult;

/**
 * @Author: HibaraAi
 * @Date: 2022-03-05, 0005 10:12:45
 * @Description:
 */
public class DetailActivity extends AppCompatActivity implements View.OnLongClickListener {

    private ActionBar actionBar;
    private TextView viewAccount1;
    private TextView viewPassword1;
    private TextView viewAccount2;
    private TextView viewPassword2;
    private TextView viewRemark;
    private TextView viewOtherName;
    private TextView viewName;
    private Button resetClipboard = null;

    private KeyService keyService = new KeyServiceImpl();
    private Key keyDetail = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_SECURE);
        setContentView(R.layout.activity_detail);
        bindViewAndSetListener();
        Intent intent = getIntent();
        int keyId = intent.getIntExtra("keyId",-1);
        if(keyId!=-1){
            keyDetail = keyService.getDecodeKeyById(keyId);
            showKeyDetails();
        }
    }

    private void bindViewAndSetListener() {
        setSupportActionBar(findViewById(R.id.default_toolbar_inc));
        actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle("??????");
        }
        viewAccount1 = findViewById(R.id.detail_value_account1);
        viewAccount2 = findViewById(R.id.detail_value_account2);
        viewPassword1 = findViewById(R.id.detail_value_password1);
        viewPassword2 = findViewById(R.id.detail_value_password2);
        viewRemark = findViewById(R.id.detail_value_remark);
        viewOtherName = findViewById(R.id.detail_value_otherName);
        viewName = findViewById(R.id.detail_value_name);

        viewName.setOnLongClickListener(this);
        viewAccount1.setOnLongClickListener(this);
        viewAccount2.setOnLongClickListener(this);
        viewPassword1.setOnLongClickListener(this);
        viewPassword2.setOnLongClickListener(this);
        viewRemark.setOnLongClickListener(this);
        viewOtherName.setOnLongClickListener(this);

        resetClipboard = findViewById(R.id.detail_btn_clear);
        resetClipboard.setOnClickListener(v->{
            if (BaseUtils.copyInClipboard(this,"")) {
                BaseUtils.alertDialog(this,"??????","????????????????????????,??????????????????(???????????????????????????)??????????????????????????????, ????????????????????????.");
            }else{
                BaseUtils.shortTipInCoast(this,"?????????????????????!");
            }
        });
    }

    /**
     * ??????key??????
     */
    private void showKeyDetails(){
        viewName.setText(keyDetail.getName());
        viewAccount1.setText(keyDetail.getAccountOne());
        viewAccount2.setText(keyDetail.getAccountTwo());
        viewPassword1.setText(keyDetail.getPasswordOne());
        viewPassword2.setText(keyDetail.getPasswordTwo());
        viewRemark.setText(keyDetail.getRemarks());
        viewOtherName.setText(keyDetail.getOtherName());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (1 == requestCode) {
            if (resultCode == RESULT_OK) {
                if (data != null) {
                    keyDetail = (Key) data.getSerializableExtra("keyReturn");
                }
                showKeyDetails();
            }
        }
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home: {
                finish();
                break;
            }
            case R.id.toolbar_help:{
                String tip = "??????????????????????????????????????????/???????????????????????????????????????";
                BaseUtils.alertDialog(this,"??????",tip);
                break;
            }
            case R.id.toolbar_edit:{
                Intent editIntent = new Intent(this,EditActivity.class);
                editIntent.putExtra("keyDetail",keyDetail);
                editIntent.putExtra(EditActivity.OPEN_FROM,EditActivity.OPEN_FROM_UPDATE);
                startActivityForResult(editIntent, 1);
                break;
            }
            case R.id.toolbar_del:{
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("??????!");
                builder.setMessage("??????????????????["+keyDetail.getName()+"] ????");
                builder.setCancelable(false);
                builder.setNegativeButton("??????", null);
                builder.setPositiveButton("??????", (dialog, which) -> {
                    SimpleResult result = keyService.deleteById(keyDetail.getId());
                    if(result.getSuccess()){
                        BaseUtils.shortTipInCoast(this,"["+keyDetail.getName()+"]????????????");
                        finish();
                    }else{
                        BaseUtils.shortTipInCoast(this,result.getMsg());
                    }
                });
                builder.show();
                break;
            }
            default:break;
        }
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.bar_detail,menu);
        return true;
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onLongClick(View v) {
        switch (v.getId()) {
            case R.id.detail_value_account1:{
                if (BaseUtils.copyInClipboard(this,viewAccount1.getText().toString())) {
                    BaseUtils.alertDialog(this,"??????","??????1?????????????????????, ????????????????????????????????????");
                }
                break;
            }
            case R.id.detail_value_account2:{
                if (BaseUtils.copyInClipboard(this,viewAccount2.getText().toString())) {
                    BaseUtils.alertDialog(this,"??????","??????2?????????????????????, ????????????????????????????????????");
                }
                break;
            }
            case R.id.detail_value_password1:{
                if (BaseUtils.copyInClipboard(this,viewPassword1.getText().toString())) {
                    BaseUtils.alertDialog(this,"??????","??????1?????????????????????, ????????????????????????????????????");
                }
                break;
            }
            case R.id.detail_value_password2:{
                if (BaseUtils.copyInClipboard(this,viewPassword2.getText().toString())) {
                    BaseUtils.alertDialog(this,"??????","??????2?????????????????????, ????????????????????????????????????");
                }
                break;
            }
            case R.id.detail_value_name:{
                if (BaseUtils.copyInClipboard(this,viewName.getText().toString())) {
                    BaseUtils.shortTipInCoast(this,"???????????????");
                }
                break;
            }
            case R.id.detail_value_remark:{
                if (BaseUtils.copyInClipboard(this,viewRemark.getText().toString())) {
                    BaseUtils.shortTipInCoast(this,"???????????????");
                }
                break;
            }
            case R.id.detail_value_otherName:{
                if (BaseUtils.copyInClipboard(this,viewOtherName.getText().toString())) {
                    BaseUtils.shortTipInCoast(this,"???????????????");
                }
                break;
            }
            default:break;
        }
        return true;
    }
}