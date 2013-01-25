/*
 * JBoss, Home of Professional Open Source
 *
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.xnio.conduits;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.concurrent.TimeUnit;
import org.xnio.ChannelListener;
import org.xnio.Option;
import org.xnio.XnioExecutor;
import org.xnio.XnioWorker;
import org.xnio.channels.CloseListenerSettable;
import org.xnio.channels.Configurable;
import org.xnio.channels.StreamSinkChannel;
import org.xnio.channels.StreamSourceChannel;
import org.xnio.channels.WriteListenerSettable;

/**
 * @author <a href="mailto:david.lloyd@redhat.com">David M. Lloyd</a>
 */
public final class ConduitStreamSinkChannel implements StreamSinkChannel, WriteListenerSettable<ConduitStreamSinkChannel>, CloseListenerSettable<ConduitStreamSinkChannel>, Cloneable {
    private final Configurable configurable;

    private StreamSinkConduit conduit;
    private ChannelListener<? super ConduitStreamSinkChannel> writeListener;
    private ChannelListener<? super ConduitStreamSinkChannel> closeListener;

    public ConduitStreamSinkChannel(final Configurable configurable, final StreamSinkConduit conduit) {
        this.configurable = configurable;
        this.conduit = conduit;
        conduit.setWriteReadyHandler(new WriteReadyHandler.ChannelListenerHandler<ConduitStreamSinkChannel>(this));
    }

    public StreamSinkConduit getConduit() {
        return conduit;
    }

    public void setConduit(final StreamSinkConduit conduit) {
        this.conduit = conduit;
    }

    public ChannelListener<? super ConduitStreamSinkChannel> getWriteListener() {
        return writeListener;
    }

    public void setWriteListener(final ChannelListener<? super ConduitStreamSinkChannel> writeListener) {
        this.writeListener = writeListener;
    }

    public ChannelListener<? super ConduitStreamSinkChannel> getCloseListener() {
        return closeListener;
    }

    public void setCloseListener(final ChannelListener<? super ConduitStreamSinkChannel> closeListener) {
        this.closeListener = closeListener;
    }

    public ChannelListener.Setter<ConduitStreamSinkChannel> getWriteSetter() {
        return new WriteListenerSettable.Setter<ConduitStreamSinkChannel>(this);
    }

    public ChannelListener.Setter<ConduitStreamSinkChannel> getCloseSetter() {
        return new CloseListenerSettable.Setter<ConduitStreamSinkChannel>(this);
    }

    public void suspendWrites() {
        conduit.suspendWrites();
    }

    public void resumeWrites() {
        conduit.resumeWrites();
    }

    public void wakeupWrites() {
        conduit.wakeupWrites();
    }

    public boolean isWriteResumed() {
        return conduit.isWriteResumed();
    }

    public void awaitWritable() throws IOException {
        conduit.awaitWritable();
    }

    public void awaitWritable(final long time, final TimeUnit timeUnit) throws IOException {
        conduit.awaitWritable(time, timeUnit);
    }

    public long transferFrom(final FileChannel src, final long position, final long count) throws IOException {
        return conduit.transferFrom(src, position, count);
    }

    public long transferFrom(final StreamSourceChannel source, final long count, final ByteBuffer throughBuffer) throws IOException {
        return conduit.transferFrom(source, count, throughBuffer);
    }

    public int write(final ByteBuffer dst) throws IOException {
        return conduit.write(dst);
    }

    public long write(final ByteBuffer[] srcs) throws IOException {
        return conduit.write(srcs, 0, srcs.length);
    }

    public long write(final ByteBuffer[] dsts, final int offs, final int len) throws IOException {
        return conduit.write(dsts, offs, len);
    }

    public boolean flush() throws IOException {
        return conduit.flush();
    }

    public boolean supportsOption(final Option<?> option) {
        return configurable.supportsOption(option);
    }

    public <T> T getOption(final Option<T> option) throws IOException {
        return configurable.getOption(option);
    }

    public <T> T setOption(final Option<T> option, final T value) throws IllegalArgumentException, IOException {
        return configurable.setOption(option, value);
    }

    public void shutdownWrites() throws IOException {
        conduit.terminateWrites();
    }

    public boolean isOpen() {
        return ! conduit.isWriteShutdown();
    }

    public void close() throws IOException {
        conduit.truncateWrites();
    }

    public XnioExecutor getWriteThread() {
        return conduit.getWriteThread();
    }

    public XnioWorker getWorker() {
        return conduit.getWorker();
    }

    public ConduitStreamSinkChannel clone() {
        try {
            return (ConduitStreamSinkChannel) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new IllegalStateException(e);
        }
    }
}
