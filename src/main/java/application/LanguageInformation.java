package application;

import java.util.Map;
import java.util.TreeMap;

/**
 * 语言信息类
 */
public class LanguageInformation {

    //Map类，用于存放关键字与其下标，模仿数组，便于查找
    public static Map<String,Integer> keyWordNameMap;
    //关键字总数--32个
    public static int KEY_WORD_NUMBER = 32;
    //变量申明关键字统计
    public static Map<String,Integer> keyWord_variablesMap;
    //课申明的系统变量总数--8个
    public static int SYS_VARIABLES = 8;

    static {
        //C语言系统关键字--32个
        String[] keyWord = {
                "char", "short", "int", "long", "enum", "float", "double", "signed",
                "unsigned", "struct", "union", "void", "for", "do", "while", "break",
                "continue", "if", "else", "goto", "switch", "case", "default", "return",
                "auto", "static", "extern", "register", "const", "sizeof", "typedef", "volatile"
        };

        //实例化Map对象，用于存储关键字
        keyWordNameMap = new TreeMap<String, Integer>();
        //初始化关键字下标
        for (int i = 0; i < KEY_WORD_NUMBER; i++) {
            keyWordNameMap.put(keyWord[i], i);
        }

        /*
         * 新增：关键字是否统计的关系，根据源程序检测时的大括号的层级关系进行判断
         */
        String[] keyWord_variables = {
                "char", "short", "int", "long", "float", "double", "unsigned", "auto"
        };
        //实例化keyWord_variablesMap对象，用于存储关键字位置
        keyWord_variablesMap = new TreeMap<String, Integer>();
        //初始化关键字下标
        for (int i = 0; i < SYS_VARIABLES; i++) {
            keyWord_variablesMap.put(keyWord_variables[i], i);
        }
    }

}
