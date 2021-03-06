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
            actionBar.setTitle("??????");
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
                    BaseUtils.longTipInCoast(context,"?????????????????????");
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
     * ?????????????????????????????????????????????
     */
    private static void backupUi(){
        AlertDialog.Builder dialog = new AlertDialog.Builder(thisContext);
        dialog.setTitle("???????????????");
        dialog.setMessage("\n???????????????????????????????????????,????????????????????????????????????????????????????????????");
        EditText editText = new EditText(thisContext);
        editText.setBackgroundResource(R.drawable.background_input);
        editText.setHint("??????????????????");
        dialog.setView(editText);
        dialog.setPositiveButton("??????", (dialog2, which) -> {
            String pin = editText.getText().toString();
            if(!"".equals(pin)){
                SimpleResult result = keyService.backupKey(pin);
                if(result.getSuccess()){
                    BaseUtils.alertDialog(thisContext,"??????",
                            "?????????????????????????????????????????????"+(String)result.getData()+"\n\n??????:????????????????????????????????????????????????????????????????????????????????????????????????.");
                }else{
                    BaseUtils.shortTipInCoast(thisContext,result.getMsg());
                }
            }else{
                BaseUtils.shortTipInCoast(thisContext,"?????????????????????");
            }

        });
        dialog.setNegativeButton("??????",null);
        dialog.show();
    }

    /**
     * ????????????
     */
    private static void txtOutput(){
        AlertDialog.Builder dialog = new AlertDialog.Builder(thisContext);
        dialog.setTitle("??????????????????");
        dialog.setMessage("\n????????????????????????????????????????????????\n???????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????");
        EditText editText = new EditText(thisContext);
        editText.setBackgroundResource(R.drawable.background_input);
        editText.setHint("??????????????????");
        dialog.setView(editText);
        dialog.setPositiveButton("???????????????", (dialog2, which) -> {
            String pin = editText.getText().toString();
            String loginPassword = BaseUtils.getSharedPreference().getString("loginPassword", "");
            boolean flag = loginPassword.equals(MD5Utils.encrypt(Constant.PASSWORD_PREFIX+pin));
            if(flag){
                SimpleResult result = keyService.outputKeyToTxt();
                if(result.getSuccess()){
                    BaseUtils.alertDialog(thisContext,"??????",
                            "???????????????????????????????????????"+(String)result.getData()+"\n\n??????:????????????????????????????????????????????????????????????????????????????????????????????????.");
                }else{
                    BaseUtils.shortTipInCoast(thisContext,result.getMsg());
                }
            }else{
                BaseUtils.shortTipInCoast(thisContext,"??????????????????????????????????????????");
            }

        });
        dialog.setNegativeButton("??????",null);
        dialog.show();
    }

    public static void OpenFile() {
        // ????????????
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
                    BaseUtils.alertDialog(SettingsActivity.this,"??????",
                            "???????????????????????????????????????\n\n(????????????????????????????????????????????????)");

                }else{
                    String name = getFileNameByUri(uri);
                    AlertDialog.Builder builder = new AlertDialog.Builder(SettingsActivity.this);
                    builder.setTitle("???????????????");
                    EditText editText = new EditText(SettingsActivity.this);
                    builder.setMessage("\n\n???????????????["+name+"]\n?????????????????????????????????");
                    builder.setView(editText);
                    builder.setPositiveButton("??????", (dialog, which) -> {
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
                    builder.setNegativeButton("??????",null);
                    builder.show();
                }
            } catch (Exception e) {
                e.printStackTrace();
                BaseUtils.alertDialog(SettingsActivity.this,"??????",
                        "???????????????????????????????????????\n\n(????????????????????????????????????????????????)");
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