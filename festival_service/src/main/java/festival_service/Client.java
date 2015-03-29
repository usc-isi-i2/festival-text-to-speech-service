package festival_service;
   
/*************************************************************************/
/*                                                                       */
/*                Centre for Speech Technology Research                  */
/*                     University of Edinburgh, UK                       */
/*                        Copyright (c) 1999                             */
/*                        All Rights Reserved.                           */
/*                                                                       */
/*  Permission is hereby granted, free of charge, to use and distribute  */
/*  this software and its documentation without restriction, including   */
/*  without limitation the rights to use, copy, modify, merge, publish,  */
/*  distribute, sublicense, and/or sell copies of this work, and to      */
/*  permit persons to whom this work is furnished to do so, subject to   */
/*  the following conditions:                                            */
/*   1. The code must retain the above copyright notice, this list of    */
/*      conditions and the following disclaimer.                         */
/*   2. Any modifications must be clearly marked as such.                */
/*   3. Original authors' names are not deleted.                         */
/*   4. The authors' names are not used to endorse or promote products   */
/*      derived from this software without specific prior written        */
/*      permission.                                                      */
/*                                                                       */
/*  THE UNIVERSITY OF EDINBURGH AND THE CONTRIBUTORS TO THIS WORK        */
/*  DISCLAIM ALL WARRANTIES WITH REGARD TO THIS SOFTWARE, INCLUDING      */
/*  ALL IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS, IN NO EVENT   */
/*  SHALL THE UNIVERSITY OF EDINBURGH NOR THE CONTRIBUTORS BE LIABLE     */
/*  FOR ANY SPECIAL, INDIRECT OR CONSEQUENTIAL DAMAGES OR ANY DAMAGES    */
/*  WHATSOEVER RESULTING FROM LOSS OF USE, DATA OR PROFITS, WHETHER IN   */
/*  AN ACTION OF CONTRACT, NEGLIGENCE OR OTHER TORTIOUS ACTION,          */
/*  ARISING OUT OF OR IN CONNECTION WITH THE USE OR PERFORMANCE OF       */
/*  THIS SOFTWARE.                                                       */
/*                                                                       */
/*************************************************************************/
/*             Original Author  :  Alan W Black (awb@cstr.ed.ac.uk)      */
/*             Original Date    :  March 1999                            */
/*			   Ported to Java by:  Linda Xu								 */
/*			   Revision Date    :  August 2013							 */
/*-----------------------------------------------------------------------*/


import java.net.Socket;
import java.net.UnknownHostException;
import java.io.*;
    
public class Client
{
    private Socket socket;
    private DataInputStream dataReader;
    private PrintWriter writer;
        
    //borrowed from the connect() method in the FreeTTS demo client    
    public Client() throws IOException, UnknownHostException
    {
    	socket = new Socket("localhost", 1314);
        dataReader = new DataInputStream(socket.getInputStream());
        writer = new PrintWriter(socket.getOutputStream(),true);
    }
    
    public Client(int port) throws IOException, UnknownHostException
    {
    	socket = new Socket("localhost", port);
        dataReader = new DataInputStream(socket.getInputStream());
        writer = new PrintWriter(socket.getOutputStream(),true);
    }
     
        
    public void disconnect()
    {
        try
        {
            socket.close();
        }
        catch (IOException ioe)
        {
            ioe.printStackTrace();
            System.exit(1);
        }
    }
        
    /**
     * @param text: The text to be synthesized
     * @param format: format of the audio file to be returned. Acceptable paramaters include {wav, nist, snd, riff, aiff, audlab, raw, ascii}
     * @param voice: Name of the voice to be used. What's available depends on what's been installed. Examples include cmu_us_awb_arctic_clunits, nitech_us_awb_arctic_hts, etc. Basically anything that shows up when using a (voice.list) command
    **/   
    public byte[] StringToWave(String text, String format, String voice)
    {
        byte[] buff = null;
        String ack;
            
        text = fixString(text);   
        try
        {
            sendLine("(Parameter.set 'Wavefiletype '" + format + ")\n");	//Tells Festival what format to return
            sendLine("(voice_"+voice+")\n");				//Optional: selects a voice. Otherwise, the default voice is used
            sendLine("(tts_return_to_client)\n");
            sendLine("(tts_textall \"\n" + text + "\" \"fundamental\")\n");
            do
            {
                ack = getACK();
                if (ack.startsWith("LP"))
                    client_accept_s_expr();
                else if (ack.startsWith("WV"))
                    buff = client_accept_waveform();
                else if (ack.startsWith("OK"));
                else if (ack.startsWith("ER"))
                {
                    System.out.println("Festival error occured. \nText to be submitted was " + text);
                    buff = null;
                	break;
                }
                else
                {
                	System.out.println("Unknown error occured. \nText to be submitted was " + text);
                    buff = null;
                	break;
                }
            }
            while (!ack.startsWith("WV"));
                
        }
        catch (Exception e)
        {
            return null;
        }
        return buff;
    } 
    
    public byte[] StringToWave(String text){
    	return StringToWave(text, "wav", "cmu_us_slt_cg");
    }
    
    public byte[] StringToWave(String text, String format){
    	return StringToWave(text, format, "cmu_us_slt_cg");
    }
    
     // Relatively Unnecessary function that can be used to find the audio duration if parsing the header fails or if the sole goal is to find the audio duration (regardless of format)
     // Very inefficient since it asks festival to resynthesize the text (just in a different format)
    public int getLength(String message)
    {
    	byte [] wave = StringToWave(message,"nist");
    	if (wave == null)
    		return 0;    	
    	
    	int samples = nist_get_param_int(wave, "sample_count".getBytes(), -1);
    	int sample_rate = nist_get_param_int(wave, "sample_rate".getBytes(), -1);
    	
    	if (samples == -1 || sample_rate == -1)
    	{
    		System.out.println("Failed to get header info");
    		return 0;
    	}
    	
    	int duration = (int)Math.ceil((double)samples/sample_rate) *1000;
    	return duration;
    }
    
    private void sendLine(String text) throws IOException
    {
        writer.print(text);
        writer.flush();
    }
    
        
    private String getACK() throws IOException
    {
        /*char c;
        StringBuffer buffer = new StringBuffer();
            
        c = (char)dataReader.readByte();
        while (c!='\n')
        {
            buffer.append(c);
            c = (char)dataReader.readByte();
        }*/
            
        byte [] buffer = new byte[3];
            
        dataReader.read(buffer);
            
        String s = new String(buffer);
            
        return s;
    }
        
        
    private byte [] resizeArray(byte[] original, int len)
    {
        byte [] newArray = new byte[len];
        System.arraycopy(original, 0, newArray, 0, original.length);
        return newArray;
    }
        
        
    private byte[] receive_file_to_buff() throws IOException
    {
        final byte[] file_stuff_key= "ft_StUfF_key\0".getBytes(); //festival's key
        int size=0;
        byte [] buff,buff2;
        int bufflen=1024;
        int k,i;
        byte c;
            
        buff = new byte[bufflen];
            
        for (k=0; file_stuff_key[k] != '\0';)
        {
                c = dataReader.readByte();
                
            if (size+k+1 >= bufflen){ 
                bufflen += bufflen/4;
                buff = resizeArray(buff,bufflen);
            }
            if (file_stuff_key[k] == c)
                k++;
            else if ((c == (byte)'X') && (file_stuff_key[k+1] == (byte)'\0')){   // It looked like the key but wasn't
                for (i=0; i < k; i++, size++)
                    buff[size] = file_stuff_key[i];
                k=0;
            }
            else
            {
                for (i=0; i < k; i++, size++)
                    buff[size] = file_stuff_key[i];
                k=0;
                buff[size] = c;
                size++;
            }
        }
        buff2 = new byte[size];
        System.arraycopy(buff, 0, buff2, 0, size);      
        return buff2;
    }
        
        
    private byte [] client_accept_s_expr() throws IOException
    {     
        byte[] buff = receive_file_to_buff();
        buff[buff.length-1] = (byte) '\0';
        return buff;
    }
    
        
    private int strstr(byte[] str1, byte[] str2)
    {
        int loc=0, i, j, stop;
        boolean found = false;
        stop = str1.length-str2.length;
            
        for (i=0; i<=stop; i++)
        { 
            if (found == true)
                break;
            loc=i;
            for (j=0; j<str2.length;j++)
            {
                if (str1[i] == str2[j])
                {
                    if (j==str2.length-1)
                        found = true;
                    else
                        i++;
                }
                else
                    break;
            }
        }
        if (found)
            return loc;
        else
            return -1;
    }
        
    private int nist_get_param_int(byte [] hdr, byte [] field, int def_val)
    {
        int loc, val;
        byte [] sub;
        byte b;
            
            
        loc = strstr(hdr, field);
            
        if (loc == -1)
            return def_val;
            
        sub = new byte [4];
        for (int i=0; i<4; i++)
        {
            sub[i] = hdr[loc+field.length+i];
        }
        String s = new String (sub);
            
        if (" -i ".compareTo(s) != 0)
            return def_val;
            
        int k=0;
        StringBuffer sb = new StringBuffer();
        b = hdr[loc+field.length+4];
        while (b!= (byte)'\n')
        {
            sb.append((char)b);
            k++;
            b = hdr[loc+field.length+4+k];
        }
        val = Integer.parseInt(sb.toString());
        return val;
            
    }
        
    private byte[] client_accept_waveform() throws IOException
    {
        byte[] buff = receive_file_to_buff();
        return buff;
    }
        
    private String fixString(String text)
    {
        text = text.trim();
            
        int loc1 = text.indexOf('"');
        int loc2 = text.indexOf('\\');
        if (loc1 == -1 && loc2 == -1)
            return text;
            
        StringBuffer s = new StringBuffer();
        for (int i=0; i<text.length(); i++)
        {
            if (text.charAt(i) == '"' || text.charAt(i) == '\\')
                s.append('\\');
            s.append(text.charAt(i));
        }
            
        return s.toString();
    }
}
