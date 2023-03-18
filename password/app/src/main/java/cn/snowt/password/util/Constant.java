package cn.snowt.password.util;

/**
 * @Author: HibaraAi
 * @Date: 2022-02-20 09:33:35
 * @Description: 常量
 */
public class Constant {
    /**
     * 软件内部版本
     */
    public static final Integer INTERNAL_VERSION = 1;
    /**
     * MD5加密的前缀
     */
    public static final String PASSWORD_PREFIX = "MD5*jiami-de$qianzhui#";

    /**
     * 密码名称的最大长度
     */
    public static final Integer NAME_MAX_LENGTH = 20;
    /**
     * 账号及密码的最长长度
     */
    public static final Integer ACCOUNT_MAX_LENGTH = 80;
    /**
     * 备注的最长长度
     */
    public static final Integer REMARKS_MAX_LENGTH = 200;

    public static final Integer OTHER_NAME_MAX_LENGTH = 100;
    /**
     * 时间格式
     */
    public static final String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";

    /**
     * 最大密码错误次数, 达到后将受到制裁
     */
    public static final Integer MAX_LOGIN_FAIL_COUNT = 5;

    /**
     * 最大制裁等级, 达到后将删库
     */
    public static final Integer MAX_PUNISHMENT_LEVEL = 15;

    /**
     * 生成密码时包含的特殊字符
     */
    public static final String SPECIAL_CHAR = "_&$@-+*/.";

    /**
     * 关于的信息
     */
    public static final String STRING_ABOUT = "作者: HibaraAi\n" +
            "反馈QQ:3192233122(注明添加缘由)\n" +
            "版本: 1.0.4\n" +
            "更新日期: 2023-03-18\n" +
            "开源代码(长按复制): https://github.com/HibaraAi/password";

    /**
     * 帮助信息
     */
    public static final String STRING_HELP = "" +
            "一、功能介绍\n" +
            "本软件主要提供密码存储服务，一个词条可以存储两个账号密码，其中只有账号、密码才会加密存储。此外，本软件还提供密码生成功能，可以按要求生成指定格式的密码。\n" +
            "\n二、推荐正确用法\n" +
            "本软件虽说是密码管理器，但还是不建议直接将密码存储在本软件。推荐的用法是：账号直接填写在本软件，密码则是填写密码提示。例如：【我的google账号为：10086，账号直接存10086；我的真正密码为@google20210804,但我绝不会将密码存储在本软件，我只会存个提示@谁210804，@为特殊字符，谁表示的是什么账号，210804表示的是账号注册日期】。建议使用者举一反三。通常，我们需要注册一些无关紧要的账号，比如去某个论坛下载PJ软件，但该论坛需要登录回复帖子才可以下载，这种账号我们大概率一年也用不到几次，这时，就建议使用密码生成功能，生成一个密码直接存储在本软件，而这个账号就算没了也无关紧要。\n" +
            "\n三、登录有关\n" +
            "登陆密码请牢记，登录密码只有登陆后才能在设置里更改。不要试图猜密码的方式来找回密码，猜错5次会受到一次制裁，第一次制裁时间位1分钟，第n次制裁时间为n^3分钟，(制裁等级的n最大为10。当内部记录的n达到"+MAX_PUNISHMENT_LEVEL+"时，软件会自动清空本软件存储的所有数据。每当输入正确密码，n重置为1。)\n" +
            "\n四、备份相关\n" +
            "本软件所有数据都是本地存储，这就导致一个问题，软件卸载了或者去系统设置里将本软件的数据清除了，软件的所有设置、所有数据都会没了。所以最后还是加了一个备份功能。备份只会备份数据库中存储的密码词条，登录密码等设置是不会备份的，备份时需要设置一个密码，这个密码很重要，从备份文件恢复密码时需要验证这个密码。备份文件会存储在‘存储卡根目录/Android/data/cn.snowt.pin/backup’目录下，名称为‘lianliankanTEMP时间.dll’\n导出功能则是直接将密码数据明文导出到txt文件。\n" +
            "2023-03-18作者发现安卓对‘Android/data目录’的读写做了限制，部分机型不能将data中的数据复制走，甚至不能访问。本软件的备份数据、导出数据都在data目录下，安卓更新加上作者设计失误带来的不便，作者深感抱歉！将在后续更新中更换存储策列。亲测华为用户可以将data下的数据用蓝牙分享出去。" +
            "\n五、密码生成相关\n" +
            "为什么密码生成没有按照我的要求生成？因为随机算法是我随便写的，在生成包含特殊字符、包含大小写字母、包含数字的6位密码时，可能会缺少某些类型的字符，但你不勾选的绝对不会生成。另外，如果你把保存类型全勾上，只有保存新密码会生效，如果密码长度项目勾上多个，只有长的哪项才会生效。\n" +
            "\n六、写在最后\n" +
            "1.本软件不需要任何额外敏感权(目前仅用了震动马达的控制权限)，就算是备份文件的存储也是走Android推荐的保护用户的方式存储，直接存储在本软件专属的文件夹下，文件读取也是走Android提供的文件选择器。作者绝不会盗用数据，因此，你在使用本软件时产生的任何形式的损失都与本作者无关！下面有网址有开源代码可查。\n" +
            "2.由于数据存储全都在本地的sqlite数据库(也有部分存在xml文件里)，正常的Android设备下，其他应用是访问不到本应用存储的所有数据，但由于root权限可以随意访问任何应用的数据，建议不要破解root权限。\n" +
            "3.如何获取更新？不支持检测更新，你必须到开源网址检查是否发布了新的版本(新release)\n" +
            "4.建议和bug反馈：3192233122@qq.com，邮件标题注明【连连看bug反馈】\n" +
            "5.开源网址打不开请多刷新几次，或者访问镜像https://gitee.com/HibaraAi/password\n" +
            "\n七、本次更新内容\n" +
            "1.修复搜索bug\n" +
            "2.压缩软件大小\n";
    public static final String SHARE_PREFERENCES_PRIVATE_KEY = "privateKey";
    public static final String SHARE_PREFERENCES_PUBLIC_KEY = "publicKey";
}
