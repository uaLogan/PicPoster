package ca.ualberta.cs.picposter;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Type;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;

import android.util.Log;
import ca.ualberta.cs.picposter.model.PicPostModel;

import com.google.gson.Gson;

public class ElasticSearchOperations
{
	public static void pushPicPostModel(final PicPostModel model)
	{
		Thread thread = new Thread() //"(background) service" better than thread for mission critical ops
		{
			@Override
			public void run()
			{
				Gson gson = new Gson();
				HttpClient client = new DefaultHttpClient();
				//HttpPut request = new HttpPut("http://cmput301.softwareprocess.es:8080/testing/lglin/");
				HttpPost request = new HttpPost("http://cmput301.softwareprocess.es:8080/testing/lglin/");
				
				//elastic search has "indexes" and "types"
				//here we use the "testing" index (each group has their own index)
				//"types" such as users or posts
				//here we use CCID as type
				//the 1 at the end is the "ID" (ID can be any number or string)
				
				String jsonString = gson.toJson(model);
				
				try
				{
					request.setEntity(new StringEntity(jsonString));
					HttpResponse response = client.execute(request);
					Log.w("ElasticSearch", response.getStatusLine().toString());
					HttpEntity entity = response.getEntity();
					BufferedReader reader = new BufferedReader(new InputStreamReader(entity.getContent()));
					String output = reader.readLine();
					while(output != null)
					{
						Log.w("ElasticSearch2", output);
						output = reader.readLine();
					}
				}
				catch (Exception e)
				{
					e.printStackTrace();
				}
			}
		};
		
		thread.start();
	}
}
