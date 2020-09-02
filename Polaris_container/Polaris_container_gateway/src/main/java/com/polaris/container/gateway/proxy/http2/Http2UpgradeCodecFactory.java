package com.polaris.container.gateway.proxy.http2;

import io.netty.handler.codec.http.HttpServerUpgradeHandler.UpgradeCodec;
import io.netty.handler.codec.http.HttpServerUpgradeHandler.UpgradeCodecFactory;
import io.netty.handler.codec.http2.Http2CodecUtil;
import io.netty.handler.codec.http2.Http2ServerUpgradeCodec;
import io.netty.util.AsciiString;

public class Http2UpgradeCodecFactory implements UpgradeCodecFactory{
    
    @Override
    public UpgradeCodec newUpgradeCodec(CharSequence protocol) {
        if (AsciiString.contentEquals(Http2CodecUtil.HTTP_UPGRADE_PROTOCOL_NAME, protocol)) {
            return new Http2ServerUpgradeCodec(Http2OrHttpHandler.createHttpToHttp2ConnectionHandler());
        } else {
            return null;
        }
    }
    
}
