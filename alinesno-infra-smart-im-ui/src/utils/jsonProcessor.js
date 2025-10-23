/**
 * JSON处理工具类（精简版）
 * 专注处理三类核心格式：任务列表、带工具ReAct、不带工具ReAct
 */
class JSONProcessor {
  /**
   * 核心入口：将输入文本转换为易读格式
   * @param {string} text - 输入文本（可能含JSON代码块/纯JSON/普通文本）
   * @returns {string} - 处理后的文本
   */
  static convertToText(text) {
    // 基础校验
    if (!text || typeof text !== 'string') return text || '';
    const trimmedText = text.trim();
    if (!trimmedText) return text;

    // 步骤1：清理文本（移除JSON代码块标记）
    const cleanJsonStr = this.cleanJsonCodeBlock(trimmedText);

    // 步骤2：尝试解析JSON
    let jsonData;
    try {
      jsonData = JSON.parse(cleanJsonStr);
    } catch (error) {
      console.warn('非标准JSON格式，返回原文本:', error);
      return text;
    }

    // 步骤3：识别JSON类型并格式化
    if (this.isTaskList(jsonData)) {
      return this.formatTaskList(jsonData);
    } else if (this.isReActStructure(jsonData)) {
      return this.formatReAct(jsonData);
    } else {
      // 非目标格式，返回格式化后的原始JSON字符串
      return JSON.stringify(jsonData, null, 2);
    }
  }

  /**
   * 清理JSON代码块标记（如```json、```）
   * @param {string} text - 原始文本
   * @returns {string} 清理后的JSON字符串
   */
  static cleanJsonCodeBlock(text) {
    // 匹配开头：```json、```javascript、```等
    let cleanStr = text.replace(/^```\w*\s*/, '');
    // 匹配结尾：```（允许前面有换行/空格）
    cleanStr = cleanStr.replace(/\s*```\s*$/, '');
    return cleanStr.trim();
  }

  /**
   * 识别是否为「任务列表」格式
   * @param {any} data - 解析后的JSON数据
   * @returns {boolean}
   */
  static isTaskList(data) {
    // 特征：数组结构 + 每个元素含order、taskName、taskDesc
    return Array.isArray(data) && data.length > 0 
      && data.every(item => item.order && item.taskName && item.taskDesc);
  }

  /**
   * 识别是否为「ReAct」格式
   * @param {any} data - 解析后的JSON数据
   * @returns {boolean}
   */
  static isReActStructure(data) {
    // 特征：对象结构 + 必含thought和finalAnswer + 可选含tools
    return typeof data === 'object' && data !== null 
      && data.thought !== undefined 
      && data.finalAnswer !== undefined 
      && (data.tools === undefined || Array.isArray(data.tools));
  }

  /**
   * 格式化「任务列表」
   * @param {Array} taskList - 任务列表数组
   * @returns {string} 易读文本
   */
  static formatTaskList(taskList) {
    let result = "### 任务列表\n\n";
    taskList.forEach(task => {
      result += `#### 任务 ${task.order}：${task.taskName || ''}\n`;
      // 确保传入cleanMarkdown的是字符串类型
      result += `**任务描述**：${this.cleanMarkdown(String(task.taskDesc || ''))}\n\n`;
    });
    return result.trim();
  }

  /**
   * 格式化「ReAct」结构（兼容带工具/不带工具）
   * @param {object} reactData - ReAct格式JSON数据
   * @returns {string} 易读文本
   */
  static formatReAct(reactData) {
    let result = "";

    // 1. 格式化思考过程（带特定样式）
    if (reactData.thought) {
      result += `<div style="color: #aaa; border-left: 2px solid #ddd; padding-left: 10px; font-size: 14px; line-height: 1.3rem; border-radius: 5px; margin-top: 5px; margin-bottom: 5px; margin-left: 10px;">`;
      // 确保传入cleanMarkdown的是字符串类型
      result += this.cleanMarkdown(String(reactData.thought));
      result += `</div>\n\n`;
    }

    // 2. 格式化工具列表（若存在）
    if (reactData.tools && reactData.tools.length > 0) {
      result += "### 计划使用工具\n";
      reactData.tools.forEach((tool, index) => {
        result += `${index + 1}. 工具名称：${tool.name || "未知工具"}\n`;
        result += `   - 工具ID：${tool.id || "无"}\n`;
        result += `   - 工具类型：${tool.type || "无"}\n`;
        if (tool.args_list) {
          const argsStr = typeof tool.args_list === "string" 
            ? tool.args_list 
            : JSON.stringify(tool.args_list, null, 2);
          result += `   - 工具参数：\n${argsStr}\n`;
        }
        result += "\n\n";
      });
    }

    // 3. 格式化最终答案（若存在）
    if (reactData.finalAnswer !== undefined && reactData.finalAnswer !== "") {
      result += "### 最终答案\n";
      // 确保传入cleanMarkdown的是字符串类型
      result += this.cleanMarkdown(String(reactData.finalAnswer)) + "\n\n";
    }

    return result.trim();
  }

  /**
   * 清理Markdown标记（保留核心结构，移除冗余格式）
   * @param {string} markdown - 含Markdown的文本
   * @returns {string} 清理后的文本
   */
  static cleanMarkdown(markdown) {
    if (!markdown) return "";
    return markdown
      .replace(/<iframe.*?<\/iframe>/g, "[图表]") // 替换图表iframe
      .replace(/!\[.*?\]\(.*?\)/g, "[图片]") // 替换图片链接
      .replace(/```[\s\S]*?```/g, "[代码块]") // 替换代码块
      .replace(/^\s+|\s+$/g, ""); // 去除首尾空白
  }

  /**
   * 辅助方法：检查文本是否为目标JSON格式
   * @param {string} text - 输入文本
   * @returns {boolean}
   */
  static isTargetJSON(text) {
    try {
      const cleanStr = this.cleanJsonCodeBlock(text);
      const data = JSON.parse(cleanStr);
      return this.isTaskList(data) || this.isReActStructure(data);
    } catch {
      return false;
    }
  }
}

// 导出工具类
export default JSONProcessor;