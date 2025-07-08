import request from '@/utils/request'
import { parseStrEmpty } from "@/utils/ruoyi";

// 接口配置项
var prefix = '/api/infra/smart/assistant/scene/examPagerJob/' ;

var managerUrl = {
  validateAccount: prefix +"validateAccount",
  saveAccountScore: prefix +"saveAccountScore",
  checkStatus: prefix + "checkStatus" ,  // 获取任务状态
  examAnalysis: prefix + "examAnalysis" , // 获取考试分析
}

// 获取考试分析结果
export function examAnalysis(examId , examineeId) {
  return request({
    url: managerUrl.examAnalysis + "?examId=" + examId + "&examineeId=" + examineeId ,
    method: 'get'
  })
}

// 获取任务状态
export function checkStatus(examId , examineeId) {
  return request({
    url: managerUrl.checkStatus + "/" + examId + "/" + examineeId ,
    method: 'get'
  })
}

// 验证考生考试信息 
export function validateAccount(data) {
  return request({
    url: managerUrl.validateAccount,
    method: 'post',
    data: data
  })
}

// 保存考生考试信息
export function saveAccountScore(params){
  return request({
    url: managerUrl.saveAccountScore,
    method: 'post',
    data: {
      accountId: params.accountId,
      examId: params.examId,
      answers: params.answers,
      submitType: params.isAutoSubmit ? 'auto' : 'manual',
      status: params.isAbnormal ? 'abnormal' : 'normal',
      submitTime: new Date().toISOString() , 
      questions: params.questions
    }
  })
}