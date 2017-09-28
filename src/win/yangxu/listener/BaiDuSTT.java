package win.yangxu.listener;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.UUID;

import javax.xml.bind.DatatypeConverter;

import com.alibaba.fastjson.JSONObject;

public class BaiDuSTT {
	
	   private static final String serverURL = "http://vop.baidu.com/server_api";  
	   private static String token = "";  
	   //put your own params here  
	   // 下面3个值要填写自己申请的app对应的值  
	   private static final String apiKey = "qTArzF3yMcNngWy5B0MvH2c0";  
	   private static final String secretKey = "y6643QTZuwebHWYdLs583GEnFDfsgeFS";  
	   private static final String cuid = UUID.randomUUID().toString().replace("-", ""); 
	   
	   BaiDuSTT()  {
		   try {
			getToken();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		   
	   }
	   
	   
	 //百度云stt引擎
        public  String listen (byte[] pcmFile) throws Exception {  
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
        	return result.getString("result");
        }else{
        	//System.out.println(result.getString("err_msg"));
        	
        }
		return null;
        
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
        //System.out.println(response.toString());  
        return response.toString();  
    }  
  
    public  void getToken() throws Exception {  
        String getTokenURL = "http://openapi.baidu.com/oauth/2.0/token?grant_type=client_credentials" +   
            "&client_id=" + apiKey + "&client_secret=" + secretKey;  
        HttpURLConnection conn = (HttpURLConnection) new URL(getTokenURL).openConnection();  
        token = JSONObject.parseObject(printResponse(conn)).getString("access_token");
        		
    }  
}
