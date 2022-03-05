package cn.snowt.password.service;

import cn.snowt.password.util.SimpleResult;

/**
 * @Author: HibaraAi
 * @Date: 2022-02-20 09:34:36
 * @Description:
 */
public interface LoginService {
    /**
     * 用户登录
     * @param inputPassword 用户输入的密码
     * @return
     */
    SimpleResult login(String inputPassword);

    /**
     * 设置登录密码
     * @param isFirstUse 是第一次使用本软件吗
     * @param oldPassword 旧密码
     * @param newPassword 新密码
     * @param newPasswordAgain 二次输入新密码
     * @return
     */
    SimpleResult setPassword(Boolean isFirstUse,String oldPassword,String newPassword,String newPasswordAgain);

}
