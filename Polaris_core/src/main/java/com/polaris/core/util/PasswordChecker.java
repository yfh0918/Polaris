package com.polaris.core.util;

import java.util.LinkedHashSet;
import java.util.Set;

/**
 * 验证密码是否符合规则
 */
public class PasswordChecker {

    private boolean upperCase = false; // 包含大写字母
    private boolean lowerCase = false; // 包含小写字母
    private boolean letter = true; // 包含字母
    private boolean digit = true; // 包含数字
    private boolean special = true; // 包含特殊字符
    private Set<Character> specialCharSet = null; // 特殊字符集合
    private int minLength = 8; // 最小长度
    private int maxLength = 20; // 最大长度

    public static PasswordChecker INSTANCE = new PasswordChecker();
    private PasswordChecker(){
        this.specialCharSet = defaultSpecialCharSet();
    }

    /**
     * 密码符合规则，返回true
     */
    public boolean check(String password,boolean upperCase, boolean lowerCase, boolean letter, boolean digit, boolean special){
    	this.upperCase = upperCase;
    	this.lowerCase = lowerCase;
    	this.letter = letter;
    	this.digit = digit;
    	this.special = special;
    	return check(password);
    }
    public boolean check(String password, boolean letter, boolean digit, boolean special){
    	this.letter = letter;
    	this.digit = digit;
    	this.special = special;
    	return check(password);
    }
    public boolean check(String password){
        if(password==null || password.length()<this.minLength || password.length()>this.maxLength){
            // 长度不符合
            return false;
        }

        boolean containUpperCase = false;
        boolean containLowerCase = false;
        boolean containLetter = false;
        boolean containDigit = false;
        boolean containSpecial = false;

        for(char ch : password.toCharArray()){
            if(Character.isUpperCase(ch)){
                containUpperCase = true;
                containLetter = true;
            }else if(Character.isLowerCase(ch)){
                containLowerCase = true;
                containLetter = true;
            }else if(Character.isDigit(ch)){
                containDigit = true;
            }else if(this.specialCharSet.contains(ch)){
                containSpecial = true;
            }else{
                // 非法字符
                return false;
            }
        }

        if(this.upperCase && !containUpperCase){
            return false;
        }
        if(this.lowerCase && !containLowerCase){
            return false;
        }
        if(this.letter && !containLetter){
            return false;
        }
        if(this.digit && !containDigit){
            return false;
        }
        if(this.special && !containSpecial){
            return false;
        }
        return true;
    }

    public static Set<Character> defaultSpecialCharSet(){
        Set<Character> specialChars = new LinkedHashSet<>();
        // 键盘上能找到的符号
        specialChars.add(Character.valueOf('~'));
        specialChars.add(Character.valueOf('`'));
        specialChars.add(Character.valueOf('!'));
        specialChars.add(Character.valueOf('@'));
        specialChars.add(Character.valueOf('#'));
        specialChars.add(Character.valueOf('$'));
        specialChars.add(Character.valueOf('%'));
        specialChars.add(Character.valueOf('^'));
        specialChars.add(Character.valueOf('&'));
        specialChars.add(Character.valueOf('*'));
        specialChars.add(Character.valueOf('('));
        specialChars.add(Character.valueOf(')'));
        specialChars.add(Character.valueOf('-'));
        specialChars.add(Character.valueOf('_'));
        specialChars.add(Character.valueOf('+'));
        specialChars.add(Character.valueOf('='));
        specialChars.add(Character.valueOf('{'));
        specialChars.add(Character.valueOf('['));
        specialChars.add(Character.valueOf('}'));
        specialChars.add(Character.valueOf(']'));
        specialChars.add(Character.valueOf('|'));
        specialChars.add(Character.valueOf('\\'));
        specialChars.add(Character.valueOf(':'));
        specialChars.add(Character.valueOf(';'));
        specialChars.add(Character.valueOf('"'));
        specialChars.add(Character.valueOf('\''));
        specialChars.add(Character.valueOf('<'));
        specialChars.add(Character.valueOf(','));
        specialChars.add(Character.valueOf('>'));
        specialChars.add(Character.valueOf('.'));
        specialChars.add(Character.valueOf('?'));
        specialChars.add(Character.valueOf('/'));
        return specialChars;
    }
}
