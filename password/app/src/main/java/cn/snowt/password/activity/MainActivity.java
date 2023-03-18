package cn.snowt.password.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.SearchView;

import java.util.ArrayList;
import java.util.List;

import cn.snowt.password.R;
import cn.snowt.password.adapter.KeyAdapter;
import cn.snowt.password.entity.Key;
import cn.snowt.password.service.KeyService;
import cn.snowt.password.service.impl.KeyServiceImpl;
import cn.snowt.password.util.BaseUtils;
import cn.snowt.password.util.ChineseCharUtils;

/**
 * @Author: HibaraAi
 * @Date: 2022-02-20, 0020 09:30:23
 * @Description: 主界面
 */
public class MainActivity extends AppCompatActivity {
    private ActionBar actionBar;

    private final KeyService keyService = new KeyServiceImpl();

    private SearchView searchView = null;
    private RecyclerView recyclerView = null;

    private List<Key> keyList = null;

    private List<String> nameSearch;
    private List<String> otherNameSearch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        bindViewAndSetListener();

        keyList = keyService.getSimpleKeyList();
        BaseUtils.shortTipInCoast(this,"共有"+keyList.size()+"条数据");
        getSearchHelp();
        updateAndFlushShowData(keyList);
    }

    private void bindViewAndSetListener() {

        setSupportActionBar(findViewById(R.id.default_toolbar_inc));
        actionBar = getSupportActionBar();
        if(null!=actionBar){
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.bar_settings);
            actionBar.setTitle("密码管理器");
        }

        recyclerView = findViewById(R.id.recyclerview_keyList);
        searchView = findViewById(R.id.main_search_input);

        searchView.setOnQueryTextListener((new SearchView.OnQueryTextListener(){
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if("".equals(newText)){
                    updateAndFlushShowData(keyList);
                }else{
                    List<Key> searchList = new ArrayList<>();
                    List<Integer> ids = new ArrayList<>();
//                    nameSearch.forEach(s -> {
//                        if(s.contains(newText)){
//                            int index = nameSearch.indexOf(s);
//                            Key key = keyList.get(index);
//                            searchList.add(key);
//                            ids.add(key.getId());
//                        }
//                    });
                    int i=0;
                    for(;i<nameSearch.size();i++){
                        String s = nameSearch.get((Integer) i);
                        if(s.contains(newText)){
                            Key key = keyList.get((Integer) i);
                            searchList.add(key);
                            ids.add(key.getId());
                        }
                    }
//                    otherNameSearch.forEach(s->{
//                        if(s.contains(newText)){
//                            int index = otherNameSearch.indexOf(s);
//                            Key key = keyList.get(index);
//                            if(!ids.contains(key.getId())){
//                                searchList.add(key);
//                                ids.add(key.getId());
//                            }
//                        }
//                    });
                    int j=0;
                    for(;j<otherNameSearch.size();j++){
                        String s = otherNameSearch.get((Integer) j);
                        if(s.contains(newText)){
                            Key key = keyList.get((Integer) j);
                            searchList.add(key);
                            ids.add(key.getId());
                        }
                    }
                    updateAndFlushShowData(searchList);
                }
                return true;
            }
        }));
        searchView.setOnCloseListener(() -> {
            updateAndFlushShowData(keyList);
            return true;
        });
    }

    /**
     * 刷新KeyList数据并展示
     */
    private void updateAndFlushShowData(List<Key> list){
        KeyAdapter keyAdapter = new KeyAdapter(list);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(keyAdapter);
    }

    /**
     * 控制生成密码的UI界面
     */
    private void generatePasswordUi(){
        //创建item
        final String[] items5 = new String[]{"保存类型:仅复制到剪贴板",
                "保存类型:不复制到剪贴板,而是生成新密码保存",
                "密码强度:包含大写字母",
                "密码强度:包含数字",
                "密码强度:包含特殊字符",
                "密码长度:6位长度",
                "密码长度:10位长度",
                "密码长度:20位长度"};
        final boolean[] booleans = {true,false,false, false, false, false,true,false};
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle("密码强度选项")
                .setIcon(R.drawable.key_itemlist_big)
                //创建多选框
                .setMultiChoiceItems(items5, booleans, (dialogInterface, i, b) -> booleans[i] = b)
                //添加"Yes"按钮
                .setPositiveButton("确定", (dialogInterface, i) -> {
                    boolean saveInDb =  booleans[1];
                    int passwordLength = 10;
                    if(booleans[7]){
                        passwordLength = 20;
                    }else if(booleans[5]){
                        passwordLength = 6;
                    }
                    String randomPassword = keyService.getRandomPassword(passwordLength, booleans[3], booleans[2], booleans[4]);
                    if(saveInDb){
                        Intent addIntent = new Intent(MainActivity.this,EditActivity.class);
                        addIntent.putExtra(EditActivity.OPEN_FROM,EditActivity.OPEN_FROM_ADD);
                        addIntent.putExtra("randomPassword",randomPassword);
                        startActivityForResult(addIntent,1);
                    }else{
                        if(BaseUtils.copyInClipboard(this,randomPassword)){
                            BaseUtils.shortTipInCoast(MainActivity.this,"生成的密码已复制到剪贴板");
                        }
                    }
                })
                //添加取消
                .setNegativeButton("取消",null)
                .create();
        dialog.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.bar_main,menu);
        return true;
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home: {
                BaseUtils.gotoActivity(MainActivity.this,SettingsActivity.class);
                break;
            }
            case R.id.toolbar_random:{
                generatePasswordUi();
                break;
            }
            case R.id.toolbar_add:{
                Intent intent = new Intent(MainActivity.this,EditActivity.class);
                intent.putExtra(EditActivity.OPEN_FROM,EditActivity.OPEN_FROM_ADD);
                MainActivity.this.startActivity(intent);
                break;
            }
            default:break;
        }
        return true;
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        keyList = keyService.getSimpleKeyList();
        getSearchHelp();
        updateAndFlushShowData(keyList);
    }

    /**
     * 将Key列表的（名字和别名）拼音缩写处理出来
     */
    private void getSearchHelp(){
        if(null==nameSearch){
            nameSearch = new ArrayList<>();
        }else{
            nameSearch.clear();
        }
        if(null==otherNameSearch){
            otherNameSearch = new ArrayList<>();
        }else{
            otherNameSearch.clear();
        }
        keyList.forEach(key -> {
            nameSearch.add(ChineseCharUtils.getAllFirstLetter(key.getName()).toLowerCase());
            otherNameSearch.add(ChineseCharUtils.getAllFirstLetter(key.getOtherName()).toLowerCase());
        });
    }
}