package cn.snowt.password.service.impl;

import android.os.Environment;
import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import org.litepal.LitePal;
import org.litepal.LitePalApplication;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

import cn.snowt.password.entity.Key;
import cn.snowt.password.service.KeyService;
import cn.snowt.password.util.BaseUtils;
import cn.snowt.password.util.Constant;
import cn.snowt.password.util.MD5Utils;
import cn.snowt.password.util.MyConfiguration;
import cn.snowt.password.util.PDFUtils;
import cn.snowt.password.util.RSAUtils;
import cn.snowt.password.util.SimpleResult;

/**
 * @Author: HibaraAi
 * @Date: 2022-03-03 22:16:02
 * @Description:
 */
public class KeyServiceImpl implements KeyService {
    public static final String TAG = "KeyServiceImpl";

    @Override
    public Key getDecodeKeyById(Integer id) {
        Key key = getNoDecodeKeyById(id);
        if(null!=key){
            decodeKey(key);
        }
        return key;
    }

    @Override
    public SimpleResult addOne(Key key) {
        //数据判断
        SimpleResult result = checkInput(key);
        key.setCreateDate(new Date());
        if(!result.getSuccess()){
            return result;
        }
        encodeKey(key);
        if (key.save()) {
            return SimpleResult.ok();
        }else{
            Log.e(TAG,"数据库写入错误!");
            return SimpleResult.error().msg("数据库写入错误!");
        }
    }

    @Override
    public SimpleResult updateById(Key key) {
        //数据判断
        SimpleResult result = checkInput(key);
        if(!result.getSuccess()){
            return result;
        }
        encodeKey(key);
        int update = key.update(key.getId());
        if(1==update){
            return SimpleResult.ok();
        }else{
            Log.e(TAG,"数据库更新失败!");
            return SimpleResult.error().msg("数据库更新失败!");
        }
    }

    @Override
    public SimpleResult deleteById(Integer id) {
        Key key = getNoDecodeKeyById(id);
        int delete = key.delete();
        if(1==delete){
            return SimpleResult.ok();
        }else{
            Log.e(TAG,"数据库删除失败!");
            return SimpleResult.error().msg("数据库删除失败!");
        }
    }

    @Override
    public SimpleResult backupKey(String pin) {
        List<Key> originalKeyList = LitePal.findAll(Key.class);
        Map<String,Object> map = new HashMap<>();
        map.put("data",originalKeyList);
        map.put("pin", MD5Utils.encrypt(Constant.PASSWORD_PREFIX+pin));
        map.put("version",Constant.INTERNAL_VERSION);
        map.put("privateKey", MyConfiguration.getInstance().getPrivateKey());
        map.put("publicKey",MyConfiguration.getInstance().getPublicKey());
        String mapJson = JSON.toJSONString(map);
        //String mapJson = BaseUtils.toJson(map);
        String backupFilePath = null;
        //获取外部存储路径
        String parentPath = LitePalApplication.getContext().getExternalFilesDir("").getAbsolutePath();
        //通过文件字节输出流存储
        try {
            //创建文件夹index888
            File backupPath=new File(parentPath+"/backup");
            if (!backupPath.exists()) {
                backupPath.mkdirs();
            }
            //创建成功后新建一个index.txt，并写入内容
            String fileName= "lianliankanTEMP_"+ BaseUtils.dateToString(new Date())+".dll";
            File backupFile=new File(backupPath+"/"+fileName);
            backupFilePath = backupFile.getPath();
            backupFile.createNewFile();
            //第二个参数的意思
            //r    以只读的方式打开文本，也就意味着不能用write来操作文件
            //rw   读操作和写操作都是允许的
            //rws  每当进行写操作，同步的刷新到磁盘，刷新内容和元数据
            //rwd  每当进行写操作，同步的刷新到磁盘，刷新内容
            RandomAccessFile FileWrite= new RandomAccessFile(backupFile, "rwd");
            //从什么位置开始写
            FileWrite.seek(backupFile.length());
            //写入数据
            FileWrite.write(mapJson.getBytes());
            FileWrite.close();
        }catch (IOException e){
            Log.e(TAG,"备份所有密码失败");
            e.printStackTrace();
            return SimpleResult.error().msg("备份失败,请重试");
        }
        return SimpleResult.ok().data(backupFilePath);
    }

    @Override
    public SimpleResult backupKeyToROM(String pin) {
        List<Key> originalKeyList = LitePal.findAll(Key.class);
        Map<String,Object> map = new HashMap<>();
        map.put("data",originalKeyList);
        map.put("pin", MD5Utils.encrypt(Constant.PASSWORD_PREFIX+pin));
        map.put("version",Constant.INTERNAL_VERSION);
        map.put("privateKey", MyConfiguration.getInstance().getPrivateKey());
        map.put("publicKey",MyConfiguration.getInstance().getPublicKey());
        String mapJson = JSON.toJSONString(map);
        //String mapJson = BaseUtils.toJson(map);
        String backupFilePath = null;
        //获取外部存储路径
        String parentPath = Environment.getExternalStoragePublicDirectory(Constant.EXTERNAL_STORAGE_LOCATION).getAbsolutePath()+"/Backup/";
        //通过文件字节输出流存储
        try {
            //创建文件夹index888
            File backupPath=new File(parentPath);
            if (!backupPath.exists()) {
                backupPath.mkdirs();
            }
            //创建成功后新建一个index.txt，并写入内容
            String fileName= "PasswordBackup"+BaseUtils.dateToString(new Date()).substring(0,10)+".dll";
            File backupFile=new File(backupPath+"/"+fileName);
            backupFilePath = backupFile.getPath();
            backupFile.createNewFile();
            //第二个参数的意思
            //r    以只读的方式打开文本，也就意味着不能用write来操作文件
            //rw   读操作和写操作都是允许的
            //rws  每当进行写操作，同步的刷新到磁盘，刷新内容和元数据
            //rwd  每当进行写操作，同步的刷新到磁盘，刷新内容
            RandomAccessFile FileWrite= new RandomAccessFile(backupFile, "rwd");
            //从什么位置开始写
            FileWrite.seek(backupFile.length());
            //写入数据
            FileWrite.write(mapJson.getBytes());
            FileWrite.close();
        }catch (IOException e){
            Log.e(TAG,"备份所有密码失败");
            e.printStackTrace();
            return SimpleResult.error().msg("备份失败,请重试");
        }
        return SimpleResult.ok().data(backupFilePath);
    }

    @Override
    public SimpleResult recoveryKey(String pin,Map<String,Object> map) {
        //验证密码
        String pinInFile = (String) map.get("pin");
        String pinInput = MD5Utils.encrypt(Constant.PASSWORD_PREFIX+pin);
        if(pinInFile.equals(pinInput)){
            //软件版本校验

            //恢复数据
            String privateKey = (String) map.get("privateKey");
            List<JSONObject> data = (List<JSONObject>) map.get("data");
            List<Key> list = LitePal.select("uuid").find(Key.class);
            List<String> uuids = new ArrayList<>();
            list.forEach(key -> uuids.add(key.getUuid()));
            data.forEach(jsonObject->{
                Key key = JSON.toJavaObject(jsonObject, Key.class);
                key.setId(null);
                if(!uuids.contains(key.getUuid())){
                    //原来的数据解密后再次加密保存
                    //密码部分
                    String passwordOne = key.getPasswordOne();
                    String passwordTwo = key.getPasswordTwo();
                    if(null!=passwordOne && !"".equals(passwordOne)){
                        String decode = RSAUtils.decode(passwordOne, privateKey);
                        String publicKey1 = MyConfiguration.getInstance().getPublicKey();
                        String s = RSAUtils.encode(decode, publicKey1);
                        key.setPasswordOne(s);
                    }
                    if(null!=passwordTwo && !"".equals(passwordTwo)){
                        String decode = RSAUtils.decode(passwordTwo, privateKey);
                        String publicKey1 = MyConfiguration.getInstance().getPublicKey();
                        String s = RSAUtils.encode(decode, publicKey1);
                        key.setPasswordTwo(s);
                    }
                    //账号部分
                    String accountOne = key.getAccountOne().trim();
                    String accountTwo = key.getAccountTwo().trim();
                    if(null!=accountOne && !"".equals(accountOne)){
                        String decode = RSAUtils.decode(accountOne, privateKey);
                        String publicKey1 = MyConfiguration.getInstance().getPublicKey();
                        String s = RSAUtils.encode(decode, publicKey1);
                        key.setAccountOne(s);
                    }
                    if(null!=accountTwo && !"".equals(accountTwo)){
                        String decode = RSAUtils.decode(accountTwo, privateKey);
                        String publicKey1 = MyConfiguration.getInstance().getPublicKey();
                        String s = RSAUtils.encode(decode, publicKey1);
                        key.setAccountTwo(s);
                    }
                    key.save();
                }
            });
            return SimpleResult.ok().msg("恢复成功! 此次共从备份文件恢复"+data.size()+"条数据");
        }else{
            return SimpleResult.error().msg("你输入的密码与备份文件时输入的密码不一致,已停止解析备份文件");
        }
    }

    @Override
    public List<Key> getSimpleKeyList() {
        return LitePal.select("id", "name","otherName").find(Key.class);
    }

    @Override
    public String getRandomPassword(Integer length, Boolean haveNum, Boolean haveBigLetter, Boolean haveSpecialChar) {
        StringBuilder builder = new StringBuilder();
        int needType;
        if(!haveNum && !haveBigLetter && !haveSpecialChar){
            //全不要
            for(int i=0;i<length;i++){
                builder.append(getOneLetterSmall());
            }
            return builder.toString();
        }else if(haveNum && !haveBigLetter && !haveSpecialChar){
            //只要数字
            needType = 2;
            for(int i=0;i<length;i++){
                Random random = new Random();
                int nowType = random.nextInt(needType);
                switch (nowType){
                    case 0:{
                        builder.append(getOneLetterSmall());
                        break;
                    }
                    case 1:{
                        builder.append(getOneNumber());
                        break;
                    }
                    default:break;
                }
            }
            return builder.toString();
        }else if(!haveNum && haveBigLetter && !haveSpecialChar){
            //只要大写
            needType = 2;
            for(int i=0;i<length;i++){
                Random random = new Random();
                int nowType = random.nextInt(needType);
                switch (nowType){
                    case 0:{
                        builder.append(getOneLetterSmall());
                        break;
                    }
                    case 1:{
                        builder.append(getOneLetterBig());
                        break;
                    }
                    default:break;
                }
            }
            return builder.toString();
        }else if(!haveNum && !haveBigLetter){
            //只要特殊
            needType = 2;
            for(int i=0;i<length;i++){
                Random random = new Random();
                int nowType = random.nextInt(needType);
                switch (nowType){
                    case 0:{
                        builder.append(getOneLetterSmall());
                        break;
                    }
                    case 1:{
                        builder.append(getOneSpecialChar());
                        break;
                    }
                    default:break;
                }
            }
            return builder.toString();
        }else if(!haveNum){
            //不要数字
            needType = 3;
            for(int i=0;i<length;i++){
                Random random = new Random();
                int nowType = random.nextInt(needType);
                switch (nowType){
                    case 0:{
                        builder.append(getOneLetterSmall());
                        break;
                    }
                    case 1:{
                        builder.append(getOneLetterBig());
                        break;
                    }
                    case 2:{
                        builder.append(getOneSpecialChar());
                        break;
                    }
                    default:break;
                }
            }
            return builder.toString();
        }else if(!haveBigLetter){
            //不要大写
            needType = 3;
            for(int i=0;i<length;i++){
                Random random = new Random();
                int nowType = random.nextInt(needType);
                switch (nowType){
                    case 0:{
                        builder.append(getOneLetterSmall());
                        break;
                    }
                    case 1:{
                        builder.append(getOneNumber());
                        break;
                    }
                    case 2:{
                        builder.append(getOneSpecialChar());
                        break;
                    }
                    default:break;
                }
            }
            return builder.toString();
        }else if(!haveSpecialChar){
            //不要特殊字符
            needType = 3;
            for(int i=0;i<length;i++){
                Random random = new Random();
                int nowType = random.nextInt(needType);
                switch (nowType){
                    case 0:{
                        builder.append(getOneLetterSmall());
                        break;
                    }
                    case 1:{
                        builder.append(getOneNumber());
                        break;
                    }
                    case 2:{
                        builder.append(getOneLetterBig());
                        break;
                    }
                    default:break;
                }
            }
            return builder.toString();
        }else {
            //全都要
            //全要
            needType = 4;
            for(int i=0;i<length;i++){
                Random random = new Random();
                int nowType = random.nextInt(needType);
                switch (nowType){
                    case 0:{
                        builder.append(getOneLetterSmall());
                        break;
                    }
                    case 1:{
                        builder.append(getOneLetterBig());
                        break;
                    }
                    case 2:{
                        builder.append(getOneNumber());
                        break;
                    }
                    case 3:{
                        builder.append(getOneSpecialChar());
                        break;
                    }
                    default:break;
                }
            }
            return builder.toString();
        }
    }

    @Override
    public SimpleResult outputKeyToTxt() {
        List<Key> ids = LitePal.select("id").find(Key.class);
        StringBuilder builder = new StringBuilder();
        AtomicInteger i= new AtomicInteger(1);
        ids.forEach(id->{
            Key key = getDecodeKeyById(id.getId());
            builder.append("(").append(i.get()).append("/").append(ids.size()).append(")");
            builder.append("条目名称：").append(key.getName()).append("\n");
            builder.append("账号1：").append(key.getAccountOne()).append("   ").append("对应密码：").append(key.getPasswordOne()).append("\n");
            builder.append("账号2：").append(key.getAccountTwo()).append("   ").append("对应密码：").append(key.getPasswordTwo()).append("\n");
            builder.append("条目备注：").append(key.getRemarks()).append("\n\n");
            i.getAndIncrement();
        });
        String outputTxtStr = builder.toString();
        String backupFilePath = null;
        //获取外部存储路径
        String parentPath = LitePalApplication.getContext().getExternalFilesDir("").getAbsolutePath();
        //通过文件字节输出流存储
        try {
            //创建文件夹index888
            File backupPath=new File(parentPath+"/backup");
            if (!backupPath.exists()) {
                backupPath.mkdirs();
            }
            //创建成功后新建一个index.txt，并写入内容
            String fileName= "导出数据_"+BaseUtils.dateToString(new Date())+".txt";
            File backupFile=new File(backupPath+"/"+fileName);
            backupFilePath = backupFile.getPath();
            backupFile.createNewFile();
            //第二个参数的意思
            //r    以只读的方式打开文本，也就意味着不能用write来操作文件
            //rw   读操作和写操作都是允许的
            //rws  每当进行写操作，同步的刷新到磁盘，刷新内容和元数据
            //rwd  每当进行写操作，同步的刷新到磁盘，刷新内容
            RandomAccessFile FileWrite= new RandomAccessFile(backupFile, "rwd");
            //从什么位置开始写
            FileWrite.seek(backupFile.length());
            //写入数据
            FileWrite.write(outputTxtStr.getBytes());
            FileWrite.close();
        }catch (IOException e){
            Log.e(TAG,"导出数据失败");
            e.printStackTrace();
            return SimpleResult.error().msg("导出大数据失败,请重试");
        }
        return SimpleResult.ok().data(backupFilePath);
    }

    @Override
    public SimpleResult outputKeyToTxtToROM() {
        List<Key> ids = LitePal.select("id").find(Key.class);
        StringBuilder builder = new StringBuilder();
        AtomicInteger i= new AtomicInteger(1);
        ids.forEach(id->{
            Key key = getDecodeKeyById(id.getId());
            builder.append("(").append(i.get()).append("/").append(ids.size()).append(")");
            builder.append("条目名称：").append(key.getName()).append("\n");
            builder.append("账号1：").append(key.getAccountOne()).append("   ").append("对应密码：").append(key.getPasswordOne()).append("\n");
            builder.append("账号2：").append(key.getAccountTwo()).append("   ").append("对应密码：").append(key.getPasswordTwo()).append("\n");
            builder.append("条目备注：").append(key.getRemarks()).append("\n\n");
            i.getAndIncrement();
        });
        String outputTxtStr = builder.toString();
        String backupFilePath = null;
        //获取外部存储路径
        String parentPath = Environment.getExternalStoragePublicDirectory(Constant.EXTERNAL_STORAGE_LOCATION).getAbsolutePath()+"/TXT/";
        //通过文件字节输出流存储
        try {
            //创建文件夹index888
            File backupPath=new File(parentPath);
            if (!backupPath.exists()) {
                backupPath.mkdirs();
            }
            //创建成功后新建一个index.txt，并写入内容
            String fileName= "PasswordTXT"+BaseUtils.dateToString(new Date()).substring(0,10)+".txt";
            File backupFile=new File(backupPath+"/"+fileName);
            backupFilePath = backupFile.getPath();
            backupFile.createNewFile();
            //第二个参数的意思
            //r    以只读的方式打开文本，也就意味着不能用write来操作文件
            //rw   读操作和写操作都是允许的
            //rws  每当进行写操作，同步的刷新到磁盘，刷新内容和元数据
            //rwd  每当进行写操作，同步的刷新到磁盘，刷新内容
            RandomAccessFile FileWrite= new RandomAccessFile(backupFile, "rwd");
            //从什么位置开始写
            FileWrite.seek(backupFile.length());
            //写入数据
            FileWrite.write(outputTxtStr.getBytes());
            FileWrite.close();
        }catch (IOException e){
            Log.e(TAG,"导出数据失败");
            e.printStackTrace();
            return SimpleResult.error().msg("导出大数据失败,请重试");
        }
        return SimpleResult.ok().data(backupFilePath);
    }

    @Override
    public List<Key> getKeyVoForPDF() {
        List<Key> keyVo = new ArrayList<>();
        List<Key> keyList = getSimpleKeyList();
        keyList.forEach(key -> {
            keyVo.add(getDecodeKeyById(key.getId()));
        });
        return keyVo;
    }

    /**
     * 查找原始Key，未解密的Key
     * @param id
     * @return
     */
    private Key getNoDecodeKeyById(Integer id){
        return LitePal.find(Key.class, id);
    }

    /**
     * 将Key进行解密
     */
    private void decodeKey(Key key){
        //解密密码
        if(null==key.getPasswordOne() || "".equals(key.getPasswordOne())){
            key.setPasswordOne("");
        }else{
            key.setPasswordOne(RSAUtils.decode(key.getPasswordOne(),MyConfiguration.getInstance().getPrivateKey()));
        }
        if(null==key.getPasswordTwo() || "".equals(key.getPasswordTwo())){
            key.setPasswordTwo("");
        }else{
            key.setPasswordTwo(RSAUtils.decode(key.getPasswordTwo(),MyConfiguration.getInstance().getPrivateKey()));
        }
        //解密账号
        if(null==key.getAccountOne() || "".equals(key.getAccountOne())){
            key.setAccountOne("");
        }else{
            key.setAccountOne(RSAUtils.decode(key.getAccountOne(),MyConfiguration.getInstance().getPrivateKey()));
        }
        if(null==key.getAccountTwo() || "".equals(key.getAccountTwo())){
            key.setAccountTwo("");
        }else{
            key.setAccountTwo(RSAUtils.decode(key.getAccountTwo(),MyConfiguration.getInstance().getPrivateKey()));
        }
    }

    /**
     * 将Key进行加密
     */
    private void encodeKey(Key key){
        //加密密码
        if(null==key.getPasswordOne() || "".equals(key.getPasswordOne())){
            key.setPasswordOne("");
        }else{
            key.setPasswordOne(RSAUtils.encode(key.getPasswordOne(), MyConfiguration.getInstance().getPublicKey()));
        }
        if(null==key.getPasswordTwo() || "".equals(key.getPasswordTwo())){
            key.setPasswordTwo("");
        }else{
            key.setPasswordTwo(RSAUtils.encode(key.getPasswordTwo(),MyConfiguration.getInstance().getPublicKey()));
        }
        //加密账号
        if(null==key.getAccountOne() || "".equals(key.getAccountOne())){
            key.setAccountOne("");
        }else{
            key.setAccountOne(RSAUtils.encode(key.getAccountOne(), MyConfiguration.getInstance().getPublicKey()));
        }
        if(null==key.getAccountTwo() || "".equals(key.getAccountTwo())){
            key.setAccountTwo("");
        }else{
            key.setAccountTwo(RSAUtils.encode(key.getAccountTwo(),MyConfiguration.getInstance().getPublicKey()));
        }
    }

    /**
     * 判断Key的输入是否合法
     * 屎山代码
     * @param key
     * @return
     */
    private SimpleResult checkInput(Key key){
        if (null==key.getName() || "".equals(key.getName())){
            return SimpleResult.error().msg("名称不能为空!");
        }
        else if(key.getName().length() > Constant.NAME_MAX_LENGTH){
            return SimpleResult.error().msg("名称长度不能超过"+Constant.NAME_MAX_LENGTH+"位!");
        }
        //第一个账号为空
        if (null==key.getAccountOne() || "".equals(key.getAccountOne())){
            //两个账号都为空
            if(null==key.getAccountTwo() || "".equals(key.getAccountTwo())){
                return SimpleResult.error().msg("不能两个账号都是空!");
            }else{
                //第一个账号为空,第二个账号有输入
                if(key.getAccountTwo().length() > Constant.ACCOUNT_MAX_LENGTH){
                    return SimpleResult.error().msg("注意账号 / 密码的长度不要超过"+Constant.ACCOUNT_MAX_LENGTH+"位!");
                }
                if(null==key.getPasswordTwo() || "".equals(key.getPasswordTwo())){
                    return SimpleResult.error().msg("有输入的账号对应的密码不能为空");
                }
                if(key.getPasswordTwo().length() > Constant.ACCOUNT_MAX_LENGTH){
                    return SimpleResult.error().msg("注意账号 / 密码的长度不要超过"+Constant.ACCOUNT_MAX_LENGTH+"位!");
                }
            }
        }else{
            //第一个账号不为空
            if(null==key.getPasswordOne() || "".equals(key.getPasswordOne())){
                return SimpleResult.error().msg("有输入的账号对应的密码不能为空");
            }
            if(key.getAccountOne().length() > Constant.ACCOUNT_MAX_LENGTH || key.getPasswordOne().length() > Constant.ACCOUNT_MAX_LENGTH){
                return SimpleResult.error().msg("注意账号 / 密码的长度不要超过"+Constant.ACCOUNT_MAX_LENGTH+"位!");
            }
            //第二个账号也不为空,但第二个账号密码为空
            if(null!=key.getAccountTwo() && !"".equals(key.getAccountTwo())){
                if(key.getAccountTwo().length() > Constant.ACCOUNT_MAX_LENGTH || key.getPasswordOne().length() > Constant.ACCOUNT_MAX_LENGTH){
                    return SimpleResult.error().msg("注意账号 / 密码的长度不要超过"+Constant.ACCOUNT_MAX_LENGTH+"位!");
                }
                if(null==key.getPasswordTwo() || "".equals(key.getPasswordTwo())){
                    return SimpleResult.error().msg("有输入的账号对应的密码不能为空");
                }else{
                    if(key.getPasswordTwo().length() > Constant.ACCOUNT_MAX_LENGTH || key.getPasswordOne().length() > Constant.ACCOUNT_MAX_LENGTH){
                        return SimpleResult.error().msg("注意账号 / 密码的长度不要超过"+Constant.ACCOUNT_MAX_LENGTH+"位!");
                    }
                }
            }
        }
        if(null!=key.getRemarks() && !"".equals(key.getRemarks()) && key.getRemarks().length() > Constant.REMARKS_MAX_LENGTH){
            return SimpleResult.error().msg("备注的长度不要超过"+Constant.REMARKS_MAX_LENGTH+"位!");
        }
        return SimpleResult.ok();
    }

    /**
     * 随机一个小写字母
     * @return
     */
    private char getOneLetterSmall(){
        Random random = new Random();
        return ((char)(97 + random.nextInt(26)));
    }

    /**
     * 随机一个大写字母
     * @return
     */
    private char getOneLetterBig(){
        Random random = new Random();
        return ((char)(65 + random.nextInt(26)));
    }

    /**
     * 随机获取数字
     * @return
     */
    private char getOneNumber(){
        Random random = new Random();
        return ((char)(48 + random.nextInt(10)));
    }

    /**
     * 随机获取一个特殊字符
     */
    private char getOneSpecialChar(){
        Random random = new Random();
        String specialChar = Constant.SPECIAL_CHAR;
        return specialChar.charAt(random.nextInt(specialChar.length()));
    }
}
