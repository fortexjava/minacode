/**<p>Description</p>
 * @author Ivan Huo
 */
package com.fortex.minaDisruptor;

import org.apache.mina.core.service.IoHandlerAdapter;  
import org.apache.mina.core.session.IoSession;  
  
/** 
 * 客户端业务处理逻辑 
 
 */  
public class MinaClientHandler extends IoHandlerAdapter {  
    // 当客户端连接进入时  
    @Override  
    public void sessionOpened(IoSession session) throws Exception {  
        System.out.println("客户端 session opened : " + session.getRemoteAddress());  
        session.write("client session opened");  
    }  
  
    @Override  
    public void exceptionCaught(IoSession session, Throwable cause)  
            throws Exception {  
        System.out.println("客户端发送信息异常....");  
    }  
  
    // 当客户端发送消息到达时  
    @Override  
    public void messageReceived(IoSession session, Object message)  
            throws Exception {  
  
        System.out.println("服务器返回的数据：" + message.toString());  
    }  
  
    @Override  
    public void sessionClosed(IoSession session) throws Exception {  
        System.out.println("客户端与服务端断开连接.....");  
    }  
  
    @Override  
    public void sessionCreated(IoSession session) throws Exception {  
        // TODO Auto-generated method stub  
        System.out.println("client session created" + session.getRemoteAddress());  
        session.write("客户端 session created······");  
    }  
  
}  
