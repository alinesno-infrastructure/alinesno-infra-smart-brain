package com.alinesno.infra.smart.assistant.screen.scene.media.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.alinesno.infra.smart.assistant.screen.scene.media.service.IAliyunMediaClipService;
import com.alinesno.infra.smart.assistant.screen.core.dto.media.VideoBean;
import com.aliyun.ice20201109.Client;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * 阿里云视频剪辑服务实现
 * @version 1.0.0
 * @author luoxiaodong
 */
@Slf4j
@Service
public class AliyunMediaClipServiceImpl implements IAliyunMediaClipService {

    private Client iceClient;

//    private final AliyunProperties aliyunProperties;
//
//    @SneakyThrows
//    public AliyunMediaClipServiceImpl(AliyunProperties aliyunProperties) {
//        this.aliyunProperties = aliyunProperties;
//        if(StringUtils.isEmpty(aliyunProperties.getAccessKeyId()) || StringUtils.isEmpty(aliyunProperties.getAccessKeySecret())){
//            log.warn("阿里云OSS未配置，请检查配置文件");
//        }else {
//            this.initClient(aliyunProperties);
//        }
//    }

    @Override
    public void generateGlobalKouBaoVideo(VideoBean bean) {
        log.debug("开始生成视频:{}" , bean);
    }

    @SneakyThrows
    @Override
    public JSONArray generateGroupKouBaoVideo(VideoBean bean) {
        log.debug("开始生成视频:{}" , bean);
        return runAliyunMediaClip(bean);
    }

    public JSONArray runAliyunMediaClip(VideoBean videoBean) throws Exception {
        JSONArray mediaGroupArray = new JSONArray();

//        List<MediaBean> mediaArray = videoBean.getMediaArray() ;
//        int index = 1 ;
//        for (MediaBean mediaBean : mediaArray) {
//            JSONObject mediaGroup = new JSONObject();
//
//            mediaGroup.put("GroupName", "middle_" + index);
//            mediaGroup.put("MediaArray", mediaBean.getMediaArray()) ; // 媒体地址
//            mediaGroup.put("SpeechTextArray" , mediaBean.getMediaSpeechTextArray()); // 口播文案
//            mediaGroup.put("Duration" , mediaBean.getDuration()) ;
//
//            mediaGroupArray.add(mediaGroup);
//            index ++ ;
//        }
//
//        List<String> titleArray = videoBean.getTitleArray() ; // 视频标题
//
//        JSONObject inputConfig = new JSONObject();
//        inputConfig.put("MediaGroupArray", mediaGroupArray);
//        inputConfig.put("TitleArray", titleArray);
//
//        if(videoBean.isGlobalVoiceover()){
//            inputConfig.put("SpeechTextArray", videoBean.getSpeechTextArray());  // 口播文案
//        }
//
//        if(CollectionUtils.isEmpty(videoBean.getBackgroundImageArray())){  // 背景图片
//            inputConfig.put("BackgroundImageArray", videoBean.getBackgroundImageArray());
//        }
//
//        if(CollectionUtils.isEmpty(videoBean.getBackgroundMusicArray())){  // 背景音乐
//            inputConfig.put("BackgroundMusicArray", videoBean.getBackgroundMusicArray());
//        }
//
//        int produceCount = videoBean.getProduceCount() ; // 生成的成片数
//
//        // 成片宽高，生成横屏文件
//        int outputWidth =  videoBean.getOutputWidth() ;
//        int outputHeight =  videoBean.getOutputHeight() ;
//
//        // 成片oss地址，需包含{index} 占位符
//        String mediaUrl = "http://" + aliyunProperties.getBucketName() + ".oss-" + aliyunProperties.getRegionId() + ".aliyuncs.com/script/output_{index}_"+System.currentTimeMillis()+"_w.mp4";
//
//        JSONObject outputConfig = new JSONObject();
//        outputConfig.put("MediaURL", mediaUrl);
//        outputConfig.put("Count", produceCount);
//        outputConfig.put("Width", outputWidth);
//        outputConfig.put("Height", outputHeight);
//
//        log.debug("请求视频生成数据:{}" , JSONUtil.toJsonPrettyStr(inputConfig.toJSONString()));
//
//        // 提交一键成片任务
//        SubmitBatchMediaProducingJobRequest request = new SubmitBatchMediaProducingJobRequest();
//        request.setInputConfig(inputConfig.toJSONString());
//        request.setOutputConfig(outputConfig.toJSONString());
//
//        SubmitBatchMediaProducingJobResponse response = iceClient.submitBatchMediaProducingJob(request);
//        String jobId = response.getBody().getJobId();
//        log.debug("Start script batch job, batchJobId: " + jobId);
//
//        JSONArray outputMedia = null ;
//
//        // 轮询任务状态直到全部结束
//        log.debug("Waiting job finished...");
//        int maxTry = 3000;
//        int i = 0;
//        while (i < maxTry) {
//            Thread.sleep(3000);
//            i++;
//            GetBatchMediaProducingJobRequest getRequest = new GetBatchMediaProducingJobRequest();
//            getRequest.setJobId(jobId);
//            GetBatchMediaProducingJobResponse getResponse = iceClient.getBatchMediaProducingJob(getRequest);
//            String status = getResponse.getBody().getEditingBatchJob().getStatus();
//            log.debug("BatchJobId: " + jobId + ", status:" + status);
//
//            if ("Failed".equals(status)) {
//                log.debug("Batch job failed. JobInfo: " + JSONObject.toJSONString(getResponse.getBody().getEditingBatchJob()));
//                throw new Exception("Produce failed. BatchJobId: " + jobId);
//            }
//
//            if ("Finished".equals(status)) {
//                log.debug("Batch job finished. JobInfo: " + JSONObject.toJSONString(getResponse.getBody().getEditingBatchJob().getSubJobList()));
//                outputMedia = JSONArray.parseArray(JSONObject.toJSONString(getResponse.getBody().getEditingBatchJob().getSubJobList())) ;
//                break;
//            }
//        }

//        return outputMedia ;

        return null ;
    }

//    /**
//     * 初始化iceClient
//      * @throws Exception
//     */
//    public void initClient(AliyunProperties aliyunProperties) throws Exception {
//        com.aliyun.credentials.Client credentialClient = new com.aliyun.credentials.Client();
//
//        Config config = new Config();
//        config.setCredential(credentialClient);
//
//        config.accessKeyId = aliyunProperties.getAccessKeyId() ;
//        config.accessKeySecret = aliyunProperties.getAccessKeySecret() ;
//
//        config.endpoint = "ice." + aliyunProperties.getRegionId() + ".aliyuncs.com";
//        config.regionId =  aliyunProperties.getRegionId() ;
//
//        iceClient = new Client(config);
//    }
}
