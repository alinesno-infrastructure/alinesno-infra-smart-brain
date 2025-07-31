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
    default: "undo redo | accordion accordionremove | blocks fontfamily fontsize| bold italic underline strikethrough ltr rtl  | align numlist bullist | link image | table | lineheight outdent indent| forecolor backcolor removeformat | charmap emoticons | anchor codesample",
  },
  readonly: {
    type: Boolean,
    default: false,
  },
  minHeight: {
    type: Number,
    default: 630,
  },
  contentMargins: {
    type: Object,
    default: () => ({ top: 2.54, bottom: 2.54, left: 3.17, right: 3.17 }) // 默认Word标准边距(厘米)
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
      padding: '0'
    });
  }
};

// 厘米转像素的转换函数
const cmToPx = (cm) => {
  return cm * 37.8; // 1cm ≈ 37.8px
};

//定义一个对象 init初始化
const init = ({
  selector: "#" + tinymceId.value, //富文本编辑器的id,
  language_url: "/tinymce/langs/zh_CN.js", // 语言包的路径，具体路径看自己的项目
  language: "zh_CN",
  // skin_url: "/tinymce/skins/ui/tinymce-5", // skin路径，具体路径看自己的项目
  skin_url: "/tinymce/skins/ui/oxide", // skin路径，具体路径看自己的项目
  editable_root: props.editable_root,
  height: 900,
  branding: false, // 是否禁用“Powered by TinyMCE”
  promotion: false, //去掉 upgrade

  toolbar: props.toolbar, //这里的数据是在props里面就定义好了的
  toolbar_location: "top" , 
  // toolbar_sticky: true,
  // toolbar_sticky_offset: 10,
  toolbar_mode: "wrap", // 工具栏模式 floating / sliding / scrolling / wrap  
  menubar: "edit view insert format tools table",
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
  quickbars_insert_toolbar: "image codesample table",
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
  // autoresize_overflow_padding: 16,
  min_height: props.minHeight,
  content_css: "/tinymce/skins/content/default/content.css", //以css文件方式自定义可编辑区域的css样式，css文件需自己创建并引入
  setup: function (editor) {
    editor.on('init', function() {

      if (editor.getContent() === '') {
        editor.setContent('<p style="color: #888;">请输入内容...</p>');
      }

      // 初始化时应用边距
      editor.dom.setStyles(editor.getBody(), {
        marginTop: `${props.contentMargins.top}cm`,
        marginBottom: `${props.contentMargins.bottom}cm`,
        marginLeft: `${props.contentMargins.left}cm`,
        marginRight: `${props.contentMargins.right}cm`,
        padding: '0'
      });
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
  },
  paste_preprocess: function(plugin, args) {
    // 处理粘贴的内容中的图片
    args.content = args.content.replace(/<img[^>]+src="file:[^>]+>/g, function(img) {
      return '<img src="data:image/png;base64,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" alt="图片需手动上传">';
    });
  },
  // images_upload_handler: async function (blobInfo, progress) {
  //   return new Promise(async (resolve, reject) => {
  //     try {
  //       const blob = blobInfo.blob();
  //       // 非base64图片需要上传
  //       if (blob.size / 1024 / 1024 > 200) {
  //         reject({
  //           message: "上传失败，文件大小请控制在 200M 以内",
  //           remove: true,
  //         });
  //         return;
  //       }
        
  //       const res = await uploadFile(blob, progress);
  //       // 根据实际API返回结构调整
  //       resolve(res.data.url || res.url);
        
  //     } catch (error) {
  //       if (error.message) {
  //         reject(error.message);
  //       } else {
  //         reject("上传失败");
  //       }
  //     }
  //   });
  // },
  // //图片上传  -实列 具体请根据官网补充-
  // images_upload_handler: async function (blobInfo, progress) {
  //   try {
  //     const file = blobInfo.blob();
  //     if (file.size / 1024 / 1024 > 10) {
  //       throw {
  //         message: "上传失败，图片大小请控制在 20M 以内",
  //         remove: true,
  //       };
  //     }
      
  //     const res = await uploadFile(file, progress);
  //     resolve(res.data.base64);
  //   } catch (error) {
  //     if (error.message) {
  //       // TinyMCE显示错误消息
  //       return Promise.reject(error.message);
  //     }
  //     return Promise.reject("上传失败");
  //   }
  // },
 
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
  border-radius: 4px;

  .tox-statusbar {
    display: none;
  }
}
</style>