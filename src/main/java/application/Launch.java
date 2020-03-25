package application;


import com.google.common.collect.Maps;

import java.io.*;
import java.util.Map;

public class Launch {

    public static void main(String[] args) {
        Launch launch = new Launch();
        launch.start("");

        Map<Integer, String> map = launch.getProgrameInfo();
        for (Map.Entry<Integer,String > entry:map.entrySet()) {
            System.out.println("Key = " + entry.getKey() + ", Value = " + entry.getValue());
        }
    }

    /**
     * 为了让其他类能够获得programeInfo
     */
    public Map<Integer, String> getProgrameInfo() {
        return this.programeInfo;
    }

    //默认读取路径
    public static String DefaultPath = "/Users/gh_23/PlagiarismDetection4C/demoCode/";

    public void setDefaultPath(String defaultPath) {
        DefaultPath = defaultPath;
    }


    /**
     * 持有一个检测程序的引用
     */
    public PlagiarismDetection pd = new PlagiarismDetection();

    public boolean start(String fileName) {

        //将文件放在队列最前端
        this.addNewFile(fileName);

        return this.pd.lanchSimiMeas();
    }

    protected void addNewFile(String fileName) {
        this.readFile(fileName);
    }

    /**
     * 文件读取方法 在指定的文件夹下读取指定文件
     */
    Map<Integer, String> programeInfo = Maps.newHashMap();


    public void readFile(String fileName) {

        String filePath = Launch.DefaultPath  + fileName;
        File file = new File(filePath);

        if (file.exists()) {

            if (true == file.isFile()) {
                //读取文件
                try {

                    //创建流管道
                    BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file), "GBK"));
                    // 每次读取文件 创建一个对象 存放计算的值
                    SourcePrograme sp = new SourcePrograme();

                    //存取一行读出的数据
                    String line = "";

                    while ((line = br.readLine()) != null) {
                        sp.lineCount++;
                        //处理字符串
                        sp.codeKeyWordCount(line);
                    }

                    if (sp.enterBrackets != 0) {
                        System.out.println("对不起，系统发生错误！" + sp.enterBrackets);
                    }

                    //设置源程序类的信息：学生的姓名，即上传的文件名称
                    sp.setStuName(file.getName());

                    //将所得到的代码信息连同文件路径一起存入到代码信息的数组里
                    pd.addIn_spList(sp);

                    //关闭通道
                    br.close();

                } catch (FileNotFoundException e) {
                    System.out.println("文件没有找到！系统退出！");
                    System.exit(0);
                } catch (IOException e) {
                    System.out.println("打开打开失败！系统推出！");
                    System.exit(0);
                }

            } else if (true == file.isDirectory()) {
                this.readDirctories(fileName);
            } else {
                System.out.println("文件不存在!");
            }

        }

    }

    /**
     * 文件夹读取方法
     */

    public boolean readDirctories(String newFileName) {
        //根据指定地址读取
        File directore = new File(Launch.DefaultPath);

        String[] fileList = directore.list();

        for (String s : fileList) {

            if (s.equals(newFileName) == false) {
                readFile(s);
            }
        }
        return true;

    }

}
