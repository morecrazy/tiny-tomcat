package com.morecrazy.tomcat;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
//author:zhanghan
public class HttpServer {
	public static final String WEB_ROOT = System.getProperty("user.dir")
			+ File.separator + "webroot";
	// 关闭命令
	private static final String SHUTDOWN_COMMAND = "/SHUTDOWN";

	// 接受shutdown命令
	private boolean shutdown = false;

	public static void main(String[] args) {
		HttpServer server = new HttpServer();
		server.await();
	}

	public void await() {
		ServerSocket serverSocket = null;
		int port = 8088;
		try {
			// 创建监听套接字
			serverSocket = new ServerSocket(port, 1,
					InetAddress.getByName("127.0.0.1"));
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.exit(1);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.exit(1);
		}

		while (!shutdown) {
			Socket socket = null;
			InputStream input = null;
			OutputStream output = null;

			try {
				socket = serverSocket.accept();
				//System.out.println("accept");
				input = socket.getInputStream();
				output = socket.getOutputStream();

				// 创建request对象
				Request request = new Request(input);
				request.parse();

				Response response = new Response(output);
				response.setRequest(request);
	
				// 检查这次请求是针对静态资源还是serlvet
				if (request.getUri().startsWith("/servlet/")) {
					ServletProcessor1 processor = new ServletProcessor1();
					processor.process(request, response);
				} else {
					StaticResourceProcessor processor = new StaticResourceProcessor();
					processor.process(request, response);
				}
				response.sendStaticResource();

				socket.close();
				shutdown = request.getUri().equals(SHUTDOWN_COMMAND);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				continue;
			}
		}

	}
}
