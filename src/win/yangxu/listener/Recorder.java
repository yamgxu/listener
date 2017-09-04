package win.yangxu.listener;

 

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;
import java.util.UUID;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.TargetDataLine;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.xml.bind.DatatypeConverter;


import com.alibaba.fastjson.JSONObject;

public class Recorder {

	 //定义录音格式  
   AudioFormat af = null;  
   //定义目标数据行,可以从中读取音频数据,该 TargetDataLine 接口提供从目标数据行的缓冲区读取所捕获数据的方法。  
   TargetDataLine td = null;  
   //定义源数据行,源数据行是可以写入数据的数据行。它充当其混频器的源。应用程序将音频字节写入源数据行，这样可处理字节缓冲并将它们传递给混频器。  
   SourceDataLine sd = null;  
   //定义字节数组输入输出流  
   ByteArrayInputStream bais = null;  
   ByteArrayOutputStream baos = null;  
   //定义音频输入流  
   AudioInputStream ais = null;  
   //定义停止录音的标志，来控制录音线程的运行  
   Boolean stopflag = false;  
   //记录开始录音的时间  
   long startPlay;  
   //设置一个播放的标志  
   Boolean playflag;  
   //每次保存的最后的文件名  
   File tarFile = null;  
   //定义音频波形每次显示的字节数  
   int intBytes = 0;  
   //定义每次录音的时候每次提取字节来画音频波  
   byte audioDataBuffer[] = null;  
   //定义所需要的组件  
   JPanel jp1,jp2,jp3;  
   JLabel jl1=null;  
   JButton captureBtn;  
   //设置画波形线程的终止的标志  
   boolean flag = true;  
   //定义播放录音时的一个计数值  
   int cnt;  
   //定义播放录音时一个缓冲数组  
   byte btsPlay[] = null;  	
	
   
   private static final String serverURL = "http://vop.baidu.com/server_api";  
   private static String token = "";  
   //put your own params here  
   // 下面3个值要填写自己申请的app对应的值  
   private static final String apiKey = "qTArzF3yMcNngWy5B0MvH2c0";  
   private static final String secretKey = "y6643QTZuwebHWYdLs583GEnFDfsgeFS";  
   private static final String cuid = UUID.randomUUID().toString().replace("-", ""); 
   
   
   
   
	public void main() {
		System.out.println("ss2");
		 try {  
	            //af为AudioFormat也就是音频格式  
			     af = getAudioFormat();  
	            DataLine.Info info = new DataLine.Info(TargetDataLine.class,af);  
	            //定义目标数据行,可以从中读取音频数据,该 TargetDataLine 接口提供从目标数据行的缓冲区读取所捕获数据的方法。  
	             td = (TargetDataLine)(AudioSystem.getLine(info));  
	         	System.out.println("ss2w");
	            //打开具有指定格式的行，这样可使行获得所有所需的系统资源并变得可操作。  
	            td.open(af);  
	            //允许某一数据行执行数据 I/O  
	            td.start();  
	        	System.out.println("ss2w1");
	            Record record = new Record();  
	            Thread t1 = new Thread(record);  
	            t1.start();  
	        	System.out.println("ss2w2");
	        	
	        	while(true){
	        		String inStr= new Scanner(System.in).nextLine();
		        	if(!"true".equals(inStr)){
		        		stopflag=true;
		        		System.out.println(inStr);
		        	}else{
		        		new Thread(record).start();
		        	}
//		        	save();
	        	}
	        	
	        	
	        } catch (Exception ex) {  
	            ex.printStackTrace();  
	            return;  
	        }  
	}
	 public  AudioFormat getAudioFormat()   
	    {  
//	        //下面注释部分是另外一种音频格式，两者都可以  
	        AudioFormat.Encoding encoding = AudioFormat.Encoding.PCM_SIGNED ;  
	        float rate = 16000f;  
	        int sampleSize = 16;  
	        String signedString = "signed";  
	        boolean bigEndian = true;  
	        int channels = 1;  
	        return new AudioFormat(encoding, rate, sampleSize, channels,  
	                (sampleSize / 8) * channels, rate, bigEndian);  
//	      //采样率是每秒播放和录制的样本数  
//	      float sampleRate = 16000.0F;  
//	      // 采样率8000,11025,16000,22050,44100  
//	      //sampleSizeInBits表示每个具有此格式的声音样本中的位数  
//	      int sampleSizeInBits = 8;  
//	      // 8,16  
//	      int channels = 1;  
//	      // 单声道为1，立体声为2  
//	      boolean signed = true;  
//	      // true,false  
//	      boolean bigEndian = true;  
//	      // true,false  
//	      return new AudioFormat(sampleRate, sampleSizeInBits, channels, signed,bigEndian);  
	    }  
	
	  //录音类，因为要用到MyRecord类中的变量，所以将其做成内部类  
	    class Record implements Runnable  
	    {  
	        //定义存放录音的字节数组,作为缓冲区  
	        byte bts[] = new byte[1600*2];  
	        //将字节数组包装到流里，最终存入到baos中  
	        //重写run函数  
	        public void run() {
	        	System.out.println("run");
	        	
	        	baos = new ByteArrayOutputStream();   
	            try {  
	                stopflag = false;  
	                double  meanSquare=0;
	                flag = false;
	                long recordTime =0;
	                while(stopflag != true)  
	                {  
	                	
	                	ByteArrayOutputStream baost = new ByteArrayOutputStream();
	                    //当停止录音没按下时，该线程一直执行   
	                    //从数据行的输入缓冲区读取音频数据。  
	                    //要读取bts.length长度的字节,cnt 是实际读取的字节数  
	                    int cnt = td.read(bts, 0, bts.length); 
	                    if(cnt > 0)  
	                    {  
	                    	meanSquare=meanSquare(bts);
	                    	//计算声音的平方均数
	                        //System.out.println(bts.length+"=----"+meanSquare);
	                        // 声音平方均数大于20  或距离上次录音时间小于录音超时时间   开始录音 录音标志为true
	                       if(meanSquare>20 || System.currentTimeMillis()-recordTime<500){
	                    	   flag=true;
	                    	   if(meanSquare>20){
	                    		   //写入间隔声音
	                    		   if(baost.size()>0)
	                    		   {
	                    			   baos.write(baost.toByteArray());
	                    			   baost.reset();
	                    		   }
	                    		   baos.write(bts, 0, cnt);
	                    		   recordTime = System.currentTimeMillis();
	                    	   }else{
	                    		   //间隔声音存入临时变量
	                    		   baost.write(bts, 0, cnt);
	                    	   }
	                    	   
	                      // 声音平方均数小于20 且 录音标志为true 取录音数据并清空
	                       }else if(flag ){
	                    	    flag= false;
	                    	    System.out.println("baos.size:"+baos.size());
	 	                       if(baos.size()>0){
	 	                    	    byte audioData[] = baos.toByteArray();  
	 	                    	    
	 	                    	  
			   	   	    	        bais = new ByteArrayInputStream(audioData);  
			   	   	    	        ais = new AudioInputStream(bais,af, audioData.length / af.getFrameSize());  
			   	   	    	    
			   	   	    	        File filePath = new File("/Users/allspread/");  
			   	   		            long time = System.currentTimeMillis();  
			   	   		            File file = new File(filePath+"/"+time+"sss.wav"); 
			   	   		            ByteArrayOutputStream bam = new ByteArrayOutputStream();
			   	   		            
			   	   		            AudioSystem.write(ais, AudioFileFormat.Type.WAVE, bam);
			   	   		            
			   	   		            baiDuSTT(bam.toByteArray()); 
			   	   		            
			   	   		            OutputStream  fot= null;
					                try {
										  fot= new FileOutputStream(file);
									} catch (FileNotFoundException e1) {
										// TODO Auto-generated catch block
										e1.printStackTrace();
									}
					              
					                fot.write(bam.toByteArray());
					                fot.flush();
					                fot.close();
					                
			   	   		            baos.reset();
	 	                       }
	                       }
	                        
	                          
	   	                  
	                       
	                    }  
	                   
	                }
//	                ///循环结束将录到的数据取出
//	              
//	                byte audioData[] = baos.toByteArray();  
//	    	        bais = new ByteArrayInputStream(audioData);  
//	    	        ais = new AudioInputStream(bais,af, audioData.length / af.getFrameSize());  
//	    	    
//	    	
//	    	        
//	    	        File filePath = new File("/Users/allspread/");  
//		            long time = System.currentTimeMillis();  
//		            File file = new File(filePath+"/"+time+"sss.wav");        
//		          
//		            
//		            ByteArrayOutputStream bam = new ByteArrayOutputStream();
//		            
//		            AudioSystem.write(ais, AudioFileFormat.Type.WAVE, bam);  
//		            
//		            OutputStream  fot= null;
//	                try {
//						  fot= new FileOutputStream(file);
//					} catch (FileNotFoundException e1) {
//						// TODO Auto-generated catch block
//						e1.printStackTrace();
//					}
//	              
//	                fot.write(bam.toByteArray());
//	                fot.flush();
//	                fot.close();
	    	        
	    	        
	    	        
	               
	            } catch (Exception e) {  
	                e.printStackTrace();  
	            }finally{  
	                try {  
	                    //intBytes = -1;  
	                    //关闭打开的字节数组流  
	                    if(baos != null)  
	                    {  
	                        baos.close();  
	                    }     
	                } catch (Exception e) {  
	                    e.printStackTrace();  
	                }finally{  
	                    //下面这句td.drain()不能要，这样如果不播放数据就阻塞再次录音会出现其他程序访问错误  
	                    //td.drain();  
	                   // td.close();  
	                    
	                    
	                }  
	            }  
	        }  
	          
	    }  
	    
	    
	    //平方平均数：是一组数据的平方和除以数据的项数的开方  
	    public static double meanSquare(byte[] x){  
	        int m=x.length;  
	        double sum=0;  
	        for(double xx: x){//计算x值的倒数  
	            sum+=xx*xx; 
	        } 
	        return Math.sqrt(sum/m);  
	    } 
	    
	    
	    //保存录音  
	    public void save()  
	    {  
	        af = getAudioFormat();  
	        byte audioData[] = baos.toByteArray();  
	        bais = new ByteArrayInputStream(audioData);  
	        ais = new AudioInputStream(bais,af, audioData.length / af.getFrameSize());  
	        //定义最终保存的文件名  
	        File file = null;  
	        //写入文件  
	        try {     
	            //以当前的时间命名录音的名字  
	            //将录音的文件存放到F盘下语音文件夹下  
	            File filePath = new File("/Users/allspread");  
	            long time = System.currentTimeMillis();  
	            file = new File(filePath+"/"+time+".wav");        
	          
	            
	            
	            OutputStream  fot= null;
               try {
					  fot= new FileOutputStream(file);
				} catch (FileNotFoundException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
	            
               AudioSystem.write(ais, AudioFileFormat.Type.WAVE, fot);  
	            //将录音产生的wav文件转换为容量较小的mp3格式  
	            //定义产生后文件名  
               fot.flush();
               fot.close();
	              
	        } catch (Exception e) {  
	            e.printStackTrace();  
	        }finally{  
	            //关闭流  
	            try {  
	                  
	                if(bais != null)  
	                {  
	                    bais.close();  
	                }   
	                if(ais != null)  
	                {  
	                    ais.close();          
	                }  
	            } catch (Exception e) {  
	                e.printStackTrace();  
	            }         
	        }  
	    }  
	    
	    
	    
	    //百度云stt引擎
	    private static void baiDuSTT(byte[] pcmFile) throws Exception {  
	        HttpURLConnection conn = (HttpURLConnection) new URL(serverURL).openConnection();  
	  
	        // construct params  
	        JSONObject params = new JSONObject();  
	        params.put("format", "pcm");  
	        params.put("rate", 16000);  
	        params.put("channel", "1");  
	        params.put("token", token);  
	        params.put("lan", "zh");  
	        params.put("cuid", cuid);  
	        params.put("len", pcmFile.length);  
	        params.put("speech", DatatypeConverter.printBase64Binary(pcmFile));  
	  
	        // add request header  
	        conn.setRequestMethod("POST");  
	        conn.setRequestProperty("Content-Type", "application/json; charset=utf-8");  
	  
	        conn.setDoInput(true);  
	        conn.setDoOutput(true);  
	  
	        // send request  
	        DataOutputStream wr = new DataOutputStream(conn.getOutputStream());  
	        wr.writeBytes(params.toJSONString());  
	        wr.flush();  
	        wr.close();  
	       
	        JSONObject result= JSONObject.parseObject(printResponse(conn));
	       
	        if("success.".equals( result.getString("err_msg"))){
	        	System.out.println(result.getString("result"));
	        }else{
	        	System.out.println(result.getString("err_msg"));
	        }
	        
	    }
	    private static String printResponse(HttpURLConnection conn) throws Exception {  
	        if (conn.getResponseCode() != 200) {  
	            // request error  
	            System.out.println("conn.getResponseCode() = " + conn.getResponseCode());  
	            return "";  
	        }  
	        InputStream is = conn.getInputStream();  
	        BufferedReader rd = new BufferedReader(new InputStreamReader(is));  
	        String line;  
	        StringBuffer response = new StringBuffer();  
	        while ((line = rd.readLine()) != null) {  
	            response.append(line);  
	            response.append('\r');  
	        }  
	        rd.close();  
	       // System.out.println(response.toString());  
	        return response.toString();  
	    }  
	  
	    public static void getToken() throws Exception {  
	        String getTokenURL = "https://openapi.baidu.com/oauth/2.0/token?grant_type=client_credentials" +   
	            "&client_id=" + apiKey + "&client_secret=" + secretKey;  
	        HttpURLConnection conn = (HttpURLConnection) new URL(getTokenURL).openConnection();  
	        token = JSONObject.parseObject(printResponse(conn)).getString("access_token");
	        		
	    }  
}
