package cn.snowt.password.entity;

import org.litepal.crud.LitePalSupport;

import java.io.Serializable;
import java.util.Date;
import java.util.Objects;

/**
 * @Author: HibaraAi
 * @Date: 2022-02-20 09:39:20
 * @Description:
 */
public class Key extends LitePalSupport implements Serializable {
    private Integer id;  //主键，受LitePal受限，只能使用int自增作为主键
    private String uuid;  //uuid在备份恢复时用于辨别重复
    private String name;  //名称
    private String accountOne;   //账号一
    private String passwordOne;  //账号一对应的密码
    private String accountTwo;  //账号二
    private String passwordTwo;  //账号二对应的密码
    private String remarks;  //备注
    private Date createDate;  //创建日期
    private String otherName;  //别名

    public Key() {
    }

    public Key(Integer id, String uuid, String name, String accountOne, String passwordOne, String accountTwo, String passwordTwo, String remarks, Date createDate, String otherName) {
        this.id = id;
        this.uuid = uuid;
        this.name = name;
        this.accountOne = accountOne;
        this.passwordOne = passwordOne;
        this.accountTwo = accountTwo;
        this.passwordTwo = passwordTwo;
        this.remarks = remarks;
        this.createDate = createDate;
        this.otherName = otherName;
    }

    /**
     * 根据uuid判断是否一致
     * @param o
     * @return
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) {return true;}
        if (o == null || getClass() != o.getClass()) {return false;}
        Key key = (Key) o;
        return Objects.equals(uuid, key.uuid);
    }

    @Override
    public int hashCode() {
        return Objects.hash(uuid);
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAccountOne() {
        return accountOne;
    }

    public void setAccountOne(String accountOne) {
        this.accountOne = accountOne;
    }

    public String getPasswordOne() {
        return passwordOne;
    }

    public void setPasswordOne(String passwordOne) {
        this.passwordOne = passwordOne;
    }

    public String getAccountTwo() {
        return accountTwo;
    }

    public void setAccountTwo(String accountTwo) {
        this.accountTwo = accountTwo;
    }

    public String getPasswordTwo() {
        return passwordTwo;
    }

    public void setPasswordTwo(String passwordTwo) {
        this.passwordTwo = passwordTwo;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public String getOtherName() {
        return otherName;
    }

    public void setOtherName(String otherName) {
        this.otherName = otherName;
    }
}
