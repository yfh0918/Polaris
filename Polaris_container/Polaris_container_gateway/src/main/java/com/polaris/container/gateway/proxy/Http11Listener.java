package com.polaris.container.gateway.proxy;

import io.netty.channel.ChannelPipeline;

public interface Http11Listener {
    void onHttp11(ChannelPipeline pipeline, boolean removeHttp11ServerCodec);
}
