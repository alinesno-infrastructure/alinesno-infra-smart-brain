import { ElMessage } from 'element-plus';

/**
 * 通用js方法封装处理
 * Copyright (c) 2019 ruoyi
 */

// 日期格式化
export function parseTime(time, pattern) {
  if (arguments.length === 0 || !time) {
    return null
  }
  const format = pattern || '{y}-{m}-{d} {h}:{i}:{s}'
  let date
  if (typeof time === 'object') {
    date = time
  } else {
    if ((typeof time === 'string') && (/^[0-9]+$/.test(time))) {
      time = parseInt(time)
    } else if (typeof time === 'string') {
      time = time.replace(new RegExp(/-/gm), '/').replace('T', ' ').replace(new RegExp(/\.[\d]{3}/gm), '');
    }
    if ((typeof time === 'number') && (time.toString().length === 10)) {
      time = time * 1000
    }
    date = new Date(time)
  }
  const formatObj = {
    y: date.getFullYear(),
    m: date.getMonth() + 1,
    d: date.getDate(),
    h: date.getHours(),
    i: date.getMinutes(),
    s: date.getSeconds(),
    a: date.getDay()
  }
  const time_str = format.replace(/{(y|m|d|h|i|s|a)+}/g, (result, key) => {
    let value = formatObj[key]
    // Note: getDay() returns 0 on Sunday
    if (key === 'a') { return ['日', '一', '二', '三', '四', '五', '六'][value] }
    if (result.length > 0 && value < 10) {
      value = '0' + value
    }
    return value || 0
  })
  return time_str
}

// 表单重置
export function resetForm(refName) {
  if (this.$refs[refName]) {
    this.$refs[refName].resetFields();
  }
}

// 字符长度截取
export function truncateString(str, maxLength) {
   if (!str || str.length <= maxLength) {
        return str;
    }
    return str.substring(0, maxLength) + '...';
}

// 添加日期范围
export function addDateRange(params, dateRange, propName) {
  let search = params;
  search.params = typeof (search.params) === 'object' && search.params !== null && !Array.isArray(search.params) ? search.params : {};
  dateRange = Array.isArray(dateRange) ? dateRange : [];
  if (typeof (propName) === 'undefined') {
    search.params['beginTime'] = dateRange[0];
    search.params['endTime'] = dateRange[1];
  } else {
    search.params['begin' + propName] = dateRange[0];
    search.params['end' + propName] = dateRange[1];
  }
  return search;
}

// 回显数据字典
export function selectDictLabel(datas, value) {
  if (value === undefined) {
    return "";
  }
  var actions = [];
  Object.keys(datas).some((key) => {
    if (datas[key].value == ('' + value)) {
      actions.push(datas[key].label);
      return true;
    }
  })
  if (actions.length === 0) {
    actions.push(value);
  }
  return actions.join('');
}

// 回显数据字典（字符串数组）
export function selectDictLabels(datas, value, separator) {
  if (value === undefined || value.length ===0) {
    return "";
  }
  if (Array.isArray(value)) {
    value = value.join(",");
  }
  var actions = [];
  var currentSeparator = undefined === separator ? "," : separator;
  var temp = value.split(currentSeparator);
  Object.keys(value.split(currentSeparator)).some((val) => {
    var match = false;
    Object.keys(datas).some((key) => {
      if (datas[key].value == ('' + temp[val])) {
        actions.push(datas[key].label + currentSeparator);
        match = true;
      }
    })
    if (!match) {
      actions.push(temp[val] + currentSeparator);
    }
  })
  return actions.join('').substring(0, actions.join('').length - 1);
}

// 字符串格式化(%s )
export function sprintf(str) {
  var args = arguments, flag = true, i = 1;
  str = str.replace(/%s/g, function () {
    var arg = args[i++];
    if (typeof arg === 'undefined') {
      flag = false;
      return '';
    }
    return arg;
  });
  return flag ? str : '';
}

// 转换字符串，undefined,null等转化为""
export function parseStrEmpty(str) {
  if (!str || str == "undefined" || str == "null") {
    return "";
  }
  return str;
}

// 数据合并
export function mergeRecursive(source, target) {
  for (var p in target) {
    try {
      if (target[p].constructor == Object) {
        source[p] = mergeRecursive(source[p], target[p]);
      } else {
        source[p] = target[p];
      }
    } catch (e) {
      source[p] = target[p];
    }
  }
  return source;
};

/**
 * 构造树型结构数据
 * @param {*} data 数据源
 * @param {*} id id字段 默认 'id'
 * @param {*} parentId 父节点字段 默认 'parentId'
 * @param {*} children 孩子节点字段 默认 'children'
 */
export function handleTree(data, id, parentId, children) {
  let config = {
    id: id || 'id',
    parentId: parentId || 'parentId',
    childrenList: children || 'children'
  };

  var childrenListMap = {};
  var nodeIds = {};
  var tree = [];

  for (let d of data) {
    let parentId = d[config.parentId];
    if (childrenListMap[parentId] == null) {
      childrenListMap[parentId] = [];
    }
    nodeIds[d[config.id]] = d;
    childrenListMap[parentId].push(d);
  }

  for (let d of data) {
    let parentId = d[config.parentId];
    if (nodeIds[parentId] == null) {
      tree.push(d);
    }
  }

  for (let t of tree) {
    adaptToChildrenList(t);
  }

  function adaptToChildrenList(o) {
    if (childrenListMap[o[config.id]] !== null) {
      o[config.childrenList] = childrenListMap[o[config.id]];
    }
    if (o[config.childrenList]) {
      for (let c of o[config.childrenList]) {
        adaptToChildrenList(c);
      }
    }
  }
  return tree;
}

/**
* 参数处理
* @param {*} params  参数
*/
export function tansParams(params) {
  let result = ''
  for (const propName of Object.keys(params)) {
    const value = params[propName];
    var part = encodeURIComponent(propName) + "=";
    if (value !== null && value !== "" && typeof (value) !== "undefined") {
      if (typeof value === 'object') {
        for (const key of Object.keys(value)) {
          if (value[key] !== null && value[key] !== "" && typeof (value[key]) !== 'undefined') {
            let params = propName + '[' + key + ']';
            var subPart = encodeURIComponent(params) + "=";
            result += subPart + encodeURIComponent(value[key]) + "&";
          }
        }
      } else {
        result += part + encodeURIComponent(value) + "&";
      }
    }
  }
  return result
}


// 返回项目路径
export function getNormalPath(p) {
  if (p.length === 0 || !p || p == 'undefined') {
    return p
  };
  let res = p.replace('//', '/')
  if (res[res.length - 1] === '/') {
    return res.slice(0, res.length - 1)
  }
  return res;
}

// 验证是否为blob格式
export function blobValidate(data) {
  return data.type !== 'application/json'
}

// 从url中查询到指定名称的参数值
export const getParam = function(name, defaultValue){
  var query = window.location.search.substring(1);
  var vars = query.split("&");
  for (var i=0;i<vars.length;i++) {
      var pair = vars[i].split("=");
      if(pair[0] == name){return pair[1];}
  }
  return(defaultValue == undefined ? null : defaultValue);
}

// 处理复制文本到剪贴板
export const handleCopyGenContent = async (item) => {
  // 输入验证
  if (!item) {
    console.error('传入的 item 无效');
    ElMessage.error('复制失败，请检查传入的数据。');
    return;
  }

  try {
    const text = item.reasoningText? (item.reasoningText + '\r\n') + item.chatText : item.chatText;

    if (navigator.clipboard && window.isSecureContext) {
      // 使用 navigator.clipboard 进行复制
      await navigator.clipboard.writeText(text);
      ElMessage.success('复制成功！');
    } else {
      // 创建 textarea 元素进行复制
      const textArea = document.createElement('textarea');
      textArea.value = text;
      // 使 textarea 不在 viewport 中，同时设置不可见
      textArea.style.position = 'absolute';
      textArea.style.left = '-9999px';
      document.body.appendChild(textArea);
      textArea.focus();
      textArea.select();

      try {
        // 执行复制命令
        const successful = document.execCommand('copy');
        if (successful) {
          ElMessage.success('复制成功！');
        } else {
          throw new Error('执行复制命令失败');
        }
      } catch (copyError) {
        console.error('复制失败:', copyError);
        ElMessage.error('复制失败，请稍后重试。');
      } finally {
        // 移除 textarea 元素
        textArea.remove();
      }
    }
  } catch (error) {
    console.error('复制失败:', error);
    ElMessage.error('复制失败，请稍后重试。');
  }
};

// 计算任务用时
export const taskUseTime = (startTime, endTime) => {
  if (!startTime || !endTime) {
    return '--';
  }
  
  // 将时间字符串转换为Date对象
  const start = new Date(startTime);
  const end = new Date(endTime);
  
  // 计算毫秒差
  const diffMs = end - start;
  
  // 转换为秒数
  const diffSec = Math.floor(diffMs / 1000);
  
  // 不足1分钟显示秒
  if (diffSec < 60) {
    return `${diffSec}秒`;
  }
  
  // 计算分钟和秒
  const minutes = Math.floor(diffSec / 60);
  const seconds = diffSec % 60;
  
  // 不足1小时显示分和秒
  if (minutes < 60) {
    return `${minutes}分${seconds}秒`;
  }
  
  // 计算小时、分钟和秒
  const hours = Math.floor(minutes / 60);
  const remainingMinutes = minutes % 60;
  
  return `${hours}时${remainingMinutes}分${seconds}秒`;
}

// 文件类型图标映射
const fileTypeIcons = {
  pdf: 'fa-file-pdf',
  doc: 'fa-file-word',
  docx: 'fa-file-word',
  xls: 'fa-file-excel',
  xlsx: 'fa-file-excel',
  jpg: 'fa-file-image',
  jpeg: 'fa-file-image',
  png: 'fa-file-image',
  txt: 'fa-file-lines',
  ppt: 'fa-file-powerpoint',
  pptx: 'fa-file-powerpoint',
  csv: 'fa-file-csv',
  // 可以继续添加其他类型...
  default: 'fa-file' // 默认图标
}

// 获取文件对应的图标
export const getFileIcon = (fileName) => {
  if (!fileName) return fileTypeIcons.default
  
  const extension = fileName.split('.').pop().toLowerCase()
  return fileTypeIcons[extension] || fileTypeIcons.default
}