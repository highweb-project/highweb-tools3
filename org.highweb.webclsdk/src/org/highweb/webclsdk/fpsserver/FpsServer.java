package org.highweb.webclsdk.fpsserver;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Map;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

public class FpsServer {
	public static interface FpsListener {
		public void setFps(int fps);
	}

	private HttpServer server;

	private FpsListener fpsListener;

	public FpsServer() {
		// TODO Auto-generated constructor stub
	}

	public void setFpsListener(FpsListener listener) {
		fpsListener = listener;
	}

	public void start() {
		try {
			server = HttpServer.create(new InetSocketAddress(8000), 0);
	        server.createContext("/fps", new MyHandler());
	        server.setExecutor(null); // creates a default executor
	        server.start();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void stop(int delay) {
		server.stop(delay);
	}

	/*
	 * Source From:
	 * http://www.rgagnon.com/javadetails/java-get-url-parameters-using-jdk-http-server.html
	 */
	private Map<String, String> queryToMap(String query) {
		Map<String, String> result = new HashMap<String, String>();
		String[] params = query.split("&");
		for (String param : params) {
			String pair[] = param.split("=");
			if (pair.length > 1) {
				result.put(pair[0], pair[1]);
			} else {
				result.put(pair[0], "");
			}
		}
		return result;
	}

	private class MyHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange t) throws IOException {
        	Map<String,String> params = queryToMap(t.getRequestURI().getQuery());
        	if(params.containsKey("fps")) {
        		String fpsStr = params.get("fps");
        		if(fpsListener != null) {
        			try {
        				System.out.println("FPS : " + fpsStr);
        				fpsListener.setFps(Integer.parseInt(fpsStr));
        			} catch(Exception e) {
        				e.printStackTrace();
        				fpsListener.setFps(0);
        			}
        		}
        	}
            String response = "OK";
            t.sendResponseHeaders(200, response.length());
            OutputStream os = t.getResponseBody();
            os.write(response.getBytes());
            os.close();
        }
    }
}
