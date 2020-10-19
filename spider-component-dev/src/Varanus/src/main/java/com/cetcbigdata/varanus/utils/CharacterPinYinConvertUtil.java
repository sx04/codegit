package com.cetcbigdata.varanus.utils;

/**
 * Created with IDEA
 * author:Matthew
 * Date:2019-3-26
 * Time:19:04
 */

import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
import net.sourceforge.pinyin4j.format.HanyuPinyinVCharType;
import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination;

/**
 * 汉字转拼音工具类，保留汉字与字母，其他部分忽略，需添加jar包：pinyin4j-2.5.0.jar
 *
 * @author jianggujin
 *
 */
public class CharacterPinYinConvertUtil {
    /** 设置汉字拼音输出的格式 **/
    HanyuPinyinOutputFormat format = null;

    /**
     * 默认构造方法，初始化汉字拼音输出格式
     */
    public CharacterPinYinConvertUtil()
    {
        // 设置汉字拼音输出的格式
        format = new HanyuPinyinOutputFormat();
        // 不使用音调标记，
        format.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
        // 如果使用WITH_TONE_MARK，则必须指定setVCharType为WITH_U_UNICODE，否则报错
        // fmt.setToneType(HanyuPinyinToneType.WITH_TONE_MARK);
        // fmt.setVCharType(HanyuPinyinVCharType.WITH_U_UNICODE);

        // 韵母“驴”(lu->lv)使用V来代替
        format.setVCharType(HanyuPinyinVCharType.WITH_V);
        // 返回的拼音为小字字母
        format.setCaseType(HanyuPinyinCaseType.LOWERCASE);
    }

    /**
     * 汉字转拼音
     *
     * @param text
     *           待转换字符串
     * @return allPinYin
     */
    public String toPinYin(String text)
    {
        StringBuilder allPinYin = new StringBuilder();
        for (int i = 0; i < text.length(); i++)
        {
            char wordChar = text.charAt(i);
            // 如果为汉字
            if (Character.toString(wordChar).matches("[\\u4E00-\\u9FA5]"))
            {
                try
                {
                    // 返回汉字的全部拼音(因为有些汉字为多音字，否则只返回一个)
                    String[] pinYinArray = PinyinHelper.toHanyuPinyinStringArray(
                            wordChar, format);
                    // 取第一个拼音
                    String pinYin = pinYinArray[0];
                    if (pinYin != null)
                    {
                        allPinYin.append(pinYin);
                    }
                }
                catch (Exception e)
                {
                }
            }
            else if (((int) wordChar >= 65 && (int) wordChar <= 90)
                    || ((int) wordChar >= 97 && (int) wordChar <= 122))
            {
                allPinYin.append(wordChar);
            }
        }
        return allPinYin.toString();
    }

    /**
     * 汉字转拼音
     *
     * @param inputString
     *           待转换字符串
     * @return pinYinSet
     */
    public static String getPingYin(String inputString) {
        HanyuPinyinOutputFormat format = new HanyuPinyinOutputFormat();
        format.setCaseType(HanyuPinyinCaseType.LOWERCASE);
        format.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
        format.setVCharType(HanyuPinyinVCharType.WITH_V);
        String output = "";
        if (inputString != null && inputString.length() > 0
                && !"null".equals(inputString)) {
            char[] input = inputString.trim().toCharArray();
            try {
                for (int i = 0; i < input.length; i++) {
                    if (java.lang.Character.toString(input[i]).matches(
                            "[\\u4E00-\\u9FA5]+")) {
                        String[] temp = PinyinHelper.toHanyuPinyinStringArray(
                                input[i], format);
                        output += temp[0];
                    } else {
                        output += java.lang.Character.toString(input[i]);
                    }
                }
            } catch (BadHanyuPinyinOutputFormatCombination e) {
                e.printStackTrace();
            }
        } else {
            return "*";
        }
        return output;
    }
    /**
     * 处理转换
     *
     * @return
     */
    private String[][] doExchange(String[][] strJaggedArray)
    {
        int len = strJaggedArray.length;
        if (len >= 2)
        {
            int len1 = strJaggedArray[0].length;
            int len2 = strJaggedArray[1].length;
            int newlen = len1 * len2;
            String[] temp = new String[newlen];
            int index = 0;
            for (int i = 0; i < len1; i++)
            {
                for (int j = 0; j < len2; j++)
                {
                    temp[index] = strJaggedArray[0][i] + strJaggedArray[1][j];
                    index++;
                }
            }
            String[][] newArray = new String[len - 1][];
            for (int i = 2; i < len; i++)
            {
                newArray[i - 1] = strJaggedArray[i];
            }
            newArray[0] = temp;
            return doExchange(newArray);
        }
        else
        {
            return strJaggedArray;
        }
    }

}
