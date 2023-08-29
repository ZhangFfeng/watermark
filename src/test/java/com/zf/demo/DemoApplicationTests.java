package com.zf.demo;

import com.zf.demo.service.TransferService;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.font.FontRenderContext;
import java.awt.font.TextLayout;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;

@SpringBootTest
class DemoApplicationTests {

    @Test
    void contextLoads() {
    }

    @Test
    void test() throws Exception {
        File file = new File("E:\\aa.jpg");
        File outFile = new File("E:\\bb.jpg");
        String text = "NOBLE BRIDGE";
        String text1 = "NOBLE BRIDGE";
        addWaterMark(file,outFile,text);
    }

    public static void addWaterMark(File inputFile, File outputFile, String text) throws Exception {
        Image image = ImageIO.read(inputFile);
        int imgWidth = image.getWidth(null);// 获取图片的宽
        int imgHeight = image.getHeight(null);// 获取图片的高

        System.out.println("图片的宽:"+imgWidth);
        System.out.println("图片的高:"+imgHeight);


        int angel = 315;//旋转角度
        int xpadding = 250;//每个水印水平间隔
        int ypadding = 250;//每个水印垂直间隔
        int fontSize = 100;

        BufferedImage bi = new BufferedImage(imgWidth, imgHeight, BufferedImage.TYPE_INT_ARGB);

        Graphics2D g = bi.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        //绘制原图片
        float alpha = 1F;
        AlphaComposite ac = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha);
        g.setComposite(ac);
        g.drawImage(image, 0, 0, imgWidth, imgHeight, null);
        g.setBackground(Color.white);

        //开始绘制水印
        //水印字体
        Font font = new Font("Times New Roman", Font.PLAIN, fontSize);
        g.setFont(font);
        FontRenderContext frc = g.getFontRenderContext();
        TextLayout tl = new TextLayout(text, font, frc);
        //水印串宽度
        int stringWidth = g.getFontMetrics(g.getFont()).charsWidth(text.toCharArray(), 0, text.length());

        //旋转水印
        g.rotate(Math.toRadians(angel), (double) imgWidth / 2, (double) imgHeight / 2);
        //水印透明度
        g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.1F));
        // 字体色
        g.setColor(Color.white);

        int x = -imgHeight / 2;
        int y = -imgWidth / 2;

        //循环绘制
        while (x < imgWidth + imgHeight / 2) {
            y = -imgWidth / 2;
            while (y < imgHeight + imgWidth / 2) {
                Shape sha = tl.getOutline(AffineTransform.getTranslateInstance(x, y));
                g.fill(sha);

                y += ypadding;
            }
            x += stringWidth + xpadding;
        }

        //释放资源
        g.dispose();
        ImageIO.write(bi, "PNG", outputFile);
    }

    @Resource
    private TransferService transferService;

    @Test
    void test2() throws Exception {
        transferService.transferPic();
    }

}
