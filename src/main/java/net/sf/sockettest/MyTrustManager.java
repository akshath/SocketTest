package net.sf.sockettest;

import javax.net.ssl.*;
import java.security.cert.*;
import java.awt.*;
import javax.swing.*;
import java.security.*;

/**
 * Custom MyTrustManager
 * @author Benjamin Fleckenstein (Thanks Ben for your email)
 * @author Akshathkumar Shetty
 */
public class MyTrustManager implements X509TrustManager {

	private Component parentComponent;
	private X509TrustManager sunJSSEX509TrustManager;



	public MyTrustManager(Component parentComponent) throws Exception {
		this.parentComponent = parentComponent;

		TrustManagerFactory tmf = TrustManagerFactory.getInstance("SunX509", "SunJSSE");
		
		
		KeyStore ks = null;
		tmf.init((KeyStore)null);

		TrustManager tms [] = tmf.getTrustManagers();


		/*
          * Iterate over the returned trustmanagers, look
          * for an instance of X509TrustManager.  If found,
          * use that as our "default" trust manager.
          */
        for(int i = 0; i < tms.length; i++) {
             if (tms[i] instanceof X509TrustManager) {
                 sunJSSEX509TrustManager = (X509TrustManager) tms[i];
                 break;
             }
        }
		if(sunJSSEX509TrustManager==null) throw new Exception("Couldn't initialize");
	}

     /*
      * The default X509TrustManager returned by SunX509.  We'll delegate
      * decisions to it, and fall back to the logic in this class if the
      * default X509TrustManager doesn't trust it.
      */

     /*
      * Delegate to the default trust manager.
      */
     public void checkClientTrusted(X509Certificate[] chain, String authType)
                 throws CertificateException {
		 try {
             sunJSSEX509TrustManager.checkClientTrusted(chain, authType);
         } catch (CertificateException excep) {
             // do any special handling here, or rethrow exception.
			 throw excep;
         }
     }

     /*
      * Delegate to the default trust manager.
      */
     public void checkServerTrusted(X509Certificate[] chain, String authType)
                 throws CertificateException {
		 try {
             sunJSSEX509TrustManager.checkServerTrusted(chain, authType);
         } catch (CertificateException excep) {
             /*
              * Possibly pop up a dialog box asking whether to trust the
              * cert chain.
              */

			 StringBuffer sb = new StringBuffer();
			 for(int i=0;i<chain.length;i++) {
				 if(i==0) {
					 sb.append("Do you want to trust this server? \n\n");
					 sb.append("Server Certificate:\n\t"+chain[i].getSubjectDN());
				 } else {
					 sb.append("\n Issued By:\n \t"+chain[i].getSubjectDN());
				 }
			 }

			 int option = JOptionPane.showConfirmDialog(parentComponent,
				sb.toString(), "Certificate Confirmation",
				JOptionPane.YES_NO_OPTION, JOptionPane.INFORMATION_MESSAGE);

			 if(option==JOptionPane.NO_OPTION) {
				 throw new CertificateException("Not Trusted Certificate!");
			 }
		}
     }

     /*
      * Merely pass this through.
      */
     public X509Certificate[] getAcceptedIssuers() {
         // return sunJSSEX509TrustManager.getAcceptedIssuers();
		 if(sunJSSEX509TrustManager==null) return null;

		 return sunJSSEX509TrustManager.getAcceptedIssuers();

     }
}

