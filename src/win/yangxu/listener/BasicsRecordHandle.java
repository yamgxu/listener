package win.yangxu.listener;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class BasicsRecordHandle {

	private ByteArrayOutputStream baos = new ByteArrayOutputStream();
	private BaiDuSTT baiDuSTT = new BaiDuSTT();
	private double number=-1;
	private List<Double> list=new ArrayList<Double>();
	private Queue<Double> queue=new LinkedList<Double>();
	private double ratio=1;
	private boolean flag=false;
	private double sum=0;
	public void handle(byte bts[], int cnt, Recorder recorder) {
		
		 
		
		if(number<0){
			 getNumber(bts);
		}
		// 要读取bts.length长度的字节,cnt 是实际读取的字节数
		if (cnt > 0) {
			double meanSquare = MathUtils.meanSquare(bts);
			if(queue.size()>=100*60){
				sum-=queue.poll();
				sum+=meanSquare;
				queue.offer(meanSquare);
			}else{
				queue.offer(meanSquare);
				sum+=meanSquare;
			}
			
			// 计算声音的平方均数
			// System.out.println(bts.length+"=----"+meanSquare);
			// 声音平方均数大于number0   开始录音  
			if (meanSquare > number &&  baos.size()<(16000*2.0*10)) {
				if(!flag){
					flag=true;
					System.out.println("开始录音");
				}
				// System.out.println("meanSquare"+meanSquare);
				baos.write(bts, 0, cnt);
				// 声音平方均数小于number  
			} else {
				//小于0.4秒不处理
				if (baos.size() > 1600*2.0*4) {
					  System.out.println("结束录音秒数："+baos.size() /(16000*2.0));
					try {
						 baiDuSTT.listen(recorder.byteToAudio(baos.toByteArray()));
					} catch (Exception e) {
						e.printStackTrace();
					}
					baos.reset();
					 
				}else{
					//设立敏感值 敏感值为最近一分钟的声音平均值
					if(Math.abs(number-(sum/queue.size()))>10){
						number=-1;
					}
					
				}  
				
			}

		}
	}
	
	void getNumber(byte bts[]){
		
		if(list.size()<10){
			list.add(MathUtils.meanSquare(bts));
		}else{
			Double s=0.0;
			for(Double d: list){
				s+=d*ratio;
			}
			this.number=s/list.size();
			System.out.println("声音平均值："+number);
			list.clear();
			baos.reset();
		}
		
	}
	

}
