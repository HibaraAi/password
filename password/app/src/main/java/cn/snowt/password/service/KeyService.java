package cn.snowt.password.service;

import java.util.List;
import java.util.Map;

import cn.snowt.password.entity.Key;
import cn.snowt.password.util.SimpleResult;

/**
 * @Author: HibaraAi
 * @Date: 2022-03-03 22:15:45
 * @Description:
 */
public interface KeyService {
    /**
     * 根据id查询某个已解密Key
     * @param id
     * @return
     */
    Key getDecodeKeyById(Integer id);

    /**
     * 新增一个Key
     * @param key
     * @return
     */
    SimpleResult addOne(Key key);

    /**
     * 更改Key
     * @param key
     * @return
     */
    SimpleResult updateById(Key key);

    /**
     * 删除Key
     * @param id
     * @return
     */
    SimpleResult deleteById(Integer id);

    /**
     * 备份已有Key
     * @param pin 设置的密码，仅供从备份文件恢复时使用
     * @return
     */
    SimpleResult backupKey(String pin);

    /**
     * 从备份文件恢复Key
     * @param pin 验证备份时输入的密码
     * @param map
     * @return
     */
    SimpleResult recoveryKey(String pin, Map<String,Object> map);

    /**
     * 获取key集合，只包含主键id、Key别名和key名称
     * @return
     */
    List<Key> getSimpleKeyList();

    /**
     * 随机一个密码
     * @param length 密码的长度
     * @param haveNum 是否包含数字
     * @param haveBigLetter 是否包含大写字母
     * @param haveSpecialChar 是否包含特殊字符
     * @return
     */
    String getRandomPassword(Integer length,Boolean haveNum,Boolean haveBigLetter,Boolean haveSpecialChar);

    /**
     * 导出所有密码到txt文件
     * @return
     */
    SimpleResult outputKeyToTxt();
}
