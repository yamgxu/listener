package win.yangxu.listener;

public class MathUtils {


    //平方平均数：是一组数据的平方和除以数据的项数的开方  
    public static double meanSquare(byte[] x){  
        int m=x.length;  
        double sum=0;  
        for(double xx: x){//计算x值的倒数  
            sum+=xx*xx; 
        } 
        return Math.sqrt(sum/m);  
    } 
    
}
