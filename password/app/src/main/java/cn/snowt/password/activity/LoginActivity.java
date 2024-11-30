package cn.snowt.password.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import org.litepal.LitePal;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import cn.snowt.password.R;
import cn.snowt.password.service.LoginService;
import cn.snowt.password.service.impl.LoginServiceImpl;
import cn.snowt.password.util.BaseUtils;
import cn.snowt.password.util.SimpleResult;

/**
 * @Author: HibaraAi
 * @Date: 2022-02-20, 0020 09:30:00
 * @Description: 登录界面
 */
public class LoginActivity extends AppCompatActivity implements View.OnClickListener{

    private final Button[] buttons = new Button[12];
    private TextView password;
    private TextView tip;

    private final LoginService loginService = new LoginServiceImpl();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_SECURE);
        //横屏、竖屏的布局处理
        if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            setContentView(R.layout.activity_login_h);
        } else if (this.getResources().getConfiguration().orientation ==Configuration.ORIENTATION_PORTRAIT) {
            setContentView(R.layout.activity_login);
        }
        bindViewAndSetListener();
        doWhenFirstLogin();
        initKeyboard();
    }

    /**
     * 初始化密码键盘
     */
    private void initKeyboard(){
        Random random = new Random();
        List<Integer> pool = new ArrayList<>();
        pool.add(0);pool.add(1);pool.add(2);pool.add(3);pool.add(4);pool.add(5);
        pool.add(6);pool.add(7);pool.add(8);pool.add(9);
        for(int i=0;i<=9;i++){
            int numIndex = random.nextInt(pool.size());
            buttons[i].setText(pool.get(numIndex)+"");
            pool.remove((Integer)pool.get(numIndex));
        }
    }

    private void bindViewAndSetListener(){
        password = findViewById(R.id.login_text_password);
        tip = findViewById(R.id.login_text_tip);
        buttons[0] = findViewById(R.id.login_btn_num0);
        buttons[1] = findViewById(R.id.login_btn_num1);
        buttons[2] = findViewById(R.id.login_btn_num2);
        buttons[3] = findViewById(R.id.login_btn_num3);
        buttons[4] = findViewById(R.id.login_btn_num4);
        buttons[5] = findViewById(R.id.login_btn_num5);
        buttons[6] = findViewById(R.id.login_btn_num6);
        buttons[7] = findViewById(R.id.login_btn_num7);
        buttons[8] = findViewById(R.id.login_btn_num8);
        buttons[9] = findViewById(R.id.login_btn_num9);
        buttons[10] = findViewById(R.id.login_btn_del);
        buttons[11] = findViewById(R.id.login_btn_login);
        for (Button button : buttons) {
            button.setOnClickListener(this);
        }

        buttons[10].setOnLongClickListener(v -> {
            password.setText("");
            return true;
        });
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onClick(View v) {
        BaseUtils.createOneShotByVibrator();
        switch (v.getId()){
            case R.id.login_btn_del:{
                String s = password.getText().toString();
                if(!"".equals(s)){
                    s = s.substring(0,s.length()-1);
                    password.setText(s);
                }
                break;
            }
            case R.id.login_btn_login:{
                String pinUserInput = password.getText().toString();
                if("".equals(pinUserInput)){
                    return;
                }
                password.setText("");
                SimpleResult result = loginService.login(pinUserInput);
                if(result.getSuccess()){
                    BaseUtils.gotoActivity(this,MainActivity.class);
                    this.finish();
                }else{
                    tip.setText(result.getMsg());
                }
                break;
            }
            default:{
                String s = password.getText().toString();
                password.setText(s+((Button) v).getText().toString());
                break;
            }
        }
    }


    /**
     * 由于第一次使用本程序需要跳转设置登录密码，所以此Activity有逻辑代码
     */
    private void doWhenFirstLogin(){
        SharedPreferences sharedPreferences = BaseUtils.getSharedPreference();
        boolean firstUse = sharedPreferences.getBoolean("firstUse", true);
        if(firstUse){
            //第一次使用本程序
            //弹出免责声明
            AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
            builder.setTitle("免责声明")
                    .setMessage("本软件不会盗取你任何数据，有开源代码可查，开源网址https://github.com/HibaraAi/password或https://gitee.com/HibaraAi/password。因此，如果你在使用本软件的过程中，产生无论何种形式的损失，都与本作者无关。")
                    .setPositiveButton("了解并接受", (dialog, which) -> {
                        //创建数据库
                        LitePal.getDatabase();
                        BaseUtils.longTipInCoast(LoginActivity.this,"第一次使用软件，正在设置加密密钥，请稍等");
                        //跳转设置登录密码界面
                        BaseUtils.gotoActivity(this, SetPasswordActivity.class);
                    })
                    .setCancelable(false)
                    .setNegativeButton("不接受并退出",((dialog, which) -> finish()))
                    .show();
        }
    }
}