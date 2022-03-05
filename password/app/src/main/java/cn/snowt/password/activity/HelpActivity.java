package cn.snowt.password.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import cn.snowt.password.R;
import cn.snowt.password.util.BaseUtils;
import cn.snowt.password.util.Constant;

/**
 * @Author: HibaraAi
 * @Date: 2022-03-04, 0004 11:16:49
 * @Description:
 */
public class HelpActivity extends AppCompatActivity {

    public static final String OPEN_TYPE = "openType";
    public static final int OPEN_FROM_HELP = 1;
    public static final int OPEN_FROM_ABOUT = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help);

        setSupportActionBar(findViewById(R.id.default_toolbar_inc));
        ActionBar supportActionBar = getSupportActionBar();
        if(null!=supportActionBar){
            supportActionBar.setDisplayHomeAsUpEnabled(true);
        }
        Intent intent = getIntent();
        ListView helpContent = findViewById(R.id.help_content);
        List<String> list = new ArrayList<>();
        switch (intent.getIntExtra(OPEN_TYPE,-1)){
            case OPEN_FROM_HELP:{
                list.add(Constant.STRING_HELP);
                assert supportActionBar != null;
                supportActionBar.setTitle("帮助");
                break;
            }
            case OPEN_FROM_ABOUT:{
                list.add(Constant.STRING_ABOUT);
                assert supportActionBar != null;
                supportActionBar.setTitle("关于");
                helpContent.setOnItemLongClickListener((parent, view, position, id) -> {
                    if (BaseUtils.copyInClipboard(this,"https://github.com/HibaraAi/Diary")) {
                        BaseUtils.shortTipInCoast(HelpActivity.this,"开源代码的网址已复制");
                    }
                    return true;
                });
                break;
            }
            default:break;
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<>(HelpActivity.this,
                android.R.layout.simple_list_item_1,
                list);
        helpContent.setAdapter(adapter);
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