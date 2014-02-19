package festival_service;

import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.io.*;
import java.util.Random;

import com.sun.jersey.api.json.JSONWithPadding;

@Path("/path/*")
public class Basic_Test {
	@Path("/sample/")
	@GET
	@Produces({"application/x-javascript", "application/json", "application/xml"})
	public Response returnPath(@QueryParam("jsoncallback") String callback){
		Response response;
		
		response = Response
	    			.ok()
	    			.entity(new JSONWithPadding("{'hash':'h-3690378823082678040','source':{'name':'Ray Bradbury','uri':'http://dbpedia.org/resource/Ray_Bradbury'},'destination':{'name':'Blue Mink','uri':'http://dbpedia.org/resource/Blue_Mink'},'path':[{'type':'node','uri':'http://dbpedia.org/resource/Ray_Bradbury'},{'type':'link','inverse':true,'uri':'http://dbpedia.org/ontology/influencedBy'},{'type':'node','uri':'http://dbpedia.org/resource/George_Orwell'},{'type':'link','inverse':true,'uri':'http://dbpedia.org/ontology/influencedBy'},{'type':'node','uri':'http://dbpedia.org/resource/James_Joyce'},{'type':'link','inverse':true,'uri':'http://dbpedia.org/ontology/birthPlace'},{'type':'node','uri':'http://dbpedia.org/resource/Dublin'},{'type':'link','inverse':true,'uri':'http://dbpedia.org/ontology/isPartOf'},{'type':'node','uri':'http://dbpedia.org/resource/Leinster'},{'type':'link','inverse':true,'uri':'http://dbpedia.org/ontology/leaderName'},{'type':'node','uri':'http://dbpedia.org/resource/Fine_Gael'},{'type':'link','inverse':true,'uri':'http://dbpedia.org/ontology/colour'},{'type':'node','uri':'http://dbpedia.org/resource/Blue'},{'type':'link','inverse':true,'uri':'http://dbpedia.org/ontology/connotation'},{'type':'node','uri':'http://dbpedia.org/resource/Sky'},{'type':'link','inverse':true,'uri':'http://dbpedia.org/ontology/associatedBand'},{'type':'node','uri':'http://dbpedia.org/resource/Blue_Mink'}]}"
	    				//"{'hash':'h-3690378823082678040','excecution_time': 1300,'source':	{label: 'Los Angeles', uri: 'http://dbpedia.org/resource/Los_Angeles'}, 'destination':	{label: 'Charles Dickens', uri: 'http://dbpedia.org/resource/Charles_Dickens'},	'path':[" +
	    				//"{'type':'node','uri':	'http://dbpedia.org/resource/Los_Angeles',	'slide_description':[	{	'type':'GoogleImageSlide',	'data':{'duration':'2000',	'uri':'http://www.shootuporputup.co.uk/wp-content/uploads/2011/07/number1.png'}},{'type':'YouTubeSlide','duration':'5000','data':{'videoID':'bTvr_2v-0HI',	'start':'10000','end':'15000'}},{	'type':'GoogleImageSlide',	'data':{'duration':'3000',	'uri':'http://cast.thirdage.com/files/originals/number%202.jpg'}},]},"+
	    				//"{'type':'link','inverse':false,'uri':'http://dbpedia.org/ontology/country', slide_description:[{'type':'img',	'duration':'1000','data':{'uri':'image_uri','effect':'ease-out'}},{'type':'youtube_slides','duration':'7000','data':{'uri':'youtube_video_URI',	'effect':'none','start':'1.40',	'end':'1.47'}}]},"+
	    				//"{'type':'node','uri':'http://dbpedia.org/resource/United_States', audio_text: \"Edited text: United States's the country of Los Angeles. The United States of America (USA or U.S.A.), commonly referred to as the United States, America, or simply the States, is a federal republic consisting of 50 states, 16 territories, and a federal district.\"},"+
	    				//"{'type':'link', 'inverse': true, 'uri':'http://dbpedia.org/ontology/birthPlace',}," +
	    				//"{'type':'node', 'uri': 'http://dbpedia.org/resource/Boris_Johnson',}," +
	    				//"{'type':'link', 'inverse':true, 'uri':'http://dbpedia.org/ontology/leaderName',},"+
	    				//"{'type':'node','uri':'http://dbpedia.org/resource/London',},"+
	    				//"{'type':'link', 'inverse':true, 'uri':'http://dbpedia.org/ontology/birthPlace'},"+
	    				//"{'type':'node','uri':'http://dbpedia.org/resource/Orsay_Tennyson_Dickens', audio_text: 'More blah blah filler text for Orsay Dickens'},"+
	    				//"{'type':'link', 'inverse':true, 'uri':'http://dbpedia.org/ontology/child'},"+
	    				//"{'type':'node','uri':'http://dbpedia.org/resource/Charles_Dickens', }"+
	    				//"]}"
	    					,callback))
	    			.build();
	        
	        return response;
	}
	
	@Path("/single/")
	@GET
	@Produces({"application/x-javascript", "application/json", "application/xml"})
	public Response returnSimplePath(@QueryParam("jsoncallback") String callback){
		BufferedReader reader = null;
		try{
			reader = new BufferedReader(new FileReader("/home/lindaxu/Desktop/hash.txt"));
		}
		catch(FileNotFoundException e){
		//
		}
		String line = null;
		int counter = 0;
		int num;
		Random random = new Random();
		num = random.nextInt(4);
		System.out.println(num);
		String s = null;
		try{
		while (counter<num){
			line = reader.readLine();
			counter ++;

		}
			s = reader.readLine();
		}
		catch(Exception e){
			//
		}
		
		Response response;
		response = Response
	    			.ok()
	    			.entity(new JSONWithPadding(
	    				//"{'hash':'h-3690378823082678040','excecution_time': 1300,'source':	{label: 'Orsay Dickens', uri: 'http://dbpedia.org/resource/Orsay_Tennyson_Dickens'}, 'destination':	{label: 'Charles Dickens', uri: 'http://dbpedia.org/resource/Charles_Dickens'},	'path':[" +
	    				//"{'type':'node','uri':'http://dbpedia.org/resource/Orsay_Tennyson_Dickens', audio_text: 'More blah blah filler text for Orsay Dickens'},"+
	    				//"{'type':'link', 'inverse':true, 'uri':'http://dbpedia.org/ontology/child'},"+
	    				//"{'type':'node','uri':'http://dbpedia.org/resource/Charles_Dickens', }"+
	    				//"]}"
	    					s
	    					,callback))
	    			.build();
	        
	        return response;
	}

}
