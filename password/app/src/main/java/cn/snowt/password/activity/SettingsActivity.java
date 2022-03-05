package cn.snowt.password.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;

import com.alibaba.fastjson.JSON;

import org.litepal.LitePal;
import org.litepal.LitePalApplication;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import cn.snowt.password.R;
import cn.snowt.password.service.KeyService;
import cn.snowt.password.service.impl.KeyServiceImpl;
import cn.snowt.password.util.BaseUtils;
import cn.snowt.password.util.Constant;
import cn.snowt.password.util.MD5Utils;
import cn.snowt.password.util.SimpleResult;

/**
 * @Author: HibaraAi
 * @Date: 2022-02-24, 0024 21:23:55
 * @Description:
 */
public class SettingsActivity extends AppCompatActivity {

    @SuppressLint("StaticFieldLeak")
    private static Context thisContext;

    private static final KeyService keyService = new KeyServiceImpl();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        thisContext = SettingsActivity.this;
        setContentView(R.layout.settings_activity);
        if (savedInstanceState == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.settings, new SettingsFragment())
                    .commit();
        }
        setSupportActionBar(findViewById(R.id.default_toolbar_inc));
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle("设置");
        }
    }

    public static class SettingsFragment extends PreferenceFragmentCompat {
        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            setPreferencesFromResource(R.xml.root_preferences, rootKey);
        }

        @Override
        public boolean onPreferenceTreeClick(Preference preference) {
            Context context = preference.getContext();
            switch (preference.getKey()) {
                case "reSetPin":{
                    BaseUtils.gotoActivity((Activity) context,SetPasswordActivity.class);
                    break;
                }
                case "backup":{
                    backupUi();
                    break;
                }
                case "recovery":{
                    OpenFile();
                    BaseUtils.longTipInCoast(context,"请选择备份文件");
                    break;
                }
                case "outToTxt":{
                    txtOutput();
                    break;
                }
                case "help":{
                    Intent intent = new Intent(context,HelpActivity.class);
                    intent.putExtra(HelpActivity.OPEN_TYPE,HelpActivity.OPEN_FROM_HELP);
                    context.startActivity(intent);
                    break;
                }
                case "about":{
                    Intent intent = new Intent(context,HelpActivity.class);
                    intent.putExtra(HelpActivity.OPEN_TYPE,HelpActivity.OPEN_FROM_ABOUT);
                    context.startActivity(intent);
                    break;
                }
                default:break;
            }
                return true;
        }


    }

    /**
     * 处理点击备份后的界面及数据传递
     */
    private static void backupUi(){
        AlertDialog.Builder dialog = new AlertDialog.Builder(thisContext);
        dialog.setTitle("请设置密码");
        dialog.setMessage("\n为备份文件设置恢复一个密码,从这个文件读取恢复数据时将要验证这个密码");
        EditText editText = new EditText(thisContext);
        editText.setBackgroundResource(R.drawable.background_input);
        editText.setHint("设置读取密码");
        dialog.setView(editText);
        dialog.setPositiveButton("确定", (dialog2, which) -> {
            String pin = editText.getText().toString();
            if(!"".equals(pin)){
                SimpleResult result = keyService.backupKey(pin);
                if(result.getSuccess()){
                    BaseUtils.alertDialog(thisContext,"提示",
                            "成功生成备份文件，存储路径为："+(String)result.getData()+"\n\n提示:请及时将备份文件剪贴到其他位置，否则该文件会在软件卸载时一并删除.");
                }else{
                    BaseUtils.shortTipInCoast(thisContext,result.getMsg());
                }
            }else{
                BaseUtils.shortTipInCoast(thisContext,"不许输入空密码");
            }

        });
        dialog.setNegativeButton("取消",null);
        dialog.show();
    }

    /**
     * 明文导出
     */
    private static void txtOutput(){
        AlertDialog.Builder dialog = new AlertDialog.Builder(thisContext);
        dialog.setTitle("校验登陆密码");
        dialog.setMessage("\n明文导出数据需要校验你的登录密码\n提示：导出过程会比较久，由于此过程并没有做进度展示，所以会表现出卡住现象，请不要退出软件，完成后自有提示。");
        EditText editText = new EditText(thisContext);
        editText.setBackgroundResource(R.drawable.background_input);
        editText.setHint("输入登录密码");
        dialog.setView(editText);
        dialog.setPositiveButton("校验并导出", (dialog2, which) -> {
            String pin = editText.getText().toString();
            String loginPassword = BaseUtils.getSharedPreference().getString("loginPassword", "");
            boolean flag = loginPassword.equals(MD5Utils.encrypt(Constant.PASSWORD_PREFIX+pin));
            if(flag){
                SimpleResult result = keyService.outputKeyToTxt();
                if(result.getSuccess()){
                    BaseUtils.alertDialog(thisContext,"提示",
                            "成功导出数据，存储路径为："+(String)result.getData()+"\n\n提示:请及时将备份文件剪贴到其他位置，否则该文件会在软件卸载时一并删除.");
                }else{
                    BaseUtils.shortTipInCoast(thisContext,result.getMsg());
                }
            }else{
                BaseUtils.shortTipInCoast(thisContext,"登录密码校验失败，已拒绝导出");
            }

        });
        dialog.setNegativeButton("取消",null);
        dialog.show();
    }

    public static void OpenFile() {
        // 指定类型
        String[] mimeTypes = {"application/x-msdos-program"};
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        StringBuilder mimeTypesStr = new StringBuilder();
        for (String mimeType : mimeTypes) {
            mimeTypesStr.append(mimeType).append("|");
        }
        intent.setType(mimeTypesStr.substring(0, mimeTypesStr.length() - 1));
        Activity activity = (Activity)thisContext;
        activity.startActivityForResult(Intent.createChooser(intent, "ChooseFile"), 1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1 && resultCode == RESULT_OK) {
            Uri uri = data.getData();
            try {
                byte[] bytes = getBytesByUri(SettingsActivity.this,uri);
                String s = new String(bytes);
                Map<String,Object> map = (Map<String,Object>) JSON.parse(s);
                String pin = (String) map.get("privateKey");
                if(null==pin || "".equals(pin)){
                    BaseUtils.alertDialog(SettingsActivity.this,"提示",
                            "读取备份文件失败，请重试。\n\n(请确保备份文件正确且没有被破坏过)");

                }else{
                    String name = getFileNameByUri(uri);
                    AlertDialog.Builder builder = new AlertDialog.Builder(SettingsActivity.this);
                    builder.setTitle("请输入密码");
                    EditText editText = new EditText(SettingsActivity.this);
                    builder.setMessage("\n\n已读取文件["+name+"]\n请输入该文件的恢复密码");
                    builder.setView(editText);
                    builder.setPositiveButton("确定", (dialog, which) -> {
                        String inputPin = editText.getText().toString();
                        SimpleResult result = keyService.recoveryKey(inputPin, map);
                        if(result.getSuccess()){
                            Intent intent1 = new Intent();
                            intent1.putExtra("addSuccess",true);
                            setResult(RESULT_OK,intent1);
                            finish();
                        }
                        BaseUtils.longTipInCoast(SettingsActivity.this,result.getMsg());
                    });
                    builder.setNegativeButton("取消",null);
                    builder.show();
                }
            } catch (Exception e) {
                e.printStackTrace();
                BaseUtils.alertDialog(SettingsActivity.this,"提示",
                        "读取备份文件失败，请重试。\n\n(请确保备份文件正确且没有被破坏过)");
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    public static byte[] getBytesByUri(Context context, Uri uri) throws IOException {
        InputStream iStream = context.getContentResolver().openInputStream(uri);
        if (iStream == null) {
            return null;
        }
        return getBytes(iStream);
    }

    private static byte[] getBytes(InputStream inputStream) throws IOException {
        ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();
        int bufferSize = 1024;
        byte[] buffer = new byte[bufferSize];

        int len = 0;
        while ((len = inputStream.read(buffer)) != -1) {
            byteBuffer.write(buffer, 0, len);
        }
        return byteBuffer.toByteArray();
    }

    public String getFileNameByUri(Uri uri) {
        String filename = "";
        Cursor returnCursor = getContentResolver().query(uri, null,
                null, null, null);
        if (returnCursor != null) {
            int nameIndex = returnCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
            returnCursor.moveToFirst();
            filename = returnCursor.getString(nameIndex);
            returnCursor.close();
        }
        return filename;
    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:{
                finish();
                break;
            }
            default:break;
        }
        return true;
    }
}