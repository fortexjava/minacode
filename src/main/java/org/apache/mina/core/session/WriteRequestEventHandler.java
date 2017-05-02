/**<p>Description</p>
 * @author Ivan Huo
 */
package org.apache.mina.core.session;

import java.nio.channels.SelectionKey;
import java.util.Date;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.file.FileRegion;
import org.apache.mina.core.filterchain.IoFilterChain;
import org.apache.mina.core.write.WriteRequest;
import org.apache.mina.core.write.WriteRequestQueue;
import org.apache.mina.transport.socket.nio.NioSession;

import com.lmax.disruptor.EventHandler;

/**
 * @author Administrator
 *
 */
public class WriteRequestEventHandler implements EventHandler<WriteRequestEvent>{

	/* (non-Javadoc)
	 * @author Ivan Huo
	 * @see com.lmax.disruptor.EventHandler#onEvent(java.lang.Object, long, boolean)
	 */
	public void onEvent(WriteRequestEvent event, long sequence, boolean endOfBatch) throws Exception {
		// TODO Auto-generated method stub
		//System.out.println("Event: " + event.toString());
	}
	
	
	 

}
