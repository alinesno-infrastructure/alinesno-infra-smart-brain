<template>
    <div class="data-display-container">

        <div class="time-data-range-container">
            <span class="title">
                时间范围与数据范围
            </span>
            <span class="description">
                明确报告的时间区间（精确到月份），列举覆盖的销售数据指标（订单量、销售额、商品类别、地区分布、顾客行为），要求数据范围描述具体且无歧义，字数约600字。
            </span>
        </div>

        <div>
            <div id="vditor" class="data-vditor-panel"></div>
        </div>
    </div>
</template>

<script setup>
import { onMounted, ref } from 'vue';

import Vditor from 'vditor';
import 'vditor/dist/index.css';

const contentEditor = ref(null);
const defaultMd = `
# 在线 MarkDown 编辑器

![](https://pandao.github.io/editor.md/images/logos/editormd-logo-180x180.png)

Markdown 是一种轻量级标记语言，它允许人们使用易读易写的纯文本格式编写文档。

Markdown 语言在 2004 由约翰·格鲁伯（英语：John Gruber）创建。

Markdown 编写的文档可以导出 HTML 、Word、图像、PDF、Epub 等多种格式的文档。

## MarkDown 效果及格式示例

**目录 (Table of Contents)**

[TOCM]

[TOC]

# Heading 1
## Heading 2
### Heading 3
#### Heading 4
##### Heading 5
###### Heading 6
# Heading 1 link [Heading link](https://github.com/pandao/editor.md "Heading link")
## Heading 2 link [Heading link](https://github.com/pandao/editor.md "Heading link")
### Heading 3 link [Heading link](https://github.com/pandao/editor.md "Heading link")
#### Heading 4 link [Heading link](https://github.com/pandao/editor.md "Heading link") Heading link [Heading link](https://github.com/pandao/editor.md "Heading link")
##### Heading 5 link [Heading link](https://github.com/pandao/editor.md "Heading link")
###### Heading 6 link [Heading link](https://github.com/pandao/editor.md "Heading link")

#### 标题（用底线的形式）Heading (underline)

This is an H1
=============

This is an H2
-------------

### 字符效果和横线等

----

~~删除线~~ <s>删除线（开启识别HTML标签时）</s>
*斜体字*      _斜体字_
**粗体**  __粗体__
***粗斜体*** ___粗斜体___

上标：X<sub>2</sub>，下标：O<sup>2</sup>

**缩写(同HTML的abbr标签)**

> 即更长的单词或短语的缩写形式，前提是开启识别HTML标签时，已默认开启

The <abbr title="Hyper Text Markup Language">HTML</abbr> specification is maintained by the <abbr title="World Wide Web Consortium">W3C</abbr>.

### 引用 Blockquotes

> 引用文本 Blockquotes

引用的行内混合 Blockquotes

> 引用：如果想要插入空白换行\`即<br />标签\`，在插入处先键入两个以上的空格然后回车即可，[普通链接](http://localhost/)。

### 锚点与链接 Links

[普通链接](http://localhost/)

[普通链接带标题](http://localhost/ "普通链接带标题")

直接链接：<https: //github.com>

[锚点链接][anchor-id]

[anchor-id]: http://www.this-anchor-link.com/

[mailto:test.test@gmail.com](mailto:test.test@gmail.com)

GFM a-tail link @pandao  邮箱地址自动链接 test.test@gmail.com  www@vip.qq.com

> @pandao


### 图片 Images

Image:

![](https://pandao.github.io/editor.md/examples/images/4.jpg)

> Follow your heart.

![](https://pandao.github.io/editor.md/examples/images/8.jpg)

> 图为：厦门白城沙滩

图片加链接 (Image + Link)：

[![](https://pandao.github.io/editor.md/examples/images/7.jpg)](https://pandao.github.io/editor.md/images/7.jpg "李健首张专辑《似水流年》封面")

> 图为：李健首张专辑《似水流年》封面

----

### 多语言代码高亮 Codes

#### 行内代码 Inline code

### End
`

onMounted(() => {
    contentEditor.value = new Vditor('vditor', {
        height: 'calc(100vh - 220px)',
        width: '100%',
        toolbarConfig: {
            pin: true,
        },
        cache: {
            enable: false,
        },
        after: () => {
            contentEditor.value.setValue(defaultMd);
        },
        cdn: 'http://data.linesno.com/vditor'
    });
});
</script>

<style lang="scss" scoped>
.data-display-container {
    margin-top: -10px;
    height: calc(100vh - 160px);

    .time-data-range-container {
        display: flex;
        flex-direction: column;
        gap: 8px;
        margin-bottom: 20px;

        .title {
            font-size: 21px;
            font-weight: bold;
        }

        .description {
            font-size: 13px;
            color: #a5a5a5;
        }
    }
}
</style>

<style >
.data-vditor-panel .vditor-toolbar , 
.data-vditor-panel .vditor-reset
{
    padding-left: 10px !important;
    padding-right: 10px !important;
}
</style>