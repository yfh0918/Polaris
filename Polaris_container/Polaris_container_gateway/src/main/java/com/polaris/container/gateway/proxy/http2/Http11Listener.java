package com.polaris.container.gateway.proxy.http2;

import io.netty.channel.ChannelPipeline;

public interface Http11Listener {
    void onHttp11(ChannelPipeline pipeline, boolean removeHttp11ServerCodec);
}
