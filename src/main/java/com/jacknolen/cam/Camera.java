package com.jacknolen.cam;

import org.bytedeco.javacv.FFmpegFrameGrabber;
import org.bytedeco.javacv.Java2DFrameConverter;
import org.opencv.core.*;
import org.opencv.imgproc.Imgproc;
import org.opencv.videoio.VideoCapture;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;

/** class regarding to camera specifics
 * This will handle all things tod o with the camera itself, as this is the object of the camera.
 * This stores the interface of the camera and the varibles associated with it.
 * @author Jack
 */
public class Camera {
//    static {
//        System.loadLibrary(org.opencv.core.Core.NATIVE_LIBRARY_NAME);
//    }

    private String cameraName;
    private String streamurl;
    private final CamCell cameraui;
    private volatile boolean running = false;
    private final List<Consumer<BufferedImage>> frameListeners = new CopyOnWriteArrayList<>();


    public Camera(String cameraName, String streamurl) throws IOException {
        this.cameraName = cameraName;
        this.streamurl = streamurl;

        this.cameraui = new CamCell();
        cameraui.setCameraData(this);
    }

//    /**
//     * Converts a Mat frame to a BufferedImage.
//     * Used in conjunction with OpenCV, this code snippet needs attribution to stackoverflow.
//     */
//    private BufferedImage matToBufferedImage(Mat mat) {
//        int type = BufferedImage.TYPE_BYTE_GRAY;
//        if (mat.channels() > 1) type = BufferedImage.TYPE_3BYTE_BGR;
//        int bufferSize = mat.channels() * mat.cols() * mat.rows();
//        byte[] bytes = new byte[bufferSize];
//        mat.get(0, 0, bytes);
//        BufferedImage image = new BufferedImage(mat.cols(), mat.rows(), type);
//        final byte[] targetPixels = ((DataBufferByte) image.getRaster().getDataBuffer()).getData();
//        System.arraycopy(bytes, 0, targetPixels, 0, bytes.length);
//        return image;
//    }

    public void addFrameListener(Consumer<BufferedImage> listener) {
        frameListeners.add(listener);
    }

    public void removeFrameListener(Consumer<BufferedImage> listener) {
        frameListeners.remove(listener);
    }

    // Called internally whenever a new frame is decoded
    private void broadcastFrame(BufferedImage frame) {
        for (Consumer<BufferedImage> l : frameListeners) {
            l.accept(frame);
        }
    }

    /**
     * Reads and displays frames from RTSP.
     */
    // Former OpenCV implementation, works the same as the FFmpegFrameGrabber option, though not necessary for packaging.
//    private void videoCapture() {
//        System.out.println("Starting RTSP stream: " + streamurl);
//        VideoCapture capture = new VideoCapture();
//
//        // Open the RTSP stream
//        if (!capture.open(streamurl)) {
//            System.err.println("Failed to open stream: " + streamurl);
//            return;
//        }
//
//        Mat frame = new Mat();
//        running = true;
//
//        while (running && capture.read(frame)) {
//            if (!frame.empty()) {
//                Imgproc.resize(frame, frame, new org.opencv.core.Size(392, 220));
//                BufferedImage image = matToBufferedImage(frame);
//                ImageIcon icon = new ImageIcon(image);
//                cameraui.getCameraPanel().setIcon(icon);
//                cameraui.getCameraPanel().repaint();
//                broadcastFrame(image);
//            }
//        }
//
//        capture.release();
//        System.out.println("RTSP stream stopped for: " + streamurl);
//    }
    private void videoCapture() {
        try (FFmpegFrameGrabber grabber = new FFmpegFrameGrabber(streamurl)) {
            grabber.setOption("rtsp_transport", "tcp");
            grabber.start();

            Java2DFrameConverter converter = new Java2DFrameConverter();

            running = true;
            while (running) {
                var frame = grabber.grabImage();
                if (frame == null) continue;

                BufferedImage image = converter.convert(frame);
                if (image == null) continue;

                // Resize the BufferedImage (if you want smaller previews)
                Image scaled = image.getScaledInstance(392, 220, Image.SCALE_SMOOTH);
                BufferedImage bufferedScaled = new BufferedImage(392, 220, BufferedImage.TYPE_INT_RGB);
                Graphics2D g2d = bufferedScaled.createGraphics();
                g2d.drawImage(scaled, 0, 0, null);
                g2d.dispose();

                ImageIcon icon = new ImageIcon(bufferedScaled);

                SwingUtilities.invokeLater(() -> {
                    cameraui.getCameraPanel().setIcon(icon);
                    cameraui.getCameraPanel().repaint();
                });

                // Notify fullscreen and others
                broadcastFrame(bufferedScaled);
            }

            grabber.stop();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Starts the stream in a background thread.
     */
    public void startVideoCapture() {
        if (running) return;
        new Thread(this::videoCapture, cameraName).start();
    }

    /**
     * Stops the stream safely.
     */
    public void stopVideoCapture() {
        running = false;
    }

    public CamCell getCameraUI() {
        return this.cameraui;
    }

    public String getCameraName() {
        return cameraName;
    }

    public void setCameraName(String cameraName) {
        this.cameraName = cameraName;
    }

    public String getStreamurl() {
        return streamurl;
    }

    public void setStreamurl(String streamurl) {
        this.streamurl = streamurl;
    }
}
