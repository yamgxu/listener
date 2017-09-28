package win.yangxu.listener;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.TargetDataLine;

public class RecorderImpl extends Recorder  {

	// 定义录音格式
	private AudioFormat af = null;
	// 定义目标数据行,可以从中读取音频数据,该 TargetDataLine 接口提供从目标数据行的缓冲区读取所捕获数据的方法。
	private TargetDataLine td = null;
	// 定义源数据行,源数据行是可以写入数据的数据行。它充当其混频器的源。应用程序将音频字节写入源数据行，这样可处理字节缓冲并将它们传递给混频器。
	//private SourceDataLine sd = null;
	// 定义字节数组输入输出流
	private ByteArrayInputStream bais = null;
	private ByteArrayOutputStream baos = null;
	private AudioInputStream ais = null;


	RecorderImpl() {
		try {
			// af为AudioFormat也就是音频格式
			af = getAudioFormat();
			DataLine.Info info = new DataLine.Info(TargetDataLine.class, af);
			// 定义目标数据行,可以从中读取音频数据,该 TargetDataLine 接口提供从目标数据行的缓冲区读取所捕获数据的方法。
			td = (TargetDataLine) (AudioSystem.getLine(info));
			// 打开具有指定格式的行，这样可使行获得所有所需的系统资源并变得可操作。
			td.open(af);
			// 允许某一数据行执行数据 I/O
			td.start();

		} catch (Exception ex) {
			ex.printStackTrace();
			return;
		}
	}

	protected void start() {
		try {
		 
			Record record = new Record();
			Thread t1 = new Thread(record);
			t1.start();

		} catch (Exception ex) {
			ex.printStackTrace();
			return;
		}
	}

	private AudioFormat getAudioFormat() {
		// //下面注释部分是另外一种音频格式，两者都可以
		AudioFormat.Encoding encoding = AudioFormat.Encoding.PCM_SIGNED;
		float rate = 16000f;
		int sampleSize = 16;
		// String signedString = "signed";
		boolean bigEndian = true;
		int channels = 1;
		return new AudioFormat(encoding, rate, sampleSize, channels,
				(sampleSize / 8) * channels, rate, bigEndian);
		// //采样率是每秒播放和录制的样本数
		// float sampleRate = 16000.0F;
		// // 采样率8000,11025,16000,22050,44100
		// //sampleSizeInBits表示每个具有此格式的声音样本中的位数
		// int sampleSizeInBits = 8;
		// // 8,16
		// int channels = 1;
		// // 单声道为1，立体声为2
		// boolean signed = true;
		// // true,false
		// boolean bigEndian = true;
		// // true,false
		// return new AudioFormat(sampleRate, sampleSizeInBits, channels,
		// signed,bigEndian);
	}

	// 录音类，因为要用到MyRecord类中的变量，所以将其做成内部类
	class Record implements Runnable {
		// 定义存放录音的字节数组,作为缓冲区
		byte bts[] = new byte[1600 * 2];
		 
		// 重写run函数
		public void run() {
			System.out.println("run");
			try {
				stopflag = false;
				while (stopflag != true) {

					// 当停止录音没按下时，该线程一直执行
					// 从数据行的输入缓冲区读取音频数据。
					// 要读取bts.length长度的字节,cnt 是实际读取的字节数
					int cnt = td.read(bts, 0, bts.length);
					if (cnt > 0) {
						//baos.write(bts, 0, cnt)
						handle(bts,cnt);
					}

				}
				// ///循环结束将录到的数据取出
				//
				// byte audioData[] = baos.toByteArray();
				// bais = new ByteArrayInputStream(audioData);
				// ais = new AudioInputStream(bais,af, audioData.length /
				// af.getFrameSize());
				//
				//
				//
				// File filePath = new File("/Users/allspread/");
				// long time = System.currentTimeMillis();
				// File file = new File(filePath+"/"+time+"sss.wav");
				//
				//
				// ByteArrayOutputStream bam = new ByteArrayOutputStream();
				//
				// AudioSystem.write(ais, AudioFileFormat.Type.WAVE, bam);
				//
				// OutputStream fot= null;
				// try {
				// fot= new FileOutputStream(file);
				// } catch (FileNotFoundException e1) {
				// e1.printStackTrace();
				// }
				//
				// fot.write(bam.toByteArray());
				// fot.flush();
				// fot.close();

			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				try {
					// intBytes = -1;
					// 关闭打开的字节数组流
					if (baos != null) {
						baos.close();
					}
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					// 下面这句td.drain()不能要，这样如果不播放数据就阻塞再次录音会出现其他程序访问错误
					// td.drain();
					// td.close();

				}
			}
		}

	}
  public  byte []  byteToAudio( byte audioData[]) {
	

	    
	  
	      ByteArrayInputStream  bais = new ByteArrayInputStream(audioData);  
	      AudioInputStream ais = new AudioInputStream(bais,af, audioData.length / af.getFrameSize());  
	    
	         
          ByteArrayOutputStream bam = new ByteArrayOutputStream();
          
          try {
			AudioSystem.write(ais, AudioFileFormat.Type.WAVE, bam);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
          
         // baiDuSTT(bam.toByteArray()); 
          
          //写文件
     OutputStream  fot= null;
    try {
    	File filePath = new File("/Users/allspread/audio");  
        long time = System.currentTimeMillis();  
        File file = new File(filePath+"/"+time+".wav");
		  fot= new FileOutputStream(file);
	} catch (FileNotFoundException e1) {
		// TODO Auto-generated catch block
		e1.printStackTrace();
	}
  
    try {
		fot.write(bam.toByteArray());
		 fot.flush();
		    fot.close();
	} catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
   
	return bam.toByteArray();
    

	
}
	
	
	// 保存录音
	private void save() {
		af = getAudioFormat();
		byte audioData[] = baos.toByteArray();
		bais = new ByteArrayInputStream(audioData);
		ais = new AudioInputStream(bais, af, audioData.length
				/ af.getFrameSize());
		// 定义最终保存的文件名
		File file = null;
		// 写入文件
		try {
			// 以当前的时间命名录音的名字
			// 将录音的文件存放到F盘下语音文件夹下
			File filePath = new File("/Users/allspread");
			long time = System.currentTimeMillis();
			file = new File(filePath + "/" + time + ".wav");

			OutputStream fot = null;
			try {
				fot = new FileOutputStream(file);
			} catch (FileNotFoundException e1) {
				e1.printStackTrace();
			}

			AudioSystem.write(ais, AudioFileFormat.Type.WAVE, fot);
			// 将录音产生的wav文件转换为容量较小的mp3格式
			// 定义产生后文件名
			fot.flush();
			fot.close();

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			// 关闭流
			try {

				if (bais != null) {
					bais.close();
				}
				if (ais != null) {
					ais.close();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

}
