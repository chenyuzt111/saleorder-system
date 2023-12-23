package com.benewake.saleordersystem.mapper;

import net.sourceforge.pinyin4j.PinyinHelper;

public class pinyin {
    public static void main(String[] args) {
        String username = "张1三1";
        String pinyin = convertToPinyin(username);
        String result = removeSpacesAndDigits(pinyin);

        System.out.println(result);
    }

    private static String convertToPinyin(String input) {
        StringBuilder pinyinBuilder = new StringBuilder();
        char[] chars = input.toCharArray();

        for (char c : chars) {
            // 将中文字符转换为拼音，非中文字符保持不变
            String[] pinyinArray = PinyinHelper.toHanyuPinyinStringArray(c);
            if (pinyinArray != null && pinyinArray.length > 0) {
                pinyinBuilder.append(pinyinArray[0]);
            } else {
                pinyinBuilder.append(c);
            }
        }

        return pinyinBuilder.toString();
    }

    private static String removeSpacesAndDigits(String input) {
        // 去除空格和数字
        return input.replaceAll("[\\s\\d]", "");
    }
}
