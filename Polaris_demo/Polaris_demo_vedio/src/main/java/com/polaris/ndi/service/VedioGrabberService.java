package com.polaris.ndi.service;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.util.concurrent.ThreadPoolExecutor;

import javax.imageio.ImageIO;

import org.bytedeco.javacv.FFmpegFrameGrabber;
import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.FrameGrabber;
import org.bytedeco.javacv.Java2DFrameConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.polaris.core.util.StringUtil;

public class VedioGrabberService extends LifeCycleServiceAbstract {
    private static Logger logger = LoggerFactory.getLogger(VedioGrabberService.class);

    private FrameGrabber defaultGrabber;
    
    private Java2DFrameConverter converter = new Java2DFrameConverter();
    
    private static ThreadPoolExecutor threadPool = newSingleThread("VedioGrabberService Thread");
    public static VedioGrabberService INSTANCE = new VedioGrabberService();
    private VedioGrabberService() {}   
    
    public LifeCycleService start(String rtspPath ) throws Exception  {
        if (running) {
            return this;
        }
        running = true;
        FrameGrabber grabber = createFrameGrabber(rtspPath);
        grabber.start();
        threadPool.execute(
                new Runnable() {
                  @Override
                  public void run() {
                      try {
                          while(running){
                              Frame frame = grabber.grab();
                              if(frame == null){
                                  continue;
                              }
                              sendFrame(frame);
                          }
                          grabber.close();
                      } catch (Exception ex) {
                          logger.error("ERROR:{}",ex);
                      }
                      running = false;
                  }
                }
         );
        return this;
    }
    
    private void sendFrame(Frame frame) throws IOException {
        if (this.type == VedioType.STREAM) {
            sendStream(frame);
        } else {
            sendImage(frame);
        }
    }
    
    private void sendImage(Frame frame) throws IOException {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            BufferedImage frameImage = converter.getBufferedImage(frame);
            ImageIO.write(frameImage, "jpeg", baos);
            output(ByteBuffer.wrap(baos.toByteArray()));
        } 
    }

    private void sendStream(Frame frame) {
        if (frame.data != null) {
            output(frame.data);
            return;   
        }
        if (frame.image != null) {
            for (Buffer buffer : frame.image) {
                if (buffer instanceof ByteBuffer) {
                  output((ByteBuffer)buffer);
                }
            }
        }
    }
    
    private FrameGrabber createFrameGrabber(String rtspPath) throws org.bytedeco.javacv.FrameGrabber.Exception {
        if (StringUtil.isEmpty(rtspPath)) {
            if (defaultGrabber == null) {
                synchronized(FrameGrabber.class) {
                    if (defaultGrabber == null) {
                        defaultGrabber = FrameGrabber.createDefault(0);
                        defaultGrabber.setImageWidth(640);
                        defaultGrabber.setImageHeight(480);
                    }
                }
            }
            return defaultGrabber;
        } else {
            FFmpegFrameGrabber grabber = FFmpegFrameGrabber.createDefault(rtspPath); 
            grabber.setOption("rtsp_transport", "tcp"); // 使用tcp的方式，不然会丢包很严重
            grabber.setImageWidth(640);
            grabber.setImageHeight(480);
            return grabber;
        }
    }
}