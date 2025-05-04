package com.alinesno.infra.smart.assistant.plugin.tool;

import com.alibaba.fastjson.JSONArray;
import com.alinesno.infra.common.web.log.utils.SpringUtils;
import com.alinesno.infra.smart.assistant.adapter.service.CloudStorageConsumer;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;
import org.icepear.echarts.Line;
import org.icepear.echarts.Option;
import org.icepear.echarts.Pie;
import org.icepear.echarts.charts.line.LineAreaStyle;
import org.icepear.echarts.charts.line.LineSeries;
import org.icepear.echarts.charts.pie.PieSeries;
import org.icepear.echarts.components.coord.cartesian.CategoryAxis;
import org.icepear.echarts.components.grid.Grid;
import org.icepear.echarts.components.tooltip.Tooltip;
import org.icepear.echarts.components.tooltip.TooltipAxisPointer;
import org.icepear.echarts.render.Engine;

import javax.lang.exception.RpcServiceRuntimeException;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;

/**
 * Echarts图片显示工具
 */
@Slf4j
@EqualsAndHashCode(callSuper = true)
@Data
public abstract class EchartsTool extends Tool {

    /**
     * 生成Echarts雷达图HTML内容
     * @param data 雷达图的数据
     * @param title 雷达图的标题
     * @return 生成的HTML文件的路径
     */
    protected String genPieEcharts(String data, String title) {

        Tooltip tooltip = new Tooltip()
                .setTrigger("axis")
                .setAxisPointer(new TooltipAxisPointer()
                        .setType("shadow")
                        .setAnimation(false));

        JSONArray dataArr = JSONArray.parseArray(data);

        Pie pieChart = new Pie()
                .setTitle(title)
                .addSeries(new PieSeries()
                        .setName("访问来源")
                        .setType("pie")
                        .setRadius("85%")
                        .setCenter(new String[]{"50%", "50%"})
                        .setData(dataArr)
                );

        Option option = pieChart.getOption();

        Grid grid = new Grid() ;
        grid.setLeft("1%");
        grid.setRight("1%");
        grid.setBottom("1%");
        grid.setContainLabel(true);

        option.setGrid(grid);
        option.setTooltip(tooltip);

        System.out.println(option);

        return getEchartsPath(option);
    }

    /**
     * 生成Echarts LineHtml内容
     * @param xData
     * @param yData
     * @param title
     * @return
     */
    protected String genLineAndBarEcharts(String xData, String yData, String title , String type) {

        Tooltip tooltip = new Tooltip()
                .setTrigger("axis")
                .setAxisPointer(new TooltipAxisPointer()
                        .setType("shadow")
                        .setAnimation(false));

        String[] xDataArr = xData.split(",");
        String[] yDataArr = yData.split(",");

        Line lineChart = new Line()
                .setTitle(title)
                .addXAxis(new CategoryAxis()
                        .setData(xDataArr)
                        .setBoundaryGap(false))
                .addYAxis()
                .addSeries(new LineSeries()
                        .setType(type)
                        .setData(yDataArr)
                        .setAreaStyle(new LineAreaStyle()));

        Option option = lineChart.getOption();

        Grid grid = new Grid() ;
        grid.setLeft("1%");
        grid.setRight("1%");
        grid.setBottom("1%");
        grid.setContainLabel(true);

        option.setGrid(grid);
        option.setTooltip(tooltip);

        return getEchartsPath(option);
    }

    /**
     * 生成Echarts HTML文件
     * @param option
     * @return
     */
    private static String getEchartsPath(Option option) {
        long timestamp = Instant.now().toEpochMilli();
        Path tempDir;
        try {
            tempDir = Files.createTempDirectory("echarts-");
        } catch (Exception e) {
            throw new RpcServiceRuntimeException("创建临时目录时发生错误: " + e.getMessage());
        }

        String filePath = Paths.get(tempDir.toString(), timestamp + ".html").toString();

        Engine engine = new Engine();
        String htmlIndex = engine.renderHtml(option);

        try {
            Files.write(Paths.get(filePath), htmlIndex.getBytes());
        } catch (Exception e) {
            throw new RpcServiceRuntimeException("写入文件时发生错误: " + e.getMessage());
        }

        // 上传到云存储
        CloudStorageConsumer cloudStorageConsumer = SpringUtils.getBean(CloudStorageConsumer.class);
//        R<String> r = cloudStorageConsumer.upload(new File(filePath)) ;
//        String previewUrl = cloudStorageConsumer.getPreviewUrl(r.getData()).getData()  ;

        String previewUrl = cloudStorageConsumer.uploadCallbackUrl(new File(filePath) , "qiniu-kodo-pub").getData() ;
        log.debug("previewUrl:{}" , previewUrl);

        return "<iframe src='"+previewUrl+"' class='role-chat-iframe-inner'></iframe>" ;
    }

}
