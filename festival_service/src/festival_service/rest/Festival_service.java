package festival_service.rest;
 
import javax.ws.rs.*;
import javax.ws.rs.core.*;
import com.sun.jersey.api.json.JSONWithPadding;
import com.sun.jersey.core.util.Base64;
import java.io.*;
import java.nio.ByteBuffer;

import festival_service.client.Client;
 
@Path("/audiotest/*")
public class Festival_service
{
    
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
                    .status(500)
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
        			.status(500)
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
    public Response returnbasic(@QueryParam("req_text") String message, @QueryParam("jsoncallback") String callback, @QueryParam("url_type") int URLmethod)
    {       
        Client client;
        Response response;
        int duration=0;
        
        try
        {
        	client = new Client();
        }
        catch (IOException ioe)
        {
        	System.out.print("Failed to connect to festival server");
        	
        	response = Response
        			.status(500)
                    .entity(new JSONWithPadding("{\"res\":\"ER\", \"err_code\": 500, \"err_text\":\"Festival connection fail\"}"))
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
            			.status(500)
                        .entity(new JSONWithPadding("{\"res\":\"ER\", \"err_code\": 500, \"err_text\":\"Speech synthesis fail\"}"))
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
        wave = client.StringToWave(message,"wav");
        client.disconnect();
        	
        if (wave == null)
        {
        	response = Response
           			.status(500)
                    .entity(new JSONWithPadding("{\"res\":\"ER\", \"err_code\": 500, \"err_text\":\"Speech synthesis fail\"}"))
                    .build();
       	}
       
     //Used to find the audio duration         
        byte[] temp = new byte[4];
        temp[0] = wave[31];
        temp[1] = wave[30];
        temp[2] = wave[29];
        temp[3] = wave[28];
        
        ByteBuffer temp2 = ByteBuffer.wrap(temp);
        int rate = temp2.getInt(0);		
        int samples = wave.length - 44;        
        duration = (samples/rate)*1000;
      ///////////////////////////////////////  
        
        
       //convert the byte array to base 64 for safety
        String s = new String(Base64.encode(wave));
        String snd_url="";
        
        if (URLmethod == 3)
        	snd_url="data:audio/wav;base64,";
        
        response = Response
    			.ok()
    			.entity(new JSONWithPadding("{\"res\":\"OK\", \"snd_url\":\"" + snd_url + "\", \"snd_time\":"+duration+",\"text\":\""+s+"\"}",callback))
    			.build();
        
        return response;
    }
    
}
