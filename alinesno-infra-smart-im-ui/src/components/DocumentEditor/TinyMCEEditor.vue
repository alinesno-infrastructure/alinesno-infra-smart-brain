<template>
  <div>  
    <editor v-model="myValue" :init="init" :enabled="enabled" :id="tinymceId" /> 
  </div>
</template>

<script setup>
import { computed, reactive, watch, ref, nextTick, onMounted } from "vue"; //全屏
import { uploadFile } from "@/api/base/im/scene/contentFormatterDocument";
  
import tinymce from "tinymce/tinymce";
// import "tinymce/skins/content/default/content.css";
import Editor from "@tinymce/tinymce-vue";
import "tinymce/icons/default/icons";
import "tinymce/models/dom"; // 一定要引入
import "tinymce/themes/silver"; // 界面UI主题
import "tinymce/plugins/image";
import "tinymce/plugins/table";
import "tinymce/plugins/lists"; // 列表插件
import "tinymce/plugins/wordcount"; // 文字计数
import "tinymce/plugins/preview"; // 预览
import "tinymce/plugins/emoticons"; // emoji表情
import "tinymce/plugins/emoticons/js/emojis.js"; //必须引入这个文件才有表情图库
import "tinymce/plugins/code"; // 编辑源码
import "tinymce/plugins/link"; // 链接插件
import "tinymce/plugins/advlist"; //高级列表
import "tinymce/plugins/codesample"; //代码示例
import "tinymce/plugins/autoresize"; // 自动调整编辑器大小
import "tinymce/plugins/quickbars"; // 光标处快捷提示
import "tinymce/plugins/nonbreaking"; //插入不间断空格
import "tinymce/plugins/searchreplace"; //查找替换
import "tinymce/plugins/autolink"; //自动链接
import "tinymce/plugins/directionality"; //文字方向
import "tinymce/plugins/visualblocks"; //显示元素范围
import "tinymce/plugins/visualchars"; //显示不可见字符
import "tinymce/plugins/charmap"; // 特殊符号
import "tinymce/plugins/nonbreaking"; //插入不间断空格
import "tinymce/plugins/insertdatetime"; //插入日期时间
import "tinymce/plugins/importcss"; //引入自定义样式的css文件
import "tinymce/plugins/accordion"; // 可折叠数据手风琴模式
import "tinymce/plugins/anchor"; //锚点
import "tinymce/plugins/fullscreen";
 
const emits = defineEmits(["update:modelValue", "setHtml"]);
//这里我选择将数据定义在props里面，方便在不同的页面也可以配置出不同的编辑器，当然也可以直接在组件中直接定义
const props = defineProps({
  modelValue: {
    type: String,
    default: () => {
      return "";
    },
  },
  baseUrl: {
    type: String,
    default: "",
  },
  enabled: {
    type: Boolean,
    default: true,
  },
  // 编辑器初始可编辑状态
  editable_root: {
    type: Boolean,
    default: true,
  },
  plugins: {
    type: [String, Array],
    default: "importcss autoresize searchreplace autolink directionality code visualblocks visualchars fullscreen image link codesample table charmap nonbreaking anchor insertdatetime advlist lists wordcount charmap quickbars emoticons accordion ",
  },
  knwlgId: {
    type: String,
  },
  toolbar: {
    type: [String, Array, Boolean],
    default: "undo redo | accordion accordionremove bold italic underline strikethrough ltr rtl indent searchreplace fullscreen",
  },
  readonly: {
    type: Boolean,
    default: false,
  },
  minHeight: {
    type: Number,
    default: 330,
  },
  contentMargins: {
    type: Object,
    default: () => ({ top: 1.04, bottom:1.54, left: 2.17, right: 2.17 }) // 默认Word标准边距(厘米)
  },
  annotations: {
    type: Array,
    default: () => []
  }
});
const loading = ref(false);
const tinymceId = ref(
  "vue-tinymce-" + +new Date() + ((Math.random() * 1000).toFixed(0) + "")
);

const font_family_formats = ref(` 
  宋体=SimSun;
  仿宋=FangSong;
  仿宋_GB2312=FangSong_GB2312;
  楷体=KaiTi;
  楷体_GB2312=KaiTi_GB2312;
  黑体=SimHei;
  微软雅黑='Microsoft YaHei';
  微软正黑体='Microsoft JhengHei';
  华文宋体=STSong;
  华文仿宋=STFangsong;
  华文楷体=STKaiti;
  华文黑体=STHeiti;
  华文中宋=STZhongsong;
  方正小标宋=FZXiaoBiaoSong-B05;
  方正仿宋=FZFangSong-Z02;
  方正楷体=FZKai-Z03;
  方正黑体=FZHei-B01;
  思源宋体=Noto Serif SC;
  思源黑体=Noto Sans SC;
   
  Arial=Arial, Helvetica, sans-serif;
  Helvetica=Helvetica, Arial, sans-serif;
  Verdana=Verdana, Geneva, sans-serif;
  Tahoma=Tahoma, Geneva, sans-serif;
  Segoe UI='Segoe UI', Tahoma, Geneva, sans-serif;
  Calibri=Calibri, Candara, Segoe, sans-serif;
  Trebuchet MS='Trebuchet MS', Helvetica, sans-serif;
  Gill Sans='Gill Sans', 'Gill Sans MT', Calibri, sans-serif;
   
  Times New Roman='Times New Roman', Times, serif;
  Georgia=Georgia, Times, serif;
  Palatino=Palatino, 'Palatino Linotype', serif;
  Garamond=Garamond, Baskerville, 'Baskerville Old Face', serif;
  Book Antiqua='Book Antiqua', Palatino, serif;
   
  Courier New='Courier New', Courier, monospace;
  Consolas=Consolas, monaco, monospace;
  Monaco=Monaco, Consolas, monospace;
   
  Impact=Impact, Charcoal, sans-serif;
  Comic Sans MS='Comic Sans MS', cursive;
  Lucida Sans Unicode='Lucida Sans Unicode', 'Lucida Grande', sans-serif;
   
  微软雅黑 + Arial='Microsoft YaHei', Arial, sans-serif;
  宋体 + Times New Roman=SimSun, 'Times New Roman', serif;
  黑体 + Helvetica=SimHei, Helvetica, sans-serif;
  思源黑体 + Segoe UI='Noto Sans SC', 'Segoe UI', sans-serif
`) ; 


// 更新边距的方法，直接接收厘米值
const updateMargins = (margins) => {
  const editor = tinymce.get(tinymceId.value);
  if (editor) {
    editor.dom.setStyles(editor.getBody(), {
      marginTop: `${margins.top}cm`,
      marginBottom: `${margins.bottom}cm`,
      marginLeft: `${margins.left}cm`,
      marginRight: `${margins.right}cm`,
      padding: '0',  
    });
  }
};

// 厘米转像素的转换函数
const cmToPx = (cm) => {
  return cm * 37.8; // 1cm ≈ 37.8px
};


const addCustomStyles = (editor) => {
  // 确保样式只添加一次
  if (editor.contentDocument.getElementById('annotation-styles')) return;
  
  const style = editor.contentDocument.createElement('style');
  style.id = 'annotation-styles';
  style.innerHTML = `
    .annotation-highlight {
      background-color: #FFF9C4 !important;
      position: relative;
      cursor: pointer;
    }
    .annotation-tooltip {
      position: absolute;
      bottom: 100%;
      left: 50%;
      transform: translateX(-50%);
      background-color: #333;
      color: white;
      padding: 8px 12px;
      border-radius: 4px;
      font-size: 14px;
      white-space: normal;
      min-width: 200px;
      z-index: 1000;
      display: none;
    }
    .annotation-highlight:hover .annotation-tooltip {
      display: block;
    }
  `;
  editor.contentDocument.head.appendChild(style);
};

// 显示批注工具栏
const highlightAnnotations = () => {
  const editor = tinymce.get(tinymceId.value);
  if (!editor) return;

  // 清除旧标记
  editor.dom.select('.annotation-marker').forEach(marker => {
    editor.dom.remove(marker, true);
  });

  // 创建浮动工具栏容器
  let tooltipContainer = editor.getBody().querySelector('.annotation-tooltip-container');
  if (!tooltipContainer) {
    tooltipContainer = editor.dom.create('div', {
      'class': 'annotation-tooltip-container',
      style: 'position: absolute; z-index: 10000; display: none;'
    });
    editor.getBody().appendChild(tooltipContainer);
  }

  props.annotations.forEach((annotation, index) => {
    const ranges = findTextRanges(editor, annotation.selectText);
    
    ranges.forEach(range => {
      // 创建标记元素
      const marker = editor.dom.create('span', {
        'class': 'annotation-marker',
        'data-annotation-id': index,
        'data-original-text': range.toString()
      });

      try {
        // 包裹选中文本
        range.surroundContents(marker);
        
        // 添加交互事件
        editor.dom.bind(marker, 'click', (e) => {
          e.stopPropagation();
          showAnnotationPopup(editor, tooltipContainer, annotation, marker);
        });
      } catch (e) {
        console.warn('Failed to mark annotation:', e);
      }
    });
  });

  // 点击编辑器其他区域关闭弹窗
  editor.on('click', (e) => {
    if (!e.target.closest('.annotation-marker')) {
      tooltipContainer.style.display = 'none';
    }
  });
};

const showAnnotationPopup = (editor, container, annotation, marker) => {

 const editorContainer = editor.getContentAreaContainer();
  const editorRect = editorContainer.getBoundingClientRect();
  
  // 移除所有active状态
  editor.dom.select('.annotation-marker.active').forEach(m => {
    editor.dom.removeClass(m, 'active');
  });
  
  // 设置当前active状态
  editor.dom.addClass(marker, 'active');

  // 获取编辑器iframe和内容文档
  const iframe = editor.iframeElement;
  const iframeRect = iframe.getBoundingClientRect();
  const iframeDoc = iframe.contentDocument || iframe.contentWindow.document;
  
  // 获取标记在iframe中的位置
  const markerRect = marker.getBoundingClientRect();
  
  // 计算相对于iframe的位置
  const relativeTop = markerRect.top - iframeRect.top;
  const relativeLeft = markerRect.left - iframeRect.left;
  
  // 弹窗尺寸
  const popoverWidth = 350;
  const popoverHeight = 200; // 预估高度
  
  // 计算理想位置（标记正上方居中）
  let top = relativeTop - popoverHeight - 5; // 5px间距
  let left = relativeLeft + (markerRect.width / 2) - (popoverWidth / 2);
  
  // 边界检查
  const iframeWidth = iframe.offsetWidth;
  const iframeHeight = iframe.offsetHeight;
  
  // 水平方向调整
  if (left < 10) left = 10;
  if (left + popoverWidth > iframeWidth - 10) {
    left = iframeWidth - popoverWidth - 10;
  }
  
  // 垂直方向调整（如果上方空间不足，显示在下方）
  if (top < 10) {
    top = relativeTop + markerRect.height + 5;
  }
  
  // 创建弹窗容器（如果不存在）
  if (!container) {
    container = document.createElement('div');
    container.className = 'annotation-tooltip-container';
    container.style.position = 'fixed';
    container.style.zIndex = '10000';
    document.body.appendChild(container);
  }

  container.innerHTML = `
    <div class="annotation-popover" style="
      background: white;
      border-radius: 4px;
      box-shadow: 0 2px 12px rgba(0,0,0,0.1);
      border: 1px solid #EBEEF5;
      width: ${popoverWidth}px;
      top: ${top}px;
      left: ${left}px;
      padding: 12px;
    ">
      <div style="
        display: flex;
        justify-content: space-between;
        align-items: center;
        margin-bottom: 8px;
        padding-bottom: 8px;
        border-bottom: 1px solid #EBEEF5;
      ">
        <div style="font-weight: 500;">批注 ${annotation.id}</div>
        <button class="close-btn" style="
          background: none;
          border: none;
          cursor: pointer;
          font-size: 16px;
        ">×</button>
      </div>
      
      <div style="margin-bottom: 12px; line-height: 1.5;">
        ${escapeHtml(annotation.comment)}
      </div>
      
      <div style="
        background: #F5F7FA;
        padding: 8px;
        border-radius: 4px;
        margin-bottom: 12px;
      ">
        <div style="color: #909399; margin-bottom: 4px;">建议修改：</div>
        <div>${escapeHtml(annotation.suggestion)}</div>
      </div>
      
      <div style="display: flex; justify-content: flex-end; gap: 8px;">
        <button class="action-btn close-btn" style="
          padding: 5px 12px;
          background: white;
          border: 1px solid #DCDFE6;
        ">关闭</button>
        <button class="action-btn reject-btn" style="
          padding: 5px 12px;
          background: white;
          border: 1px solid #DCDFE6;
        ">拒绝</button>
        <button class="action-btn accept-btn" style="
          padding: 5px 12px;
          background: #409EFF;
          color: white;
          border: none;
        ">接受</button>
      </div>
      
      <div style="
        position: absolute;
        bottom: -6px;
        left: 50%;
        transform: translateX(-50%);
        width: 0;
        height: 0;
        border-left: 6px solid transparent;
        border-right: 6px solid transparent;
        border-top: 6px solid white;
      "></div>
    </div>
  `;

  // 定位弹窗
  container.style.display = 'block';
  container.style.top = `${markerRect.top - editorRect.top - container.offsetHeight - 5}px`;
  container.style.left = `${Math.max(
    10,
    Math.min(
      markerRect.left - editorRect.left + markerRect.width/2 - container.offsetWidth/2,
      editorRect.width - container.offsetWidth - 10
    )
  )}px`;

  // 添加按钮事件
  container.querySelectorAll('.close-btn').forEach(btn => {
    btn.addEventListener('click', () => {
      container.style.display = 'none';
      editor.dom.removeClass(marker, 'active');
    });
  });
  
  container.querySelector('.reject-btn').addEventListener('click', () => {
    // 恢复原始文本
    const originalText = marker.getAttribute('data-original-text');
    editor.dom.remove(marker);
    editor.selection.setContent(originalText);
    container.style.display = 'none';
  });
  
  container.querySelector('.accept-btn').addEventListener('click', () => {
    // 替换为建议内容
    editor.dom.remove(marker);
    editor.selection.setContent(annotation.suggestion);
    container.style.display = 'none';
  });
};
 
// 搜索和替换
const searchAndReplace = (searchText, replaceText = '') => {
  const editor = tinymce.get(tinymceId.value);
  if (editor) {  
    if(replaceText){
      editor.plugins.searchreplace.find(searchText) ;
      editor.plugins.searchreplace.replace(replaceText)
    }else{
      editor.plugins.searchreplace.find(searchText) ;
    } 
  }
};

const findTextRanges = (editor, text) => {
  const body = editor.getBody();
  const ranges = [];
  const walker = document.createTreeWalker(body, NodeFilter.SHOW_TEXT);
  
  // 标准化搜索文本（合并连续空格）
  const searchText = text ; // escapeSearchText(text, true).trim();
  const regex = new RegExp(escapeRegExp(searchText), 'gi');

  let node;
  while (node = walker.nextNode()) {
    let match;
    while ((match = regex.exec(node.nodeValue)) !== null) {
      const range = document.createRange();
      range.setStart(node, match.index);
      range.setEnd(node, match.index + searchText.length);
      ranges.push(range);
    }
  }

  return ranges;
};

const escapeRegExp = (str) => {
  return str.replace(/[.*+?^${}()|[\]\\]/g, '\\$&');
};

// HTML转义
const escapeHtml = (str) => {
  return str ? str.replace(/&/g, "&amp;")
             .replace(/</g, "&lt;")
             .replace(/>/g, "&gt;")
             .replace(/"/g, "&quot;")
             .replace(/'/g, "&#039;") : '';
};

// 监听annotations变化 - 添加延迟确保编辑器就绪
watch(() => props.annotations, (newVal) => {
  setTimeout(() => {
    highlightAnnotations();
  }, 500);
}, { deep: true });

//定义一个对象 init初始化
const init = ({
  selector: "#" + tinymceId.value, //富文本编辑器的id,
  language_url: "/tinymce/langs/zh_CN.js", // 语言包的路径，具体路径看自己的项目
  language: "zh_CN",  
  skin_url: "/tinymce/skins/ui/document-oxide", // skin路径，具体路径看自己的项目
  editable_root: props.editable_root, 
  branding: false, // 是否禁用“Powered by TinyMCE”
  promotion: false, //去掉 upgrade
  menubar: false , // 隐藏菜单栏 menubar: "edit view insert format tools table",
  toolbar: props.toolbar, //这里的数据是在props里面就定义好了的
  toolbar_location: "top" , 
  // toolbar_sticky: true,
  // toolbar_sticky_offset: 10,
  toolbar_mode: "wrap", // 工具栏模式 floating / sliding / scrolling / wrap   
  paste_data_images: true, //允许粘贴图像
  image_dimensions: false, //去除宽高属性
  plugins: props.plugins, //这里的数据是在props里面就定义好了的
  // 取消图片资源路径转换
  convert_urls: false,
  // table边框位0是否展示网格线
  visual: false,
  // 超链接默认打开方式
  link_default_target: "_blank",
  link_context_toolbar: true,
  // 默认快捷菜单
  // quickbars_insert_toolbar: "image codesample table",
  // 选中图片的快捷提示
  quickbars_image_toolbar: "alignleft aligncenter alignright | rotateleft rotateright | imageoptions",
  editimage_toolbar: "rotateleft rotateright | flipv fliph | editimage imageoptions",
  // 文字样式
  font_family_formats: font_family_formats.value ,   
  font_size_formats: "初号=31.5pt 小初=27pt 一号=19.5pt 小一=18pt 二号=16.5pt 小二=13.5pt 三号=16pt 小三=11.25pt 四号=10.5pt 小四=9pt 五号=7.875pt 小五=6.75pt 六号=5.625pt 七号=4.875pt 八号=3.75pt 6pt 7.5pt 8.25pt 36pt 48pt 54pt", //文字大小
  image_caption: true,
  editimage_cors_hosts: ["picsum.photos"],
  noneditable_class: "mceNonEditable",
  // 默认样式
  // 在init配置中添加 
  content_style: `
    .annotation-marker {
      background-color: rgba(255, 229, 143, 0.3);
      border-bottom: 2px dotted #FFB800;
      padding: 0 1px;
      border-radius: 2px;
      transition: all 0.2s;
      position: relative;
      cursor: pointer;
    }
    
    .annotation-marker.active {
      background-color: rgba(255, 213, 79, 0.5);
      box-shadow: 0 0 0 2px rgba(255, 184, 0, 0.3);
    }
    
    .annotation-popover {
      position: relative;
      z-index: 10000;
    }
  `,
  image_advtab: true,
  importcss_append: true,
  paste_webkit_styles: "all",
  paste_merge_formats: true,
  nonbreaking_force_tab: false,
  paste_auto_cleanup_on_paste: false,
  file_picker_types: "file",
  // 选中文字的快捷提示
  quickbars_selection_toolbar: "bold italic | quicklink h2 h3 blockquote quickimage quicktable",

  // 编辑器高度自适应 
  autoresize_bottom_margin: 20,  
  autoresize_overflow_padding: 16,
  min_height: props.minHeight-1,
  max_height: props.minHeight , 
  content_css: "/tinymce/skins/content/default/content.css", //以css文件方式自定义可编辑区域的css样式，css文件需自己创建并引入
  setup: function (editor) {
    editor.on('init', function() {

      // 初始化时应用边距
      editor.dom.setStyles(editor.getBody(), {
        marginTop: `${props.contentMargins.top}cm`,
        marginBottom: `${props.contentMargins.bottom}cm`,
        marginLeft: `auto`,
        marginRight: `auto`,
        padding: '0' , 
        maxWidth: '21cm'
      });

      if (editor.getContent() === '') {
        editor.setContent('<p style="color: #888;">请输入内容...</p>');
      }

      // 添加自定义样式
      addCustomStyles(editor);
      
      // 延迟执行高亮，确保DOM完全加载
      setTimeout(() => {
        highlightAnnotations();
      }, 1000);
      
    });

    editor.on('focus', function () {
      if (editor.getContent() === '<p style="color: #888;">请输入内容...</p>') {
        editor.setContent('');
      }
    });

    editor.on('blur', function () {
      if (editor.getContent() === '') {
        editor.setContent('<p style="color: #888;">请输入内容...</p>');
      }
    });

    // 内容改变时重新高亮批注
    editor.on('change', function() {
      // highlightAnnotations();
    });
  },
  paste_preprocess: function(plugin, args) {
    // 处理粘贴的内容中的图片
    // args.content = args.content.replace(/<img[^>]+src="file:[^>]+>/g, function(img) {
    //  return '<img src="" alt="图片需手动上传">';
    // });
  },
  
});

// 外部传递进来的数据变化
const myValue = computed({
  get() {
    return props.modelValue;
  },
  set(val) {
    emits("update:modelValue", val);
  },
});

//监听富文本中的数据变化
watch(
  () => myValue.value,
  () => {
    emits(
      "setHtml",
      tinymce.activeEditor.getContent({ format: "text" }),
      myValue.value
    );
  }
);

// 设置编辑器只读模式
watch(
  () => props.readonly,
  (newValue, oldValue) => {
    nextTick(() => {
      tinymce.activeEditor.mode.set(newValue ? "readonly" : "design");
      let iframeDom = document.querySelector("iframe");
      iframeDom &&
        (iframeDom.contentWindow.document.body.style.margin = newValue
          ? 0
          : "16px");
    });
  },
  { immediate: true }
);

//初始化编辑器
onMounted(() => {
  tinymce.init({});
});

// 设置值
const handleSetContent = (content) => {
  tinymce.activeEditor.setContent(content);
};

// 获取值
const handleGetContent = () => {
  console.log("tinymce.activeEditor.getContent() = " + tinymce.activeEditor.getContent());
  return tinymce.activeEditor.getContent();
};

defineExpose({
  handleSetContent,
  handleGetContent,
  updateMargins,
  searchAndReplace
});
</script>

<style lang="scss" scoped>
:deep(.tox-tinymce) {
  border: 1px solid #dcdfe6;
  border-radius: 10px;

  .tox-statusbar {
    display: none;
  }
}
</style>