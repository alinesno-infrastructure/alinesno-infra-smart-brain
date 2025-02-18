package com.alinesno.infra.base.search.utils.parse;

import com.alinesno.infra.base.search.utils.TextParser;
import com.alinesno.infra.base.search.utils.bean.PPtPageBean;
import org.apache.poi.hslf.extractor.PowerPointExtractor;
import org.apache.poi.hslf.model.HeadersFooters;
import org.apache.poi.hslf.usermodel.*;
import org.apache.poi.sl.extractor.SlideShowExtractor;
import org.apache.poi.sl.usermodel.SlideShow;
import org.apache.poi.xslf.usermodel.*;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * 用于解析PDF文件内容
 */
public class PPTParser extends TextParser {

    public static void dealPpt(String filePath){

        try {
            InputStream input = new FileInputStream(filePath);
            HSLFSlideShow hss = new HSLFSlideShow(input);
            //得到全部文本
            String pptText = readBySlideShowExtractor(hss);
            //System.out.println(pptText);
            //hss.getPictureData(); 返回此幻灯片的所有图片。
            List<HSLFPictureData> hslfPictureDataList = hss.getPictureData();
            //输出图片
            for (HSLFPictureData hslfPictureData : hslfPictureDataList){
                byte[] data = hslfPictureData.getData();
                FileOutputStream out = new FileOutputStream("E:\\GPT-Tmp\\" + UUID.randomUUID() + ".jpg");
                out.write(data);
                out.close();
            }

            //HSLFSlide 表示一张幻灯片对象
            //hss.getSlides(); 返回幻灯片中找到的所有普通幻灯片
            List<HSLFSlide> hslfSlideList = hss.getSlides();
            for (HSLFSlide hslfSlide : hslfSlideList){
                //hslfSlide.getHeadersFooters(); 此幻灯片的页眉/页脚
                HeadersFooters headersFooters = hslfSlide.getHeadersFooters();
                //headersFooters.getFooterText();页脚文字
                System.out.println(headersFooters.getFooterText());
                //headersFooters.getHeaderText() 标头文字
                System.out.println(headersFooters.getHeaderText());
                //headersFooters.getDateTimeText() 这是用户希望在页脚中显示的固定日期
                //headersFooters.isDateTimeVisible() 日期是否显示在页脚中。
                //headersFooters.isUserDateVisible() 是否使用自定义用户日期而不是今天的日期。
                if(headersFooters.isDateTimeVisible() && headersFooters.isUserDateVisible()){
                    System.out.println(headersFooters.getDateTimeText());
                }
                //headersFooters.getDateTimeFormat() 一个整数，指定用于设置日期时间样式的格式ID。
                System.out.println(headersFooters.getDateTimeFormat());

                //HSLFShape表示工作表中包含的所有形状（幻灯片或注释）
                List <HSLFShape> hslfShapeList = hslfSlide.getShapes();
                for (HSLFShape hslfShape : hslfShapeList){
                    System.out.println("形状类型："+hslfShape.getShapeType());
                    if(hslfShape instanceof HSLFTextShape){
                        System.out.println("HSLFTextShape");
                        String text = ((HSLFTextShape) hslfShape).getText();
                        System.out.println(text);
                    }
                    if(hslfShape instanceof HSLFTable){
                        System.out.println("HSLFTable");
                        int rowSize = ((HSLFTable) hslfShape).getNumberOfRows();
                        int columnSize = ((HSLFTable) hslfShape).getNumberOfColumns();
                        for (int rowNum = 0; rowNum < rowSize; rowNum++) {
                            for (int columnNum = 0; columnNum < columnSize; columnNum++) {
                                HSLFTableCell cell = ((HSLFTable) hslfShape).getCell(rowNum, columnNum);
                                if (cell != null) {
                                    String text = cell.getText();
                                    System.out.println(text);
                                }
                            }
                        }
                    }
                }
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static List<PPtPageBean> dealPptX(String filePath){

        List<PPtPageBean> pages = new ArrayList<>() ;

        try {
            InputStream input = new FileInputStream(filePath);
            XMLSlideShow xss = new XMLSlideShow(input);
            //得到全部文本
            String pptText = readBySlideShowExtractor(xss);
            //System.out.println(pptText);
            //hss.getPictureData(); 返回此幻灯片的所有图片。
            List<XSLFPictureData> xslfPictureDataList = xss.getPictureData();
            //输出图片
            for (XSLFPictureData xslfPictureData : xslfPictureDataList){
                byte[] data = xslfPictureData.getData();
                FileOutputStream out = new FileOutputStream("E:\\GPT-Tmp\\" + UUID.randomUUID() + ".jpg");
                out.write(data);
                out.close();
            }

            //HSLFSlide 表示一张幻灯片对象
            //hss.getSlides(); 返回幻灯片中找到的所有普通幻灯片
            List<XSLFSlide> xslfSlideList = xss.getSlides();
            for (XSLFSlide xslfSlide : xslfSlideList){
                //HSLFShape表示工作表中包含的所有形状（幻灯片或注释）
                List <XSLFShape> xslfShapeList = xslfSlide.getShapes();
                for (XSLFShape xslfShape : xslfShapeList){
                    if(xslfShape instanceof XSLFTextShape){
                        System.out.println("XSLFTextShape");
                        String text = ((XSLFTextShape) xslfShape).getText();
                        System.out.println(text);

                        pages.add(new PPtPageBean(1 , text)) ;

                    }
                    if(xslfShape instanceof XSLFTable){
                        System.out.println("HSLFTable");
                        int rowSize = ((XSLFTable) xslfShape).getNumberOfRows();
                        int columnSize = ((XSLFTable) xslfShape).getNumberOfColumns();
                        for (int rowNum = 0; rowNum < rowSize; rowNum++) {
                            for (int columnNum = 0; columnNum < columnSize; columnNum++) {
                                XSLFTableCell cell = ((XSLFTable) xslfShape).getCell(rowNum, columnNum);
                                if (cell != null) {
                                    String text = cell.getText();
                                    System.out.println("----->>>>>>>>>>>>" + text);

                                    pages.add(new PPtPageBean(1 , text)) ;
                                }
                            }
                        }
                    }
                }
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return pages ;
    }

    /**
     * 得到文档的全部的文本,包括页脚，不包括批注
     * @param slideShow ppt/pptx文档对象
     * @return 全部文本
     */
    public static String readBySlideShowExtractor(SlideShow slideShow){
        SlideShowExtractor slideShowExtractor = new SlideShowExtractor(slideShow);
        return slideShowExtractor.getText();
    }
    //PowerPointExtractor即将要淘汰，建议使用SlideShowExtractor
    public static String readByPowerPointExtractor(InputStream is){
        PowerPointExtractor extractor= null;
        try {
            extractor = new PowerPointExtractor(is);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return extractor.getText();
    }
}