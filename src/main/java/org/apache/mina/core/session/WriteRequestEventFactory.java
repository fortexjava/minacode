/**<p>Description</p>
 * @author Ivan Huo
 */
package org.apache.mina.core.session;

import com.lmax.disruptor.EventFactory;

/**
 * @author Administrator
 *
 */
public class WriteRequestEventFactory implements EventFactory<WriteRequestEvent>{
	  public WriteRequestEvent newInstance()
      {
          return new WriteRequestEvent();
      } 
}
