package com.javatechig.ideapp1;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.google.analytics.tracking.android.EasyTracker;
import com.javatechig.ideapp1.model.FeedItem;

public class FeedListActivity extends Activity {

	private ArrayList<FeedItem> feedList = null;
	private ProgressBar progressbar = null;
	private ListView feedListView = null;
	
/*
	public void onPause() {
		super.onPause();
		printaNaTelaPause();
	}

	public void onResume() {
		super.onResume();
		printaNaTelaResume();
	}

	public void onDestroy() {
		super.onDestroy();
		printaNaTelaDestroy();
	}
*/
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_posts_list);

		// parser
//		Parse.initialize(this, "50ba7jTsCIT6hpI7fm4IIgpkcpnhTJCYKl869qpM",
	//			"25Il4cGq15wXyOQ51a23F7dS9SbweFLBALvO5GPS");
		//PushService.setDefaultPushCallback(this, FeedListActivity.class);
		// Save the current installation.
		//ParseInstallation.getCurrentInstallation().saveInBackground();
		// end-parser

		String url = "http://ideapp.web44.net/api/get_posts/";

		if (!verificaConexao()) {
			alertConexao();
		} else {
			progressbar = (ProgressBar) findViewById(R.id.progressBar);
			new DownloadFilesTask().execute(url);
		}
	}

	/*
	 * Função que avisa da conexao com a Internet inexistente
	 */
	private AlertDialog alerta;

	private void alertConexao() {
		// Cria o gerador do AlertDialog
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		// define o titulo
		builder.setTitle("Conexão");
		// define a mensagem
		builder.setMessage("Favor verificar sua conexão com a Internet");
		// define um botão como positivo
		builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface arg0, int arg1) {
				finish();
				System.exit(0);
			}
		});
		// cria o AlertDialog
		alerta = builder.create();
		// Exibe
		alerta.show();
	}
	
	private void printaNaTelaDestroy() {
		// Cria o gerador do AlertDialog
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		// define o titulo
		builder.setTitle("Destroy");
		// define a mensagem
		builder.setMessage("Destroy");
		// define um botão como positivo
		builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface arg0, int arg1) {
				//finish();
				//System.exit(0);
			}
		});
		// cria o AlertDialog
		alerta = builder.create();
		// Exibe
		alerta.show();
	}
	private void printaNaTelaResume() {
		// Cria o gerador do AlertDialog
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		// define o titulo
		builder.setTitle("Resume");
		// define a mensagem
		builder.setMessage("Resume");
		// define um botão como positivo
		builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface arg0, int arg1) {
				//finish();
				//System.exit(0);
			}
		});
		// cria o AlertDialog
		alerta = builder.create();
		// Exibe
		alerta.show();
	}
	private void printaNaTelaPause() {
		// Cria o gerador do AlertDialog
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		// define o titulo
		builder.setTitle("Pause");
		// define a mensagem
		builder.setMessage("pause");
		// define um botão como positivo
		builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface arg0, int arg1) {
				//finish();
				//System.exit(0);
			}
		});
		// cria o AlertDialog
		alerta = builder.create();
		// Exibe
		alerta.show();
	}


	@Override
	public void onStart() {
		super.onStart();
		// The rest of your onStart() code.
		EasyTracker.getInstance(this).activityStart(this); // Add this method.
	}

	@Override
	public void onStop() {
		super.onStop();
		// The rest of your onStop() code.
		EasyTracker.getInstance(this).activityStop(this); // Add this method.
	}

	/*
	 * Função para verificar existência de conexão com a internet
	 */
	public boolean verificaConexao() {
		boolean conectado;
		ConnectivityManager conectivtyManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		if (conectivtyManager.getActiveNetworkInfo() != null
				&& conectivtyManager.getActiveNetworkInfo().isAvailable()
				&& conectivtyManager.getActiveNetworkInfo().isConnected()) {
			conectado = true;
		} else {
			conectado = false;
		}
		return conectado;
	}

	public void updateList() {
		feedListView = (ListView) findViewById(R.id.custom_list);
		feedListView.setVisibility(View.VISIBLE);
		progressbar.setVisibility(View.GONE);

		feedListView.setAdapter(new CustomListAdapter(this, feedList));
		feedListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> a, View v, int position,
					long id) {
				Object o = feedListView.getItemAtPosition(position);
				FeedItem newsData = (FeedItem) o;

				Intent intent = new Intent(FeedListActivity.this,
						FeedDetailsActivity.class);
				intent.putExtra("feed", newsData);
				startActivity(intent);
			}
		});
	}

	private class DownloadFilesTask extends AsyncTask<String, Integer, Void> {

		@Override
		protected void onProgressUpdate(Integer... values) {
		}

		@Override
		protected void onPostExecute(Void result) {
			if (null != feedList) {
				updateList();
			}
		}

		@Override
		protected Void doInBackground(String... params) {
			String url = params[0];

			// getting JSON string from URL
			try {

				JSONObject json = getJSONFromUrl(url);

				// parsing json data
				parseJson(json);
			} catch (Exception ex) {
				Log.e("JSON Parser", "Error parsing data " + ex.toString());
			}
			return null;
		}
	}

	public JSONObject getJSONFromUrl(String url) {
		InputStream is = null;
		JSONObject jObj = null;
		String json = null;

		// Making HTTP request
		try {
			// defaultHttpClient
			DefaultHttpClient httpClient = new DefaultHttpClient();
			HttpPost httpPost = new HttpPost(url);

			HttpResponse httpResponse = httpClient.execute(httpPost);
			HttpEntity httpEntity = httpResponse.getEntity();
			is = httpEntity.getContent();

			BufferedReader reader = new BufferedReader(new InputStreamReader(
					is, "iso-8859-1"), 8);
			StringBuilder sb = new StringBuilder();
			String line = null;
			while ((line = reader.readLine()) != null) {
				sb.append(line + "\n");
			}
			is.close();
			json = sb.toString();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		try {
			jObj = new JSONObject(json);
		} catch (JSONException e) {
			Log.e("JSON Parser", "Error parsing data " + e.toString());
		}

		// return JSON String
		return jObj;

	}

	public void parseJson(JSONObject json) {
		try {

			// parsing json object
			if (json.getString("status").equalsIgnoreCase("ok")) {
				JSONArray posts = json.getJSONArray("posts");

				feedList = new ArrayList<FeedItem>();

				for (int i = 0; i < posts.length(); i++) {
					JSONObject post = (JSONObject) posts.getJSONObject(i);
					FeedItem item = new FeedItem();
					item.setTitle(post.getString("title"));
					item.setDate(post.getString("date"));
					item.setId(post.getString("id"));
					item.setUrl(post.getString("url"));
					item.setContent(post.getString("content"));
					JSONArray attachments = post.getJSONArray("attachments");

					if (null != attachments && attachments.length() > 0) {
						//JSONObject attachment = attachments.getJSONObject(0);
						//if (attachment != null)
						
						
						/* 
						 * Método importante que ajusta somente pega a imagem thumbnail
						 * oferecida pelo site Wordpress setado por lah...
						 * Caso aumente de mais o tamanho da imagem, ira dar estouro de memoria
						 * e crash do aplicativo, por isso tem q deixar pegando thumbs de ateh 300x300
						 */
						for(int j = 0; j < attachments.length(); j++){
		                    JSONObject d = attachments.getJSONObject(j);


		                    JSONObject images = d.getJSONObject("images");

		                    JSONObject thumbnail = images.getJSONObject("medium");
		                    //String url = thumbnail.getString("url");
							
		                    item.setAttachmentUrl(thumbnail.getString("url"));
						}
					}

					feedList.add(item);
				}
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

}
