package application;

import com.google.common.collect.Lists;
import lombok.Data;
import java.util.*;
import java.util.regex.Pattern;

/**
 * 每个学生代码文件的代码信息
 */

@Data
public class SourcePrograme {


    //为了实现可视化效果，需要将处理后的源代码保存起来源代码保存起来
    Map<Integer, String> programeInfo = new TreeMap<Integer, String>();

    /**
     * 代码的分割正则
     */
    private static final String regex = "\\:|\\,|\\s|;|\\(|\\)|\\&|\\|";

    /**
     * 学生姓名
     */
    private String stuName;

    /**
     * 关键字统计数组List
     */
    List<Integer> keyWordCountList = Lists.newArrayList();

    // 语言信息类，创建一次，执行其静态方法，避免重复创建
    LanguageInformation li = new LanguageInformation();

    //在大括号的层级关系条件下，判断读取的指定字符串是否应该进行加1操作
    List<Integer> variableList = Lists.newArrayList();


    //用于记录读取文件的第几行了，次变量就在segmentStr方法处利用，并且存入Map中
    int lineCount = 0;


    /**
     * 关键字统计
     */
    //判断是否进入一个括号，进入一层加1，退出一层减1
    int enterBrackets = 0;

    protected void codeKeyWordCount(String str) {
        List<String> strList = this.segmentString(str);
        if (strList == null) {
            return;
        }

        //创建油标
        Iterator<String> it = strList.iterator();

        while (it.hasNext()) {

            String s = it.next();
            if (s.equals("{")) {
                this.enterBrackets++;
                continue;
            } else if (s.equals("}")) {
                this.enterBrackets--;
                continue;
            }

            s = BracketsClass(s);

            //二次处理
            String[] strArray = this.BracketsClass2(s);

            if (this.enterBrackets == 0) {
                this.initSysVariables();
            }

            if (strArray == null) {

                addOfKeyWordCountList(s);
            } else {
                for (int i = 0; i < strArray.length; i++) {
                    addOfKeyWordCountList(strArray[i]);
                }
            }
        }
    }


    /**
     * 大括号层级检测方法
     * 为了减少代码的冗余度，抽象出大括号层级检测方法
     * 为了防止其有多个括号连续，因此用循环
     * s.length()-temps.length(),大小代表取消取代的括号数目
     */
    private String BracketsClass(String str) {
        //针对复杂的情况，判断是否含有大括号
        if (str.startsWith("{")) {
            String temps = str.replace("{", "");
            this.changeBrackets(str.length() - temps.length(), "{");
            // 将大括号去掉
            return str.replace("{", "");
        } else if (str.startsWith("}")) {
            String temps = str.replace("}", "");
            this.changeBrackets(str.length() - temps.length(), "}");
            // 将大括号去掉
            return str.replace("}", "");
        }
        //判断理由及其条件同上
        if (str.endsWith("{")) {
            String temps = str.replace("{", "");
            this.changeBrackets(str.length() - temps.length(), "{");
            // 将大括号去掉
            return str.replace("{", "");
        } else if (str.endsWith("}")) {
            String temps = str.replace("}", "");
            this.changeBrackets(str.length() - temps.length(), "}");
            // 将大括号去掉
            return str.replace("}", "");
        }
        /**
         * 有些同学把大括号嵌入在字符串里面eg: do{printf / do{if
         * 此处不作处理，如果在上一级方法，即codeKeyWordCount中额外的方法，进行检测
         */
        return str;
    }


    /**
     * 依旧是大括号层级判断方法，不过是二次判断
     * 针对某些代码的特殊情况，进行判断大括号否在处理过后的字符串中
     * 接受等待处理的字符进行（二次）处理，返回一个数组，便于关键字统计
     */
    private String[] BracketsClass2(String str) {
        //判断提交进来的字符串是否包含大括号在其中,在字符串中如果没有找到则返回值为-1
        if (str.indexOf("{") == -1 && str.indexOf("}") == -1) {
            return null;
            //接受的字符串中包含左大括号
        } else if (str.indexOf("{") != -1 || str.indexOf("}") != -1) {
            Pattern pattern = Pattern.compile("\\{|\\}");
            //创建临时变量，主要用于替换的大括号数目
            String s;
            s = str.replace("{", "");
            //根据替换的大括号数目，对大括号层级标志位进行加减操作
            for (int i = 0; i < str.length() - s.length(); i++) {
                this.enterBrackets++;
            }
            //理由同上，只不过此处是对右大括号进行自减操作，退出N层大括号
            s = str.replace("}", "");
            for (int i = 0; i < str.length() - s.length(); i++) {
                this.enterBrackets--;
            }

//System.out.println("处理过后的字符串："+pattern.split(str));

            return pattern.split(str);
            //下面的语句为了迎合JAVA虚拟机报错机智而设定，理论上只有上面两种情况的出现
        } else {
            System.out.println("警告：系统遇到严重错误，在BracketsClass2方法中！");
            System.exit(0);
        }
        return null;
    }

    /**
     * 改变大括号层级标志位的方法
     * 减少代码冗余，从大括号层级检测方法中抽出改变大括号层级标志位的方法
     * count实际达标大括号的出现次数，str表示未处理的字符串
     */
    private void changeBrackets(int count, String str) {
        //判断是否与大括号匹配
        if (str.equals("{")) {
            for (int i = 0; i < count; i++) {
                // 进入大括号一层，标志位需要加1
                this.enterBrackets++;
            }
        }
        if (str.equals("}")) {
            for (int i = 0; i < count; i++) {
                // 进入大括号一层，标志位需要加1
                this.enterBrackets--;
            }
        }
    }

    /**
     * 字符串分割
     * 分割传入的字符串
     * 先去除注释和一些空白和没有用的头文件
     * <p>
     * <p>
     * 对于或行注释问题的解决：
     * 设置一个全局变量，初始值为false，如果进入一层注释则相应的值设置为true，退出后则置为false
     * 发现问题：有些注释是跟在大括号之后的
     */

    boolean enterNotes = false;

    private List<String> segmentString(String str) {

        //保存真正有用的字符串数组
        List<String> strList = Lists.newArrayList();

        //去掉开头和结尾的空白串
        str = str.trim();

        //去掉空白行 去掉#include开头的行，引入文件不作处理直接删除
        if (str.matches("^[\\n]*[\\s]*&&\\n$") || str.matches("^#include.*")) {
            return null;
        }

        //去除单行注释
        if (str.startsWith("//")) {
            return null;
        }

        //以及/* */结尾的注释
        str = this.hasComments(str);


        Pattern pattern = Pattern.compile(regex);
        String[] tempStr = pattern.split(str);

        for (String temp : tempStr) {
            if (temp.equals("//")) {
                break;
            }
            if (temp.startsWith("/*") || temp.endsWith("*/")) {
                this.enterNotes = !this.enterNotes;
                continue;
            }
            if (false == this.enterNotes) {
                if (temp != "") {
                    strList.add(temp);
                }
            }
        }

        /**
         * 做一下打印处理，打印处理之后的字符串
         *
         * 遍历一遍 放到类开头的map中 key值为所在行 value为实际文本
         */
        Iterator<String> it = strList.iterator();
        String s = "";
        while (it.hasNext()) {
            s += it.next() + "  ";
        }

        /**
         * 不是空白行 加入到pg中
         */
        if (!s.matches("^[\\n]*[\\s]*&&[\\n]$")) {
            programeInfo.put(lineCount, s);
        }

        return strList;
    }

    /**
     * 判断语句之后的是否有注释 此处处理的是不夸行的的注释
     */
    private String hasComments(String str) {
        if (-1 != str.indexOf("/*") && -1 != str.indexOf("*/")) {
            str = str.substring(0, str.indexOf("/*"));
        }
        return str;
    }

    /**
     * 初始化8个系统申明的变量
     */
    private void initSysVariables() {
        for (int i = 0; i < LanguageInformation.SYS_VARIABLES; i++) {
            this.variableList.add(i, 0);
        }
    }

    /**
     * 字符串出现次数统计方法
     */
    private void addOfKeyWordCountList(String str) {

        if (needAddOrNot(str)) {

            int index = LanguageInformation.keyWordNameMap.get(str);

            this.keyWordCountList.set(index, this.keyWordCountList.get(index) + 1);

        } else if (LanguageInformation.keyWordNameMap.containsKey(str)
                && !LanguageInformation.keyWord_variablesMap.containsKey(str)) {
            //如果是：则增加次数
            int index = LanguageInformation.keyWordNameMap.get(str);
            //根据位置对其进行加1操作
            this.keyWordCountList.set(index, this.keyWordCountList.get(index) + 1);
        } else {
            /**
             * 不是需要统计的字串
             */
            return;
        }
    }

    /**
     * 根据大括号的层级关系来判断关键词的出现次数是否应该增加1
     */
    protected boolean needAddOrNot(String str) {

        //判断接收的字符串是否是系统变量申明的关键字
        if (LanguageInformation.keyWord_variablesMap.containsKey(str)) {

            int index = LanguageInformation.keyWord_variablesMap.get(str);

            if (variableList.get(index) == 0) {
                this.variableList.set(index, 1);
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    /**
     * 初始化32个关键字
     */
    private void initKeyWordNumber() {
        for (int i = 0; i < LanguageInformation.KEY_WORD_NUMBER; i++) {
            this.keyWordCountList.add(i, 0);
        }
    }

    /**
     * 初始化用户提交的源代码信息
     */
    protected void initLanguageInformation() {
        // 为其初始化32次，分别对应32个关键字
        initKeyWordNumber();
        // 为其初始化8此，分别对应大括号层级关系内的值是否应该增加
        initSysVariables();
    }

    /**
     * 构造方法：每当用户提交源代码的时候应该初始化源代码信息
     */
    public SourcePrograme() {
        // TODO Auto-generated constructor stub
        //源代码信息初始化
        initLanguageInformation();
    }


}
