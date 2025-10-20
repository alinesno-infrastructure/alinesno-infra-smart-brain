// utils/llmIcons.js (or you can put this in your component file)
const getLlmIconPath = (providerCode) => {
  const iconMap = {
    'aip': 'aip.png',
    'customModel': 'customModel.png',
    'deepseek': 'deepseek.png',
    'doubao': 'doubao.png',
    'gitee': 'gitee.png',
    'ollama': 'ollama.png',
    'qwen': 'qwen.png',
    'qwq': 'qwq.png',
    'jdcloud': 'jdcloud.png',
    'siliconflow': 'siliconflow.png'
  };
  
  const fileName = iconMap[providerCode] || 'customModel.png';
  return new URL(`/src/assets/icons/llm/${fileName}`, import.meta.url).href;
};

export { 
  getLlmIconPath,
};