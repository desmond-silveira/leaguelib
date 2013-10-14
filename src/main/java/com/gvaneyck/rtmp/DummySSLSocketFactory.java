package com.gvaneyck.rtmp;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;

import javax.net.SocketFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;

public class DummySSLSocketFactory extends SSLSocketFactory
{
    private SSLSocketFactory factory;

    public DummySSLSocketFactory()
    {
        try
        {
            SSLContext sslcontent = SSLContext.getInstance("TLS");
            sslcontent.init(null, new TrustManager[]{new DummyTrustManager()}, new java.security.SecureRandom());
            factory = sslcontent.getSocketFactory();
        }
        catch (NoSuchAlgorithmException e)
        {
            e.printStackTrace(System.out);
        }
        catch (KeyManagementException e)
        {
            e.printStackTrace(System.out);
        }
    }

    public static SocketFactory getDefault()
    {
        return new DummySSLSocketFactory();
    }

    @Override
    public Socket createSocket(Socket socket, String s, int i, boolean flag)
        throws IOException
    {
        return factory.createSocket(socket, s, i, flag);
    }

    @Override
    public Socket createSocket(InetAddress inaddr, int i, InetAddress inaddr2, int j)
        throws IOException
    {
        return factory.createSocket(inaddr, i, inaddr2, j);
    }

    @Override
    public Socket createSocket(InetAddress inaddr, int i)
        throws IOException
    {
        return factory.createSocket(inaddr, i);
    }

    @Override
    public Socket createSocket(String s, int i, InetAddress inaddr, int j)
        throws IOException
    {
        return factory.createSocket(s, i, inaddr, j);
    }

    @Override
    public Socket createSocket(String s, int i) throws IOException {
        return factory.createSocket(s, i);
    }

    @Override
    public String[] getDefaultCipherSuites()
    {
        return factory.getSupportedCipherSuites();
    }

    @Override
    public String[] getSupportedCipherSuites()
    {
        return factory.getSupportedCipherSuites();
    }
}
