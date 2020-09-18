package com.polaris.ndi.service;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.nio.ByteBuffer;
import java.util.concurrent.ThreadPoolExecutor;

import javax.imageio.ImageIO;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.xuggle.mediatool.IMediaListener;
import com.xuggle.mediatool.IMediaReader;
import com.xuggle.mediatool.ToolFactory;
import com.xuggle.mediatool.event.IAddStreamEvent;
import com.xuggle.mediatool.event.IAudioSamplesEvent;
import com.xuggle.mediatool.event.ICloseCoderEvent;
import com.xuggle.mediatool.event.ICloseEvent;
import com.xuggle.mediatool.event.IFlushEvent;
import com.xuggle.mediatool.event.IOpenCoderEvent;
import com.xuggle.mediatool.event.IOpenEvent;
import com.xuggle.mediatool.event.IReadPacketEvent;
import com.xuggle.mediatool.event.IVideoPictureEvent;
import com.xuggle.mediatool.event.IWriteHeaderEvent;
import com.xuggle.mediatool.event.IWritePacketEvent;
import com.xuggle.mediatool.event.IWriteTrailerEvent;

public class CameraService extends LifeCycleServiceAbstract implements IMediaListener{
    private static Logger logger = LoggerFactory.getLogger(CameraService.class);
    
    public static CameraService INSTANCE = new CameraService();
    private static ThreadPoolExecutor threadPool = newSingleThread("CameraService Thread");
    private CameraService(){}
    
    @Override
    public LifeCycleService start(String streamLocation) throws Exception{
        if (running) {
            return this;
        }
        running = true;
        final IMediaReader mediaReader = ToolFactory.makeReader(streamLocation);
        mediaReader.setBufferedImageTypeToGenerate(BufferedImage.TYPE_3BYTE_BGR);
        mediaReader.addListener(INSTANCE);
        threadPool.execute(
                new Runnable() {
                  @Override
                  public void run() {
                      try {
                          while (mediaReader.readPacket() == null && running);
                      } catch (Exception ex) {
                          logger.error("ERROR:{}",ex);
                      } finally {
                          if (mediaReader != null) {
                              mediaReader.close();
                          }
                      }
                      running = false;
                  }
                }
         );
        return this;
    }
    
    @Override
    public void onAddStream(IAddStreamEvent arg0) {
    }

    @Override
    public void onAudioSamples(IAudioSamplesEvent arg0) {
    }

    @Override
    public void onClose(ICloseEvent arg0) {
    }

    @Override
    public void onCloseCoder(ICloseCoderEvent arg0) {
    }

    @Override
    public void onFlush(IFlushEvent arg0) {
    }

    @Override
    public void onOpen(IOpenEvent arg0) {
    }

    @Override
    public void onOpenCoder(IOpenCoderEvent arg0) {
    }

    @Override
    public void onReadPacket(IReadPacketEvent arg0) {
    }

    @Override
    public void onVideoPicture(IVideoPictureEvent arg0) {
        if (sessions.size() == 0) {
            return;
        }
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()){
            BufferedImage frame = arg0.getImage();
            ImageIO.write(frame, "jpeg", baos);
            output(ByteBuffer.wrap(baos.toByteArray()));
        } catch (Exception ioe) {
            logger.error("ERROR:{}",ioe);
        }
    }
    
    @Override
    public void onWriteHeader(IWriteHeaderEvent arg0) {
    }

    @Override
    public void onWritePacket(IWritePacketEvent arg0) {
    }

    @Override
    public void onWriteTrailer(IWriteTrailerEvent arg0) {
    }
    
}
