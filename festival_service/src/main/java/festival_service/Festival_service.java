package festival_service;
 
import javax.ws.rs.*;
import javax.ws.rs.core.*;

import com.sun.jersey.api.json.JSONWithPadding;
import com.sun.jersey.core.util.Base64;
import it.sauronsoftware.jave.*;
//import com.sun.org.apache.xerces.internal.impl.dv.util.Base64;


import java.io.*;
import java.nio.ByteBuffer;
 
@Path("/audiotest/*")
public class Festival_service
{    
    @Path("/get/{Message}")
    @GET
    @Produces("audio/mpeg")
    public Response returnSound(@PathParam("Message") String message)
    {
    	
    	byte [] wave = null;
        
        Client client;
        Response response;        
        
        try
        {
        	client = new Client(1314);
        }
        catch (IOException ioe)
        {
        	System.out.print("Failed to connect to festival server");
        	ioe.printStackTrace();
        	
        	response = Response
                    .status(201)
                    .type("text/plain")
                    .entity("Failed to connect to server")
                    .build();
            return response;       
        }
        
        wave = client.StringToWave(message,"wav");
        client.disconnect();
           
        if (wave==null)
        {
        	response = Response
        			.status(201)
                    .type("text/plain")
                    .entity("TTS failed")
                    .build();
        }
        
        byte[] mp3 = convertToMp3(wave);
        if (mp3==null)
        {
        	response = Response
        			.status(201)
                    .type("text/plain")
                    .entity("Encoding failed")
                    .build();
        }
        
        response = Response
                .ok()
                .type("audio/mpeg")
                .entity(mp3)
                .build();
        return response;       
    }
    
    @Path("/jsonfull/")
    @GET
    @Produces({"application/x-javascript", "application/json", "application/xml"})
    public Response returnFull(@QueryParam("req_text") String message, @QueryParam("jsoncallback") String callback, @QueryParam("url_type") int URLmethod, @QueryParam("voice_name") String voice, @QueryParam("id") int attemptId)
    {       
        Client client;
        Response response;
        int duration=0;
        
        if (message.trim().isEmpty()){
        	response = Response
        			.status(201)
                    .entity(new JSONWithPadding("{\"res\":\"ER\", \"err_code\": 404, \"err_text\":\"Message of length 0\"}",callback))
                    .build();
        	return response;
        }
        
        int port=1314;
        
        if (voice!=null){
			if(voice.contains("nitech"))
				port=1315;
		}
		
		
		try
        {
        	client = new Client(port);
        }
        catch (IOException ioe)
        {
        	System.out.print("Failed to connect to festival server");
        	
        	response = Response
        			.status(201)
                    .entity(new JSONWithPadding("{\"res\":\"ER\", \"err_code\": 201, \"err_text\":\"Festival connection fail\"}",callback))
                    .build();
        	return response;
        }
             
        
        if (URLmethod==2)
        {
        	duration = client.getLength(message);           
        	client.disconnect();
        	
            if (duration==0)
            {
            	
            	response = Response
            			.status(201)
                        .entity(new JSONWithPadding("{\"res\":\"ER\", \"err_code\": 201, \"err_text\":\"Speech synthesis fail\"}",callback))
                        .build();
            }
            else
            {
            	response = Response
        			.ok()
        			.entity(new JSONWithPadding("{\"res\":\"OK\", \"id\": "+attemptId+", \"snd_url\":\"http://localhost:7001/festival_service/rest/audiotest/get/\", \"snd_time\":"+duration+",\"text\":\""+message+"\"}",callback))
        			.build();
            }
            
        	return response;
        }
 
        byte[] wave = null;
        
		if (voice==null)
			wave = client.StringToWave(message,"wav");
		else
			wave = client.StringToWave(message,"wav",voice);
			
        client.disconnect();
        	
        if (wave == null)
        {
        	response = Response
           			.status(201)
                    .entity(new JSONWithPadding("{\"res\":\"ER\", \"err_code\": 201, \"err_text\":\"Speech synthesis fail\"}",callback))
                    .build();
       	}
        else{  
        	//Calculate the duration...this doesn't change even after encoding
	        byte[] temp = new byte[4];
	        temp[0] = wave[31];
	        temp[1] = wave[30];
	        temp[2] = wave[29];
	        temp[3] = wave[28];
	        
	        ByteBuffer temp2 = ByteBuffer.wrap(temp);
	        int rate = temp2.getInt(0);		
	        int samples = wave.length - 44;        
	        duration = (samples/rate)*1000;
	        
	        //Convert to mp3
	        byte[] mp3 = convertToMp3(wave);
	        
	        if (mp3==null)
	        {
	        	response = Response
	        			.status(201)
	                    .type("text/plain")
	                    .entity("Encoding failed")
	                    .build();
	        }
	        
	        //convert to base 64 for safety?
	        String s = new String(Base64.encode(mp3));
	        String snd_url="";
	        
	        if (URLmethod == 3)
	        	snd_url="data:audio/mpeg;base64,";
	        
	        response = Response
	    			.ok()
	    			.entity(new JSONWithPadding("{\"res\":\"OK\", \"id\": "+attemptId+", \"snd_url\":\"" + snd_url + "\", \"snd_time\":"+duration+",\"text\":\""+s+"\"}",callback))
	    			.build();
        }
        
        return response;
    }
    
    @Path("/getDemo/{Message}/{Voice}")
    @GET
    @Produces("audio/mpeg")
    public Response returnDemo(@PathParam("Message") String message, @PathParam("Voice") String voice)
    {
        byte [] wave = null;
        
        Client client;
        Response response;
                
        int port=1314;
        
		if(voice.contains("nitech"))
			port=1315;
        
        try
        {
        	client = new Client(port);
        }
        catch (IOException ioe)
        {
        	System.out.print("Failed to connect to festival server");
        	ioe.printStackTrace();
        	
        	response = Response
                    .status(201)
                    .type("text/plain")
                    .entity("Failed to connect to server")
                    .build();
            return response;       
        }
        
		if (voice.isEmpty())
			wave = client.StringToWave(message,"wav");
		else
			wave = client.StringToWave(message,"wav",voice);
			
        client.disconnect();
           
        if (wave==null)
        {
        	response = Response
        			.status(201)
                    .type("text/plain")
                    .entity("TTS failed")
                    .build();
        }
        
        byte[] mp3 = convertToMp3(wave);
        if (mp3==null)
        {
        	response = Response
        			.status(201)
                    .type("text/plain")
                    .entity("Encoding failed")
                    .build();
        }
        
        response = Response
                .ok()
                .type("audio/mpeg")
                .entity(mp3)
                .build();
        return response;          
    }
    
    private byte[] convertToMp3(byte[] wave){
    	File tempwav = null, tempmp3 = null;
    	
    	//Create temporary files for the encoder class
    	try{
        	tempwav = File.createTempFile("wav", ".wav");
        	tempmp3 = File.createTempFile("mp3", ".mp3");
        	BufferedOutputStream bs = new BufferedOutputStream(new FileOutputStream(tempwav));
        	bs.write(wave);
            
        }catch(IOException e){
        	 
        	System.out.print("Failed to create file");
        	e.printStackTrace();
        	
        	if (tempwav!=null)
        		tempwav.delete();
        	
        	if (tempmp3!=null)
        		tempmp3.delete();
        	
        	return null;      

        }
        
    	//Encode: taken straight from the documentation examples
        AudioAttributes audio = new AudioAttributes();
        audio.setCodec("libmp3lame");
        audio.setBitRate(new Integer(128000));
        audio.setChannels(new Integer(2));
        audio.setSamplingRate(new Integer(44100));
        EncodingAttributes attrs = new EncodingAttributes();
        attrs.setFormat("mp3");
        attrs.setAudioAttributes(audio);
        Encoder encoder = new Encoder();

		try {
			encoder.encode(tempwav, tempmp3, attrs);
		} catch (IllegalArgumentException | EncoderException e) {
			System.out.print("Failed to encode mp3 file");
        	e.printStackTrace();
        	
        	tempwav.delete();
    		tempmp3.delete();
        	return null;      
		}

		//Convert from file to a byte array
		FileInputStream fis;
		ByteArrayOutputStream bos;
		byte [] mp3;
		try {
			fis = new FileInputStream(tempmp3);
			bos = new ByteArrayOutputStream();
			
			//Writes len bytes from the specified byte array starting at offset off to this byte array output stream.
			for (int readNum; (readNum = fis.read(wave)) != -1;) {
                bos.write(wave, 0, readNum); //no doubt here is 0
            }
			mp3 = bos.toByteArray();
		} catch (IOException ex) {
			tempwav.delete();
			tempmp3.delete();
			return null;    
		} 
        
		tempwav.delete();
		tempmp3.delete();		
        return mp3;     
    }    
}



