import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import org.jdeferred.DeferredScheduler;
import org.jdeferred.FailCallback;
import org.jdeferred.impl.DefaultDeferredScheduler;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

public class PokeFetcher {
	private static String DATA_URL = "https://pokevision.com/map/data/54.088698086/12.140487617";

	private static String SCAN_URL = "https://pokevision.com/map/scan/54.088698086/12.140487617";

	static File pokeFile = new File("/root/pokemon/" + System.currentTimeMillis() + ".csv");

	static OkHttpClient client = new OkHttpClient();

	static List<Long> written = new LinkedList<Long>();

	public static void main(String[] args) throws IOException {

		if (args.length == 2) {
			System.out.println("adjusting with lat " + args[0] + " and lon " + args[1]);
			DATA_URL = "https://pokevision.com/map/data/" + args[0] + "/" + args[1];
			SCAN_URL = "https://pokevision.com/map/scan/" + args[0] + "/" + args[1];
		}

		final OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream(pokeFile));
		DeferredScheduler scheduler = new DefaultDeferredScheduler("myID", new RejectedExecutionHandler() {
			@Override
			public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
				System.out.println("ERROR");
			}
		}, Logger.getLogger("My Logger"));

		scheduler.repeatWithFixedDelay(new Runnable() {
			@Override
			public void run() {
				try {
					String s = getData();
					System.out.println(s);
					JSONObject joData = new JSONObject(s);
					JSONArray jaPokemons = joData.getJSONArray("pokemon");
					for (int i = 0; i < jaPokemons.length(); i++) {
						JSONObject joPokemon = jaPokemons.getJSONObject(i);
						int pokeID = joPokemon.getInt("pokemonId");
						long expirationTime = joPokemon.getLong("expiration_time");
						double lon = joPokemon.getDouble("longitude");
						double lat = joPokemon.getDouble("latitude");
						if (!written.contains(expirationTime) && !written.contains(expirationTime - 1) && !written.contains(
								expirationTime + 1)) {
							writer.write(pokeID + ";" + PokeDex.getPokemonName(pokeID) + ";" + lat + ";" + lon + ";" + expirationTime + ";" + System
									.currentTimeMillis() + "\n");
							written.add(expirationTime);
							writer.flush();
						}
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}, 0, 1, TimeUnit.MINUTES).fail(new FailCallback<Throwable>() {
			@Override
			public void onFail(Throwable throwable) {
				System.err.println(throwable);
			}
		});

		scheduler.repeatWithFixedDelay(new Runnable() {
			@Override
			public void run() {
				try {
					String data = scan();
					System.out.println(data);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}, 0, 1, TimeUnit.MINUTES);
	}


	private static String getData() throws IOException {
		Request request = getRequest(DATA_URL);

		// Execute the request and retrieve the response.
		Response response = client.newCall(request).execute();

		ResponseBody body = response.body();
		return body.string();
	}

	private static String scan() throws IOException {
		Request request = getRequest(SCAN_URL);

		// Execute the request and retrieve the response.
		Response response = client.newCall(request).execute();

		ResponseBody body = response.body();
		return body.string();
	}

	private static Request getRequest(String url) {
		return new Request.Builder().addHeader("__cfduid", "dd5a0d80d284836832ab049b28a5002821469512857")
		                            .addHeader("app-session", "fgm1bcvdm4n3k2r09f1u31bpl7")
		                            .addHeader("cdmu", "1469512859319")
		                            .addHeader("OX_plg", "swf|shk|pm")
		                            .addHeader("bknx_fa", "1469512860867")
		                            .addHeader("bknx_ss", "1469512860867")
		                            .addHeader("__gads",
		                                       "ID=0e0e7f2c8543000b:T=1469512861:S=ALNI_MZhaO90szlZh1fOrVMlwzjthL4E_w")
		                            .addHeader("cdmblk",
		                                       "0:0:0:0:0:0:0:0:0:0:0:0,0.08:0.1:0:0:0:0:0:0:0:0:0.25:0,0:0:0:0:0:0:0:0:0:0:0:0,0:0:0:0:0:0:0:0:0:0:0:0,0:0:0:0:0:0:0:0:0:0:0:0,0:0:0:0:0:0:0:0:0:0:0:0")
		                            .addHeader("cdmtlk", "1737:1060:1807:2260:1594:0:1958:1739:1107:0:1698:0")
		                            .addHeader("cdmgeo", "de")
		                            .addHeader("cdmbaserate", "2.1")
		                            .addHeader("cdmbaseraterow", "1.1")
		                            .addHeader("cdmint", "0")
		                            .addHeader("cdmblk2",
		                                       "0:0:0:0:0:0:0:0:0:0:0:0,0.08:0.1:0:0:0:0:0:0:0:0:0.25:0,0:0:0:0:0:0:0:0:0:0:0:0,0:0:0:0:0:0:0:0:0:0:0:0,0:0:0:0:0:0:0:0:0:0:0:0,0:0:0:0:0:0:0:0:0:0:0:0")
		                            .addHeader("_ga", "GA1.2.1744552683.1469512859")
		                            .addHeader("OX_sd", "2")
		                            .url(url)
		                            .build();
	}
}
