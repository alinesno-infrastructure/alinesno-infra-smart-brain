import request from '@/utils/request'
import { parseStrEmpty } from "@/utils/ruoyi";

// 接口配置项
const examPrefix = '/api/infra/smart/assistant/scene/examInfo/';

const examUrl = {
  // 考试管理
  addExamInfo: examPrefix + "addExamInfo",
  detail: examPrefix + "detail",
  deleteExam: examPrefix + "deleteById",
  queryExamList: examPrefix + "queryExamList",
  saveMarkingResults: examPrefix + "saveMarkingResults",
  exportUrl: examPrefix + "export/url/"
}

// 保存阅卷结果
export function saveMarkingResults(data) {
  return request({
    url: examUrl.saveMarkingResults,
    method: 'post',
    data: data
  })
}

// 详细查询
export function detail(id) {
  return request({
    url: examUrl.detail + '/' + parseStrEmpty(id),
    method: 'get'
  })
}

// 添加考试信息
export function addExamInfo(data) {
  return request({
    url: examUrl.addExamInfo,
    method: 'post',
    data: data
  })
}

// 删除考试信息
export function deleteExam(id) {
  return request({
    url: examUrl.deleteExam,
    method: 'get',
    params: { id: parseStrEmpty(id) }
  })
}

//  查询考试列表
export function queryExamList(data) {
  return request({
    url: examUrl.queryExamList,
    method: 'post',
    data: data
  })
}

// 获取试卷导出URL
export function getExportUrl(pagerId, showAnswer = false) {
  return request({
    url: examUrl.exportUrl + parseStrEmpty(pagerId),
    method: 'get',
    params: { showAnswer }
  })
}