package com.zf.demo.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.*;
import java.lang.String;
import java.awt.font.FontRenderContext;
import java.awt.font.TextLayout;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

@Service
public class TransferServiceImpl implements TransferService {


    @Value("${origin}")
    private String origin;

    @Value("${dest}")
    private String dest;

    @Value("${content}")
    private String content;
    @Value("${angel}")
    private int angel;
    @Value("${xpadding}")
    private int xpadding;
    @Value("${ypadding}")
    private int ypadding;
    @Value("${fontSize}")
    private int fontSize;
    @Value("${alpha1}")
    private float alpha1;


    @Override
    public void transferPic() throws Exception {
        Long l1 = System.currentTimeMillis();
        System.out.println("***************开始处理***************，原始目录为"+origin);
        File originFile = new File(origin);
        File destFile = new File(dest);

        List<String> list1 = new ArrayList<String>();
        List<String> list2 = new ArrayList<String>();
        List<String> list3 = new ArrayList<String>();
        List<String> originList = getList(list1, originFile);
        List<String> destList = getList(list2, destFile);
        originList.parallelStream().forEach(e->{

            if (!destList.contains(e.replace(origin,dest))){
                list3.add(e);
            }
        });
        System.out.println("共需处理文件"+list3.size());
        list3.parallelStream().forEach(e->{
            System.out.println("*********"+e);
            File originFile1 = new File(e);
            File destFile1 = new File(e.replace(origin,dest));
            try {
                transfer(originFile1,destFile1,content);
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }
        });
        System.out.println("***************处理完成***************,目标目录为"+origin);
        System.out.println("***************共耗时***************"+(System.currentTimeMillis()-l1)+"毫秒");
    }

    public List<String> getList(List<String> list, File file) {

        if (file.isDirectory()) {
            File[] fileArr = file.listFiles();
            for (File dir2 : fileArr) {
                getList(list, dir2);
            }

        } else {
            list.add(file.getAbsolutePath());
        }
        return list;
    }

    public void transfer(File originFile,File destFile,String text) throws Exception {

        addWaterMark(originFile, destFile, text);
    }

    public void addWaterMark(File inputFile, File outputFile, String text) throws Exception {
        Image image = ImageIO.read(inputFile);
        if(image==null) {
            return ;
        }
        int imgWidth = image.getWidth(null);// 获取图片的宽
        int imgHeight = image.getHeight(null);// 获取图片的高
        BufferedImage bi = new BufferedImage(imgWidth, imgHeight, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = bi.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        //绘制原图片
        float alpha = alpha1;
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

}