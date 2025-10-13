package com.pcc.start;

import java.util.EnumSet;
import java.util.concurrent.Executors;

import org.eclipse.jetty.ee10.servlet.ServletHolder;
import org.eclipse.jetty.ee10.webapp.WebAppContext;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.util.thread.QueuedThreadPool;

import com.pcc.IndexServlet;
import com.pcc.LoginServlet;
import com.pcc.MenuServlet;
import com.pcc.api.core.AppApiServlet;
import com.pcc.api.core.Authen;
import com.pcc.api.core.JwtAuthFilter;
import com.pcc.sys.lib.FConstComm;
import com.pcc.sys.lib.MyStartConfigListener;

import jakarta.servlet.DispatcherType;

public class MainApp {

	static final org.slf4j.Logger log = org.slf4j.LoggerFactory
			.getLogger(Thread.currentThread().getStackTrace()[1].getClassName());

	public static MainApp mainapp = null;

	static Server server = null;
	private int server_port = 8080;

	public static void main(String[] args) throws Exception {
		startService();
	}

	/**
	 * ใช้สำหรับ Procrun ด้วย
	 */
	public static void startService() {
		try {
			mainapp = new MainApp();
			mainapp.startServer();
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
	}

	/**
	 * ใช้สำหรับ Procrun
	 */
	public static void stopService() {
		if (mainapp != null) {
			mainapp.stopServer();
			mainapp = null;
		}
	}

	public void startServer() throws Exception {

		log.info("<== Start by main method ==>");
		FConstComm.runAppMode = 1; //มีผลกับการเชื่อมฐานข้อมูล

		var threadPool = new QueuedThreadPool();
		//กำหนดให้ทำงานแบบ Virtual Threads
		//threadPool.setVirtualThreadsExecutor(Executors.newVirtualThreadPerTaskExecutor());
		// สามารถกำหนดชื่อ prefix ของ Virtual Threads ได้เพื่อการ Debug
		threadPool.setVirtualThreadsExecutor(
				Executors.newThreadPerTaskExecutor(Thread.ofVirtual().name("jetty-vt-", 0).factory()));

		server = new Server(threadPool);

		// ==== add connector
		var connector = new ServerConnector(server);
		connector.setPort(server_port);
		server.addConnector(connector);

		// ==== แบบใช้ WebAppContext ต้องเพิ่ม lib = jetty-webapp
		var webapp = new WebAppContext();
		webapp.setContextPath("/"); // อยู่ใน root เลย

		java.net.URL rscURL = MainApp.class.getResource("/webapp/");
		log.info("webResource.toExternalForm : " + rscURL.toExternalForm());
		//รันด้วย .jar 
		//webResource.toExternalForm : jar:file:/D:/pccsoft12Jetty12/pccsoft12Jetty12/startApp/target/startApp-0.0.1.jar!/webapp/
		//
		//รันด้วย IDE 
		//webResource.toExternalForm : file:/D:/pccsoft12Jetty12/pccsoft12Jetty12/startApp/target/classes/webapp/

		//org.eclipse.jetty.util.resource.Resource baseResource = org.eclipse.jetty.util.resource.ResourceFactory.of(webapp).newResource(rscURL.toURI());
		//System.out.println("Using BaseResource: " + baseResource);
		//webapp.setBaseResource(baseResource);
		webapp.setBaseResourceAsString(rscURL.toExternalForm());//ใช้แบบนี้ jetty ไม่เตือนว่า feature resource จะไม่รองรับในอนาคต

		webapp.setContextPath("/");
		webapp.setWelcomeFiles(new String[] { "index.html" });
		webapp.setParentLoaderPriority(true);
		// webapp.getSessionHandler().setMaxInactiveInterval(900);//ไม่ผ่านต้องใช้ไฟล์ /WEB-INF/web.xml ถึงจะผ่าน ,test 7/7/68

		//============= เพิ่ม servlet
		webapp.addServlet(IndexServlet.class, "");// Home Page
		webapp.addServlet(Authen.class, "/auth/login");
		webapp.addServlet(AppApiServlet.class, "/appapi");
		webapp.addServlet(MenuServlet.class, "/menu");
		webapp.addServlet(LoginServlet.class, "/login");

		// เพิ่ม ServletHolder ของ zk framework แทนการใช้ web.xml
		ServletHolder zkLoaderHolder = new ServletHolder(org.zkoss.zk.ui.http.DHtmlLayoutServlet.class);
		zkLoaderHolder.setInitParameter("update-uri", "/zkau");
		zkLoaderHolder.setInitOrder(1);
		webapp.addServlet(zkLoaderHolder, "*.zul");

		webapp.addServlet(org.zkoss.zk.au.http.DHtmlUpdateServlet.class, "/zkau/*");

		//============= เพิ่ม filter
		webapp.addFilter(JwtAuthFilter.class, "/appapi/*", EnumSet.of(DispatcherType.REQUEST));

		//============= เพิ่ม Listener
		webapp.addEventListener(new MyStartConfigListener());
		webapp.addEventListener(new org.zkoss.zk.ui.http.HttpSessionListener()); //zk Listener

		//============= เพิ่มเข้า handlers
		server.setHandler(webapp);

		Runtime.getRuntime().addShutdownHook(new Thread(() -> stopServer()));

		server.start();
		server.join();

	}

	public void stopServer() {
		try {
			// ใช้เวลาหยุดเซิร์ฟเวอร์
			server.setStopTimeout(60 * 1000l);// รอ 60 นาทีก่อนจะบังคับปิด
			server.stop();
			log.info("Jetty server stopped gracefully");
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
	}

}
