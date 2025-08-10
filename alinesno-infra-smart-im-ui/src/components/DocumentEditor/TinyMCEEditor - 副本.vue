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
    default: "importcss autoresize searchreplace autolink directionality code visualblocks visualchars fullscreen image link codesample table charmap nonbreaking anchor insertdatetime advlist lists wordcount charmap quickbars emoticons accordion",
  },
  knwlgId: {
    type: String,
  },
  toolbar: {
    type: [String, Array, Boolean],
    default: "undo redo | accordion accordionremove bold italic underline strikethrough ltr rtl indent",
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

const highlightAnnotations = () => {
  const editor = tinymce.get(tinymceId.value);
  if (!editor || !editor.initialized) return;

  // 保存当前选区
  const bookmark = editor.selection.getBookmark();

  // 移除所有现有高亮
  editor.dom.select('.annotation-highlight').forEach(el => {
    editor.dom.remove(el, true);
  });

  // 获取编辑器内容
  const body = editor.getBody();
  const contentText = body.innerText || body.textContent;

  props.annotations.forEach((annotation, index) => {
    const searchText = annotation.selectText.replace(/\s+/g, ' ').trim();
    
    if (!contentText.includes(searchText)) {
      console.warn(`未找到文本: "${annotation.selectText}"`);
      return;
    }

    const ranges = findTextRanges(editor, searchText);
    
    ranges.forEach(range => {
      // 创建高亮元素
      const span = editor.dom.create('span', {
        'class': 'annotation-highlight',
        'data-annotation-id': index,
        'style': `
          background-color: rgba(255, 229, 143, 0.3);
          cursor: pointer;
          position: relative;
          border-bottom: 2px dotted #FFB800;
          padding: 0 2px;
          border-radius: 2px;
        `
      });
      
      // 创建批注弹窗内容
      const popoverContent = `
        <div class="annotation-popover" style="
          position: absolute;
          bottom: calc(100% + 8px);
          left: 50%;
          transform: translateX(-50%);
          background: #FFFFFF;
          color: #606266;
          padding: 12px;
          margin-bottom: -10px;
          border-radius: 4px;
          box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
          font-size: 14px;
          width: 420px; 
          z-index: 10000;
          display: none;
          text-indent: initial;
          border: 1px solid #EBEEF5;
        ">
          <div style="
            display: flex;
            justify-content: space-between;
            align-items: center;
            margin-bottom: 8px; 
            border-bottom: 1px solid #EBEEF5;
          ">
            <div style="font-weight: 500; color: #303133;">
              批注 ${index + 1}
            </div>
            <button class="annotation-close" style="
              background: none;
              border: none;
              cursor: pointer;
              font-size: 16px;
              color: #909399;
              padding: 0;
            ">×</button>
          </div>
          <div style="margin-bottom: 8px; line-height: 1.5;">
            ${escapeHtml(annotation.comment)}
          </div>
          <div style="
            margin-bottom: 12px;
            padding: 8px;
            background: #F5F7FA;
            border-radius: 4px;
            line-height: 1.5;
          ">
            <div style="font-weight: 500; color: #909399; margin-bottom: 4px;">建议修改:</div>
            <div>${escapeHtml(annotation.suggestion)}</div>
          </div>
          <div style="
            display: flex;
            justify-content: flex-end;
            gap: 8px;
            margin-top: 8px;
          ">
            <button class="annotation-close" style="
              padding: 5px 12px;
              background: #FFFFFF;
              border: 1px solid #DCDFE6;
              border-radius: 4px;
              color: #606266;
              cursor: pointer;
              font-size: 12px;
              transition: all 0.3s;
            ">关闭</button>
            <button class="annotation-cancel" style="
              padding: 5px 12px;
              background: #FFFFFF;
              border: 1px solid #DCDFE6;
              border-radius: 4px;
              color: #606266;
              cursor: pointer;
              font-size: 12px;
              transition: all 0.3s;
            ">取消</button>
            <button class="annotation-accept" style="
              padding: 5px 12px;
              background: #409EFF;
              border: 1px solid #409EFF;
              border-radius: 4px;
              color: #FFFFFF;
              cursor: pointer;
              font-size: 12px;
              transition: all 0.3s;
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
            border-top: 6px solid #FFFFFF;
          "></div>
        </div>
      `;
      
      span.innerHTML = range.toString() + popoverContent;
      
      range.deleteContents();
      range.insertNode(span);

      // 添加交互事件
      const highlight = span;
      const popover = highlight.querySelector('.annotation-popover');
      const closeBtn = popover.querySelector('.annotation-close');
      
      // 点击高亮显示弹窗
      highlight.onclick = (e) => {
        e.stopPropagation();
        // 隐藏所有其他弹窗
        document.querySelectorAll('.annotation-popover').forEach(el => {
          if (el !== popover) el.style.display = 'none';
        });
        // 切换当前弹窗
        popover.style.display = popover.style.display === 'block' ? 'none' : 'block';
      };
      
      // 关闭按钮点击事件
      closeBtn.onclick = (e) => {
        e.stopPropagation();
        popover.style.display = 'none';
      };
      
      // 取消按钮点击事件
      const cancelBtn = popover.querySelector('.annotation-cancel');
      cancelBtn.onclick = (e) => {
        e.stopPropagation();
        
        // 1. 获取高亮文本内容
        const textContent = highlight.textContent;
        
        // 2. 移除高亮span元素，保留文本内容
        const parent = highlight.parentNode;
        const textNode = document.createTextNode(textContent);
        parent.replaceChild(textNode, highlight);
        
        // 3. 隐藏弹窗
        popover.style.display = 'none';
        
        // 4. 恢复选区到原文本位置
        const range = document.createRange();
        range.selectNodeContents(textNode);
        const selection = window.getSelection();
        selection.removeAllRanges();
        selection.addRange(range);
      };
            
      // 接受按钮点击事件
      const acceptBtn = popover.querySelector('.annotation-accept');
      acceptBtn.onclick = (e) => {
        e.stopPropagation();
        // 替换为建议内容
        const newContent = annotation.suggestion;
        editor.dom.remove(highlight);
        editor.selection.select(highlight);
        editor.selection.setContent(newContent);
        popover.style.display = 'none';
      };
      
      // 点击编辑器其他区域关闭弹窗
      editor.on('click', (e) => {
        if (!highlight.contains(e.target)) {
          popover.style.display = 'none';
        }
      });
    });
  });

  // 恢复选区
  editor.selection.moveToBookmark(bookmark);
};

// 辅助函数：查找文本范围
const findTextRanges = (editor, searchText) => {
  const body = editor.getBody();
  const walker = document.createTreeWalker(
    body,
    NodeFilter.SHOW_TEXT,
    null
  );

  let ranges = [];
  let currentNode = null;
  let currentOffset = 0;
  let searchIndex = 0;
  let startNode = null;
  let startOffset = 0;

  while (walker.nextNode()) {
    currentNode = walker.currentNode;
    const nodeText = currentNode.nodeValue;

    for (let i = 0; i < nodeText.length; i++) {
      if (nodeText[i] === searchText[searchIndex]) {
        if (searchIndex === 0) {
          startNode = currentNode;
          startOffset = i;
        }
        searchIndex++;
        
        if (searchIndex === searchText.length) {
          // 找到完整匹配
          const range = document.createRange();
          range.setStart(startNode, startOffset);
          range.setEnd(currentNode, i + 1);
          ranges.push(range);
          
          // 重置搜索状态
          searchIndex = 0;
          startNode = null;
          startOffset = 0;
        }
      } else {
        // 不匹配，重置搜索状态
        searchIndex = 0;
        startNode = null;
        startOffset = 0;
      }
    }
  }

  return ranges;
};

// 正则表达式转义
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
  content_style: "body { font-family:Helvetica,Arial,sans-serif; font-size:16px }p {margin:3px; line-height:24px;}",
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
  return tinymce.activeEditor.getContent();
};

defineExpose({
  handleSetContent,
  handleGetContent,
  updateMargins,
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