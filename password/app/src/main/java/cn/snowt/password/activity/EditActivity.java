package cn.snowt.password.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.EditText;

import java.util.Date;
import java.util.UUID;

import cn.snowt.password.R;
import cn.snowt.password.entity.Key;
import cn.snowt.password.service.KeyService;
import cn.snowt.password.service.impl.KeyServiceImpl;
import cn.snowt.password.util.BaseUtils;
import cn.snowt.password.util.Constant;
import cn.snowt.password.util.SimpleResult;

/**
 * @Author: HibaraAi
 * @Date: 2022-02-24, 0024 21:41:19
 * @Description: 新增/修改密钥界面
 */
public class EditActivity extends AppCompatActivity {

    /**
     * 打开activity的类型
     */
    public static final String OPEN_FROM = "open_from";
    public static final int OPEN_FROM_ADD = 1;
    public static final int OPEN_FROM_UPDATE = 2;

    /**
     * Intent携带的参数的名字
     */
    public static final String INTENT_ARGS_KEY_ID = "id";

    private ActionBar actionBar;
    private Integer openType = -1;
    private final KeyService keyService = new KeyServiceImpl();

    private EditText viewName;
    private EditText viewAccount1;
    private EditText viewPassword1;
    private EditText viewAccount2;
    private EditText viewPassword2;
    private EditText viewRemark;
    private EditText viewOtherName;

    private Boolean isAdd = true;
    private Key keyDetail = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_SECURE);
        setContentView(R.layout.activity_edit);
        bindViewAndSetListener();
        showDataWhenNeed();
    }

    private void bindViewAndSetListener() {
        setSupportActionBar(findViewById(R.id.default_toolbar_inc));
        actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        assert actionBar != null;
        Intent intent = getIntent();
        openType = intent.getIntExtra(OPEN_FROM,-1);
        switch (openType) {
            case OPEN_FROM_ADD:{
                actionBar.setTitle("新增");
                isAdd = true;
                break;
            }
            case OPEN_FROM_UPDATE:{
                actionBar.setTitle("更新");
                isAdd = false;
                break;
            }
            default:{
                finish();
                BaseUtils.longTipInCoast(EditActivity.this,"编辑Key时出错...");
                break;
            }
        }

        viewName = findViewById(R.id.edit_input_name);
        viewAccount1 = findViewById(R.id.edit_input_account1);
        viewPassword1 = findViewById(R.id.edit_input_password1);
        viewAccount2 = findViewById(R.id.edit_input_account2);
        viewPassword2 = findViewById(R.id.edit_input_password2);
        viewRemark = findViewById(R.id.edit_input_remark);
        viewOtherName = findViewById(R.id.edit_input_otherName);
        viewName.setHint("最大长度: "+ Constant.NAME_MAX_LENGTH +" 位");
        viewAccount1.setHint("最大长度: "+ Constant.ACCOUNT_MAX_LENGTH +" 位");
        viewAccount2.setHint("最大长度: "+ Constant.ACCOUNT_MAX_LENGTH +" 位");
        viewPassword1.setHint("最大长度: "+ Constant.ACCOUNT_MAX_LENGTH +" 位");
        viewPassword2.setHint("最大长度: "+ Constant.ACCOUNT_MAX_LENGTH +" 位");
        viewRemark.setHint("最大长度: "+ Constant.REMARKS_MAX_LENGTH +" 位");
        viewOtherName.setHint("最大长度: "+ Constant.OTHER_NAME_MAX_LENGTH +" 位");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //根据打开类型判断是否需要展示信息流按钮
        switch (openType) {
            case OPEN_FROM_ADD:{
                getMenuInflater().inflate(R.menu.bar_add,menu);
                break;
            }
            case OPEN_FROM_UPDATE:{
                getMenuInflater().inflate(R.menu.bar_update,menu);
                break;
            }
            default:break;
        }
        return true;
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home: {
                finish();
                break;
            }
            case R.id.toolbar_finish:{
                if(isAdd){
                    //add
                    readEditValueToKey();
                    SimpleResult result = keyService.addOne(keyDetail);
                    if(result.getSuccess()){
                        BaseUtils.shortTipInCoast(this,"新增成功!");
                        Intent intent1 = new Intent();
                        intent1.putExtra("addSuccess",true);
                        setResult(RESULT_OK,intent1);
                        finish();
                    }else{
                        BaseUtils.alertDialog(this,"注意!",result.getMsg());
                    }
                }else{
                    //update
                    readEditValueToKey();
                    SimpleResult result = keyService.updateById(keyDetail);
                    if(result.getSuccess()){
                        keyDetail = keyService.getDecodeKeyById(keyDetail.getId());
                        BaseUtils.shortTipInCoast(this,"修改成功!");
                        Intent intent1 = new Intent();
                        intent1.putExtra("keyReturn",keyDetail);
                        setResult(RESULT_OK,intent1);
                        finish();
                    }else{
                        BaseUtils.alertDialog(this,"注意!",result.getMsg());
                    }
                }
                break;
            }
            case R.id.toolbar_help:{
                String tip = "1.名称用于列表显示，且不能为空" +
                        "\n2.账号n和密码n必须对应，必须存储一对账号密码" +
                        "\n3.名称和别名都能用于搜索，但别名只有在详情界面显示" +
                        "\n4.为了安全起见，此界面禁止截屏/录屏";
                BaseUtils.alertDialog(this,"说明",tip);
            }
            default:break;
        }
        return true;
    }

    /**
     * 展示从其他Activity传递过来的数据
     */
    private void showDataWhenNeed(){
        Intent intent = getIntent();
        String generatePassword = intent.getStringExtra("randomPassword");
        if (null!=generatePassword && !"".equals(generatePassword)){
            viewPassword1.setText(generatePassword);
            actionBar.setTitle("生成随机密码");
        }
        //从详情界面点“编辑”按钮过来的
        keyDetail = (Key) intent.getSerializableExtra("keyDetail");
        if(keyDetail!=null){
            //keyName.setText("编辑["+keyDetail.getName()+"]");
            actionBar.setTitle("编辑["+keyDetail.getName()+"]");
            viewAccount1.setText(keyDetail.getAccountOne());
            viewAccount2.setText(keyDetail.getAccountTwo());
            viewPassword1.setText(keyDetail.getPasswordOne());
            viewPassword2.setText(keyDetail.getPasswordTwo());
            viewName.setText(keyDetail.getName());
            viewRemark.setText(keyDetail.getRemarks());
            viewOtherName.setText(keyDetail.getOtherName());
            isAdd = false;
        }
    }

    /**
     * 读取用户的输入
     * @return
     */
    private void readEditValueToKey(){
        if(keyDetail==null){
            keyDetail = new Key();
            keyDetail.setCreateDate(new Date());
            keyDetail.setUuid(UUID.randomUUID().toString());
        }

        String keyNameStr = viewName.getText().toString();
        String accountOneStr = viewAccount1.getText().toString();
        String accountTwoStr = viewAccount2.getText().toString();
        String passwordOneStr = viewPassword1.getText().toString();
        String passwordTwoStr = viewPassword2.getText().toString();
        String remarksStr = viewRemark.getText().toString();
        String otherName = viewOtherName.getText().toString();

        keyDetail.setName(keyNameStr);
        keyDetail.setAccountOne(accountOneStr);
        keyDetail.setAccountTwo(accountTwoStr);
        keyDetail.setPasswordOne(passwordOneStr);
        keyDetail.setPasswordTwo(passwordTwoStr);
        keyDetail.setRemarks(remarksStr);
        keyDetail.setOtherName(otherName);
    }
}