package application;

import com.google.common.collect.Lists;

import java.util.ArrayList;
import java.util.List;

/**
 * 源程序检测类，用于检测指定文件夹下的文件的相似度
 *
 *
 * 属性：
 *  存放源程序信息的数组 也就是存放Sp类的数组
 * 方法：
 *  1.将源程序类加入到比较队列
 *  2.
 *
 */
public class PlagiarismDetection {

    //源程序信息存放的数组
    List<SourcePrograme> spList= Lists.newArrayList();

    //检测代码的初设阈值
    public double THRESHOLD = 0.95;

    //用户自己设定阈值
    public void setTHRESHOLD(double threshold){this.THRESHOLD=threshold;}


    /**
     * 比较所有源程序的相似度
     */
    public boolean lanchSimiMeas(){
        double res=0.0;
        info.clear();

        // 如果超过两个源程序 就一次读出来对比，否则直接返回
        if(spList.size() >=2 ){
            //取出第一个元素 避免与自己比较
            List<Integer> l1 = spList.get(0).keyWordCountList;

            for(int i=1;i<spList.size();i++){
                List<Integer> l2 = spList.get(i).keyWordCountList;
                Double result=new Double(this.AdjustedCosineSimilarity(l1,l2));
                res=0.0;

                if(result.toString().length() >= 5){
                    res=Double.parseDouble(result.toString().substring(0,5));
                }else{
                    res=result;
                }

                if(res >= -this.THRESHOLD){
                    System.out.println("stname = " + this.spList.get(0).getStuName());
                    System.out.println(" res   = " + res);
                }
                if(res >= this.THRESHOLD){
                    return false;
                }
            }
            return true;

        }else{
            return true;
        }
    }


    /**
     * 余弦函数：
     * 	定义向量a,b
     * 	cossim = ab/|a||b|
     * 调整余弦函数：
     * 	定义向量a,b,mean
     * 	adjcossim = (a-mean)(b-mean)/|a-mean||b-mean|
     * 比较两个源程序的相似程度(关键字的出现次数)，返回比较结果
     *
     */
    public double AdjustedCosineSimilarity(List<Integer> list1,List<Integer> list2){
        //先将接受的两个参数进行修正
        List<Double> lista = this.adjustCount(list1);
        List<Double> listb = this.adjustCount(list2);
        /*
         * 求分子：向量a * 向量b
         * 分母：向量a的模 * 向量b的模
         */
        double aa = 0.0;
        double bb = 0.0;
        double ab = 0.0;
        for(int i = 0; i < lista.size(); i++){
            aa += lista.get(i) * lista.get(i);
            bb += listb.get(i) * listb.get(i);
            ab += lista.get(i) * listb.get(i);
        }
        return ab / (Math.sqrt(aa) * Math.sqrt(bb));
    }


    /**
     * 统计结果修正方法
     * 根据修正余弦函数的要求，需要对统计结果进行修正即：
     * 学生的代码关键字出现次数减去该学生代码中出现所有关键字的均值得到修正次数
     * 根据接受的参数依次遍历其中的值，然后减去均值
     */
    private List<Double> adjustCount(List<Integer> list){
        List<Double> l = new ArrayList<Double>();
        //获得该学生所有关键字t统计的修正均值
        double mean = this.adjustMean(list);
        //依次遍历其中的值，将非零项均减去关键字种类出现次数，即转换为修正统计次数
        for(int i = 0; i < list.size(); i++) {
            //如果循环的项中有非零项，则将其修正
            if (0 != list.get(i)) {
                l.add(list.get(i) - mean);
            } else {
                l.add(0.0);
            }
        }
        return l;
    }

    /**
     * 统计结果修正方法之统计总次数，即统计过的关键字次数
     * 根据接受的参数，循环遍历，统计非零项
     */
    private double adjustMean(List<Integer> list) {
        //关键字的出现总次数
        int allNum = 0;
        //关键字种类出现的次数
        int num = 0;
        for(int i = 0; i < list.size(); i++){
            //对于其中的非零项，统计结果加1
            if(0 != list.get(i)) {
                allNum += list.get(i);
                num++;
            }
        }
        return allNum * 1.0 / num;
    }



    /**
     * 由于没有获得启动类的引用，此时通过一个函数将值传入出去
     */
    public List<String> info = new ArrayList<String>();
    public void setInfo(String name1, String name2, Double result){
        info.add(name1);
        info.add(name2);
        info.add(result.toString());
    }


    /**
     * 源程序信息添加入组
     */
    public void addIn_spList(SourcePrograme sp) {
        this.spList.add(sp);
    }



}
