package festival_service;
 
import javax.ws.rs.*;
import javax.ws.rs.core.*;

import com.sun.jersey.api.json.JSONWithPadding;
import com.sun.jersey.core.util.Base64;
//import com.sun.org.apache.xerces.internal.impl.dv.util.Base64;


import java.io.*;
import java.nio.ByteBuffer;
 
@Path("/audiotest/*")
public class Festival_service
{
    @Path("/post")
	@POST
	@Consumes("text/plain")
    @Produces({"audio/wav", "text/plain"})
    public Response returnSound(String message)
    {
        byte [] wave = null;
        
        Client client;
        Response response;
        
        try
        {
        	client = new Client();
        }
        catch (IOException ioe)
        {
        	response = Response
                    .ok()
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
                    .ok()
                    .type("text/plain")
                    .entity("Failed to connect to server")
                    .build();
        }
        
        response = Response
                .ok()
                .type("audio/wav")
                .entity(wave)
                .build();
        return response;       
    }
    
    @Path("/get/{Message}")
    @GET
    @Produces("audio/wav")
    public Response returnSound2(@PathParam("Message") String message)
    {
        byte [] wave = null;
        
        Client client;
        Response response;
        
        try
        {
        	client = new Client();
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
        
        response = Response
                .ok()
                .type("audio/wav")
                .entity(wave)
                .build();
        return response;       
    }
    
    @Path("/jsonfull/")
    @GET
    @Produces({"application/x-javascript", "application/json", "application/xml"})
    public Response returnbasic(@QueryParam("req_text") String message, @QueryParam("jsoncallback") String callback, @QueryParam("url_type") int URLmethod, @QueryParam("voice_name") String voice)
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
        			.entity(new JSONWithPadding("{\"res\":\"OK\", \"snd_url\":\"http://localhost:7001/festival_service/rest/audiotest/get/\", \"snd_time\":"+duration+",\"text\":\""+message+"\"}",callback))
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
	        byte[] temp = new byte[4];
	        temp[0] = wave[31];
	        temp[1] = wave[30];
	        temp[2] = wave[29];
	        temp[3] = wave[28];
	        
	        ByteBuffer temp2 = ByteBuffer.wrap(temp);
	        int rate = temp2.getInt(0);		
	        int samples = wave.length - 44;        
	        duration = (samples/rate)*1000;
	        
	        
	        //convert to base 64 for safety?
	        String s = new String(Base64.encode(wave));
	        String snd_url="";
	        
	        if (URLmethod == 3)
	        	snd_url="data:audio/wav;base64,";
	        
	        response = Response
	    			.ok()
	    			.entity(new JSONWithPadding("{\"res\":\"OK\", \"snd_url\":\"" + snd_url + "\", \"snd_time\":"+duration+",\"text\":\""+s+"\"}",callback))
	    			.build();
        }
        
        return response;
    }
    
    @Path("/jsonbase/")
    @GET
    @Produces({"application/x-javascript", "application/json", "application/xml"})
    public Response returnbasic2(@QueryParam("req_text") String message, @QueryParam("jsoncallback") String callback, @QueryParam("url_type") int URLmethod)
    {       
        Client client;
        Response response;
        
        try
        {
        	client = new Client();
        }
        catch (IOException ioe)
        {
        	System.out.print("Failed to connect to festival server");
        	ioe.printStackTrace();
        	
        	response = Response
        			.status(201)
                    .entity(new JSONWithPadding("{\"res\":\"ER\", \"err_code\": 201, \"err_text\":\"Festival connection fail\"}",callback))
                    .build();
        	return response;
        }   
           
        byte[] wave = null;
        wave = client.StringToWave(message,"wav","cmu_us_awb_arctic_clunits");
        client.disconnect();
        	
        if (wave == null)
        {
        	response = Response
           			.status(201)
                    .entity(new JSONWithPadding("{\"res\":\"ER\", \"err_code\": 201, \"err_text\":\"Speech synthesis fail\"}",callback))
                    .build();
       	}
        	
		//convert to base 64 for safety?
        String s = new String(Base64.encode(wave));
        String snd_url="";
        
        if (URLmethod == 3)
        	snd_url="data:audio/wav;base64,";
        
        response = Response
    			.ok()
    			.entity(new JSONWithPadding("{\"res\":\"OK\", \"snd_url\":\"" + snd_url + "\", \"text\":\""+s+"\"}",callback))
    			.build();
        
        return response;
    }
    
    @Path("/getDemo/{Message}/{Voice}")
    @GET
    @Produces("audio/wav")
    public Response returnDemo(@PathParam("Message") String message, @PathParam("Voice") String voice)
    {
        byte [] wave = null;
        
        Client client;
        Response response;
        
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
        	ioe.printStackTrace();
        	
        	response = Response
                    .status(201)
                    .type("text/plain")
                    .entity("Failed to connect to server")
                    .build();
            return response;       
        }
        
		if (voice==null)
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
        
        response = Response
                .ok()
                .type("audio/wav")
                .entity(wave)
                .build();
        return response;       
    }
    
}
