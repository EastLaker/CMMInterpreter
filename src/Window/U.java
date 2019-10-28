/**
 * @author Ulysses Chen
 * @date 2019/10/28 16:59
 * @version 1.0
 */
package Window;


import java.awt.Image;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.IntBuffer;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import javafx.scene.canvas.Canvas;
import javafx.scene.image.PixelFormat;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritablePixelFormat;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.filechooser.FileSystemView;


public class U {

    //设置图标
    public static Canvas getFileIconToNode(File file) {

        //获取系统文件的图标
        Image image = (Image) ((ImageIcon) FileSystemView.getFileSystemView()
                .getSystemIcon(file)).getImage();
        //构建图片缓冲区，设定图片缓冲区的大小和背景，背景为透明
        BufferedImage bi = new BufferedImage(image.getWidth(null),
                image.getHeight(null), BufferedImage.BITMASK);
        bi.getGraphics().drawImage(image, 0, 0, null);          //把图片画到图片缓冲区
        //将图片缓冲区的数据转换成int型数组
        int[] data = ((DataBufferInt) bi.getData().getDataBuffer()).getData();
        //获得写像素的格式模版
        WritablePixelFormat<IntBuffer> pixelFormat
                = PixelFormat.getIntArgbInstance();
        Canvas canvas = new Canvas(bi.getWidth() + 2, bi.getHeight() + 2);    //新建javafx的画布
        //获取像素的写入器
        PixelWriter pixelWriter = canvas.getGraphicsContext2D().getPixelWriter();
        //根据写像素的格式模版把int型数组写到画布
        pixelWriter.setPixels(1, 1, bi.getWidth(), bi.getHeight(),
                pixelFormat, data, 0, bi.getWidth());
        //设置树节点的图标
        return canvas;

    }

    public static String getFileName(File file) {
        return FileSystemView.getFileSystemView().getSystemDisplayName(file);
    }


}