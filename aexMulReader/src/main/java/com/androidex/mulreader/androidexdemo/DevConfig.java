package com.androidex.lockaxial.androidexdemo;

/**
 * Created by Administrator on 2018/7/18.
 */

public class DevConfig {
    public static final String START = "f5";
    public static final String Zero = "00";
    public static final String Fingerprint_CMD_1 = "01";
    public static final String Fingerprint_CMD_2 = "02";
    public static final String Fingerprint_CMD_3 = "03";
    public static final String MOUNT_CMD = "09";
    public static final String DELETE_CMD_ALL = "05";
    public static final String DELETE_CMD_NUMBER = "04";
    public static final String CONTRAST_USER = "0C";
    public static final String Power_1 = "01";
    public static final String Power_2 = "02";
    public static final String Power_3 = "03";
    public static final String END = "f5";

    public static final String ACK_SUCCESS = "00"; //操作成功
    public static final String ACK_FAIL = "01"; //操作失败
    public static final String ACK_FULL = "04"; //指纹数据库已满
    public static final String ACK_NOUSER = "05"; //无此用户
    public static final String ACK_USER_OCCUPIED  = "06"; //此ID用户已存在
    public static final String ACK_USER_EXIST  = "07"; //用户已存在
    public static final String ACK_TIMEOUT  = "08"; //采集超时


    public static final String IDCARD_SAM = "AAAAAA9669000312FFEE";
    public static final String IDCARD_FIND = "AAAAAA96690003200122";
    public static final String IDCARD_SELT = "AAAAAA96690003200221";
    public static final String IDCARD_READ = "AAAAAA96690003300132";
    public static final String IDCARD_SLEEP = "AAAAAA966900020002";
    public static final String IDCARD_WEAK = "AAAAAA966900020103";

    public static final String NFC_FIND = "55555569960003200122";
    public static final String CPU_FIND = "55555569960003200122";

    public static final String BANK_NUMBER = "55555569960008500200B2010C00E5";

//    public static final String BANK_FIND_1 = "00A404000E315041592E5359532E4444463031";
//    public static final String BANK_FIND_2 = "00B2010c00";
//    public static final String BANK_FIND_3 = "00A4040008A000000333010101";
//    public static final String BANK_FIND_4 = "80A800000b8309010000000004000156";
//    public static final String BANK_FIND_5 = "00B2010C00";
//    public static final String BANK_FIND_6 = "00B2020C00";

    public static final String BANK_FIND_1 = "55555569960016500200A404000E315041592E5359532E44444630318D";
    public static final String BANK_FIND_2 = "55555569960008500200B2010C00E5";
    public static final String BANK_FIND_3 = "55555569960010500200A4040008A0000003330101017B";
    public static final String BANK_FIND_4 = "55555569960013500280A800000b8309010000000004000156BA";
    public static final String BANK_FIND_5 = "55555569960008500200B2010C00E5";
    public static final String BANK_FIND_6 = "55555569960008500200B2020C00E6";
}
