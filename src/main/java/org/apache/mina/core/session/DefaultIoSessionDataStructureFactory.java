/*
 *  Licensed to the Apache Software Foundation (ASF) under one
 *  or more contributor license agreements.  See the NOTICE file
 *  distributed with this work for additional information
 *  regarding copyright ownership.  The ASF licenses this file
 *  to you under the Apache License, Version 2.0 (the
 *  "License"); you may not use this file except in compliance
 *  with the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 *
 */
package org.apache.mina.core.session;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import org.apache.mina.core.write.WriteRequest;
import org.apache.mina.core.write.WriteRequestQueue;

import com.lmax.disruptor.RingBuffer;
import com.lmax.disruptor.YieldingWaitStrategy;
import com.lmax.disruptor.dsl.Disruptor;
import com.lmax.disruptor.dsl.EventHandlerGroup;
import com.lmax.disruptor.dsl.ProducerType;

/**
 * The default {@link IoSessionDataStructureFactory} implementation
 * that creates a new {@link HashMap}-based {@link IoSessionAttributeMap}
 * instance and a new synchronized {@link ConcurrentLinkedQueue} instance per
 * {@link IoSession}.
 * 
 * @author <a href="http://mina.apache.org">Apache MINA Project</a>
 */
public class DefaultIoSessionDataStructureFactory implements IoSessionDataStructureFactory {

    public IoSessionAttributeMap getAttributeMap(IoSession session) throws Exception {
        return new DefaultIoSessionAttributeMap();
    }

    public WriteRequestQueue getWriteRequestQueue(IoSession session) throws Exception {
    	//return new DefaultWriteRequestQueue();
    	return new DisruptorWriteRequestQueue();
    	
    }

    private static class DefaultIoSessionAttributeMap implements IoSessionAttributeMap {
        private final ConcurrentHashMap<Object, Object> attributes = new ConcurrentHashMap<Object, Object>(4);

        /**
         * Default constructor
         */
        public DefaultIoSessionAttributeMap() {
            super();
        }

        /**
         * {@inheritDoc}
         */
        public Object getAttribute(IoSession session, Object key, Object defaultValue) {
            if (key == null) {
                throw new IllegalArgumentException("key");
            }

            if (defaultValue == null) {
                return attributes.get(key);
            }

            Object object = attributes.putIfAbsent(key, defaultValue);

            if (object == null) {
                return defaultValue;
            } else {
                return object;
            }
        }

        /**
         * {@inheritDoc}
         */
        public Object setAttribute(IoSession session, Object key, Object value) {
            if (key == null) {
                throw new IllegalArgumentException("key");
            }

            if (value == null) {
                return attributes.remove(key);
            }

            return attributes.put(key, value);
        }

        /**
         * {@inheritDoc}
         */
        public Object setAttributeIfAbsent(IoSession session, Object key, Object value) {
            if (key == null) {
                throw new IllegalArgumentException("key");
            }

            if (value == null) {
                return null;
            }

            return attributes.putIfAbsent(key, value);
        }

        /**
         * {@inheritDoc}
         */
        public Object removeAttribute(IoSession session, Object key) {
            if (key == null) {
                throw new IllegalArgumentException("key");
            }

            return attributes.remove(key);
        }

        /**
         * {@inheritDoc}
         */
        public boolean removeAttribute(IoSession session, Object key, Object value) {
            if (key == null) {
                throw new IllegalArgumentException("key");
            }

            if (value == null) {
                return false;
            }

            try {
                return attributes.remove(key, value);
            } catch (NullPointerException e) {
                return false;
            }
        }

        /**
         * {@inheritDoc}
         */
        public boolean replaceAttribute(IoSession session, Object key, Object oldValue, Object newValue) {
            try {
                return attributes.replace(key, oldValue, newValue);
            } catch (NullPointerException e) {
            }

            return false;
        }

        /**
         * {@inheritDoc}
         */
        public boolean containsAttribute(IoSession session, Object key) {
            return attributes.containsKey(key);
        }

        /**
         * {@inheritDoc}
         */
        public Set<Object> getAttributeKeys(IoSession session) {
            synchronized (attributes) {
                return new HashSet<Object>(attributes.keySet());
            }
        }

        /**
         * {@inheritDoc}
         */
        public void dispose(IoSession session) throws Exception {
            // Do nothing
        }
    }

    private static class DefaultWriteRequestQueue implements WriteRequestQueue {
        /** A queue to store incoming write requests */
        private final Queue<WriteRequest> q = new ConcurrentLinkedQueue<WriteRequest>();

        /**
         * Default constructor
         */
        public DefaultWriteRequestQueue() {
            super();
        }

        /**
         * {@inheritDoc}
         */
        public void dispose(IoSession session) {
            // Do nothing
        	System.out.println("queue dispose");
        }

        /**
         * {@inheritDoc}
         */
        public void clear(IoSession session) {
            q.clear();
            System.out.println("queue clear");
        }

        /**
         * {@inheritDoc}
         */
        public synchronized boolean isEmpty(IoSession session) {
        	boolean result= q.isEmpty();
        	System.out.println("queue is empty?" + result);
            return result;
        }

        /**
         * {@inheritDoc}
         */
        public synchronized void offer(IoSession session, WriteRequest writeRequest) {
        	System.out.println("queue offering data");
            q.offer(writeRequest);
        }

        /**
         * {@inheritDoc}
         */
        public synchronized WriteRequest poll(IoSession session) {
        	System.out.println("queue polling data");
            return q.poll();
        }

        @Override
        public String toString() {
            return q.toString();
        }

        /**
         * {@inheritDoc}
         */
        public int size() {
        	System.out.println("queue size is" + q.size());
            return q.size();
        }
    }
    
              
    
    public static class DisruptorWriteRequestQueue implements WriteRequestQueue {
        /** A queue to store incoming write requests */
     //   private final Queue<WriteRequest> q = new ConcurrentLinkedQueue<WriteRequest>();
        RingBuffer<WriteRequestEvent> ringBuffer;
        Disruptor<WriteRequestEvent> disruptor ;
        /**
         * Default constructor
         */
        public DisruptorWriteRequestQueue() {
            super();
          //  Executor executor = Executors.defaultThreadFactory();           
            // The factory for the event
            
            // Specify the size of the ring buffer, must be power of 2.
            int bufferSize = 16384;
            
            WriteRequestEventFactory factory = new WriteRequestEventFactory();
            
            // Construct the Disruptor
            //disruptor = new Disruptor<WriteRequestEvent>(factory, bufferSize, Executors.defaultThreadFactory(),ProducerType.SINGLE, new YieldingWaitStrategy());
            disruptor = new Disruptor<WriteRequestEvent>(factory, bufferSize, Executors.defaultThreadFactory());
            
            disruptor.handleEventsWith(new WriteRequestEventHandler());
            
            ringBuffer= disruptor.getRingBuffer();
                                    
            disruptor.start();
            
            System.out.println("Disruptor started..");
            
        }

        /**
         * {@inheritDoc}
         */
        public void dispose(IoSession session) {
            // Do nothing
        	disruptor.shutdown(); 
        	 System.out.println("Disruptor shutdown..");
        }

        /**
         * {@inheritDoc}
         */
        public void clear(IoSession session) {
            //q.clear();
        	
        }

        /**
         * {@inheritDoc}
         */
        public   boolean isEmpty(IoSession session) {
           // return q.isEmpty();
        	boolean result= ringBuffer.hasAvailableCapacity(ringBuffer.getBufferSize());
        	System.out.println("q is empty " + result);
        	return result;
        }

        /**
         * {@inheritDoc}
         */
        public   void offer(IoSession session, WriteRequest writeRequest) {
            //q.offer(writeRequest);
        	long sequence = ringBuffer.next(); 
        	try
            {
                WriteRequestEvent event = ringBuffer.get(sequence); // Get the entry in the Disruptor
                                                            // for the sequence
                event.set(writeRequest,session);  // Fill with data
                System.out.println("Disruptor fill(offer) data..");
            }
            finally
            {
                ringBuffer.publish(sequence);
            }
        }

        /**
         * {@inheritDoc}
         */
        public  WriteRequest poll(IoSession session) {
           // return q.poll();
        	//ringBuffer.ge
        	 WriteRequestEvent event =  ringBuffer.get(ringBuffer.getCursor());
        	 //System.out.println("Disruptor poll data..");
        	 return event.getRequest();
        }

        @Override
        public String toString() {
           // return q.toString();
        	return ringBuffer.toString();
        }

        /**
         * {@inheritDoc}
         */
        public int size() {
            //return q.size();
        	return ringBuffer.getBufferSize();
        }
    }
    
    
}
