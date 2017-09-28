package win.yangxu.listener;


public class Main {

	public static void main(String[] args) throws Exception {
		
		
		new BaiDuSTT().getToken();
		
		Recorder recorder=new RecorderImpl();
		recorder.startListening(new BasicsRecordHandle());
		

	}

	
//	  
//	
//	ByteArrayOutputStream baost = new ByteArrayOutputStream();
//    //当停止录音没按下时，该线程一直执行   
//    //从数据行的输入缓冲区读取音频数据。  
//    //要读取bts.length长度的字节,cnt 是实际读取的字节数  
//    int cnt = td.read(bts, 0, bts.length); 
//    if(cnt > 0)  
//    {  
//    	meanSquare=meanSquare(bts);
//    	//计算声音的平方均数
//        //System.out.println(bts.length+"=----"+meanSquare);
//        // 声音平方均数大于20  或距离上次录音时间小于录音超时时间   开始录音 录音标志为true
//       if(meanSquare>20 || System.currentTimeMillis()-recordTime<500){
//    	   flag=true;
//    	   if(meanSquare>20){
//    		   //写入间隔声音
//    		   if(baost.size()>0)
//    		   {
//    			   baos.write(baost.toByteArray());
//    			   baost.reset();
//    		   }
//    		   baos.write(bts, 0, cnt);
//    		   recordTime = System.currentTimeMillis();
//    	   }else{
//    		   //间隔声音存入临时变量
//    		   baost.write(bts, 0, cnt);
//    	   }
//    	   
//      // 声音平方均数小于20 且 录音标志为true 取录音数据并清空
//       }else if(flag ){
//    	    flag= false;
//    	    System.out.println("baos.size:"+baos.size());
//            if(baos.size()>0){
//         	    byte audioData[] = baos.toByteArray();  
//         	    
//         	  
//  	    	        bais = new ByteArrayInputStream(audioData);  
//  	    	        ais = new AudioInputStream(bais,af, audioData.length / af.getFrameSize());  
//  	    	    
//  	    	        File filePath = new File("/Users/allspread/");  
//  		            long time = System.currentTimeMillis();  
//  		            File file = new File(filePath+"/"+time+"sss.wav"); 
//  		            ByteArrayOutputStream bam = new ByteArrayOutputStream();
//  		            
//  		            AudioSystem.write(ais, AudioFileFormat.Type.WAVE, bam);
//  		            
//  		           // baiDuSTT(bam.toByteArray()); 
//  		            
//  		            OutputStream  fot= null;
//                try {
//					  fot= new FileOutputStream(file);
//				} catch (FileNotFoundException e1) {
//					// TODO Auto-generated catch block
//					e1.printStackTrace();
//				}
//              
//                fot.write(bam.toByteArray());
//                fot.flush();
//                fot.close();
//                
//  		            baos.reset();
//            }
//       }
//        
//          
//         
//       
//    }  
   

}
