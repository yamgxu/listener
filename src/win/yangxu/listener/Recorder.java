package win.yangxu.listener;

/**
 * 录音器接口
 * @author allspread
 *
 */
public abstract class Recorder {

	protected BasicsRecordHandle recordHandle;
	// 定义停止录音的标志，来控制录音线程的运行
	protected Boolean stopflag = false;
	/**
	 * 开始监听 
	 * @return
	 */
	public void startListening(BasicsRecordHandle recordHandle){
		this.recordHandle=recordHandle;
		this.start();
	}
	
	public void stopListening(BasicsRecordHandle recordHandle){
		this.stopflag=true;
	}
	protected abstract  void start(); 
	
	public abstract byte []  byteToAudio( byte audioData[]);
	
	protected void handle(byte bts[],int cnt){
		//可加多个处理器
		recordHandle.handle(bts, cnt, this);
	}
}
