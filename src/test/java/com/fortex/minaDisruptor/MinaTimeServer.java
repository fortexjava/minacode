/**<p>Description</p>
 * @author Ivan Huo
 */
package com.fortex.minaDisruptor;

import java.net.InetSocketAddress;
import java.nio.charset.Charset;

import org.apache.mina.core.service.IoAcceptor;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.codec.textline.TextLineCodecFactory;
import org.apache.mina.filter.logging.LoggingFilter;
import org.apache.mina.transport.socket.nio.NioSocketAcceptor;

/**
 * @author Administrator
 *
 */
public class MinaTimeServer {
	private static final int PORT = 9999;  
    /** 
     *  
     */  
    public MinaTimeServer() {  
        // TODO Auto-generated constructor stub  
    }  
  
    /** 
     * @param args 
     */  
    public static void main(String[] args) {  
        // 服务器端的主要对象  
        IoAcceptor acceptor = new NioSocketAcceptor();  
          
        // 设置Filter链   
        acceptor.getFilterChain().addLast("logger", new LoggingFilter());  
        // 协议解析，采用mina现成的UTF-8字符串处理方式  
        acceptor.getFilterChain().addLast("codec", new ProtocolCodecFilter(new TextLineCodecFactory(Charset.forName("UTF-8"))));  
          
        // 设置消息处理类（创建、关闭Session，可读可写等等，继承自接口IoHandler）  
        acceptor.setHandler(new TimeServerHandler() );  
        // 设置接收缓存区大小  
        acceptor.getSessionConfig().setReadBufferSize(2048);  
        acceptor.getSessionConfig().setIdleTime(IdleStatus.BOTH_IDLE, 10);  
        try {  
            // 服务器开始监听  
            acceptor.bind( new InetSocketAddress(PORT) );  
        }catch(Exception e){  
            e.printStackTrace();  
        }  
    }  
}
