package org.apache.mina.filter.ssl;

import java.security.InvalidAlgorithmParameterException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.Provider;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import javax.net.ssl.ManagerFactoryParameters;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.TrustManagerFactorySpi;
import javax.net.ssl.X509TrustManager;

public class BogusTrustManagerFactory extends TrustManagerFactory {
   private static final X509TrustManager X509 = new X509TrustManager() {
      public void checkClientTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {
      }

      public void checkServerTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {
      }

      public X509Certificate[] getAcceptedIssuers() {
         return new X509Certificate[0];
      }
   };
   private static final TrustManager[] X509_MANAGERS;

   public BogusTrustManagerFactory() {
      super(new BogusTrustManagerFactorySpi(), new Provider("MinaBogus", (double)1.0F, "") {
         private static final long serialVersionUID = -4024169055312053827L;
      }, "MinaBogus");
   }

   static {
      X509_MANAGERS = new TrustManager[]{X509};
   }

   private static class BogusTrustManagerFactorySpi extends TrustManagerFactorySpi {
      private BogusTrustManagerFactorySpi() {
      }

      protected TrustManager[] engineGetTrustManagers() {
         return BogusTrustManagerFactory.X509_MANAGERS;
      }

      protected void engineInit(KeyStore keystore) throws KeyStoreException {
      }

      protected void engineInit(ManagerFactoryParameters managerFactoryParameters) throws InvalidAlgorithmParameterException {
      }
   }
}
