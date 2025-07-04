import request from '@/utils/request'
import { parseStrEmpty } from "@/utils/ruoyi";

// 接口配置项
var prefix = '/api/infra/smart/assistant/scene/examPagerJob/' ;

var managerUrl = {
  validateAccount: prefix +"validateAccount",
  saveAccountScore: prefix +"saveAccountScore",
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