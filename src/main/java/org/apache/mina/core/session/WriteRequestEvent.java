/**<p>Description</p>
 * @author Ivan Huo
 */
package org.apache.mina.core.session;

import org.apache.mina.core.write.WriteRequest;

/**
 * @author Administrator
 *
 */
public class WriteRequestEvent {
	 private WriteRequest request;
	 private IoSession session;

     public void set(WriteRequest request,IoSession session)
     {
         this.request = request;
         this.session=session;
     }

	public WriteRequest getRequest() {
		return request;
	}

	public IoSession getSession() {
		return session;
	}

	 
	 
     
}
